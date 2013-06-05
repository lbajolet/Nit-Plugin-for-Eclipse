/* This file was generated by SableCC (http://www.sablecc.org/). */

package org.nitlanguage.gen.simplec.node;

import java.util.*;
import org.nitlanguage.gen.simplec.analysis.*;

public final class ATranslationUnit extends PTranslationUnit
{
    private final LinkedList _declarationOrDefinition_ = new TypedLinkedList(new DeclarationOrDefinition_Cast());

    public ATranslationUnit()
    {
    }

    public ATranslationUnit(
        List _declarationOrDefinition_)
    {
        {
            this._declarationOrDefinition_.clear();
            this._declarationOrDefinition_.addAll(_declarationOrDefinition_);
        }

    }

    public ATranslationUnit(
        XPDeclarationOrDefinition _declarationOrDefinition_)
    {
        if(_declarationOrDefinition_ != null)
        {
            while(_declarationOrDefinition_ instanceof X1PDeclarationOrDefinition)
            {
                this._declarationOrDefinition_.addFirst(((X1PDeclarationOrDefinition) _declarationOrDefinition_).getPDeclarationOrDefinition());
                _declarationOrDefinition_ = ((X1PDeclarationOrDefinition) _declarationOrDefinition_).getXPDeclarationOrDefinition();
            }
            this._declarationOrDefinition_.addFirst(((X2PDeclarationOrDefinition) _declarationOrDefinition_).getPDeclarationOrDefinition());
        }

    }
    public Object clone()
    {
        return new ATranslationUnit(
            cloneList(_declarationOrDefinition_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseATranslationUnit(this);
    }

    public LinkedList getDeclarationOrDefinition()
    {
        return _declarationOrDefinition_;
    }

    public void setDeclarationOrDefinition(List list)
    {
        _declarationOrDefinition_.clear();
        _declarationOrDefinition_.addAll(list);
    }

    public String toString()
    {
        return ""
            + toString(_declarationOrDefinition_);
    }

    void removeChild(Node child)
    {
        if(_declarationOrDefinition_.remove(child))
        {
            return;
        }

    }

    void replaceChild(Node oldChild, Node newChild)
    {
        for(ListIterator i = _declarationOrDefinition_.listIterator(); i.hasNext();)
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

    private class DeclarationOrDefinition_Cast implements Cast
    {
        public Object cast(Object o)
        {
            PDeclarationOrDefinition node = (PDeclarationOrDefinition) o;

            if((node.parent() != null) &&
                (node.parent() != ATranslationUnit.this))
            {
                node.parent().removeChild(node);
            }

            if((node.parent() == null) ||
                (node.parent() != ATranslationUnit.this))
            {
                node.parent(ATranslationUnit.this);
            }

            return node;
        }
    }
}
