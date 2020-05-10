package segsoft.exception;

/**
 * ResponseException Exception
 * 
 * @author Tiago Bica N 47207 , Joao Peres N 48320
 *
 */

public class ResponseException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	public ResponseException(String error) {
		super(error);
	}
}
