package se.plushogskolan.casemanagement.exception;

public class IllegalArgumentException extends ServiceException{

	private static final long serialVersionUID = 970534009192827260L;

	public IllegalArgumentException(String message){
		super(message);
	}
	
	public IllegalArgumentException(String message, Exception e){
		super(message, e);
	}
}
