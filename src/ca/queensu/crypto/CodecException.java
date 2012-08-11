package ca.queensu.crypto;

public class CodecException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9131174678464283118L;
	private int intError;

	CodecException(int intErrNo){
		intError = intErrNo;
	}

	CodecException(String strMessage){
		super(strMessage);
	}

	public String toString(){
		return "QueryException["+intError+"]";
	}  

}
