package ca.queensu.pervasive.xquery;

public class QueryException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9131174678464283118L;
	private int intError;

	QueryException(int intErrNo){
		intError = intErrNo;
	}

	QueryException(String strMessage){
		super(strMessage);
	}

	public String toString(){
		return "QueryException["+intError+"]";
	}  

}
