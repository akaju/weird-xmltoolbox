package ee.pri.kaju.xmltools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class TestMultiXMLStreamIterable {

	public static void main(String[] args) {
		File file = new File("src/test/data/multi-xml.txt");
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = dbf.newDocumentBuilder();
			FileInputStream fis = new FileInputStream(file);
			XMLStreams xmlStreams = new XMLStreams(fis);
			for(InputStream is: xmlStreams) {
				Document doc = builder.parse(is);
				if(doc != null)
					System.out.println(doc.getDocumentElement().getTagName());
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

}
