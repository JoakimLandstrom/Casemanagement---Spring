package se.plushogskolan.casemanagement.exception;

public class NoSpaceException extends ServiceException {

	private static final long serialVersionUID = 4399002405813100045L;

	public NoSpaceException(String message) {
		super(message);
	}

	public NoSpaceException(String message, Exception e) {
		super(message, e);
	}

}
