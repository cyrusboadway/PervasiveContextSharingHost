package test;

import ca.queensu.crypto.Codec;
import ca.queensu.crypto.CodecException;
import ca.queensu.crypto.Key;

/**
 * <p>This is a test/example class that creates a new key,
 * encrypts a string, signs the ciphertext, verifies the
 * signature against the content, and decrypts the content.</p>
 * 
 * @author cyrus
 * @version 1.0
 *
 */
public class CryptographyTest {

	public static void main(String[] args) throws CodecException {

		//Content to be encrypted.
		String plaintext = "This is a test of the cryptographic tools.";
		
		//Create a keypair.
		Key keyring = new Key();
		System.out.println("Public key:"+keyring.getPublicKey());
		System.out.println("Private key:"+keyring.getPrivateKey());
		System.out.println("Public hash:"+Codec.sha1(keyring.getPublicKey().getBytes()));
		
		
		//Encrypt the message.
		String ciphertext = Codec.encrypt(plaintext, keyring.getPublicKey());
		System.out.println("\nEncrypted Message:"+ciphertext);
		
		//Create a signature.
		String signature = Codec.sign(ciphertext.getBytes(), keyring.getPrivateKey());
		System.out.println("\nSignature:"+signature);
		
		//Verify signature.
		if(Codec.verifySignature(ciphertext.getBytes(), signature, keyring.getPublicKey()))
			System.out.println("\nSignature: valid");
		else
			System.out.println("\nSignature: invalid");
		
		//Decrypt content.
		String result = Codec.decrypt(ciphertext,keyring.getPrivateKey());
			System.out.println("\nDecrypted message: "+result);
	}

}