package ca.queensu.crypto;

/**
 * <p>This is a static class that provides the encryption tools required
 * to securely transmit protected data.</p>
 * 
 */

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class Codec {

	/**
	 * Decrypts Base64 encoded ciphertext to plaintext.
	 * 
	 * @param ciphertext Base64 encoded ciphertext to be decoded.
	 * @param encodedRSAPrivateKey Base64 encoded private (secret) key.
	 * @return plain text result of the decryption.
	 * 
	 */
	public static String decrypt(String ciphertext,String encodedRSAPrivateKey) throws CodecException{

		//String plaintext = null;

		try {
			// Get secret key from encoded public key.
			PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(Base64.fromBase64(encodedRSAPrivateKey));
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);

			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(Cipher.DECRYPT_MODE, privateKey);

			// Decrypt the ciphertext using the same key

			String a = new String();

			byte[] cipherbytes = Base64.fromBase64(ciphertext);

			//It would be better to use a buffered reader here, but array copy will do.
			for(int i=0; i<cipherbytes.length; i+=128){
				byte[] temp = new byte[128];
				System.arraycopy(cipherbytes, i, temp, 0, 128);
				a = a.concat(new String(cipher.doFinal(temp)));
			}
			return a;

		} catch (InvalidKeyException e) {

			//e.printStackTrace();
			throw new CodecException(0);
		} catch (IllegalBlockSizeException e) {
			//e.printStackTrace();
			throw new CodecException(0);
		} catch (BadPaddingException e) {

			//e.printStackTrace();
			throw new CodecException(0);
		} catch (NoSuchAlgorithmException e) {

			//e.printStackTrace();
			throw new CodecException(0);
		} catch (NoSuchPaddingException e) {

			//e.printStackTrace();
			throw new CodecException(0);
		} catch (InvalidKeySpecException e) {
			//e.printStackTrace();
			throw new CodecException(0);
		}
	}

	/**
	 * Encrypts plaintext string to Base64 encoded ciphertext.
	 * 
	 * @param plaintext
	 * @param encodedRSAPublicKey
	 * @return
	 * @throws CodecException 
	 */
	public static String encrypt(String plaintext, String encodedRSAPublicKey) throws CodecException{

		try {
			//Get public key from encoded public key.
			X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(Base64.fromBase64(encodedRSAPublicKey));
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			PublicKey pk = keyFactory.generatePublic(publicKeySpec);

			//This is hard coded because I don't know how to check the actual key size from the pk.
			int keysize=1024;

			//This is the limit to how much data can be encoded with the given key size.
			int blocksize=keysize/8-11;

			byte[] plaintextbytes = plaintext.getBytes();

			// Initializes the Cipher object.
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(Cipher.ENCRYPT_MODE, pk);

			// Encrypt the plaintext using the public key
			byte[] cipherText = {};
			for (int blockid=0; blockid<plaintextbytes.length; blockid+=blocksize){

				//number of bytes to encrypt
				int left = Math.min(plaintextbytes.length-blockid,blocksize);

				//contains bytes to be encrypted
				byte[] block = new byte[left];

				//get plaintext byte block
				System.arraycopy(plaintextbytes, blockid, block, 0, left);

				//Encrypt current block
				byte[] cipherBlock = cipher.doFinal(block);

				//"Append" new encrypted byte block to the end of the array.
				byte[] newCipherText = new byte[cipherText.length+cipherBlock.length];
				System.arraycopy(cipherText,0,newCipherText,0,cipherText.length);
				System.arraycopy(cipherBlock,0,newCipherText,cipherText.length,cipherBlock.length);
				cipherText=newCipherText;
			}

			return Base64.toBase64(cipherText);

		} catch (IllegalBlockSizeException e) {
			throw new CodecException(0);
		} catch (BadPaddingException e) {
			e.printStackTrace();
			throw new CodecException(0);
		} catch (InvalidKeyException e) {
			e.printStackTrace();
			throw new CodecException(0);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			throw new CodecException(0);
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
			throw new CodecException(0);
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
			throw new CodecException(0);
		}
	}

	/**
	 * For a given data input, produces a Base64 encoded signature 
	 * for <code>data</code> using the given private key.
	 * 
	 * @param data content to be signed. This is in byte array form since the data to be encoded may be of various formats, and not just string. 
	 * @param encodedRSAPrivateKey key with which to sign the data.
	 * @return Base 64 signature.
	 */
	public static String sign(byte[] data, String encodedRSAPrivateKey){
		try {
			// Get secret key from encoded public key.
			PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(Base64.fromBase64(encodedRSAPrivateKey));
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);

			//Sign Data, return result.
			Signature signer = Signature.getInstance("SHA1withRSA");
			signer.initSign(privateKey);
			signer.update(data);
			return Base64.toBase64(signer.sign());

		} catch (NoSuchAlgorithmException e) {
			System.err.println("No such algorithm.");
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			System.err.println("Invalid Key Spec.");
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			System.err.println("Invalid Key.");
			e.printStackTrace();
		} catch (SignatureException e) {
			System.err.println("Could not sign.");
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Verifies that a <code>signature</code> given for <code>data</code> signed with
	 * the given <code>key</code> is in fact a valid signature. 
	 * 
	 * @param data signed content.
	 * @param signature the signature for the signed content.
	 * @param encodedRSAPublicKey public key of purported signer.
	 * @return <code>True</code> if signature is valid, <code>False</code> otherwise.
	 */
	public static boolean verifySignature(byte[] data, String signature, String encodedRSAPublicKey){

		try {
			//Decode PK
			X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(Base64.fromBase64(encodedRSAPublicKey));
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			PublicKey pubKey = keyFactory.generatePublic(pubKeySpec);

			//Sign, return verified
			Signature sig = Signature.getInstance("SHA1withRSA");
			sig.initVerify(pubKey);
			sig.update(data);
			return sig.verify(Base64.fromBase64(signature));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (SignatureException e) {
			e.printStackTrace();
		}

		return false;
	}

	public static String sha1(byte[] data){
		MessageDigest sha1;
		try {
			sha1 = MessageDigest.getInstance("SHA1");
		} catch (java.security.NoSuchAlgorithmException e) {
			return null;
		}
		return Base64.toBase64(sha1.digest(data));
	}
}