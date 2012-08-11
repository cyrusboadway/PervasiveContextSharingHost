package ca.queensu.pervasive.common;

public class QueryResponse extends Response{

	private static final long serialVersionUID = -5115792806453521967L;
	
	public String response;
	
	public QueryResponse(){
		super();
		this.response = "";
	}
	
	public void setResponse(String response){
		this.response = response;
	}
	
	public String response(){
		return this.response;
	}
}