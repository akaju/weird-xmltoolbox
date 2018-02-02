package ee.pri.kaju.xmltools;

import java.io.IOException;
import java.io.Reader;

import org.apache.commons.io.input.ProxyReader;

public class ReaderWrapper extends ProxyReader {

	long offset = 0;
	
	long markOffset = 0;

	public ReaderWrapper(Reader proxy) {
		super(proxy);
	}

	@Override
	protected void afterRead(int n) throws IOException {
		offset += n;
	}

	@Override
	protected void beforeRead(int n) throws IOException {
		//System.err.println("n: " + n + ", offs: " + offset + ", mark: " + markOffset);
		markOffset = offset;
		if(in.markSupported())
			in.mark(n);
	}
	
	public void resetTo(long newOffset) throws IOException {
		if(newOffset <= markOffset) {
			in.reset();
			return;
		}
		
		if(newOffset > offset) return;
		
		long shift = newOffset - markOffset;
		in.reset();
		in.skip(shift);
	}
	
	public long getOffset() {
		return offset;
	}

	public long getMarkOffset() {
		return markOffset;
	}

}
