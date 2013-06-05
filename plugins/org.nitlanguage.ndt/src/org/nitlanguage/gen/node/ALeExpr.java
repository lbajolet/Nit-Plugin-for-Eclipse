/* This file was generated by SableCC (http://www.sablecc.org/). */

package org.nitlanguage.gen.node;

import org.nitlanguage.gen.analysis.Analysis;

@SuppressWarnings("nls")
public final class ALeExpr extends PExpr
{
    private PExpr _expr_;
    private PExpr _expr2_;

    public ALeExpr()
    {
        // Constructor
    }

    public ALeExpr(
        @SuppressWarnings("hiding") PExpr _expr_,
        @SuppressWarnings("hiding") PExpr _expr2_)
    {
        // Constructor
        setExpr(_expr_);

        setExpr2(_expr2_);

    }

    @Override
    public Object clone()
    {
        return new ALeExpr(
            cloneNode(this._expr_),
            cloneNode(this._expr2_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseALeExpr(this);
    }

    public PExpr getExpr()
    {
        return this._expr_;
    }

    public void setExpr(PExpr node)
    {
        if(this._expr_ != null)
        {
            this._expr_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._expr_ = node;
    }

    public PExpr getExpr2()
    {
        return this._expr2_;
    }

    public void setExpr2(PExpr node)
    {
        if(this._expr2_ != null)
        {
            this._expr2_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._expr2_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._expr_)
            + toString(this._expr2_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._expr_ == child)
        {
            this._expr_ = null;
            return;
        }

        if(this._expr2_ == child)
        {
            this._expr2_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._expr_ == oldChild)
        {
            setExpr((PExpr) newChild);
            return;
        }

        if(this._expr2_ == oldChild)
        {
            setExpr2((PExpr) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
