/**
 * This file does various XML formatting and handling stuff, like validating XML.
 * 
 * Cyrus Boadway 2009
 */

package ca.queensu.xml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class XMLHandler {

	/**
	 * Read's a file into a string. Ugliest file reader ever, but hell, it works.
	 * 
	 * @param fileName - Path to file's location.
	 * @return - The string contents of the file.
	 * @throws Exception - If the file cannot be retrieved.
	 */
	public static String readFile(String fileName) throws Exception {
		String doc = new String();

		BufferedReader in = new BufferedReader(new FileReader(new File(fileName)));
		String line;

		while ((line = in.readLine()) != null)
			doc += line+"\n";
		
		in.close();

		return doc;
	}
	
	/**
	 * Checks an XML string for well-formedness (not a word).
	 * 
	 * @param xml - an xml string.
	 * @return - true if well-formed, false, otherwise.
	 */
	public static boolean SAXParserCheck(String xml){
		//No null inputs. Helps solve another problem, somewhere, for null query requests.
		if (xml==null) return false;
		
		//Use the nice SAX libraries to parse XML.
		try{
			XMLReader reader = XMLReaderFactory.createXMLReader();
			
			reader.parse(new InputSource(new StringReader(xml)));
			return true;
		}
		catch (SAXException sax){
			return false;
		} catch (IOException e) {
			return false;
		}
	}
}
