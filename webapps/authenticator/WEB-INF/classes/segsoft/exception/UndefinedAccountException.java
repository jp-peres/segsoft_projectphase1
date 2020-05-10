package segsoft.exception;

/**
 * UndefinedAccountException
 * 
 * @author Tiago Bica N 47207 , Joao Peres N 48320
 *
 */

public class UndefinedAccountException extends ResponseException{

	private static final long serialVersionUID = 1L;
	
	public UndefinedAccountException(String error) {
		super(error);
	}
	
}
