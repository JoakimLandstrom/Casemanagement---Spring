package se.plushogskolan.casemanagement.exception;

public class AlreadyPersistedException extends ServiceException {

	private static final long serialVersionUID = 5111143904950122400L;

	public AlreadyPersistedException(String message) {
		super(message);
	}
	
	public AlreadyPersistedException(String message, Exception e){
		super(message, e);
	}

}
