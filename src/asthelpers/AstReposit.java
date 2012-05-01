package asthelpers;

import java.util.HashMap;

import node.Start;

/**
 * Singleton reposit for Abstract Syntax Trees of modules
 */
public class AstReposit {

	private HashMap<String, Start> asts;

	public AstReposit() {
		this.asts = new HashMap<String, Start>();
	}

	/**
	 * Adds the AST start node in the reposit, replaces the old one if the AST
	 * already exists
	 * 
	 * @param nameOfAST
	 * @param startNodeOfAST
	 */
	public void addOrReplaceAST(String nameOfAST, Start startNodeOfAST) {
		if (asts.containsKey(nameOfAST)) {
			asts.remove(nameOfAST);
		}
		asts.put(nameOfAST, startNodeOfAST);
	}

	/**
	 * Gets the AST from the reposit
	 * 
	 * @param nameOfAST
	 * @return The Start node of the AST, null if it does not exist in the
	 *         reposit
	 */
	public Start getAST(String nameOfAST) {
		if (asts.containsKey(nameOfAST)) {
			return asts.get(nameOfAST);
		}
		return null;
	}

}
