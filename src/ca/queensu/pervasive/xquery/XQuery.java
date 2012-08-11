/**
 * 
 * @author Cyrus Boadway
 * @date 14/5/2009
 * 
 * This static class does all of the xquery processing required for this pervasive computing
 * platform. It has a base method "query" which performs the actual xquery, as well as other
 * methods that provide embedded specialized queries for handling the pervasive computing
 * situation.
 * 
 */

package ca.queensu.pervasive.xquery;

import org.basex.core.Context; 
import org.basex.core.proc.Set; 
import org.basex.core.proc.CreateDB;
import org.basex.data.Result;  
import org.basex.data.XMLSerializer;
import org.basex.io.CachedOutput;
import org.basex.query.QueryProcessor;
import org.basex.query.QueryException;

import ca.queensu.xml.XMLHandler;

import java.util.Date;

public class XQuery {

	/**
	 * Builds a context from an XML string. 
	 * 
	 * @param xml
	 * @return
	 */
	
	private static Context newContext(String xml){
		
		Context context = new Context();
		
		new Set("xmloutput", "on").execute(context);
		new CreateDB(xml,"joined").execute(context);
		
		return context;
	}

	/**
	 * This method performs xquery on a given xml string. Xquery is limited to basex supported
	 * features, which included embedded functions. 
	 * 
	 * @param xml - xml document on which to perform the xquery statement.
	 * @param XQueryStatement - statement to be processed.
	 * @return - result of xquery applied to xml.
	 * @throws QueryException - in the event of a query failure.
	 */
	public static synchronized String query(String xml, String XQueryStatement) throws QueryException{
		String queryResult = "";

		//Build xml context.
		Context context = newContext(xml);

		//Cache is used for storing temporary unserialized result.
		CachedOutput cache = new CachedOutput();

		//Create query processor for this query on these nodes.
		QueryProcessor processor = new QueryProcessor(XQueryStatement,context);
		
		try {

			//Run query.
			Result result = processor.query();
			cache = new CachedOutput();

			//Turn result to a string.
			result.serialize(new XMLSerializer(cache));
			cache.flush();
			queryResult = cache.toString();

			//Close objects.
			processor.close();
			cache.close();
			context.close();

		} catch (Exception e) {
			//System.err.println("Failed to run query.");
			//System.err.println("query: " + XQueryStatement + "\n");
			//System.err.println("pile : " + xml + "\n");
			throw new QueryException("Query Failed.");
		}
					
		//Return good result.
		return queryResult;

	}

	/**
	 *	The method searches a situation document for the public key of a given user's id.
	 * 
	 * @param xml - the situation document
	 * @param userid - id to search for.
	 * @return Base64 encoded RSA public key, or null if no key is found for the given clientid.
	 * @throws QueryException 
	 */
	public static String getKey(String xml, String userid) throws QueryException{

		//Quick xpath to find the correct key.
		//String keyquery ="let $x:=root()//entities/*[@ID/string()=\""+escapeString(userid)+"\"]/key/public/string() return $x";
		String keyquery ="//permissions/permission/accessor[@IDREF=\""+escapeString(userid)+"\"][1]/key/string()";
		//String keyquery;

		//Result string.
		String response;

		//Apply xquery/xpath
		response = query(xml, keyquery);

		//return null if no key found.
		if(response.length()==0) return null;

		return response;
	}

	/**
	 * Finds the key for a given client id.
	 * @param xml
	 * @param keyString
	 * @return
	 * @throws QueryException
	 */
	public static String getClient(String xml, String keyString) throws QueryException{
		//This query gets the (first) ID of the node (entity) containing a matching <key /> node.
		String clientquery = "//*[key/string()=\""+escapeString(keyString)+"\"][1]/@ID/string()";
		String result = null;

		result = XQuery.query(xml, clientquery);
		if(result.length()==0) return "publicView";

		return result;
	}

	/**
	 * This method returns the public view of the context repository for a given userid
	 * @param userid - public key of the entity on whose behalf the query is being made.
	 * @return xml string containing all accessible components.
	 * @throws QueryException 
	 */
	public static String getView(String xml, String userid) throws QueryException{

		//If the userid is set to null, then the view returned is the "Base Public View".
		if (userid==null) userid = "noone";

		String view = new String();

		String viewquery = new String();
		try{
			//This query is stored in xqueries/view.xq
			viewquery = XMLHandler.readFile("/home/cyrus/Documents/workspace/Host/src/xqueries/view.xq").replaceAll("REPLACE", escapeString(userid));
		} catch(Exception e){
			e.printStackTrace();
			throw new QueryException(0);
		}

		//run query
		try {
			view = query(XQuery.identity(xml), viewquery);
		} catch (QueryException e) {
			System.err.println("Couldn't run the view query.");
			e.printStackTrace();
			throw new QueryException(0);
		}
		return view;
	}

	/**
	 * 
	 * 
	 * 
	 * @param xml
	 * @return
	 * @throws QueryException
	 */
	public static String identity(String xml) throws QueryException{
		
		String viewquery = new String();
		String view = new String();
		
		try{
			//This query is stored in xqueries/view.xq
			viewquery = XMLHandler.readFile("/home/cyrus/Documents/workspace/Host/src/xqueries/identity.xq");
		} catch(Exception e){
			e.printStackTrace();
			throw new QueryException(0);
		}
		//run query
		try {
			view = query(xml, viewquery);
		} catch (QueryException e) {
			System.err.println("Couldn't run the identity query.");
			e.printStackTrace();
			throw new QueryException(0);
		}
		return view;
	}

	/**
	 * Using the apache commons XML string escape library.
	 * 
	 * @param XML str
	 * @return XML escaped string
	 * @throws QueryException 
	 */
	public static String escapeString(String str) throws QueryException{
		try {
			org.apache.commons.lang.Entities.XML.escape(str);
		} catch (Exception e) {
			throw new QueryException(0);
		}

		return str;
	}	

	/**
	 * Checks the data model to determine whether the data is "fresh",
	 * which is to say up to date, with no expired values.
	 * 
	 * @return: True if the data in the model is up to date, False if not (or upon query failure).
	 * @throws QueryException 
	 */
	public static boolean fresh(String xml) throws QueryException{

		//test whether data needs to be requeried.
		long time = new Date().getTime();
		String queryString = "for $x in //@expiry/number() return not(min($x)<"+time+")";
		
		//empty string for result.
		String result = new String();

		result = query(xml, queryString);

		//WARNING: This fresh test is disabled here.
		int test = 1;
		if(result.compareToIgnoreCase("true")==0||1==test) return true;
		else return false;
	}

}