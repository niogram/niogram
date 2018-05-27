package net.ognyanov.niogram.analysis;
/*
 * Copyright (c) 2018 by Nikolay Ognyanov. All rights reserved.
 * 
 * Use of this file is governed by the licensing conditions   
 * in the LICENSE.txt file in the root folder of the project.
 */

import net.ognyanov.niogram.ast.Block;
import net.ognyanov.niogram.ast.Grammar;
import net.ognyanov.niogram.ast.GrammarNode;
import net.ognyanov.niogram.ast.GrammarVisitor;
import net.ognyanov.niogram.ast.Nonterminal;
import net.ognyanov.niogram.ast.NonterminalRule;
import net.ognyanov.niogram.ast.Term;
import net.ognyanov.niogram.ast.Terminal;
import net.ognyanov.niogram.ast.TerminalRule;
import net.ognyanov.niogram.util.BaseInterruptable;
import net.ognyanov.niogram.util.IntLLString;
import net.ognyanov.niogram.util.IntLLStringSet;
import net.ognyanov.niogram.util.NioGramException;

/**
 * A calculator for FirstK/FollowK sets and the related conflicts data.
 *
 * @author Nikolay Ognyanov
 */
public class FirstKFollowKCalculator
    extends BaseInterruptable
    implements AttributeCalculator
{
    @Override
    public void calculate(Grammar grammar)
    {
        if (grammar == null) {
            throw new IllegalArgumentException("null argument");
        }
        if (grammar.getNonterminalRules().size() == 0) {
            grammar.setFFK(true);
            return;
        }

        grammar.clearFFK();
        grammar.setFFK(true);
        PrepareFFKVisitor prepareVisitor = new PrepareFFKVisitor();
        prepareVisitor.visitGrammar(grammar);
        FirstKVisitor firstVisitor = new FirstKVisitor();
        FollowKVisitor followVisitor = new FollowKVisitor();
        FFKConflictsVisitor conflictsVisitor = new FFKConflictsVisitor();
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
            grammar.clearFFK();
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
            this.k = grammar.getK();
            super.visitGrammar(grammar);
        }

        @Override
        public void preVisit(GrammarNode node)
        {
            if (node instanceof TerminalRule) {
                TerminalRule rule = (TerminalRule) node;
                IntLLStringSet firstK = new IntLLStringSet(k, grammar);
                IntLLString typeString = new IntLLString(k, grammar);
                typeString.add(rule.getType());
                firstK.add(typeString);
                rule.setFirstK(firstK);
            }
            else if (!(node instanceof Terminal
                    || node instanceof Nonterminal)) {
                IntLLStringSet firstK = new IntLLStringSet(k, grammar);
                node.setFirstK(firstK);
            }

            node.setFollowK(new IntLLStringSet(k, grammar));
            if (node instanceof Term) {
                Term term = (Term) node;
                term.setSuffixFirstK(new IntLLStringSet(k, grammar));
            }
            if (node instanceof NonterminalRule) {
                NonterminalRule rule = (NonterminalRule) node;
                rule.getConflictsK().clear();
                rule.setFfConflictK(null);
                rule.setMinK(0);
                rule.setMinFfK(0);
            }
            else if (node instanceof Block) {
                Block block = (Block) node;
                block.getConflictsK().clear();
                block.setFfConflictK(null);
                block.setMinK(0);
                block.setMinFfK(0);
            }
        }
    }

}
