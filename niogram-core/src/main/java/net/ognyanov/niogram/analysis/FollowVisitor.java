/*
 * Copyright (c) 2018 by Nikolay Ognyanov. All rights reserved.
 * 
 * Use of this file is governed by the licensing conditions   
 * in the LICENSE.txt file in the root folder of the project.
 */
package net.ognyanov.niogram.analysis;

import java.util.List;
import java.util.ListIterator;

import net.ognyanov.niogram.ast.Alternative;
import net.ognyanov.niogram.ast.Block;
import net.ognyanov.niogram.ast.Grammar;
import net.ognyanov.niogram.ast.GrammarNode;
import net.ognyanov.niogram.ast.Nonterminal;
import net.ognyanov.niogram.ast.NonterminalRule;
import net.ognyanov.niogram.ast.Term;
import net.ognyanov.niogram.ast.Terminal;
import net.ognyanov.niogram.util.BiasedBitSet;

class FollowVisitor
    extends InterruptableGrammarVisitor
{
    private BitSetCache cache     = null;
    private boolean     modified  = false;
    private int         pass      = 0;
    private boolean     debug     = false;
    private boolean     moreDebug = false;

    @Override
    public void visitGrammar(Grammar grammar)
    {
        if (debug) {
            System.out.println("Start Follow");
        }
        cache = new BitSetCache(grammar);
        pass = 1;
        super.visitGrammar(grammar);
        pass = 2;
        do {
            modified = false;
            super.visitGrammar(grammar);
        } while (modified);
        pass = 3;
        super.visitGrammar(grammar);
        BiasedBitSet grammarFollow = grammar.getFollow();
        grammarFollow.clear();
        for (NonterminalRule rule : grammar.getNonterminalRules()) {
            grammarFollow.or(rule.getFollow());
        }
        if (debug) {
            System.out.println("End   Follow");
        }
    }

    @Override
    public void visitNonterminalRule(NonterminalRule rule)
    {
        super.visitNonterminalRule(rule);
        if (pass == 2) {
            List<Nonterminal> references = rule.getReferences();
            BiasedBitSet newFollow = cache.get();
            for (Nonterminal reference : references) {
                GrammarNode context = reference.getParent().getParent();
                BiasedBitSet contextFollow = context.getFollow();
                BiasedBitSet referenceSuffixFirst =
                    reference.getSuffixFirst();
                newFollow.or(referenceSuffixFirst);
                if (reference.isSuffixNullable()) {
                    newFollow.or(contextFollow);
                }
            }
            if (!newFollow.equals(rule.getFollow())) {
                printDebug(rule, newFollow);
                cache.put(rule.getFollow());
                rule.setFollow(newFollow);
                modified = true;
            }
            else {
                cache.put(newFollow);
            }
        }
    }

    @Override
    public void visitBlock(Block block)
    {
        super.visitBlock(block);
        if (pass == 2) {
            GrammarNode context = block.getParent().getParent();
            BiasedBitSet contextFollow = context.getFollow();
            BiasedBitSet blockSuffixFirst =
                block.getSuffixFirst();
            BiasedBitSet newFollow = cache.get();
            newFollow.or(blockSuffixFirst);
            if (block.isSuffixNullable()) {
                newFollow.or(contextFollow);
            }
            if (!newFollow.equals(block.getFollow())) {
                cache.put(block.getFollow());
                block.setFollow(newFollow);
                modified = true;
            }
            else {
                cache.put(newFollow);
            }
        }
    }

    @Override
    public void visitAlternative(Alternative alternative)
    {
        super.visitAlternative(alternative);
        List<Term> terms = alternative.getTerms();
        if (pass == 1) {
            ListIterator<Term> termsIt = terms.listIterator();
            while (termsIt.hasNext()) {
                Term term = termsIt.next();
                BiasedBitSet termSuffixFirst = term.getSuffixFirst();
                termSuffixFirst.clear();
                if (termsIt.hasNext()) {
                    int position = termsIt.nextIndex();
                    ListIterator<Term> suffix = terms.listIterator(position);
                    while (suffix.hasNext()) {
                        Term other = suffix.next();
                        termSuffixFirst.or(other.getFirst());
                        if (!other.isNullable()) {
                            break;
                        }
                    }
                    if (term instanceof Terminal) {
                        ((Terminal) term).getRule().getFollow()
                            .or(termSuffixFirst);
                    }
                    else if (term instanceof Nonterminal) {
                        ((Nonterminal) term).getRule().getFollow()
                            .or(termSuffixFirst);
                    }
                    else {
                        // must be block
                        ((Block) term).getFollow().or(termSuffixFirst);
                    }
                }
            }
        }
        else if (pass == 3) {
            BiasedBitSet alternativeFollow = alternative.getFollow();
            BiasedBitSet parentFollow = alternative.getParent().getFollow();
            alternativeFollow.or(parentFollow);
            for (Term term : terms) {
                BiasedBitSet termFollow = term.getFollow();
                termFollow.clear();
                termFollow.or(term.getSuffixFirst());
                if (term.isSuffixNullable()) {
                    termFollow.or(parentFollow);
                }
            }
        }
    }

    private void printDebug(GrammarNode node, BiasedBitSet newFollow)
    {
        if (debug) {
            BiasedBitSet nodeFollow = node.getFollow();
            boolean error = !newFollow.contains(nodeFollow);
            String header = "INFO : ";
            if (error) {
                header = "ERROR: ";
            }
            if (error || moreDebug) {
                System.out
                    .println(header + node.getDisplayName() + " : " +
                            nodeFollow + " => " + newFollow);
            }
        }
    }
}
