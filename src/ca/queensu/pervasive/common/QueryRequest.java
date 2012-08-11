package ca.queensu.pervasive.common;


public class QueryRequest extends Request{

	private static final long serialVersionUID = -8088535479323939541L;

	public static final int LOCAL = 0;
	
	public static final int GLOBAL = 1;
		
	private String XQueryStatement;
	
	private int scope;
	
	private String clientKey;
	
	private LetterOfMark letterOfMark;
	
	public QueryRequest(){
		super();
		this.XQueryStatement = null;
		this.scope = LOCAL;
		this.letterOfMark = null;
	}
	
	/**
	 * 
	 * @param XQueryStatement the xquery statement to be run.
	 */
	public void setQuery(String XQueryStatement){
		this.XQueryStatement = XQueryStatement;
	}
	
	/**
	 * 
	 * @return the XQuery statement to be run.
	 */
	public String query(){
		return this.XQueryStatement;
	}
	
	/**
	 * Returns the scope over which the query should be run.
	 * @return An integer value indicating the query's scope; see GLOBAL/LOCAL.
	 */
	public int scope(){
		return this.scope;
	}
	
	/**
	 * Sets the scope over which the query should be run. 
	 * 
	 * @param scope An integer value indicating the query's scope; see GLOBAL/LOCAL.
	 */
	public void setScope(int scope){
		this.scope = scope;
	}
	
	/**
	 * Sets the key to which a response should be encoded.
	 * @param clientKey - RSA public key (Base64 encoded).
	 */
	public void setClientKey(String clientKey){
		this.clientKey= clientKey;
	}
	
	/**
	 * Returns the key to which results should be encoded.
	 * @return - RSA public key (Base64 encoded).
	 */
	public String clientKey(){
		return this.clientKey;
	}
	
	public void setLetterOfMark(LetterOfMark letterOfMark){
		this.letterOfMark = letterOfMark;
	}
	
	public LetterOfMark letterOfMark(){
		return this.letterOfMark;
	}
}