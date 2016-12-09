package se.plushogskolan.casemanagement.exception;

public class StatusConflictException extends ServiceException{

	private static final long serialVersionUID = 3163669420855376613L;

	public StatusConflictException(String message) {
		super(message);
	}
	
	public StatusConflictException(String message, Exception e){
		super(message, e);
	}
	
	
	
	

}
