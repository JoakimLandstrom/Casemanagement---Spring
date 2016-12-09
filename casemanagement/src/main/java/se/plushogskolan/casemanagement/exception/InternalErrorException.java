package se.plushogskolan.casemanagement.exception;

public class InternalErrorException extends ServiceException {

	private static final long serialVersionUID = -1204763191255779515L;

	public InternalErrorException(String message) {
		super(message);
	}

	public InternalErrorException(String message, Exception e) {
		super(message, e);
	}

}
