/* This file was generated by SableCC (http://www.sablecc.org/). */

package org.nitlanguage.gen.node;

import org.nitlanguage.gen.analysis.Analysis;

@SuppressWarnings("nls")
public final class AOnceExpr extends PExpr
{
    private TKwonce _kwonce_;
    private PExpr _expr_;

    public AOnceExpr()
    {
        // Constructor
    }

    public AOnceExpr(
        @SuppressWarnings("hiding") TKwonce _kwonce_,
        @SuppressWarnings("hiding") PExpr _expr_)
    {
        // Constructor
        setKwonce(_kwonce_);

        setExpr(_expr_);

    }

    @Override
    public Object clone()
    {
        return new AOnceExpr(
            cloneNode(this._kwonce_),
            cloneNode(this._expr_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAOnceExpr(this);
    }

    public TKwonce getKwonce()
    {
        return this._kwonce_;
    }

    public void setKwonce(TKwonce node)
    {
        if(this._kwonce_ != null)
        {
            this._kwonce_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._kwonce_ = node;
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

    @Override
    public String toString()
    {
        return ""
            + toString(this._kwonce_)
            + toString(this._expr_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._kwonce_ == child)
        {
            this._kwonce_ = null;
            return;
        }

        if(this._expr_ == child)
        {
            this._expr_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._kwonce_ == oldChild)
        {
            setKwonce((TKwonce) newChild);
            return;
        }

        if(this._expr_ == oldChild)
        {
            setExpr((PExpr) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
