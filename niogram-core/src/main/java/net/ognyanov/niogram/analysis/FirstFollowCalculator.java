package net.ognyanov.niogram.analysis;
/*
 * Copyright (c) 2018 by Nikolay Ognyanov. All rights reserved.
 * 
 * Use of this file is governed by the licensing conditions   
 * in the LICENSE.txt file in the root folder of the project.
 */

import net.ognyanov.niogram.ast.Block;
import net.ognyanov.niogram.ast.BuiltInTypes;
import net.ognyanov.niogram.ast.Grammar;
import net.ognyanov.niogram.ast.GrammarNode;
import net.ognyanov.niogram.ast.GrammarVisitor;
import net.ognyanov.niogram.ast.Nonterminal;
import net.ognyanov.niogram.ast.NonterminalRule;
import net.ognyanov.niogram.ast.Term;
import net.ognyanov.niogram.ast.Terminal;
import net.ognyanov.niogram.ast.TerminalRule;
import net.ognyanov.niogram.util.BaseInterruptable;
import net.ognyanov.niogram.util.BiasedBitSet;
import net.ognyanov.niogram.util.NioGramException;

/**
 * A calculator for First/Follow sets and the related conflicts data.
 *
 * @author Nikolay Ognyanov
 */
public final class FirstFollowCalculator
    extends BaseInterruptable
    implements AttributeCalculator
{
    /**
     * Calculates the First and Follow sets of a grammar
     * and the related conflicts data. If basic flags
     * data is not available, invokes first {@link FlagsCalculator}.
     * 
     * @param grammar the grammar to be processed
     */
    @Override
    public void calculate(Grammar grammar)
    {
        if (grammar == null) {
            throw new IllegalArgumentException("null argument");
        }
        if (!grammar.hasFlags()) {
            throw new IllegalArgumentException("grammar has no flags");
        }
        if (grammar.getNonterminalRules().size() == 0) {
            grammar.setFF(true);
            return;
        }

        if (!grammar.hasFF()) {
            new FlagsCalculator().calculate(grammar);
        }
        grammar.clearFF();
        grammar.setFF(true);
        PrepareFFVisitor prepareVisitor = new PrepareFFVisitor();
        prepareVisitor.visitGrammar(grammar);
        FirstVisitor firstVisitor = new FirstVisitor();
        FollowVisitor followVisitor = new FollowVisitor();
        FFConflictsVisitor conflictsVisitor = new FFConflictsVisitor();
        try {
            setRelayTarget(firstVisitor);
            firstVisitor.visitGrammar(grammar);
            setRelayTarget(followVisitor);
            followVisitor.visitGrammar(grammar);
            setRelayTarget(conflictsVisitor);
            conflictsVisitor.visitGrammar(grammar);
            setRelayTarget(null);
        }
        catch (NioGramException e) {
            setRelayTarget(null);
            grammar.clearFF();
        }
    }

    private static class PrepareFFVisitor
        extends GrammarVisitor
    {
        private Grammar grammar;

        @Override
        public void visitGrammar(Grammar grammar)
        {
            this.grammar = grammar;
            super.visitGrammar(grammar);
        }

        @Override
        public void preVisit(GrammarNode node)
        {
            if (!((node instanceof Terminal) ||
                    (node instanceof Nonterminal))) {
                node.setFirst(
                    new BiasedBitSet(BuiltInTypes.MIN_TYPE, grammar));
            }
            if (node instanceof TerminalRule) {
                node.getFirst().set(node.getType());
            }
            node.setFollow(
                new BiasedBitSet(BuiltInTypes.MIN_TYPE, grammar));
            if (node instanceof Term) {
                Term term = (Term) node;
                term.setSuffixFirst(
                    new BiasedBitSet(BuiltInTypes.MIN_TYPE, grammar));
            }
            if (node instanceof NonterminalRule) {
                NonterminalRule rule = (NonterminalRule) node;
                rule.getConflicts().clear();
                rule.setFfConflict(null);
            }
            else if (node instanceof Block) {
                Block block = (Block) node;
                block.getConflicts().clear();
                block.setFfConflict(null);
            }
        }
    }

}
