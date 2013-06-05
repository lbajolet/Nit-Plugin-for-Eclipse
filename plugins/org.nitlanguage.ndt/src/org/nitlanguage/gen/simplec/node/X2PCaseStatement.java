/* This file was generated by SableCC (http://www.sablecc.org/). */

package org.nitlanguage.gen.simplec.node;

import org.nitlanguage.gen.simplec.analysis.*;

public final class X2PCaseStatement extends XPCaseStatement
{
    private PCaseStatement _pCaseStatement_;

    public X2PCaseStatement()
    {
    }

    public X2PCaseStatement(
        PCaseStatement _pCaseStatement_)
    {
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
            toString(_pCaseStatement_);
    }
}
