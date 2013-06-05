/* This file was generated by SableCC (http://www.sablecc.org/). */

package org.nitlanguage.gen.node;

import org.nitlanguage.gen.analysis.Analysis;

@SuppressWarnings("nls")
public final class AIfExpr extends PExpr
{
    private TKwif _kwif_;
    private PExpr _expr_;
    private PExpr _then_;
    private PExpr _else_;

    public AIfExpr()
    {
        // Constructor
    }

    public AIfExpr(
        @SuppressWarnings("hiding") TKwif _kwif_,
        @SuppressWarnings("hiding") PExpr _expr_,
        @SuppressWarnings("hiding") PExpr _then_,
        @SuppressWarnings("hiding") PExpr _else_)
    {
        // Constructor
        setKwif(_kwif_);

        setExpr(_expr_);

        setThen(_then_);

        setElse(_else_);

    }

    @Override
    public Object clone()
    {
        return new AIfExpr(
            cloneNode(this._kwif_),
            cloneNode(this._expr_),
            cloneNode(this._then_),
            cloneNode(this._else_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAIfExpr(this);
    }

    public TKwif getKwif()
    {
        return this._kwif_;
    }

    public void setKwif(TKwif node)
    {
        if(this._kwif_ != null)
        {
            this._kwif_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._kwif_ = node;
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

    public PExpr getThen()
    {
        return this._then_;
    }

    public void setThen(PExpr node)
    {
        if(this._then_ != null)
        {
            this._then_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._then_ = node;
    }

    public PExpr getElse()
    {
        return this._else_;
    }

    public void setElse(PExpr node)
    {
        if(this._else_ != null)
        {
            this._else_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._else_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._kwif_)
            + toString(this._expr_)
            + toString(this._then_)
            + toString(this._else_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._kwif_ == child)
        {
            this._kwif_ = null;
            return;
        }

        if(this._expr_ == child)
        {
            this._expr_ = null;
            return;
        }

        if(this._then_ == child)
        {
            this._then_ = null;
            return;
        }

        if(this._else_ == child)
        {
            this._else_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._kwif_ == oldChild)
        {
            setKwif((TKwif) newChild);
            return;
        }

        if(this._expr_ == oldChild)
        {
            setExpr((PExpr) newChild);
            return;
        }

        if(this._then_ == oldChild)
        {
            setThen((PExpr) newChild);
            return;
        }

        if(this._else_ == oldChild)
        {
            setElse((PExpr) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
