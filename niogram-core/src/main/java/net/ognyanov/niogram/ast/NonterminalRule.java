/*
 * Copyright (c) 2018 by Nikolay Ognyanov. All rights reserved.
 * 
 * Use of this file is governed by the licensing conditions   
 * in the LICENSE.txt file in the root folder of the project.
 */
package net.ognyanov.niogram.ast;

import java.util.ArrayList;
import java.util.List;

import net.ognyanov.niogram.util.BiasedBitSet;
import net.ognyanov.niogram.util.BitSetLLString;
import net.ognyanov.niogram.util.IntLLStringSet;

/**
 * The AST node for nonterminal rules.<p>
 * 
 * Has a the following own attributes:
 * <ul>
 * <li><strong>alternatives</strong>
 * - a list of alternatives.</li>
 * <li><strong>references</strong>
 * - a list of all Nonterminal nodes referencing this rule.
 *</ul>
 * @author Nikolay Ognyanov
 */
public final class NonterminalRule
    extends GrammarNode
    implements Rule, Multiplex
{
    private static final long serialVersionUID = 1L;

    private List<Alternative> alternatives     = new ArrayList<Alternative>();

    private boolean           used;
    private List<Nonterminal> references       = new ArrayList<Nonterminal>();

    private List<Conflict>    conflicts        =
        new ArrayList<Multiplex.Conflict>();
    private List<ConflictK>   conflictsK       =
        new ArrayList<Multiplex.ConflictK>();
    private List<ConflictKL>  conflictsKL      =
        new ArrayList<Multiplex.ConflictKL>();
    private BiasedBitSet      ffConflict       = null;
    private IntLLStringSet    ffConflictK      = null;
    private BitSetLLString    ffConflictKL     = null;
    private int               minK             = 0;
    private int               minKL            = 0;
    private int               minFfK           = 0;
    private int               minFfKL          = 0;

    public NonterminalRule(int type)
    {
        super(type);
    }

    @Override
    public List<Alternative> getAlternatives()
    {
        return alternatives;
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

    /**
     * Retrieve the list of references.
     * 
     * @return the list of references
     */
    public List<Nonterminal> getReferences()
    {
        return references;
    }

    public List<Conflict> getConflicts()
    {
        return conflicts;
    }

    @Override
    public List<ConflictK> getConflictsK()
    {
        return conflictsK;
    }

    @Override
    public List<ConflictKL> getConflictsKL()
    {
        return conflictsKL;
    }

    @Override
    public BiasedBitSet getFfConflictSet()
    {
        return ffConflict;
    }

    public void setFfConflict(BiasedBitSet ffConflict)
    {
        this.ffConflict = ffConflict;
    }

    @Override
    public IntLLStringSet getFfConflictSetK()
    {
        return ffConflictK;
    }

    public void setFfConflictK(IntLLStringSet ffConflictK)
    {
        this.ffConflictK = ffConflictK;
    }

    @Override
    public BitSetLLString getFfConflictSetKL()
    {
        return ffConflictKL;
    }

    public void setFfConflictKL(BitSetLLString ffConflictKL)
    {
        this.ffConflictKL = ffConflictKL;
    }

    @Override
    public int getMinK()
    {
        return minK;
    }

    public void setMinK(int minK)
    {
        this.minK = minK;
    }

    @Override
    public int getMinKL()
    {
        return minKL;
    }

    public void setMinKL(int minKL)
    {
        this.minKL = minKL;
    }

    @Override
    public int getMinFfK()
    {
        return minFfK;
    }

    public void setMinFfK(int minFfK)
    {
        this.minFfK = minFfK;
    }

    @Override
    public int getMinFfKL()
    {
        return minFfKL;
    }

    public void setMinFfKL(int minFfKL)
    {
        this.minFfKL = minFfKL;
    }
}
