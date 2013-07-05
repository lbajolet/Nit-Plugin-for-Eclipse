/* This file was generated by SableCC (http://www.sablecc.org/). */

package org.nitlanguage.gen.simplec.node;

import java.util.*;
import org.nitlanguage.gen.simplec.analysis.*;

public final class ANeqRelop extends PRelop
{
    private TNeq _neq_;

    public ANeqRelop()
    {
    }

    public ANeqRelop(
        TNeq _neq_)
    {
        setNeq(_neq_);

    }
    public Object clone()
    {
        return new ANeqRelop(
            (TNeq) cloneNode(_neq_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseANeqRelop(this);
    }

    public TNeq getNeq()
    {
        return _neq_;
    }

    public void setNeq(TNeq node)
    {
        if(_neq_ != null)
        {
            _neq_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _neq_ = node;
    }

    public String toString()
    {
        return ""
            + toString(_neq_);
    }

    void removeChild(Node child)
    {
        if(_neq_ == child)
        {
            _neq_ = null;
            return;
        }

    }

    void replaceChild(Node oldChild, Node newChild)
    {
        if(_neq_ == oldChild)
        {
            setNeq((TNeq) newChild);
            return;
        }

    }
}