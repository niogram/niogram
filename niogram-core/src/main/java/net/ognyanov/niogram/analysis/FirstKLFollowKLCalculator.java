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
import net.ognyanov.niogram.util.BitSetLLString;
import net.ognyanov.niogram.util.NioGramException;

/**
 * A calculator for FirstKL/FollowKL sets and the related conflicts data.
 *
 * @author Nikolay Ognyanov
 */
public final class FirstKLFollowKLCalculator
    extends BaseInterruptable
    implements AttributeCalculator
{
    /**
     * Calculates the FirstKL and FollowKL sets of a grammar
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
        if (grammar.getNonterminalRules().size() == 0) {
            grammar.setFFKL(true);
            return;
        }

        if (!grammar.hasFF()) {
            new FlagsCalculator().calculate(grammar);
        }
        grammar.clearFFKL();
        grammar.setFFKL(true);
        PrepareFFKVisitor prepareVisitor = new PrepareFFKVisitor();
        prepareVisitor.visitGrammar(grammar);
        FirstKLVisitor firstVisitor = new FirstKLVisitor();
        FollowKLVisitor followVisitor = new FollowKLVisitor();
        FFKLConflictsVisitor conflictsVisitor = new FFKLConflictsVisitor();
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
            grammar.clearFFKL();
        }
    }

    private static class PrepareFFKVisitor
        extends GrammarVisitor
    {
        private Grammar grammar;
        private int     k;

        @Override
        public void visitGrammar(Grammar grammar)
        {
            this.grammar = grammar;
            this.k = grammar.getKL();
            super.visitGrammar(grammar);
        }

        @Override
        public void preVisit(GrammarNode node)
        {
            if (node instanceof TerminalRule) {
                TerminalRule rule = (TerminalRule) node;
                BiasedBitSet firstKLContent =
                    new BiasedBitSet(BuiltInTypes.MIN_TYPE, grammar);
                firstKLContent.set(rule.getType());
                BitSetLLString firstKL = new BitSetLLString(k, grammar);
                firstKL.add(firstKLContent);
                rule.setFirstKL(firstKL);
            }
            else if (!(node instanceof Terminal
                    || node instanceof Nonterminal)) {
                BitSetLLString firstKL = new BitSetLLString(k, grammar);
                node.setFirstKL(firstKL);
            }

            node.setFollowKL(new BitSetLLString(k, grammar));
            if (node instanceof Term) {
                Term term = (Term) node;
                term.setSuffixFirstKL(new BitSetLLString(k, grammar));
            }
            if (node instanceof NonterminalRule) {
                NonterminalRule rule = (NonterminalRule) node;
                rule.getConflictsKL().clear();
                rule.setFfConflictKL(null);
                rule.setMinKL(0);
                rule.setMinFfKL(0);
            }
            else if (node instanceof Block) {
                Block block = (Block) node;
                block.getConflictsKL().clear();
                block.setFfConflictKL(null);
                block.setMinK(0);
                block.setMinFfK(0);
            }
        }
    }

}
