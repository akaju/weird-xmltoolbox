package ee.pri.kaju.xmltools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.xml.stream.Location;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndDocument;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.codehaus.stax2.XMLInputFactory2;
import org.codehaus.stax2.XMLOutputFactory2;
import org.codehaus.stax2.evt.XMLEventFactory2;

/**
 * Extracts xml documents from input stream, where xml documents are appended.
 * 
 * NB! It is important to use org.codehaus.stax2 implementation, because Oracle jdk8 implementation
 * is broken, gives wrong character location.
 * 
 * @author Andres Kaju
 *
 */
public class NonXMLStreamSplitter {
	
	InputStream inputStream = null;
	
	DestinationFactory destinationFactory = null;

	private XMLInputFactory inputFactory = XMLInputFactory2.newFactory();

	private XMLOutputFactory outputFactory = XMLOutputFactory2.newFactory();

	private XMLEventFactory eventFactory = XMLEventFactory2.newFactory();

	private BufferedReader bufferedReader = null;
	
	private ReaderWrapper reader = null;
	
	private int bufferLength = 10240;
	
	boolean eof = false;
	
	private XMLEventListener eventListener = null;
	
	public void extractAllDocuments() throws XMLStreamException, IOException {
		while(!eof) {
			extractNextDocument();
		}
	}
	
	public void extractNextDocument() throws XMLStreamException, IOException {
		int elementLevel = 0;
		boolean firstElementSeen = false;
		boolean xmlDocRead = false;
		XMLEventWriter eventWriter = null;
		Writer writer = null;
		
		if(bufferedReader == null) {
			bufferedReader = new BufferedReader(new InputStreamReader(inputStream), bufferLength);
			//System.out.println("new buffered reader created.");
		}

		if(reader == null) {
			reader = new ReaderWrapper(bufferedReader);
			//System.out.println("new reader wrapper created.");
		}
		
		/*
		bufferedReader.mark(1000);
		String line = bufferedReader.readLine();
		if(line != null)
			System.out.println("next line: " + line);
		bufferedReader.reset();
		*/
		
		XMLEventReader eventReader = inputFactory.createXMLEventReader(reader);
		while(!xmlDocRead && eventReader.hasNext()) {
			XMLEvent xmlEvent = eventReader.nextEvent();
			Location loc = xmlEvent.getLocation();

			if(eventListener != null)
				eventListener.event(xmlEvent);
			
			int elemTagLen = 3;
			if(xmlEvent instanceof StartElement) {
				elementLevel += 1;
				firstElementSeen = true;
			} else if(xmlEvent instanceof EndElement) {
				elementLevel -= 1;
				elemTagLen += ((EndElement) xmlEvent).getName().getLocalPart().length();
			} else if(xmlEvent instanceof StartDocument) {
				if(destinationFactory != null) {
					writer = new OutputStreamWriter(destinationFactory.getOutputStream());
					if(writer != null)
						eventWriter = outputFactory.createXMLEventWriter(writer);
				}
			} else if(xmlEvent instanceof EndDocument) {
				
			} 
			
			if(eventWriter != null)
				eventWriter.add(xmlEvent);
			
			if(firstElementSeen && elementLevel <= 0) {
				xmlDocRead = true;
				if(eventWriter != null) {
					XMLEvent lastEvent = eventFactory.createEndDocument();
					if(eventListener != null)
						eventListener.event(lastEvent);
					eventWriter.add(lastEvent);
					eventWriter.flush();
					eventWriter.close();
					if(writer != null) {
						writer.flush();
						writer = null;
					}
					eventWriter = null;
				}
				int charOffset = loc.getCharacterOffset();
				boolean resetResult = reader.resetTo(charOffset + elemTagLen, '<');

				eof = !resetResult;
				if(eof)
					break;
			}
		}
	}
	
	/**
	 * Returns true, if input stream has not been read till end.
	 * @return
	 */
	public boolean hasMoreElements() {
		return !eof;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public DestinationFactory getDestinationFactory() {
		return destinationFactory;
	}

	public void setDestinationFactory(DestinationFactory destinationFactory) {
		this.destinationFactory = destinationFactory;
	}

	public XMLInputFactory getInputFactory() {
		return inputFactory;
	}

	public void setInputFactory(XMLInputFactory inputFactory) {
		this.inputFactory = inputFactory;
	}

	public XMLOutputFactory getOutputFactory() {
		return outputFactory;
	}

	public void setOutputFactory(XMLOutputFactory outputFactory) {
		this.outputFactory = outputFactory;
	}

	public int getBufferLength() {
		return bufferLength;
	}

	public void setBufferLength(int bufferLength) {
		this.bufferLength = bufferLength;
	}

	public boolean isEof() {
		return eof;
	}

	public XMLEventListener getEventListener() {
		return eventListener;
	}

	public void setEventListener(XMLEventListener eventListener) {
		this.eventListener = eventListener;
	}
}
