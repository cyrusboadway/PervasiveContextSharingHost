package test;

import ca.queensu.pervasive.xquery.XQuery;
import ca.queensu.xml.XMLHandler;

public class testQuerySystem {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		
		String xml = XMLHandler.readFile("xml/testEntity.xml");
		//String query = XMLHandler.readFile("xqueries/keyfind.xq").replaceAll("REPLACE", "person2");
		String query = "/";
		
		String result = XQuery.query(xml, query);
		
		System.out.println(xml);
		
		System.out.println(result);
	}

}
