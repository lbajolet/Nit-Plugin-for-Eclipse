/* This file was generated by SableCC (http://www.sablecc.org/). */

package org.nitlanguage.gen.simplec.node;

import java.util.*;
import org.nitlanguage.gen.simplec.analysis.*;

public final class ADirectAbstractPointer extends PAbstractPointer
{
    private TStar _star_;
    private PAbstractDirectDeclarator _abstractDirectDeclarator_;

    public ADirectAbstractPointer()
    {
    }

    public ADirectAbstractPointer(
        TStar _star_,
        PAbstractDirectDeclarator _abstractDirectDeclarator_)
    {
        setStar(_star_);

        setAbstractDirectDeclarator(_abstractDirectDeclarator_);

    }
    public Object clone()
    {
        return new ADirectAbstractPointer(
            (TStar) cloneNode(_star_),
            (PAbstractDirectDeclarator) cloneNode(_abstractDirectDeclarator_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseADirectAbstractPointer(this);
    }

    public TStar getStar()
    {
        return _star_;
    }

    public void setStar(TStar node)
    {
        if(_star_ != null)
        {
            _star_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _star_ = node;
    }

    public PAbstractDirectDeclarator getAbstractDirectDeclarator()
    {
        return _abstractDirectDeclarator_;
    }

    public void setAbstractDirectDeclarator(PAbstractDirectDeclarator node)
    {
        if(_abstractDirectDeclarator_ != null)
        {
            _abstractDirectDeclarator_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _abstractDirectDeclarator_ = node;
    }

    public String toString()
    {
        return ""
            + toString(_star_)
            + toString(_abstractDirectDeclarator_);
    }

    void removeChild(Node child)
    {
        if(_star_ == child)
        {
            _star_ = null;
            return;
        }

        if(_abstractDirectDeclarator_ == child)
        {
            _abstractDirectDeclarator_ = null;
            return;
        }

    }

    void replaceChild(Node oldChild, Node newChild)
    {
        if(_star_ == oldChild)
        {
            setStar((TStar) newChild);
            return;
        }

        if(_abstractDirectDeclarator_ == oldChild)
        {
            setAbstractDirectDeclarator((PAbstractDirectDeclarator) newChild);
            return;
        }

    }
}