package segsoft.unit;

/**
 * 
 * AuthenticatorDBTest class
 * 
 * @author Tiago Bica N 47207 , Joao Peres N 48320
 *
 */

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;

import org.apache.commons.codec.digest.DigestUtils;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;

import segsoft.exception.ResponseException;
import segsoft.impl.Account;
import segsoft.impl.AuthenticatorDB;

public class AuthenticatorDBTest {
/**
 * THIS TEST IS ONLY COVERING THE FUNCTIONALLITY THESE ARE NOT REAL TESTS
 */
	private static final String NAME = "name";
	private static final String PASS = "pwd";
	private static final String HOST = "localhost";
	private static final int PORT = 27017;
	private static final String DB = "demo";
	private static final String ENTITY = "user";
	
	@Before
	public void setUp() {
		MongoClient client = new MongoClient(HOST,PORT);
		CodecRegistry pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
				fromProviders(PojoCodecProvider.builder().automatic(true).build()));
		MongoDatabase db = client.getDatabase(DB).withCodecRegistry(pojoCodecRegistry);
		db.createCollection(ENTITY);
		MongoCollection<Account> col = db.getCollection(ENTITY, Account.class);
		col.createIndex(Indexes.ascending("accountName"), new IndexOptions().unique(true));
		client.close();
	}
	@After
	public void tearDown() {
		MongoClient client = new MongoClient(HOST,PORT);
		CodecRegistry pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
				fromProviders(PojoCodecProvider.builder().automatic(true).build()));
		MongoDatabase db = client.getDatabase(DB).withCodecRegistry(pojoCodecRegistry);
		MongoCollection<Document> col = db.getCollection(ENTITY);
		col.drop();
		client.close();
	}
	
	@Test
	public void createAccount() {
		//
		MongoClient client = new MongoClient(HOST,PORT);
		CodecRegistry pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
				fromProviders(PojoCodecProvider.builder().automatic(true).build()));
		MongoDatabase db = client.getDatabase(DB).withCodecRegistry(pojoCodecRegistry);
		MongoCollection<Document> col = db.getCollection(ENTITY);
		long count = col.countDocuments(Filters.eq("accountName", NAME));
		assertEquals(0, count);
		//
		AuthenticatorDB.getInstance().create_account(NAME, PASS, PASS);
		//
		count = col.countDocuments(Filters.eq("accountName", NAME));
		assertEquals(1, count);
		client.close();
	}
	@Test
	public void getAccount() {
		createAccount();
		Account acc = AuthenticatorDB.getInstance().get_account(NAME);
		assertEquals(NAME,acc.getAccountName());
		assertEquals(DigestUtils.sha512Hex(PASS), acc.getPasswordHashed());
	}
	@Test
	public void deleteAccount() {
		createAccount();
		try {
			AuthenticatorDB.getInstance().delete_account(NAME);
		}catch (ResponseException e) {
			fail(String.format("An error has occurred! (%s)", e.getClass().getSimpleName()));
		}
		//
		MongoClient client = new MongoClient(HOST,PORT);
		CodecRegistry pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
				fromProviders(PojoCodecProvider.builder().automatic(true).build()));
		MongoDatabase db = client.getDatabase(DB).withCodecRegistry(pojoCodecRegistry);
		MongoCollection<Document> col = db.getCollection(ENTITY);
		long count = col.countDocuments(Filters.eq("accountName", PASS));
		assertEquals(0, count);
		client.close();
		//
		
	}
	@Test
	public void changePwd() {
		createAccount();
		Account oldAcc = AuthenticatorDB.getInstance().get_account(NAME);
		String newPass = "newpwd";
		AuthenticatorDB.getInstance().change_pwd(NAME, newPass, newPass);
		Account newAcc = AuthenticatorDB.getInstance().get_account(NAME);
		assertEquals(DigestUtils.sha512Hex(newPass), newAcc.getPasswordHashed());
		assertNotEquals(oldAcc.getPasswordHashed(), newAcc.getPasswordHashed());
	}
	@Test
	public void login() {
		createAccount();
		AuthenticatorDB.getInstance().login("name", "pwd");
	}
}