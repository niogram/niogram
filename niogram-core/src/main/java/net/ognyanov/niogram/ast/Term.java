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
 * Common superclass of the AST term types - Terminal, Nonterminal, Block.<p>
 * 
 * Has the following own attributes:
 * <ul>
 * </li>
 * <li><strong>prefixNullable</strong>
 *  Flags that the prefix of the term is nullable.</li>
 * <li><strong>suffixNullable</strong>
 *  Flags that the suffix of the term is nullable.</li>
 * <li><strong>suffixFirst</strong>
 * - The First set of the suffix to the term.</li>
 * <li><strong>suffixFirstK</strong>
 * - The FirstK set of the suffix to the term.</li>
 * <li><strong>suffixFirstKL</strong>
 * - The linearized FirstKL set of the suffix to the term.</li>
 * </ul>
 *
 * @author Nikolay Ognyanov
 */
public abstract class Term
    extends GrammarNode
{
    private static final long serialVersionUID = 1L;

    private boolean           prefixNullable   = false;
    private boolean           suffixNullable   = false;
    private BiasedBitSet      suffixFirst      = null;
    private IntLLStringSet    suffixFirstK     = null;
    private BitSetLLString    suffixFirstKL    = null;

    public Term(int type)
    {
        super(type);
    }

    public boolean isPrefixNullable()
    {
        return prefixNullable;
    }

    public void setPrefixNullable(boolean prefixNullable)
    {
        this.prefixNullable = prefixNullable;
    }

    public boolean isSuffixNullable()
    {
        return suffixNullable;
    }

    public void setSuffixNullable(boolean suffixNullable)
    {
        this.suffixNullable = suffixNullable;
    }

    public BiasedBitSet getSuffixFirst()
    {
        return suffixFirst;
    }

    public void setSuffixFirst(BiasedBitSet suffixFirst)
    {
        this.suffixFirst = suffixFirst;
    }

    public IntLLStringSet getSuffixFirstK()
    {
        return suffixFirstK;
    }

    public void setSuffixFirstK(IntLLStringSet suffixFirstK)
    {
        this.suffixFirstK = suffixFirstK;
    }

    public BitSetLLString getSuffixFirstKL()
    {
        return suffixFirstKL;
    }

    public void setSuffixFirstKL(BitSetLLString suffixFirstKL)
    {
        this.suffixFirstKL = suffixFirstKL;
    }
}
