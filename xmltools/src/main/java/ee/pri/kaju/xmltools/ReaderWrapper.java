package ee.pri.kaju.xmltools;

import java.io.IOException;
import java.io.Reader;

import org.apache.commons.io.input.ProxyReader;

public class ReaderWrapper extends ProxyReader {

	/** Cursor position of underlying stream. */
	long offset = 0;
	
	/** Mark position of underying stream. */
	long markOffset = 0;

	/** Start of substream, position in original stream. */
	long substreamOffset = 0;
	
	public ReaderWrapper(Reader proxy) {
		super(proxy);
	}

	@Override
	protected void afterRead(int n) throws IOException {
		offset += n;
	}

	@Override
	protected void beforeRead(int n) throws IOException {
		markOffset = offset;
		if(in.markSupported())
			in.mark(n);
	}
	
	public boolean resetTo(long newOffset) throws IOException {
		long newSubstreamOffset = substreamOffset + newOffset;
		if(newSubstreamOffset > offset) return false;
		if(newSubstreamOffset <= markOffset) {
			in.reset();
			return false;
		}
		
		long shift = newSubstreamOffset - markOffset;
		in.reset();
		in.skip(shift);
		offset = newSubstreamOffset;
		markOffset = offset;
		substreamOffset = newSubstreamOffset;
		if(in.markSupported())
			in.mark(10240);
		
		return true;
	}

	/**
	 * Resets to offset and to next character c.
	 * @param newOffset
	 * @param c
	 * @throws IOException
	 */
	public boolean resetTo(long newOffset, char c) throws IOException {
		if(!resetTo(newOffset))
			return false;

		in.mark(10);
		boolean eof = true;
		int c0 = 0;
		while((c0 = in.read()) >= 0) {
			if(c0 == c) {
				in.reset();
				eof = false;
				break;
			}
			in.mark(1);
			substreamOffset += 1;
			offset += 1;
			markOffset += 1;
		}
		return !eof;
	}
	
	public long getOffset() {
		return offset;
	}

	public long getMarkOffset() {
		return markOffset;
	}

	public long getSubstreamOffset() {
		return substreamOffset;
	}

}
