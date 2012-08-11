package ca.queensu.crypto;

/**
 * <p>This class is a stripped down version of gnu.crypto.util.Base64, modified
 * to be lean, and static.</p>
 * 
 * <p>This tool is used for converting the binary output of cryptographic content,
 * including keys, signatures, and ciphertext, to a quote-safe, human readable,
 * and S/MIME compliant format (which amounts to XML safe content).</p>
 * 
 * @author cyrus
 * @source gnu.crypto.utils.Base64
 *
 */

public class Base64 {

	private static final String BASE64_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz./";
	private static final char[] BASE64_CHARSET = BASE64_CHARS.toCharArray();
	
	/**
	 * <p>Converts a designated byte array to a Base-64 representation, with the
	 * exceptions that (a) leading 0-byte(s) are ignored, and (b) the character
	 * '.' (dot) shall be used instead of "+' (plus).</p>
	 *
	 * <p>Used by SASL password file manipulation primitives.</p>
	 *
	 * @param buffer an arbitrary sequence of bytes to represent in Base-64.
	 * @return unpadded (without the '=' character(s)) Base-64 representation of
	 * the input.
	 */
	public static final String toBase64(byte[] buffer) {
		int len = buffer.length, pos = len % 3;
		byte b0 = 0, b1 = 0, b2 = 0;
		switch (pos) {
		case 1:
			b2 = buffer[0];
			break;
		case 2:
			b1 = buffer[0];
			b2 = buffer[1];
			break;
		}
		StringBuffer sb = new StringBuffer();
		int c;
		boolean notleading = false;
		do {
			c = (b0 & 0xFC) >>> 2;
			if (notleading || c != 0) {
				sb.append(BASE64_CHARSET[c]);
				notleading = true;
			}
			c = ((b0 & 0x03) << 4) | ((b1 & 0xF0) >>> 4);
			if (notleading || c != 0) {
				sb.append(BASE64_CHARSET[c]);
				notleading = true;
			}
			c = ((b1 & 0x0F) << 2) | ((b2 & 0xC0) >>> 6);
			if (notleading || c != 0) {
				sb.append(BASE64_CHARSET[c]);
				notleading = true;
			}
			c = b2 & 0x3F;
			if (notleading || c != 0) {
				sb.append(BASE64_CHARSET[c]);
				notleading = true;
			}
			if (pos >= len) {
				break;
			} else {
				try {
					b0 = buffer[pos++];
					b1 = buffer[pos++];
					b2 = buffer[pos++];
				} catch (ArrayIndexOutOfBoundsException x) {
					break;
				}
			}
		} while (true);

		if (notleading) {
			return sb.toString();
		}
		return "0";
	}

	/**
	 * <p>The inverse function of the above.</p>
	 *
	 * <p>Converts a string representing the encoding of some bytes in Base-64
	 * to their original form.</p>
	 *
	 * @param str the Base-64 encoded representation of some byte(s).
	 * @return the bytes represented by the <code>str</code>.
	 * @throws NumberFormatException if <code>str</code> is <code>null</code>, or
	 * <code>str</code> contains an illegal Base-64 character.
	 * @see #toBase64(byte[])
	 */
	public static final byte[] fromBase64(String str) {
		int len = str.length();
		if (len == 0) {
			throw new NumberFormatException("Empty string");
		}
		byte[] a = new byte[len + 1];
		int i, j;
		for (i = 0; i < len; i++) {
			try {
				a[i] = (byte) BASE64_CHARS.indexOf(str.charAt(i));
			} catch (ArrayIndexOutOfBoundsException x) {
				throw new NumberFormatException("Illegal character at #"+i);
			}
		}
		i = len - 1;
		j = len;
		try {
			while (true) {
				a[j] = a[i];
				if (--i < 0) {
					break;
				}
				a[j] |= (a[i] & 0x03) << 6;
				j--;
				a[j] = (byte)((a[i] & 0x3C) >>> 2);
				if (--i < 0) {
					break;
				}
				a[j] |= (a[i] & 0x0F) << 4;
				j--;
				a[j] = (byte)((a[i] & 0x30) >>> 4);
				if (--i < 0) {
					break;
				}
				a[j] |= (a[i] << 2);
				j--;
				a[j] = 0;
				if (--i < 0) {
					break;
				}
			}
		} catch (Exception ignored) {
		}

		try { // ignore leading 0-bytes
			while(a[j] == 0) {
				j++;
			}
		} catch (Exception x) {
			return new byte[1]; // one 0-byte
		}
		byte[] result = new byte[len - j + 1];
		System.arraycopy(a, j, result, 0, len - j + 1);
		return result;
	}
}
