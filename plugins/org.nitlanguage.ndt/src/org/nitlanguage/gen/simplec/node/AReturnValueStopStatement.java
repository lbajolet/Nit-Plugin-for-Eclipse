/* This file was generated by SableCC (http://www.sablecc.org/). */

package org.nitlanguage.gen.simplec.node;

import java.util.*;
import org.nitlanguage.gen.simplec.analysis.*;

public final class AReturnValueStopStatement extends PStopStatement
{
    private TReturn _return_;
    private PValue _value_;
    private TSemicolon _semicolon_;
    private final LinkedList _deadCode_ = new TypedLinkedList(new DeadCode_Cast());

    public AReturnValueStopStatement()
    {
    }

    public AReturnValueStopStatement(
        TReturn _return_,
        PValue _value_,
        TSemicolon _semicolon_,
        List _deadCode_)
    {
        setReturn(_return_);

        setValue(_value_);

        setSemicolon(_semicolon_);

        {
            this._deadCode_.clear();
            this._deadCode_.addAll(_deadCode_);
        }

    }

    public AReturnValueStopStatement(
        TReturn _return_,
        PValue _value_,
        TSemicolon _semicolon_,
        XPDeadCode _deadCode_)
    {
        setReturn(_return_);

        setValue(_value_);

        setSemicolon(_semicolon_);

        if(_deadCode_ != null)
        {
            while(_deadCode_ instanceof X1PDeadCode)
            {
                this._deadCode_.addFirst(((X1PDeadCode) _deadCode_).getPDeadCode());
                _deadCode_ = ((X1PDeadCode) _deadCode_).getXPDeadCode();
            }
            this._deadCode_.addFirst(((X2PDeadCode) _deadCode_).getPDeadCode());
        }

    }
    public Object clone()
    {
        return new AReturnValueStopStatement(
            (TReturn) cloneNode(_return_),
            (PValue) cloneNode(_value_),
            (TSemicolon) cloneNode(_semicolon_),
            cloneList(_deadCode_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAReturnValueStopStatement(this);
    }

    public TReturn getReturn()
    {
        return _return_;
    }

    public void setReturn(TReturn node)
    {
        if(_return_ != null)
        {
            _return_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _return_ = node;
    }

    public PValue getValue()
    {
        return _value_;
    }

    public void setValue(PValue node)
    {
        if(_value_ != null)
        {
            _value_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _value_ = node;
    }

    public TSemicolon getSemicolon()
    {
        return _semicolon_;
    }

    public void setSemicolon(TSemicolon node)
    {
        if(_semicolon_ != null)
        {
            _semicolon_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _semicolon_ = node;
    }

    public LinkedList getDeadCode()
    {
        return _deadCode_;
    }

    public void setDeadCode(List list)
    {
        _deadCode_.clear();
        _deadCode_.addAll(list);
    }

    public String toString()
    {
        return ""
            + toString(_return_)
            + toString(_value_)
            + toString(_semicolon_)
            + toString(_deadCode_);
    }

    void removeChild(Node child)
    {
        if(_return_ == child)
        {
            _return_ = null;
            return;
        }

        if(_value_ == child)
        {
            _value_ = null;
            return;
        }

        if(_semicolon_ == child)
        {
            _semicolon_ = null;
            return;
        }

        if(_deadCode_.remove(child))
        {
            return;
        }

    }

    void replaceChild(Node oldChild, Node newChild)
    {
        if(_return_ == oldChild)
        {
            setReturn((TReturn) newChild);
            return;
        }

        if(_value_ == oldChild)
        {
            setValue((PValue) newChild);
            return;
        }

        if(_semicolon_ == oldChild)
        {
            setSemicolon((TSemicolon) newChild);
            return;
        }

        for(ListIterator i = _deadCode_.listIterator(); i.hasNext();)
        {
            if(i.next() == oldChild)
            {
                if(newChild != null)
                {
                    i.set(newChild);
                    oldChild.parent(null);
                    return;
                }

                i.remove();
                oldChild.parent(null);
                return;
            }
        }

    }

    private class DeadCode_Cast implements Cast
    {
        public Object cast(Object o)
        {
            PDeadCode node = (PDeadCode) o;

            if((node.parent() != null) &&
                (node.parent() != AReturnValueStopStatement.this))
            {
                node.parent().removeChild(node);
            }

            if((node.parent() == null) ||
                (node.parent() != AReturnValueStopStatement.this))
            {
                node.parent(AReturnValueStopStatement.this);
            }

            return node;
        }
    }
}
