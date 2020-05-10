package segsoft.inter;

/**
 * 
 * Account Interface used on Account class
 * 
 * @author Tiago Bica N 47207 , Joao Peres N 48320
 *
 */
public interface AccountInterface {
	String getAccountName();
	String getPasswordHashed();
	void setLocked(boolean val);
	void setLoggedIn(boolean val);
	boolean isLoggedIn();
	boolean isLocked();
}
