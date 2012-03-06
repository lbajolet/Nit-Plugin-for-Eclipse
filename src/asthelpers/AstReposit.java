package asthelpers;

import java.util.HashMap;

import node.Start;

/**
 * Singleton reposit for Abstract Syntax Trees of modules
 */
public class AstReposit {
	
	private static AstReposit instance;
	
	private HashMap<String, Start> asts = new HashMap<String, Start>();
	
	private AstReposit(){
		
	}
	
	public static AstReposit getInstance(){
		if(instance == null){
			AstReposit.instance = new AstReposit();
		}
		return AstReposit.instance;
	}
	
	public void addOrReplaceAST(String nameOfAST, Start startNodeOfAST){
		if(asts.containsKey(nameOfAST)){
			asts.remove(nameOfAST);
		}
		asts.put(nameOfAST, startNodeOfAST);
	}
	
	public Start getAST(String nameOfAST){
		if(asts.containsKey(nameOfAST)){
			return asts.get(nameOfAST);
		}
		return null;
	}
	
}
