package org.nitlanguage.ndt.ui.docmodel;

/**
 * Enumeration of available recognized declaration types
 * @author nathan.heu
 */
public enum DeclarationType {
	MODULE, 
	IMPORT,
	CLASS, 
	INLINE_FUN, 
	MULTILINE_FUN, 
	MULTILINE_FUN_CLEAN,
	END, 
	INLINE_IF, 
	STRUCT, 
	ELSE, 
	COMMENT, 
	EMPTY_LINE, 
	OTHER
}