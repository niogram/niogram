/*
 * Copyright (c) 2018 by Nikolay Ognyanov. All rights reserved.
 * 
 * Use of this file is governed by the licensing conditions   
 * in the LICENSE.txt file in the root folder of the project.
 */
package net.ognyanov.niogram.analysis;

import java.util.List;

import net.ognyanov.niogram.ast.Alternative;
import net.ognyanov.niogram.ast.Block;
import net.ognyanov.niogram.ast.Grammar;
import net.ognyanov.niogram.ast.GrammarNode;
import net.ognyanov.niogram.ast.NonterminalRule;
import net.ognyanov.niogram.ast.Term;
import net.ognyanov.niogram.util.BiasedBitSet;

class FirstVisitor
    extends InterruptableGrammarVisitor
{
    private boolean     modified  = false;
    private BitSetCache cache     = null;
    private boolean     debug     = false;
    private boolean     moreDebug = false;

    @Override
    public void visitGrammar(Grammar grammar)
    {
        if (debug) {
            System.out.println("Start First");
        }
        cache = new BitSetCache(grammar);
        do {
            modified = false;
            super.visitGrammar(grammar);
        } while (modified);
        BiasedBitSet grammarFirst = grammar.getFirst();
        grammarFirst.clear();   
        for (NonterminalRule rule : grammar.getNonterminalRules()) {
            grammarFirst.or(rule.getFirst());
        }
        cache.clear();
        if (debug) {
            System.out.println("End   First");
        }
    }

    @Override
    public void visitNonterminalRule(NonterminalRule rule)
    {
        super.visitNonterminalRule(rule);
        BiasedBitSet newFirst = cache.get();
        for (Alternative alternative : rule.getAlternatives()) {
            newFirst.or(alternative.getFirst());
        }
        if (!newFirst.equals(rule.getFirst())) {
            printDebug(rule, newFirst);
            cache.put(rule.getFirst());
            rule.setFirst(newFirst);
            modified = true;
        }
        else {
            cache.put(newFirst);
        }
    }

    @Override
    public void visitAlternative(Alternative alternative)
    {
        super.visitAlternative(alternative);
        List<Term> terms = alternative.getTerms();
        if (!terms.isEmpty()) {
            BiasedBitSet newFirst = cache.get();
            for (Term term : terms) {
                newFirst.or(term.getFirst());
                if (!term.isNullable()) {
                    break;
                }
            }
            if (!newFirst.equals(alternative.getFirst())) {
                printDebug(alternative, newFirst);
                cache.put(alternative.getFirst());
                alternative.setFirst(newFirst);
                modified = true;
            }
            else {
                cache.put(newFirst);
            }
        }
    }

    @Override
    public void visitBlock(Block block)
    {
        super.visitBlock(block);
        BiasedBitSet newFirst = cache.get();
        for (Alternative alternative : block.getAlternatives()) {
            newFirst.or(alternative.getFirst());
        }
        if (!newFirst.equals(block.getFirst())) {
            printDebug(block, newFirst);
            cache.put(block.getFirst());
            block.setFirst(newFirst);
            modified = true;
        }
        else {
            cache.put(newFirst);
        }
    }

    private void printDebug(GrammarNode node, BiasedBitSet newFirst)
    {
        if (debug) {
            BiasedBitSet nodeFirst = node.getFirst();
            boolean error = !newFirst.contains(nodeFirst);
            if (error) {
                System.out
                    .println("ERROR: " + node.getDisplayName() + " : " +
                            nodeFirst + " => " + newFirst);
            }
            else if (moreDebug) {
                System.out
                    .println("INFO : " + node.getDisplayName() + " : " +
                            nodeFirst + " => " + newFirst);
            }
        }
    }
}
