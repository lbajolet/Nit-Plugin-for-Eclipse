package asthelpers;

import java.util.HashMap;

/**
 * Singleton reposit for Abstract Syntax Trees of modules
 */
public class AstReposit {
	
	private static AstReposit instance;
	
	private HashMap<String, AstFile> asts = new HashMap<String, AstFile>();
	
	private AstReposit(){
		
	}
	
	public static AstReposit getInstance(){
		if(instance == null){
			AstReposit.instance = new AstReposit();
		}
		return AstReposit.instance;
	}
	
}
