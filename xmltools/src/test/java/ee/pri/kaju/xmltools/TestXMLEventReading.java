package ee.pri.kaju.xmltools;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.xml.stream.Location;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndDocument;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class TestXMLEventReading {

	final static String EVENT_NAMES[] = {
			"NONE", "START_ELEMENT", "END_ELEMENT", "PROCESSING_INSTRUCTION",
			"CHARACTERS", "COMMENT", "SPACE", "START_DOCUMENT", "END_DOCUMENT",
			"ENTITY_REFERENCE", "ATTRIBUTE", "DTD", "CDATA", "NAMESPACE",
			"NOTATION_DECLARATION","ENTITY_DECLARATION"  
	};
	
	public static void main(String[] args) {
		XMLInputFactory inputFact = XMLInputFactory.newFactory();
		
		int elementLevel = 0;
		boolean firstElementSeen = false;
		
		try(BufferedReader bis = new BufferedReader(new FileReader(args[0])); ) {
			ReaderWrapper sr = new ReaderWrapper(bis);
			XMLEventReader eventReader = inputFact.createXMLEventReader(sr);
			while(eventReader.hasNext()) {
				XMLEvent xmlEvent = eventReader.nextEvent();
				Location loc = xmlEvent.getLocation();
				int evType = xmlEvent.getEventType();

				String eventNameOut = (evType <= EVENT_NAMES.length ? EVENT_NAMES[evType] + " ": "");
				String locStr = "line: " + loc.getLineNumber() + ", col: " + loc.getColumnNumber() + ", char: " + loc.getCharacterOffset();
				if(xmlEvent instanceof StartElement) {
					StartElement stElem = (StartElement) xmlEvent;
					System.out.print(eventNameOut + stElem.getName().getLocalPart());
					System.out.println(" -- " + elementLevel + " -- " + sr.getOffset() + " / " + sr.getMarkOffset() + ", " + locStr);
					elementLevel += 1;
					firstElementSeen = true;
				} else if(xmlEvent instanceof EndElement) {
					EndElement endElem = (EndElement) xmlEvent;
					System.out.print(eventNameOut + endElem.getName().getLocalPart());
					System.out.println(" -- " + elementLevel + " -- " + sr.getOffset() + " / " + sr.getMarkOffset() + ", " + locStr);
					elementLevel -= 1;
				} else if(xmlEvent instanceof StartDocument) {
					System.out.println(eventNameOut + ", " + locStr);
				} else if(xmlEvent instanceof EndDocument) {
					System.out.println(eventNameOut + ", " + locStr);
				} 

				if(firstElementSeen && elementLevel <= 0) {
					System.out.println("=== END DOCUMENT ===");
					int charOffset = loc.getCharacterOffset();
					System.out.println("=== char offset: " + charOffset + ", stream offset: " 
							+ sr.getOffset() + ", stream mark: " + sr.getMarkOffset() + " ===");
					sr.resetTo(charOffset);
					
					int c = 0;
					bis.mark(10);
					boolean eof = true;
					while((c = bis.read()) >= 0) {
						System.out.print((char) c);
						if(c == '<') {
							bis.reset();
							eof = false;
							sr = new ReaderWrapper(bis);
							eventReader = inputFact.createXMLEventReader(sr);
							elementLevel = 0;
							firstElementSeen = false;
							break;
						}
						bis.mark(1);
					}
					System.out.println();
					if(eof)
						break;
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}