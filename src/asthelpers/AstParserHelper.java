package asthelpers;

import java.io.IOException;
import java.io.PushbackReader;
import java.util.ArrayList;
import java.util.LinkedList;

import lexer.Lexer;
import lexer.LexerException;
import node.AAttrPropdef;
import node.AConcreteInitPropdef;
import node.AConcreteMethPropdef;
import node.ADeferredMethPropdef;
import node.AModule;
import node.AModuledecl;
import node.AStdClassdef;
import node.AStdImport;
import node.PClassdef;
import node.PImport;
import node.PPropdef;
import node.Start;
import node.TBlank;
import node.TEol;
import node.TId;
import node.TKwmodule;
import node.Token;

import org.eclipse.jface.text.IDocument;

import parser.Parser;
import parser.ParserException;
import editor.DocumentBufferStream;

/**
 * @author R4PaSs
 * 
 */
public class AstParserHelper {

	public Start getAstForDocument(IDocument document) {
		DocumentBufferStream dbs = new DocumentBufferStream();

		dbs.setDoc(document);

		PushbackReader pbr = new PushbackReader(dbs);

		Parser pp = new Parser(new Lexer(pbr));

		Start st = null;
		try {
			st = pp.parse();
		} catch (ParserException e) {
		} catch (LexerException e) {
		} catch (IOException e) {
		}

		if (st == null) {
			String moduleName = getModuleNameInFile(dbs);
			if (moduleName != "") {
				return AstReposit.getInstance().getAST(moduleName.trim());
			} else {
				return null;
			}
		} else {
			AModuledecl mod = (AModuledecl) ((AModule) st.getPModule())
					.getModuledecl();
			String mdName = mod.getName().toString().trim();
			AstReposit.getInstance().addOrReplaceAST(mdName, st);
			return st;
		}

	}

	private String getModuleNameInFile(DocumentBufferStream dbs) {

		dbs.reset();

		Lexer lx = new Lexer(new PushbackReader(dbs));

		Token tk = null;
		do {
			try {
				tk = lx.next();
			} catch (LexerException e) {
			} catch (IOException e) {
			}
		} while (!(tk instanceof TKwmodule));

		do {
			try {
				tk = lx.next();
			} catch (LexerException e) {
				return "";
			} catch (IOException e) {
				return "";
			}
		} while (tk instanceof TBlank || tk instanceof TEol);

		if (tk instanceof TId) {
			return tk.toString();
		} else {
			return "";
		}
	}

	public AModule getModuleOfAST(Start startNode) {

		if (startNode.getPModule() instanceof AModule) {
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

	public LinkedList<PPropdef> getPropsOfClass(AStdClassdef className) {

		LinkedList<PPropdef> props = className.getPropdefs();

		return props;
	}

	public ArrayList<AConcreteMethPropdef> getConcreteMethsInPropList(
			LinkedList<PPropdef> props) {
		ArrayList<AConcreteMethPropdef> methods = new ArrayList<AConcreteMethPropdef>();

		for (PPropdef prp : props) {
			if (prp instanceof AConcreteMethPropdef) {
				methods.add((AConcreteMethPropdef) prp);
			}
		}

		return methods;
	}

	public ArrayList<ADeferredMethPropdef> getDeferredMethsInPropList(
			LinkedList<PPropdef> props) {
		ArrayList<ADeferredMethPropdef> methods = new ArrayList<ADeferredMethPropdef>();

		for (PPropdef prp : props) {
			if (prp instanceof ADeferredMethPropdef) {
				methods.add((ADeferredMethPropdef) prp);
			}
		}

		return methods;
	}

	public ArrayList<AAttrPropdef> getNonMethPropsInPropList(
			LinkedList<PPropdef> props) {
		ArrayList<AAttrPropdef> nonMethProps = new ArrayList<AAttrPropdef>();

		for (PPropdef prp : props) {
			if (prp instanceof AAttrPropdef) {
				nonMethProps.add((AAttrPropdef) prp);
			}
		}

		return nonMethProps;
	}

	public ArrayList<AConcreteInitPropdef> getConstructMethsInPropList(
			LinkedList<PPropdef> props) {
		ArrayList<AConcreteInitPropdef> constructs = new ArrayList<AConcreteInitPropdef>();

		for (PPropdef prp : props) {
			if (prp instanceof AConcreteInitPropdef) {
				constructs.add((AConcreteInitPropdef) prp);
			}
		}

		return constructs;
	}
}
