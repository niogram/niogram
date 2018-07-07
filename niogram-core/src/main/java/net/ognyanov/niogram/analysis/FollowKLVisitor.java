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
import net.ognyanov.niogram.util.BitSetLLString;

class FollowKLVisitor
    extends InterruptableGrammarVisitor
{
    private BitSetLLStringCache cache     = null;
    private boolean             modified  = false;
    private int                 pass      = 0;
    private boolean             debug     = false;
    private boolean             moreDebug = false;

    @Override
    public void visitGrammar(Grammar grammar)
    {
        if (debug) {
            System.out.println("Start FollowKL");
        }
        cache = new BitSetLLStringCache(grammar);
        pass = 1;
        super.visitGrammar(grammar);
        pass = 2;
        do {
            modified = false;
            super.visitGrammar(grammar);
        } while (modified);
        pass = 3;
        super.visitGrammar(grammar);
        BitSetLLString grammarFollowKL = grammar.getFollowKL();
        grammarFollowKL.clear();
        for (NonterminalRule rule : grammar.getNonterminalRules()) {
            grammarFollowKL.addAll(rule.getFollowKL());
        }
        if (debug) {
            System.out.println("End   FollowKL");
        }
    }

    @Override
    public void visitNonterminalRule(NonterminalRule rule)
    {
        super.visitNonterminalRule(rule);
        if (pass == 2) {
            BitSetLLString ruleFollowKL = rule.getFollowKL();
            List<Nonterminal> references = rule.getReferences();
            BitSetLLString newFollowKL = cache.get();
            newFollowKL.addAll(ruleFollowKL);
            for (Nonterminal reference : references) {
                GrammarNode context = reference.getParent().getParent();
                BitSetLLString contextFollowKL = context.getFollowKL();
                BitSetLLString referenceSuffixFirstKL =
                    reference.getSuffixFirstKL();
                BitSetLLString referenceFollowK = cache.get();
                referenceFollowK.addAll(referenceSuffixFirstKL);
                referenceFollowK.append(contextFollowKL);
                newFollowKL.addAll(referenceFollowK);
                cache.put(referenceFollowK);
            }
            if (!newFollowKL.equals(ruleFollowKL)) {
                printDebug(rule, newFollowKL);
                cache.put(rule.getFollowKL());
                rule.setFollowKL(newFollowKL);
                modified = true;
            }
            else {
                cache.put(newFollowKL);
            }
        }
    }

    @Override
    public void visitBlock(Block block)
    {
        super.visitBlock(block);
        if (pass == 2) {
            BitSetLLString blockFollowKL = block.getFollowKL();
            GrammarNode context = block.getParent().getParent();
            BitSetLLString contextFollowKL = context.getFollowKL();
            BitSetLLString blockSuffixFirstK =
                block.getSuffixFirstKL();
            BitSetLLString newFollowKL = cache.get();
            newFollowKL.addAll(blockSuffixFirstK);
            newFollowKL.append(contextFollowKL);
            newFollowKL.addAll(blockFollowKL);
            if (!newFollowKL.equals(blockFollowKL)) {
                printDebug(block, newFollowKL);
                cache.put(block.getFollowKL());
                block.setFollowKL(newFollowKL);
                modified = true;
            }
            else {
                cache.put(newFollowKL);
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
                BitSetLLString termSuffixFirst = term.getSuffixFirstKL();
                termSuffixFirst.clear();
                if (termsIt.hasNext()) {
                    int position = termsIt.nextIndex();
                    ListIterator<Term> suffix = terms.listIterator(position);
                    while (suffix.hasNext()) {
                        Term other = suffix.next();
                        termSuffixFirst.append(other.getFirstKL());
                    }
                    if (term instanceof Terminal) {
                        ((Terminal) term).getRule().getFollowKL()
                            .addAll(termSuffixFirst);
                    }
                    else if (term instanceof Nonterminal) {
                        ((Nonterminal) term).getRule().getFollowKL()
                            .addAll(termSuffixFirst);
                    }
                    else {
                        // must be block
                        ((Block) term).getFollowKL().addAll(termSuffixFirst);
                    }
                }
            }
        }
        else if (pass == 3) {
            BitSetLLString alternativeFollowKL = alternative.getFollowKL();
            BitSetLLString parentFollowKL =
                alternative.getParent().getFollowKL();
            alternativeFollowKL.clear();
            alternativeFollowKL.addAll(parentFollowKL);
            for (Term term : terms) {
                if (!(term instanceof Block)) {
                    BitSetLLString termFollowKL = term.getFollowKL();
                    termFollowKL.clear();
                    termFollowKL.addAll(term.getSuffixFirstKL());
                    termFollowKL.append(parentFollowKL);
                    termFollowKL.addAll(parentFollowKL);
                }
            }
        }
    }

    private void printDebug(GrammarNode node, BitSetLLString newFollowKL)
    {
        if (debug) {
            BitSetLLString nodeFollowKL = node.getFollowKL();
            boolean error = !newFollowKL.containsAll(nodeFollowKL);
            String header = "INFO : ";
            if (error) {
                header = "ERROR: ";
            }
            if (error || moreDebug) {
                System.out
                    .println(header + node.getDisplayName() + " : " +
                            nodeFollowKL + " => " + newFollowKL);
            }
        }
    }
}
