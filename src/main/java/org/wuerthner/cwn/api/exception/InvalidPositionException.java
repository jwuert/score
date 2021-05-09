package org.wuerthner.cwn.api.exception;

public class InvalidPositionException extends RuntimeException {
	
	private static final long serialVersionUID = 8172024396152648114L;
	
	public InvalidPositionException(String msg) {
		super(msg);
	}
}
