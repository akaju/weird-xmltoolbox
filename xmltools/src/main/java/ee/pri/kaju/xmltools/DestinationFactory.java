package ee.pri.kaju.xmltools;

import java.io.OutputStream;

public interface DestinationFactory {

	/** Returns output stream for next document. */
	public OutputStream getOutputStream();
	
}
