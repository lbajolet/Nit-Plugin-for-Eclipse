package org.nitlanguage.ndt.ui.docmodel;

/**
 * Declaration aimed to be stacked on the folding element stack 
 * @author nathan.heu
 */
public class Declaration{
	private int beginOffset;
	private DeclarationType type;
	
	public Declaration(int beginOffset, DeclarationType type){
		this.beginOffset = beginOffset;
		this.type = type;
	}
	
	public int getBeginOffset(){
		return beginOffset; 
	}
	
	public DeclarationType getType(){
		return type;
	}
	
    /**
     * Evaluate the kind of the declaration in the line
     * @param line
     * @param next_line
     * @return
     */
    public static DeclarationType evaluateDeclaration(String line, String next_line){
    	line = removeModificatorKeyword(cleanLine(line));
    	 
    	if(line.trim().isEmpty()){
    		return DeclarationType.EMPTY_LINE;
    	} else if(line.startsWith("#")){
    		return DeclarationType.COMMENT;
    	} else if(line.startsWith("module ")){
    		return DeclarationType.MODULE;
    	} else if(line.startsWith("import ")){
    		return DeclarationType.IMPORT;
    	} else if(line.startsWith("class ") 
    				|| line.startsWith("interface ")){
    		return DeclarationType.CLASS;
    	} else if(line.startsWith("fun ") 
    				|| line.startsWith("init ")
    				|| line.startsWith("init(")){
    		String nextLine = cleanLine(next_line);
    		if(line.endsWith(" end") || (!line.endsWith(" do") && !nextLine.equals("do"))){
    			return DeclarationType.INLINE_FUN;
    		}
    		if(nextLine.equals("do")) return DeclarationType.MULTILINE_FUN_CLEAN;
    		else return DeclarationType.MULTILINE_FUN;
    	} else if(line.startsWith("do ")
					|| line.startsWith("while ")
    				|| line.startsWith("loop ") 
    				|| line.startsWith("assert ")
    				|| line.startsWith("for ")){
    		return DeclarationType.STRUCT;
    	} else if(line.startsWith("if ")){
    		if(line.endsWith("then")){
        		return DeclarationType.STRUCT;
    		}
    		else return DeclarationType.INLINE_IF;
    	} else if(line.startsWith("else ")
    			|| line.startsWith("else if ")){
    		return DeclarationType.ELSE;
    	} else if(line.equals("end")){
    		return DeclarationType.END;
    	}
    	return DeclarationType.OTHER;
    }
    
    /**
     * Remove visibility (public, protected, private) and refinment (redef) keywords
     * @param line
     * @return
     */
    private static String removeModificatorKeyword(String line){
    	if(line.startsWith("redef ")){
    		return line.substring(5, line.length()-1).trim();
    	} else if(line.startsWith("public ")){
    		return line.substring(6, line.length()-1).trim();
    	} else if(line.startsWith("protected ")){
    		return line.substring(9, line.length()-1).trim();
    	} else if(line.startsWith("private ")){
    		return line.substring(7, line.length()-1).trim();
    	} else {
    		return line;
    	}
    }
    
    /**
     * Remove all formatting characters from the beginninf of line
     * @param line
     * @return
     */
    private static String cleanLine(String line){
    	char sep_line[] = line.toCharArray();
    	int nbBlankChars = 0;
    	for(char c : sep_line)
    	{
    		if(c == '\t' || c == '\n' || c == ' ') nbBlankChars++;
    		else break;
    	}
    	if (nbBlankChars > 0) return line.substring(nbBlankChars);
    	return line;
    }
}