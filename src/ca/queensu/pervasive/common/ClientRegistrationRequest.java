package ca.queensu.pervasive.common;


public class ClientRegistrationRequest extends Request{

	private static final long serialVersionUID = -6153679189325907421L;

	private String XML;
	
	private LetterOfMark letterOfMark;
	
	public ClientRegistrationRequest(String clientXML, LetterOfMark issuedLetterOfMark) {
		super();
		this.XML = clientXML;
		this.letterOfMark = issuedLetterOfMark;
	}
	
	public String clientXML(){
		return this.XML;
	}
	
	public LetterOfMark issuedLetterOfMark(){
		return this.letterOfMark;
	}

}
