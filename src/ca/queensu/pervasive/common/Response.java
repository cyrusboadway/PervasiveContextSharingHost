package ca.queensu.pervasive.common;

import java.io.Serializable;

/**
 * <p> This is the base message class, which is extended for
 * specific message types. This class supports basic features
 * like errors, and flags marking the content of the response
 * as encrytped or unencrypted.</p>
 * 
 * @author cyrus
 *
 */

public class Response implements Serializable {

	private static final long serialVersionUID = -5595580004494626532L;

	public boolean encrypted;
	
	private int error;
	public static final int ERROR_NONE = 0;
	public static final int ERROR_AUTHENTICATION = 1;
	public static final int ERROR_COULD_NOT_COMPUTE = 2;
	
	
	public Response(){
		encrypted = false;
		error = 0;
	}
	
	public void setError(int error){
		this.error = error;
	}
	
	public int error(){
		return this.error;
	}
}