/* This file was generated by SableCC (http://www.sablecc.org/). */

package org.nitlanguage.gen.simplec.node;

import java.util.*;
import org.nitlanguage.gen.simplec.analysis.*;

public final class AEnumDeclarationDeclarationOrDefinition extends PDeclarationOrDefinition
{
    private PEnumDeclaration _enumDeclaration_;

    public AEnumDeclarationDeclarationOrDefinition()
    {
    }

    public AEnumDeclarationDeclarationOrDefinition(
        PEnumDeclaration _enumDeclaration_)
    {
        setEnumDeclaration(_enumDeclaration_);

    }
    public Object clone()
    {
        return new AEnumDeclarationDeclarationOrDefinition(
            (PEnumDeclaration) cloneNode(_enumDeclaration_));
    }

    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAEnumDeclarationDeclarationOrDefinition(this);
    }

    public PEnumDeclaration getEnumDeclaration()
    {
        return _enumDeclaration_;
    }

    public void setEnumDeclaration(PEnumDeclaration node)
    {
        if(_enumDeclaration_ != null)
        {
            _enumDeclaration_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        _enumDeclaration_ = node;
    }

    public String toString()
    {
        return ""
            + toString(_enumDeclaration_);
    }

    void removeChild(Node child)
    {
        if(_enumDeclaration_ == child)
        {
            _enumDeclaration_ = null;
            return;
        }

    }

    void replaceChild(Node oldChild, Node newChild)
    {
        if(_enumDeclaration_ == oldChild)
        {
            setEnumDeclaration((PEnumDeclaration) newChild);
            return;
        }

    }
}