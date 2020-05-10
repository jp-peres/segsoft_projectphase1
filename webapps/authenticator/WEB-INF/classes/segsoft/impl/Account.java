package segsoft.impl;

/**
 * 
 * Account class used in AuthenticatorDB
 * 
 * @author Tiago Bica N 47207 , Joao Peres N 48320
 *
 */

import org.apache.commons.codec.digest.DigestUtils;
import org.bson.codecs.pojo.annotations.BsonProperty;

import segsoft.inter.AccountInterface;

public class Account implements AccountInterface {

	@BsonProperty
	public String accountName;
	@BsonProperty
	public String password;
	@BsonProperty
	public boolean loggedIn;
	@BsonProperty
	public boolean locked;

	public Account() {
	}

	public Account(String accName, String pwd) {
		this.accountName = accName;
		this.password = DigestUtils.sha512Hex(pwd);
		this.loggedIn = false;
		this.locked = false;
	}
	
	public String getAccountName() {
		return accountName;
	}

	public String getPasswordHashed() {
		return password;
	}

	public boolean isLoggedIn() {
		return loggedIn;
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean val) {
		locked = val;
	}

	public void setLoggedIn(boolean val) {
		loggedIn = val;
	}
	/* for now */

	public void putNewPassword(String pwd) {
		this.password = DigestUtils.sha512Hex(pwd);
	}
}