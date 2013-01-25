package org.nitlanguage.ndt.ui.editor;

import java.io.IOException;
import java.io.Reader;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;

/**
 * Adapts an IDocument used by Eclipse to the NitLexer, which uses a PushbackReader with a Reader as base to read characters from
 * 
 */
public class DocumentBufferStream extends Reader {
	
	/**	Start range of the parsing (useful for the DamageRepairer and Presenter of the Eclipse Editor) */
	protected int fStartRange;
	/**	End range of the parsing (useful for the DamageRepairer and Presenter of the Eclipse Editor) */
	protected int fEndRange;
	/**	The current offset in document */
	protected int currOffset;
	
	/**	The current cached document */
	protected IDocument doc;
	
	/**
	 * Sets the region of the document to scan
	 * 
	 * @param offset : Offset to parse from
	 * @param length : Total length of the parsed region
	 */
	public void setRange(int offset, int length) {
		this.fStartRange = offset;
		this.fEndRange = offset + length;
		this.currOffset = offset;
	}
	
	/**
	 * Sets the document to parse data from
	 * 
	 * @param doc The IDocument object to scan
	 */
	public void setDoc(IDocument doc){
		this.doc = doc;
		this.setRange(0,doc.getLength());
	}
	
	/**
	 * Sets the start rang of the stream at @param startOffset
	 * 
	 * @param startOffset The offset to start parsing from
	 */
	public void setStartRange(int startOffset){
		this.fStartRange = startOffset;
	}
	
	/**
	 * Sets the end range of the stream to @param endOffset
	 * 
	 * @param endOffset The offset to stop parsing at
	 */
	public void setEndRange(int endOffset){
		this.fEndRange = endOffset;
	}
	
	/**
	 * Default constructor for the DocumentBufferStream
	 * After instantiating, you'll need to set the IDocument to parse from and the fStartRange/fEndRange for the
	 * Stream to work
	 */
	public DocumentBufferStream(){
		doc = null;
		fStartRange = 0;
		fEndRange = 0;
	}
	
	@Override
	public void close() throws IOException {
		
	}

	@Override
	public boolean ready(){
		return true;
	}
	
	@Override
	public int read(){
		int toReturn = -1;
		if(this.currOffset < fEndRange){
			try {
				toReturn = doc.getChar(currOffset);
			} catch (BadLocationException e) {
				return -1;
			}
		}else{
			return -1;
		}
		this.currOffset++;
		return toReturn;
	}
	
	@Override
	public int read(char[] cbuf) throws IOException{
		for(int i = 0; i < cbuf.length; i++){
			try {
				if(this.currOffset < fEndRange){
					cbuf[i] = doc.getChar(i);
				}else{
					throw new BadLocationException();
				}
			} catch (BadLocationException e) {
				if(i == 0){
					return -1;
				}else{
					return i;
				}
			}
			currOffset++;
		}
		return cbuf.length;
	}
	
	@Override
	public int read(char[] cbuf, int off, int len) throws IOException {
		if(this.doc == null){
			throw new IOException("Trying to read un unset document");
		}else{
			for(int i = 0; i < cbuf.length; i++){
				try {
					if(this.currOffset < fEndRange){
						cbuf[i] = this.doc.getChar(off+i);
					}else{
						throw new BadLocationException();
					}
				} catch (BadLocationException e) {
					if(i == 0){
						return -1;
					}else{
						return i;
					}
				}
				this.currOffset++;
			}
			return len;
		}
	}
	
	@Override
	public void reset(){
		this.currOffset = 0;
		this.fStartRange = 0;
		this.fEndRange = this.doc.getLength()-1;
	}
	
	@Override
	public long skip(long n){
		if(currOffset + n > doc.getLength()){
			currOffset = doc.getLength()-1;
			return doc.getLength() - currOffset;
		}else{
			currOffset += n;
			return n;
		}
	}

}
