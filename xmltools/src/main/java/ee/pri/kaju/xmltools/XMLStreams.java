package ee.pri.kaju.xmltools;

import java.io.InputStream;
import java.util.Iterator;

public class XMLStreams implements Iterable<InputStream> {

	private InputStream inputStream = null;
	
	public XMLStreams(InputStream inputStream) {
		this.inputStream = inputStream;
	}
	
	@Override
	public Iterator<InputStream> iterator() {
		return new NonXMLStreamIterator(inputStream);
	}

}
