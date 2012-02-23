package asthelpers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PushbackReader;
import java.util.ArrayList;
import java.util.LinkedList;

import lexer.Lexer;
import lexer.LexerException;

import org.eclipse.jface.text.IDocument;

import parser.Parser;
import parser.ParserException;

import editor.DocumentBufferStream;

import node.AModule;
import node.AStdClassdef;
import node.AStdImport;
import node.PClassdef;
import node.PImport;
import node.Start;

public class AstParserHelper {
	
	public Start getAstForDocument(IDocument document) throws ParserException, LexerException, IOException {
		DocumentBufferStream dbs = new DocumentBufferStream();

		dbs.setDoc(document);
		
		Parser pp = new Parser(new Lexer(new PushbackReader(dbs)));
		
		return pp.parse();
	}

	public Start getAstForFile(File file) throws FileNotFoundException, IOException, ParserException, LexerException {
		Parser parser = null;
		
		parser = new Parser(new Lexer(new PushbackReader(
				new BufferedReader(new FileReader(file)))));
		
		return parser.parse();
	}
	
	public ArrayList getPropsOfStdClassNode(AStdClassdef classNode) {
		// TODO: Implement Method
		return null;
	}

	public ArrayList getTopLevelPropsOfModule(AModule module) {
		// TODO: Implement Method
		return null;
	}

	public AModule getModuleOfAST(Start startNode){
		if(startNode.getPModule() instanceof AModule){
			return (AModule) startNode.getPModule();
		}
		return null;
	}
	
	public ArrayList<AStdClassdef> getClassesOfModule(AModule module) {
		ArrayList<AStdClassdef> defs = new ArrayList<AStdClassdef>();

		for (PClassdef pclass : module.getClassdefs()) {
			if (pclass instanceof AStdClassdef) {
				AStdClassdef amc = (AStdClassdef) pclass;
				defs.add(amc);
			}
		}
		return defs;
	}

	public ArrayList<AStdImport> getImports(AModule module) {

		ArrayList<AStdImport> results = new ArrayList<AStdImport>();
		LinkedList<PImport> pi = module.getImports();

		for (PImport pim : pi) {
			if (pim instanceof AStdImport) {
				results.add((AStdImport) pim);
			}
		}

		return results;
	}

}
