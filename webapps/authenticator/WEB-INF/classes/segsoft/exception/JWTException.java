package segsoft.exception;

/**
 * JWTException Exception
 * 
 * @author Tiago Bica N 47207 , Joao Peres N 48320
 *
 */

public class JWTException extends ResponseException{
	
	private static final long serialVersionUID = 1L;

	public JWTException(String error) {
		super(error);
	}
}
