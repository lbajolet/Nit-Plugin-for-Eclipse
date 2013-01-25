/* This file was generated by SableCC (http://www.sablecc.org/). */

package org.nitlanguage.gen.node;

import org.nitlanguage.gen.analysis.*;

@SuppressWarnings("nls")
public final class ACastAsExternCall extends PExternCall
{
    private PType _fromType_;
    private TKwas _kwas_;
    private PType _toType_;

    public ACastAsExternCall()
    {
        // Constructor
    }

    public ACastAsExternCall(
        @SuppressWarnings("hiding") PType _fromType_,
        @SuppressWarnings("hiding") TKwas _kwas_,
        @SuppressWarnings("hiding") PType _toType_)
    {
        // Constructor
        setFromType(_fromType_);

        setKwas(_kwas_);

        setToType(_toType_);

    }

    @Override
    public Object clone()
    {
        return new ACastAsExternCall(
            cloneNode(this._fromType_),
            cloneNode(this._kwas_),
            cloneNode(this._toType_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseACastAsExternCall(this);
    }

    public PType getFromType()
    {
        return this._fromType_;
    }

    public void setFromType(PType node)
    {
        if(this._fromType_ != null)
        {
            this._fromType_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._fromType_ = node;
    }

    public TKwas getKwas()
    {
        return this._kwas_;
    }

    public void setKwas(TKwas node)
    {
        if(this._kwas_ != null)
        {
            this._kwas_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._kwas_ = node;
    }

    public PType getToType()
    {
        return this._toType_;
    }

    public void setToType(PType node)
    {
        if(this._toType_ != null)
        {
            this._toType_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._toType_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._fromType_)
            + toString(this._kwas_)
            + toString(this._toType_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._fromType_ == child)
        {
            this._fromType_ = null;
            return;
        }

        if(this._kwas_ == child)
        {
            this._kwas_ = null;
            return;
        }

        if(this._toType_ == child)
        {
            this._toType_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._fromType_ == oldChild)
        {
            setFromType((PType) newChild);
            return;
        }

        if(this._kwas_ == oldChild)
        {
            setKwas((TKwas) newChild);
            return;
        }

        if(this._toType_ == oldChild)
        {
            setToType((PType) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}