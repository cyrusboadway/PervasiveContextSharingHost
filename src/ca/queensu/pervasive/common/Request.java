package ca.queensu.pervasive.common;

import java.io.Serializable;


public class Request implements Serializable{

	private static final long serialVersionUID = 5288399712300487350L;

	private int error;

	public LetterOfMark letterOfMark;

	public Request(){
		this.error = 0;
		this.letterOfMark = null;
	}

	public void setError(int errorNumber){
		this.error = errorNumber;
	}
	
	public int error(){
		return this.error;
	}
	
	public void setLetterOfMark(LetterOfMark lom){
		this.letterOfMark = lom;
	}

}