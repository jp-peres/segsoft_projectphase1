package segsoft.exception;


/**
 * AuthenticationError Exception
 * 
 * @author Tiago Bica N 47207 , Joao Peres N 48320
 *
 */

public class AuthenticationError extends ResponseException {

	private static final long serialVersionUID = 1L;

	public AuthenticationError(String error) {
		super(error);
	}
}