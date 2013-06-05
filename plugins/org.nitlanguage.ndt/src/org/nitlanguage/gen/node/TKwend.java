/* This file was generated by SableCC (http://www.sablecc.org/). */

package org.nitlanguage.gen.node;

import org.nitlanguage.gen.analysis.Analysis;

@SuppressWarnings("nls")
public final class TKwend extends Token
{
    public TKwend()
    {
        super.setText("end");
    }

    public TKwend(int line, int pos)
    {
        super.setText("end");
        setLine(line);
        setPos(pos);
    }

    @Override
    public Object clone()
    {
      return new TKwend(getLine(), getPos());
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTKwend(this);
    }

    @Override
    public void setText(@SuppressWarnings("unused") String text)
    {
        throw new RuntimeException("Cannot change TKwend text.");
    }
}
