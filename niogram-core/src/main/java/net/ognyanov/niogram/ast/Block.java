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
 * The AST node for block terms.<p>
 * 
 * Has the following own attributes:
 * <ul>
 * <li><strong>alternatives</strong>
 * - The list of all alternatives.</li>
 * <li><strong>optional</strong>
 * - A flag indicating that the block was marked as EBNF-optional
 *   in the text of the grammar. Informational only. Should not
 *   be used in code since the parser inserts into each such
 *   block an empty alternative.</li>
 * <li><strong>repeatable</strong>
 * - A flag indicating that the block was marked as EBNF-repeatable
 *   in the text of the grammar.</li>
 * <li><strong>greedy</strong>
 * - A flag indicating that the EBNF occurrence indicator (if any)
 *   in the text of the grammar was greedy. Also true if no
 *   occurrence indicator was present.</li>
 * </ul>
*
 * @author Nikolay Ognyanov
 */
public class Block
    extends Term
    implements Multiplex
{
    private static final long serialVersionUID = 1L;

    private List<Alternative> alternatives     = new ArrayList<Alternative>();

    private boolean           optional         = false;
    private boolean           repeatable       = false;
    private boolean           greedy           = true;

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

    public Block(int type, boolean optional, boolean repeatable, boolean greedy)
    {
        super(type);
        this.optional = optional;
        this.repeatable = repeatable;
        this.greedy = greedy;

    }

    @Override
    public List<Alternative> getAlternatives()
    {
        return alternatives;
    }

    public boolean isOptional()
    {
        return optional;
    }

    public boolean isRepeatable()
    {
        return repeatable;
    }

    public boolean isGreedy()
    {
        return greedy;
    }

    @Override
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
    public BiasedBitSet getFfConflict()
    {
        return ffConflict;
    }

    public void setFfConflict(BiasedBitSet ffConflict)
    {
        this.ffConflict = ffConflict;
    }

    @Override
    public IntLLStringSet getFfConflictK()
    {
        return ffConflictK;
    }

    public void setFfConflictK(IntLLStringSet ffConflictK)
    {
        this.ffConflictK = ffConflictK;
    }

    @Override
    public BitSetLLString getFfConflictKL()
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
