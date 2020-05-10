package segsoft.impl;

/**
 * 
 * AuthenticatorDB class mostly used for authentication and using account operations
 * 
 * @author Tiago Bica N 47207 , Joao Peres N 48320
 *
 */
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import java.io.FileReader;
import java.nio.file.Paths;
import java.security.Key;
import java.util.Date;

import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.codec.digest.DigestUtils;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import com.google.gson.Gson;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import segsoft.exception.AuthenticationError;
import segsoft.exception.ErrorMessage;
import segsoft.exception.JWTException;
import segsoft.exception.LockedAccountException;
import segsoft.exception.ResponseException;
import segsoft.exception.UndefinedAccountException;
import segsoft.inter.Authenticator;
import segsoft.utils.ConfigData;

public class AuthenticatorDB implements Authenticator {

	private MongoClient client;
	private CodecRegistry pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
			fromProviders(PojoCodecProvider.builder().automatic(true).build()));
	private MongoDatabase db;
	private static final String USERS = "user";
	// base64 encode - bicaperes4720748320win
	private static final String secretKey = "YmljYXBlcmVzNDcyMDc0ODMyMHdpbg==";
	// TTL in ms for JWT expiration ~= 3 mins
	private static long TTL = 180000;
	
	
	/*
	 * Singleton
	 */
	private static AuthenticatorDB ADB;

	private AuthenticatorDB() {
		Gson g = new Gson();
		ConfigData data;
		try {
			data = g.fromJson(
					new FileReader(Paths.get(System.getProperty("catalina.base"),"webapps","authenticator", "config.json").toFile()),
					ConfigData.class);
		} catch (Exception e) {
			throw new ResponseException(e.getMessage());
		}
		client = new MongoClient(data.host,data.port);
		db = client.getDatabase("demo").withCodecRegistry(pojoCodecRegistry);
	}

	public static AuthenticatorDB getInstance() {
		if (ADB == null)
			ADB = new AuthenticatorDB();
		return ADB;
	}

	public void create_account(String name, String pwd1, String pwd2) {
		if (!pwd1.equals(pwd2))
			throw new AuthenticationError(ErrorMessage.ACC_WRONGPASS);
		MongoCollection<Account> col = db.getCollection(USERS, Account.class);
		if (col.find(Filters.eq("accountName", name)).first() != null)
			throw new UndefinedAccountException(ErrorMessage.ACC_EXISTS);
		Account acc = new Account(name, pwd1);
		col.insertOne(acc);
	}

	public void delete_account(String name) {
		MongoCollection<Account> col = db.getCollection(USERS, Account.class);
		Account acc = col.find(Filters.eq("accountName", name)).first();
		if (acc == null)
			throw new UndefinedAccountException(ErrorMessage.ACC_NOTFOUND);
		if (acc.isLoggedIn())
			throw new AuthenticationError(ErrorMessage.LOGGEDIN);
		if (!acc.isLocked())
			throw new AuthenticationError(ErrorMessage.NOT_LOCKED);
		col.deleteOne(Filters.eq("accountName", name));
	}

	public Account get_account(String name) {
		MongoCollection<Account> col = db.getCollection(USERS, Account.class);
		return col.find(Filters.eq("accountName", name)).first();
	}
	
	public MongoCursor<Account> getAllAccounts(){
		MongoCollection<Account> col = db.getCollection(USERS, Account.class);
		return col.find().iterator();
	}

	public void change_pwd(String name, String pwd1, String pwd2) {
		if (!pwd1.equals(pwd2))
			throw new AuthenticationError(ErrorMessage.ACC_WRONGPASS);
		MongoCollection<Account> col = db.getCollection(USERS, Account.class);
		Account acc;
		if ((acc = col.find(Filters.eq("accountName", name)).first()) == null)
			throw new UndefinedAccountException(ErrorMessage.ACC_NOTFOUND);
		acc.putNewPassword(pwd1);
		col.replaceOne(Filters.eq("accountName", name), acc);
	}

	public Account login(String name, String pwd) {
		MongoCollection<Account> col = db.getCollection(USERS, Account.class);
		Account acc = col.find(Filters.eq("accountName", name)).first();
		if (acc == null)
			throw new UndefinedAccountException(ErrorMessage.ACC_NOTFOUND);
		if (acc.isLocked())
			throw new LockedAccountException(ErrorMessage.LOCKED);
		String aux = DigestUtils.sha512Hex(pwd);
		if (!acc.getPasswordHashed().equals(aux))
			throw new AuthenticationError(ErrorMessage.ACC_WRONGPASS);
		if (!acc.isLoggedIn()) {
			acc.setLoggedIn(true);
			col.replaceOne(Filters.eq("accountName", name), acc);
		}
		return acc;
	}

	public void logout(Account acc) {
		MongoCollection<Account> col = db.getCollection(USERS, Account.class);
		acc.setLoggedIn(false);
		col.replaceOne(Filters.eq("accountName", acc.getAccountName()), acc);
	}
	
	public void lock(Account acc) {
		MongoCollection<Account> col = db.getCollection(USERS, Account.class);
		acc.setLocked(true);
		col.replaceOne(Filters.eq("accountName", acc.getAccountName()), acc);
	}

	public void unlock(Account acc) {
		MongoCollection<Account> col = db.getCollection(USERS, Account.class);
		acc.setLocked(false);
		col.replaceOne(Filters.eq("accountName", acc.getAccountName()), acc);
	}

	public Account login(HttpServletRequest req, HttpServletResponse resp)
			throws AuthenticationError, ResponseException {
		Object currJwt = req.getSession().getAttribute("jwt");
		if (currJwt == null)
			throw new JWTException(ErrorMessage.JWT_MISSING);
		else {
			String user;
			Account acc;
			// if session jwt has expired
			if ((user = validateAndExtractUserJwt((String)currJwt)) == null)
				throw new JWTException(ErrorMessage.JWT_EXPIRED);
			MongoCollection<Account> col = db.getCollection(USERS, Account.class);
			if ((acc = col.find(Filters.eq("accountName", user)).first()) == null)
				throw new UndefinedAccountException(ErrorMessage.ACC_NOTFOUND);
			if (!acc.isLoggedIn())
				throw new AuthenticationError(ErrorMessage.NOT_LOGGEDIN);
			return acc;
		}
	}
	
	public String createJWT(String id, String issuer, String subject) {

		long currentNow = System.currentTimeMillis();
		// The JWT signature algorithm used to sign the token
		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

		// sign JWT with ApiKey secret
		byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(secretKey);
		Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

		// Set the JWT Claims
		JwtBuilder builder = Jwts.builder().setId(id).setSubject(subject).setIssuer(issuer).signWith(signatureAlgorithm,
				signingKey);

		// Set JWT expiration
		long expiration = currentNow + TTL;
		Date exp = new Date(expiration);
		builder.setExpiration(exp);

		// Builds the JWT and serializes it to a compact, URL-safe string
		return builder.compact();
	}

	public String validateAndExtractUserJwt(String jwt) {
		String res;
		try {
			res = Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary(secretKey))
				.parseClaimsJws(jwt).getBody().getIssuer();
		} catch(ExpiredJwtException ex) {
			// Sets account as logged off
			Account acc = get_account(ex.getClaims().getIssuer());
			logout(acc);
			res = null;
		}
		return res;
	}
}