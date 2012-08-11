package ca.queensu.crypto;

/**
 * <p>This package is used to generate a new 1024 bit RSA key-pair.</p>
 * <p>Keys are Base 64 encoded.</p>
 * 
 */

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
//The following import is required only if a non-default initializations is made in the constructor.
//import java.security.SecureRandom;

public class Key {
	private KeyPair key;
	private String publicKey;
	private String privateKey;
	
	/**
	 * Constructor generates the new keys.
	 */
	public Key(){

		try {
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
			//This line isn't necessary as these are the default initializations.
			// keyGen.initialize(1024, new SecureRandom());
			//keyGen.initialize(1024);
			key = keyGen.generateKeyPair();
			this.privateKey = Base64.toBase64(key.getPrivate().getEncoded());
			this.publicKey = Base64.toBase64(key.getPublic().getEncoded());
		} catch (NoSuchAlgorithmException e) {
			System.err.println("Could not generate keypair.");
			e.printStackTrace();
		}
	}
	
	/**
	 * Sometimes you already have the key values, and would like to set them to a Key class... you do this here.
	 * 
	 * @param publicKey
	 * @param privateKey
	 */
	public Key(String publicKey, String privateKey) {
		this.privateKey = privateKey;
		this.publicKey = publicKey;
	}

	/**
	 * 
	 * @return the Base 64 encoded public key.
	 */
	public String getPublicKey(){
		return this.publicKey;
	}
	
	/**
	 * 
	 * @return the Base 64 encoded private key.
	 */
	public String getPrivateKey(){
		return this.privateKey;
	}
	
	public static void main (String[] args){
		Key k = new Key();
		System.out.println("public: "+k.getPublicKey());
		System.out.println("private:"+k.getPrivateKey());
	}
}
