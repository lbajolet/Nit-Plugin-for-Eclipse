/* This file was generated by SableCC (http://www.sablecc.org/). */

package org.nitlanguage.gen.simplec.node;

import java.util.*;
import org.nitlanguage.gen.simplec.analysis.*;

public final class ALtRelop extends PRelop
{
    private TLt _lt_;

    public ALtRelop()
    {
    }

    public ALtRelop(
        TLt _lt_)
    {
        setLt(_lt_);

    }
    public Object clone()
    {
        return new ALtRelop(
            (TLt) cloneNode(_lt_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseALtRelop(this);
    }

    public TLt getLt()
    {
        return _lt_;
    }

    public void setLt(TLt node)
    {
        if(_lt_ != null)
        {
            _lt_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _lt_ = node;
    }

    public String toString()
    {
        return ""
            + toString(_lt_);
    }

    void removeChild(Node child)
    {
        if(_lt_ == child)
        {
            _lt_ = null;
            return;
        }

    }

    void replaceChild(Node oldChild, Node newChild)
    {
        if(_lt_ == oldChild)
        {
            setLt((TLt) newChild);
            return;
        }

    }
}