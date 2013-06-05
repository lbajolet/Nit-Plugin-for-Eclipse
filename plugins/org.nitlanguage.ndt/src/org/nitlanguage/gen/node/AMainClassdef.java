/* This file was generated by SableCC (http://www.sablecc.org/). */

package org.nitlanguage.gen.node;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.nitlanguage.gen.analysis.Analysis;

@SuppressWarnings("nls")
public final class AMainClassdef extends PClassdef
{
    private final LinkedList<PPropdef> _propdefs_ = new LinkedList<PPropdef>();

    public AMainClassdef()
    {
        // Constructor
    }

    public AMainClassdef(
        @SuppressWarnings("hiding") List<?> _propdefs_)
    {
        // Constructor
        setPropdefs(_propdefs_);

    }

    @Override
    public Object clone()
    {
        return new AMainClassdef(
            cloneList(this._propdefs_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAMainClassdef(this);
    }

    public LinkedList<PPropdef> getPropdefs()
    {
        return this._propdefs_;
    }

    public void setPropdefs(List<?> list)
    {
        for(PPropdef e : this._propdefs_)
        {
            e.parent(null);
        }
        this._propdefs_.clear();

        for(Object obj_e : list)
        {
            PPropdef e = (PPropdef) obj_e;
            if(e.parent() != null)
            {
                e.parent().removeChild(e);
            }

            e.parent(this);
            this._propdefs_.add(e);
        }
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._propdefs_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._propdefs_.remove(child))
        {
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        for(ListIterator<PPropdef> i = this._propdefs_.listIterator(); i.hasNext();)
        {
            if(i.next() == oldChild)
            {
                if(newChild != null)
                {
                    i.set((PPropdef) newChild);
                    newChild.parent(this);
                    oldChild.parent(null);
                    return;
                }

                i.remove();
                oldChild.parent(null);
                return;
            }
        }

        throw new RuntimeException("Not a child.");
    }
}
