package org.nitlanguage.ndt.core.model.ctools;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.Queue;
import org.nitlanguage.gen.simplec.lexer.Lexer;
import org.nitlanguage.gen.simplec.lexer.LexerException;
import org.nitlanguage.gen.simplec.node.EOF;
import org.nitlanguage.gen.simplec.node.Token;

/**
 * Used to scan a C code and get the corresponding tokens
 * @author nathan.heu
 */
public class CLexerHelper {

	private Lexer lex;
	private boolean isNitExternC;
	public static final String NITEXTERN_OPEN = "`{";
	public static final String NITEXTERN_CLOSE = "`}";
	
	/**
	 * 
	 * @param cCode : C code to scans
	 */
	public CLexerHelper(String cCode){
		//removes nit extern syntax if any and raises the flag if it is the case
		cCode = removeNitExternSyntax(cCode);
		//scans the strings looking for C tokens.
		lex = new Lexer(new PushbackReader(new StringReader(cCode)));
	}
	
	/**
	 * 
	 * @param cCode
	 * @return
	 */
	public String removeNitExternSyntax(String cCode){
		if(cCode.startsWith(NITEXTERN_OPEN)) isNitExternC = true;
		return cCode.replace(NITEXTERN_OPEN, "").replace(NITEXTERN_CLOSE, "");
	}
	
	/**
	 * 
	 * @return
	 */
	public Queue<Token> getASTTokens(){
		Queue<Token> astTokens = null;
		if(lex != null){
			astTokens = new LinkedList<Token>();
			//step 1 - if c code inside nit then add `{ token
			if(isNitExternC) astTokens.add(new CLNitExtern());			
			//step 2 - add parsed tokens
			try {
				while(lex.peek() != null && !(lex.peek() instanceof EOF)){
					astTokens.add(lex.next());
				}
			} catch (LexerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//step 3 - if c code inside nit then add `} token
			if(isNitExternC) astTokens.add(new CRNitExtern());
		}
		return astTokens;
	}
}
