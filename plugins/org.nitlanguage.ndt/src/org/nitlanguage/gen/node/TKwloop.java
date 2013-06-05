/* This file was generated by SableCC (http://www.sablecc.org/). */

package org.nitlanguage.gen.node;

import org.nitlanguage.gen.analysis.Analysis;

@SuppressWarnings("nls")
public final class TKwloop extends Token
{
    public TKwloop()
    {
        super.setText("loop");
    }

    public TKwloop(int line, int pos)
    {
        super.setText("loop");
        setLine(line);
        setPos(pos);
    }

    @Override
    public Object clone()
    {
      return new TKwloop(getLine(), getPos());
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTKwloop(this);
    }

    @Override
    public void setText(@SuppressWarnings("unused") String text)
    {
        throw new RuntimeException("Cannot change TKwloop text.");
    }
}
