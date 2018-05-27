package net.ognyanov.niogram.analysis;
/*
 * Copyright (c) 2018 by Nikolay Ognyanov. All rights reserved.
 * 
 * Use of this file is governed by the licensing conditions   
 * in the LICENSE.txt file in the root folder of the project.
 */

import net.ognyanov.niogram.ast.Grammar;
import net.ognyanov.niogram.ast.GrammarNode;
import net.ognyanov.niogram.ast.GrammarVisitor;
import net.ognyanov.niogram.ast.Nonterminal;
import net.ognyanov.niogram.ast.Rule;
import net.ognyanov.niogram.ast.Terminal;
import net.ognyanov.niogram.ast.TerminalRule;
import net.ognyanov.niogram.util.BaseInterruptable;

/**
 * A calculator for the basic flag attributes of a
 * grammar : the nullability of all nodes and
 * the productivity, reachability and use of
 * the nonterminal rules.
 * @author Nikolay Ognyanov
 */
public class FlagsCalculator
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
        PrepareFlagsVisitor prepareVisitor = new PrepareFlagsVisitor();
        prepareVisitor.visitGrammar(grammar);
        new NullableMarker().visitGrammar(grammar);
        new ProductiveMarker().visitGrammar(grammar);
        new ReachableMarker().visitGrammar(grammar);
        new UsedMarker().visitGrammar(grammar);
        grammar.setFlags(true);
    }

    private static class PrepareFlagsVisitor
        extends GrammarVisitor
    {
        @Override
        public void preVisit(GrammarNode node)
        {
            if (!((node instanceof Terminal) ||
                    (node instanceof Nonterminal) ||
                    (node instanceof TerminalRule))) {
                node.setNullable(false);
                node.setProductive(false);
                node.setReachable(false);
            }
            if (node instanceof Rule) {
                ((Rule) node).setUsed(false);
            }
        }
    }
}
