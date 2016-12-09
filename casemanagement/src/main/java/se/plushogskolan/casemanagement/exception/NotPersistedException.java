package se.plushogskolan.casemanagement.exception;

public class NotPersistedException extends ServiceException {

	private static final long serialVersionUID = 8938215853503218701L;

	public NotPersistedException(String message) {
		super(message);
	}
	
	public NotPersistedException(String message, Exception e){
		super(message, e);
	}

}
