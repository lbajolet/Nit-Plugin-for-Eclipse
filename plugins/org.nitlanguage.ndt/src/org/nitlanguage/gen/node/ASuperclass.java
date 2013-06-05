/* This file was generated by SableCC (http://www.sablecc.org/). */

package org.nitlanguage.gen.node;

import org.nitlanguage.gen.analysis.Analysis;

@SuppressWarnings("nls")
public final class ASuperclass extends PSuperclass
{
    private TKwsuper _kwsuper_;
    private PType _type_;

    public ASuperclass()
    {
        // Constructor
    }

    public ASuperclass(
        @SuppressWarnings("hiding") TKwsuper _kwsuper_,
        @SuppressWarnings("hiding") PType _type_)
    {
        // Constructor
        setKwsuper(_kwsuper_);

        setType(_type_);

    }

    @Override
    public Object clone()
    {
        return new ASuperclass(
            cloneNode(this._kwsuper_),
            cloneNode(this._type_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseASuperclass(this);
    }

    public TKwsuper getKwsuper()
    {
        return this._kwsuper_;
    }

    public void setKwsuper(TKwsuper node)
    {
        if(this._kwsuper_ != null)
        {
            this._kwsuper_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._kwsuper_ = node;
    }

    public PType getType()
    {
        return this._type_;
    }

    public void setType(PType node)
    {
        if(this._type_ != null)
        {
            this._type_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._type_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._kwsuper_)
            + toString(this._type_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._kwsuper_ == child)
        {
            this._kwsuper_ = null;
            return;
        }

        if(this._type_ == child)
        {
            this._type_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._kwsuper_ == oldChild)
        {
            setKwsuper((TKwsuper) newChild);
            return;
        }

        if(this._type_ == oldChild)
        {
            setType((PType) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
