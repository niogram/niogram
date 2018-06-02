package net.ognyanov.niogram.analysis;
/*
 * Copyright (c) 2018 by Nikolay Ognyanov. All rights reserved.
 * 
 * Use of this file is governed by the licensing conditions   
 * in the LICENSE.txt file in the root folder of the project.
 */

import net.ognyanov.niogram.ast.Grammar;
import net.ognyanov.niogram.util.BaseInterruptable;

/**
 * A calculator for the basic flag attributes of a
 * grammar : the nullability of all nodes and
 * the productivity, reachability and use of
 * the nonterminal rules.
 * @author Nikolay Ognyanov
 */
public final class FlagsCalculator
    extends BaseInterruptable
    implements AttributeCalculator
{
    /**
     * Calculates the basic flag attributes of a
     * grammar : the nullability of all nodes and
     * the productivity, reachability and use of
     * the nonterminal rules.
     * 
     * @param grammar the grammar to be processed
     */
    @Override
    public void calculate(Grammar grammar)
    {
        if (grammar == null) {
            throw new IllegalArgumentException("null argument");
        }
        if (grammar.getNonterminalRules().size() == 0) {
            grammar.setFlags(true);
            return;
        }
        grammar.clearFlags();
        grammar.setFlags(true);
        new NullableMarker().visitGrammar(grammar);
        new ProductiveMarker().visitGrammar(grammar);
        new ReachableMarker().visitGrammar(grammar);
        new UsedMarker().visitGrammar(grammar);
    }
}
