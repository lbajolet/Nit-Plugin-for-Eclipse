/* This file was generated by SableCC (http://www.sablecc.org/). */

package node;

import analysis.*;

@SuppressWarnings("nls")
public final class TKwreturn extends Token
{
    public TKwreturn()
    {
        super.setText("return");
    }

    public TKwreturn(int line, int pos)
    {
        super.setText("return");
        setLine(line);
        setPos(pos);
    }

    @Override
    public Object clone()
    {
      return new TKwreturn(getLine(), getPos());
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTKwreturn(this);
    }

    @Override
    public void setText(@SuppressWarnings("unused") String text)
    {
        throw new RuntimeException("Cannot change TKwreturn text.");
    }
}