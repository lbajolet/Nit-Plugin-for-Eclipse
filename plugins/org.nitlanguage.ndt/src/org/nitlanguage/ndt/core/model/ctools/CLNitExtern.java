package org.nitlanguage.ndt.core.model.ctools;
import org.nitlanguage.gen.simplec.node.*;

/**
 * Token representing nit extern opening character `{
 * Neither used nor returned by the lexer/scanner but
 * manually added to the tokens queue returned by the CLexerHelper
 * if the original string was a nit extern code fragment.
 * @author nathan.heu
 */
public class CLNitExtern extends TComment{

	public CLNitExtern() {
		super(CLexerHelper.NITEXTERN_OPEN);
	}
}
