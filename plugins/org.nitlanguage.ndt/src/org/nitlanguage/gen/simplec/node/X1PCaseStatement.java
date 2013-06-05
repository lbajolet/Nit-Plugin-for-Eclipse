/* This file was generated by SableCC (http://www.sablecc.org/). */

package org.nitlanguage.gen.simplec.node;

import org.nitlanguage.gen.simplec.analysis.*;

public final class X1PCaseStatement extends XPCaseStatement
{
    private XPCaseStatement _xPCaseStatement_;
    private PCaseStatement _pCaseStatement_;

    public X1PCaseStatement()
    {
    }

    public X1PCaseStatement(
        XPCaseStatement _xPCaseStatement_,
        PCaseStatement _pCaseStatement_)
    {
        setXPCaseStatement(_xPCaseStatement_);
        setPCaseStatement(_pCaseStatement_);
    }

    public Object clone()
    {
        throw new RuntimeException("Unsupported Operation");
    }

    public void apply(Switch sw)
    {
        throw new RuntimeException("Switch not supported.");
    }

    public XPCaseStatement getXPCaseStatement()
    {
        return _xPCaseStatement_;
    }

    public void setXPCaseStatement(XPCaseStatement node)
    {
        if(_xPCaseStatement_ != null)
        {
            _xPCaseStatement_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _xPCaseStatement_ = node;
    }

    public PCaseStatement getPCaseStatement()
    {
        return _pCaseStatement_;
    }

    public void setPCaseStatement(PCaseStatement node)
    {
        if(_pCaseStatement_ != null)
        {
            _pCaseStatement_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _pCaseStatement_ = node;
    }

    void removeChild(Node child)
    {
        if(_xPCaseStatement_ == child)
        {
            _xPCaseStatement_ = null;
        }

        if(_pCaseStatement_ == child)
        {
            _pCaseStatement_ = null;
        }
    }

    void replaceChild(Node oldChild, Node newChild)
    {
    }

    public String toString()
    {
        return "" +
            toString(_xPCaseStatement_) +
            toString(_pCaseStatement_);
    }
}
