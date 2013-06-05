/* This file was generated by SableCC (http://www.sablecc.org/). */

package org.nitlanguage.gen.simplec.node;

import java.util.*;
import org.nitlanguage.gen.simplec.analysis.*;

public final class ASignedLongSignedLongSpecifier extends PSignedLongSpecifier
{
    private TSigned _signed_;
    private TLong _long_;

    public ASignedLongSignedLongSpecifier()
    {
    }

    public ASignedLongSignedLongSpecifier(
        TSigned _signed_,
        TLong _long_)
    {
        setSigned(_signed_);

        setLong(_long_);

    }
    public Object clone()
    {
        return new ASignedLongSignedLongSpecifier(
            (TSigned) cloneNode(_signed_),
            (TLong) cloneNode(_long_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseASignedLongSignedLongSpecifier(this);
    }

    public TSigned getSigned()
    {
        return _signed_;
    }

    public void setSigned(TSigned node)
    {
        if(_signed_ != null)
        {
            _signed_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _signed_ = node;
    }

    public TLong getLong()
    {
        return _long_;
    }

    public void setLong(TLong node)
    {
        if(_long_ != null)
        {
            _long_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _long_ = node;
    }

    public String toString()
    {
        return ""
            + toString(_signed_)
            + toString(_long_);
    }

    void removeChild(Node child)
    {
        if(_signed_ == child)
        {
            _signed_ = null;
            return;
        }

        if(_long_ == child)
        {
            _long_ = null;
            return;
        }

    }

    void replaceChild(Node oldChild, Node newChild)
    {
        if(_signed_ == oldChild)
        {
            setSigned((TSigned) newChild);
            return;
        }

        if(_long_ == oldChild)
        {
            setLong((TLong) newChild);
            return;
        }

    }
}
