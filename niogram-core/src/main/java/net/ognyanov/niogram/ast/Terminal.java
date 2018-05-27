/*
 * Copyright (c) 2018 by Nikolay Ognyanov. All rights reserved.
 * 
 * Use of this file is governed by the licensing conditions   
 * in the LICENSE.txt file in the root folder of the project.
 */
package net.ognyanov.niogram.ast;

import net.ognyanov.niogram.util.BiasedBitSet;
import net.ognyanov.niogram.util.BitSetLLString;
import net.ognyanov.niogram.util.IntLLStringSet;

/**
 * The AST node for terminals.<p>
 * 
 * Has a single own attribute <strong>rule</strong> - a reference
 * to the correspondent TerminalRule node.
 *
 * @author Nikolay Ognyanov
 */
public final class Terminal
    extends Term
{
    private static final long serialVersionUID = 1L;

    private TerminalRule      rule;

    public Terminal(TerminalRule rule)
    {
        super(rule.getType());
        this.rule = rule;
        rule.getReferences().add(this);
    }

    public TerminalRule getRule()
    {
        return rule;
    }

    /**
     * Relays to the correspondent method of the rule.
     * 
     * @return the symbolic name of the rule
     */
    @Override
    public String getSymbolicName()
    {
        return rule.getSymbolicName();
    }

    /**
     * Not supported for this class.
     * 
     * @param name the symbolic name to be set
     */
    @Override
    public void setSymbolicName(String name)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Relays to the correspondent method of the rule.
     * 
     * @return the display name of the rule
     */
    @Override
    public String getDisplayName()
    {
        String name = super.getDisplayName();
        if (name == null) {
            name = rule.getDisplayName();
        }
        return name;
    }

    /**
     * Not supported for this class.
     * 
     * @param name the display name to be set
     */
    @Override
    public void setDisplayName(String name)
    {
        super.setDisplayName(name);
    }

    /**
     * Relays to the correspondent method of the rule.
     * 
     * @return true if the rule is nullable; otherwise false
     */
    @Override
    public boolean isNullable()
    {
        return rule.isNullable();
    }

    /**
     * Not supported for this class.
     * 
     * @param nullable the value to be set
     */
    @Override
    public void setNullable(boolean nullable)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Relays to the correspondent method of the rule.
     * 
     * @return true if the rule is productive; otherwise false
     */
    @Override
    public boolean isProductive()
    {
        return rule.isProductive();
    }

    /**
     * Not supported for this class.
     * 
     * @param productive the value to be set
     */
    @Override
    public void setProductive(boolean productive)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Relays to the correspondent method of the rule.
     * 
     * @return true if the rule is productive; otherwise false
     */
    @Override
    public boolean isReachable()
    {
        return rule.isReachable();
    }

    /**
     * Not supported for this class.
     * 
     * @param productive the value to be set
     */
    @Override
    public void setReachable(boolean productive)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Relays to the correspondent method of the rule.
     * 
     * @return the First set of the rule
     */
    @Override
    public BiasedBitSet getFirst()
    {
        return rule.getFirst();
    }

    /**
     * Not supported for this class.
     * 
     * @param first the value to be set
     */
    @Override
    public void setFirst(BiasedBitSet first)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Relays to the correspondent method of the rule.
     * 
     * @return the FirstK set of the rule
     */
    @Override
    public IntLLStringSet getFirstK()
    {

        return rule.getFirstK();
    }

    /**
     * Not supported for this class.
     * 
     * @param firstK the value to be set
     */
    @Override
    public void setFirstK(IntLLStringSet firstK)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Relays to the correspondent method of the rule.
     * 
     * @return the FirstKL set of the rule
     */
    @Override
    public BitSetLLString getFirstKL()
    {
        return rule.getFirstKL();
    }

    /**
    * Not supported for this class.
    * 
    * @param firstKL the value to be set
    */
    @Override
    public void setFirstKL(BitSetLLString firstKL)
    {
        throw new UnsupportedOperationException();
    }
}
