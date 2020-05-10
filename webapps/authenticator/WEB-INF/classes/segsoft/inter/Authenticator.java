package segsoft.inter;

/**
 * 
 * Authenticator Interface used on AuthenticatorDB class
 * 
 * @author Tiago Bica N 47207 , Joao Peres N 48320
 *
 */

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import segsoft.impl.Account;


public interface Authenticator {
	void create_account(String name, String pwd1, String pwd2);
	void delete_account(String name);
	Account get_account(String name);
	void change_pwd(String name, String pwd1, String pwd2);
	Account login(String name, String pwd);
	void logout(Account acc);
	void lock(Account acc);
	void unlock(Account acc);
	Account login(HttpServletRequest req, HttpServletResponse resp);
}
