package ca.queensu.pervasive.common;

import java.io.Serializable;

public class HostKeyResponse extends Response implements Serializable{

	private static final long serialVersionUID = -3848106598334224269L;

	private String key;
	
	public HostKeyResponse(){
		super();
		this.key = "";
	}

	public HostKeyResponse(String hostKey){
		super();
		this.key = hostKey;
	}
	
	public void setKey(String key){
		this.key = key;
	}
	
	public String key(){
		return this.key;
	}
	
}
