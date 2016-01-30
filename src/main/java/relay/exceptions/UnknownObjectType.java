package relay.exceptions;

/**
 * When constructing Node objects this is thrown when we are unable to construct the object from the specified ID.
 */
public class UnknownObjectType extends RuntimeException{

	public UnknownObjectType(String message) {
		super(message);
	}
}
