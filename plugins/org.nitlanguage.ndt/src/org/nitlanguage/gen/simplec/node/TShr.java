/* This file was generated by SableCC (http://www.sablecc.org/). */

package org.nitlanguage.gen.simplec.node;

import org.nitlanguage.gen.simplec.analysis.*;

public final class TShr extends Token
{
    public TShr()
    {
        super.setText(">>");
    }

    public TShr(int line, int pos)
    {
        super.setText(">>");
        setLine(line);
        setPos(pos);
    }

    public Object clone()
    {
      return new TShr(getLine(), getPos());
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTShr(this);
    }

    public void setText(String text)
    {
        throw new RuntimeException("Cannot change TShr text.");
    }
}
