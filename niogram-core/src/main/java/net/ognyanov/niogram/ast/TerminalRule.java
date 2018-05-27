/*
 * Copyright (c) 2018 by Nikolay Ognyanov. All rights reserved.
 * 
 * Use of this file is governed by the licensing conditions   
 * in the LICENSE.txt file in the root folder of the project.
 */
package net.ognyanov.niogram.ast;

import java.util.ArrayList;
import java.util.List;

/**
 * The AST node for nonterminals.<p>
 * 
 * Has a single own attribute <strong>references</strong>
 * - a list of all Terminal nodes referencing this
 * rule.
 *
 * @author Nikolay Ognyanov
 */
public final class TerminalRule
    extends GrammarNode
    implements Rule
{
    private static final long serialVersionUID = 1L;

    private boolean           used;
    private List<Terminal>    references       = new ArrayList<Terminal>();

    public TerminalRule(int type)
    {
        super(type);
        super.setProductive(true);
    }

    /**
     * Retrieve the list of references.
     * 
     * @return the list of references
     */
    public List<Terminal> getReferences()
    {
        return references;
    }

    @Override
    public void setNullable(boolean nullable)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isUsed()
    {
        return used;
    }

    @Override
    public void setUsed(boolean used)
    {
        this.used = used;
    }

    @Override
    public void setProductive(boolean productive)
    {
        throw new UnsupportedOperationException();
    }
}
