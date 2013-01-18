/* This file was generated by SableCC (http://www.sablecc.org/). */

package node;

import analysis.*;

@SuppressWarnings("nls")
public final class AMinusAssignOp extends PAssignOp
{
    private TMinuseq _minuseq_;

    public AMinusAssignOp()
    {
        // Constructor
    }

    public AMinusAssignOp(
        @SuppressWarnings("hiding") TMinuseq _minuseq_)
    {
        // Constructor
        setMinuseq(_minuseq_);

    }

    @Override
    public Object clone()
    {
        return new AMinusAssignOp(
            cloneNode(this._minuseq_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAMinusAssignOp(this);
    }

    public TMinuseq getMinuseq()
    {
        return this._minuseq_;
    }

    public void setMinuseq(TMinuseq node)
    {
        if(this._minuseq_ != null)
        {
            this._minuseq_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._minuseq_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._minuseq_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._minuseq_ == child)
        {
            this._minuseq_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._minuseq_ == oldChild)
        {
            setMinuseq((TMinuseq) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}