package org.nitlanguage.ndt.core.model.asthelpers;

import java.util.HashMap;

import org.nitlanguage.gen.node.Start;

/**
 * Singleton deposit for Abstract Syntax Trees of modules
 * @author lucas.bajolet
 */
public class AstReposit {

	/**
	 * Collection of all the ASTs of a Project
	 * To get an AST, the key is : "filename.nit"
	 */
	private HashMap<String, Start> asts;

	/**
	 * Default constructor, creates a new HashMap
	 */
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
