package ca.queensu.pervasive.common;

import java.io.Serializable;

public class ClientRegistrationResponse extends Response implements Serializable{

	private static final long serialVersionUID = 5111482498571715412L;

	private boolean success;
	
	public ClientRegistrationResponse(){
		super();
		this.success=true;
	}
	
	public void setSuccess(boolean success){
		this.success = success;
	}
	
	public boolean success(){
		return this.success;
	}
}
