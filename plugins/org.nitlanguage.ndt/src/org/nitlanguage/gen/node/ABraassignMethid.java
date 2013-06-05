/* This file was generated by SableCC (http://www.sablecc.org/). */

package org.nitlanguage.gen.node;


import org.nitlanguage.gen.analysis.Analysis;

@SuppressWarnings("nls")
public final class ABraassignMethid extends PMethid
{
    private TObra _obra_;
    private TCbra _cbra_;
    private TAssign _assign_;

    public ABraassignMethid()
    {
        // Constructor
    }

    public ABraassignMethid(
        @SuppressWarnings("hiding") TObra _obra_,
        @SuppressWarnings("hiding") TCbra _cbra_,
        @SuppressWarnings("hiding") TAssign _assign_)
    {
        // Constructor
        setObra(_obra_);

        setCbra(_cbra_);

        setAssign(_assign_);

    }

    @Override
    public Object clone()
    {
        return new ABraassignMethid(
            cloneNode(this._obra_),
            cloneNode(this._cbra_),
            cloneNode(this._assign_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseABraassignMethid(this);
    }

    public TObra getObra()
    {
        return this._obra_;
    }

    public void setObra(TObra node)
    {
        if(this._obra_ != null)
        {
            this._obra_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._obra_ = node;
    }

    public TCbra getCbra()
    {
        return this._cbra_;
    }

    public void setCbra(TCbra node)
    {
        if(this._cbra_ != null)
        {
            this._cbra_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._cbra_ = node;
    }

    public TAssign getAssign()
    {
        return this._assign_;
    }

    public void setAssign(TAssign node)
    {
        if(this._assign_ != null)
        {
            this._assign_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._assign_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._obra_)
            + toString(this._cbra_)
            + toString(this._assign_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._obra_ == child)
        {
            this._obra_ = null;
            return;
        }

        if(this._cbra_ == child)
        {
            this._cbra_ = null;
            return;
        }

        if(this._assign_ == child)
        {
            this._assign_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._obra_ == oldChild)
        {
            setObra((TObra) newChild);
            return;
        }

        if(this._cbra_ == oldChild)
        {
            setCbra((TCbra) newChild);
            return;
        }

        if(this._assign_ == oldChild)
        {
            setAssign((TAssign) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
