package ee.pri.kaju.xmltools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

import javax.xml.stream.XMLStreamException;

public class TestMultiXMLReading {

	final static String EVENT_NAMES[] = {
			"NONE", "START_ELEMENT", "END_ELEMENT", "PROCESSING_INSTRUCTION",
			"CHARACTERS", "COMMENT", "SPACE", "START_DOCUMENT", "END_DOCUMENT",
			"ENTITY_REFERENCE", "ATTRIBUTE", "DTD", "CDATA", "NAMESPACE",
			"NOTATION_DECLARATION","ENTITY_DECLARATION"  
	};

	public static void main(String[] args) {
		File file = new File("src/test/data/multi-xml.txt");
		try {
			FileInputStream fis = new FileInputStream(file);
			NonXMLStreamSplitter task = new NonXMLStreamSplitter();
			task.setInputStream(fis);
			task.setDestinationFactory(new DestinationFactory() {
				int partNumber = 0;
				@Override
				public OutputStream getOutputStream() {
					System.out.println();
					System.out.println("=== document " + (++partNumber));
					return System.out;
				}
				
			});
			/*
			task.setEventListener(new XMLEventListener() {
				@Override
				public void event(XMLEvent event) {
					Location loc = event.getLocation();
					int evType = event.getEventType();
					String eventNameOut = (evType <= EVENT_NAMES.length ? EVENT_NAMES[evType] + " ": "");
					String locStr = loc == null ? "no location" : "line: " + loc.getLineNumber() + ", col: " + loc.getColumnNumber() + ", char: " + loc.getCharacterOffset();
					if(event instanceof StartElement) {
						StartElement stElem = (StartElement) event;
						//System.out.println(eventNameOut + stElem.getName().getLocalPart() + ", " + locStr);
					} else if(event instanceof EndElement) {
						EndElement endElem = (EndElement) event;
						//System.out.println(eventNameOut + endElem.getName().getLocalPart() + ", " + locStr);
					} else {
						//System.out.println("xmlevent: " + eventNameOut + locStr);
					}
				}
			})
			*/;
			task.extractAllDocuments();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if(e.getNestedException() != null)
				e.getNestedException().printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

}
