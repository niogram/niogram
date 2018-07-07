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
import net.ognyanov.niogram.util.IntLLStringSet;

class FollowKVisitor
    extends InterruptableGrammarVisitor
{
    private IntLLStringSetCache cache     = null;
    private boolean             modified  = false;
    private int                 pass      = 0;
    private boolean             debug     = false;
    private boolean             moreDebug = false;

    @Override
    public void visitGrammar(Grammar grammar)
    {
        if (debug) {
            System.out.println("Start FollowK");
        }
        cache = new IntLLStringSetCache(grammar);
        pass = 1;
        super.visitGrammar(grammar);
        pass = 2;
        do {
            modified = false;
            super.visitGrammar(grammar);
        } while (modified);
        pass = 3;
        super.visitGrammar(grammar);
        IntLLStringSet grammarFollowK = grammar.getFollowK();
        grammarFollowK.clear();
        for (NonterminalRule rule : grammar.getNonterminalRules()) {
            grammarFollowK.addAll(rule.getFollowK());
        }
        if (debug) {
            System.out.println("End   FollowK");
        }
    }

    @Override
    public void visitNonterminalRule(NonterminalRule rule)
    {
        super.visitNonterminalRule(rule);
        if (pass == 2) {
            IntLLStringSet ruleFollowK = rule.getFollowK();
            List<Nonterminal> references = rule.getReferences();
            IntLLStringSet newFollowK = cache.get();
            newFollowK.addAll(ruleFollowK);
            for (Nonterminal reference : references) {
                GrammarNode context = reference.getParent().getParent();
                IntLLStringSet contextFollowK = context.getFollowK();
                IntLLStringSet referenceSuffixFirstK =
                    reference.getSuffixFirstK();
                IntLLStringSet referenceFollowK = cache.get();
                referenceFollowK.addAll(referenceSuffixFirstK);
                referenceFollowK.append(contextFollowK);
                newFollowK.addAll(referenceFollowK);
                cache.put(referenceFollowK);
            }
            if (!newFollowK.equals(ruleFollowK)) {
                printDebug(rule, newFollowK);
                cache.put(rule.getFollowK());
                rule.setFollowK(newFollowK);
                modified = true;
            }
            else {
                cache.put(newFollowK);
            }
        }
    }

    @Override
    public void visitBlock(Block block)
    {
        super.visitBlock(block);
        if (pass == 2) {
            IntLLStringSet blockFollowK = block.getFollowK();
            GrammarNode context = block.getParent().getParent();
            IntLLStringSet contextFollowK = context.getFollowK();
            IntLLStringSet blockSuffixFirstK =
                block.getSuffixFirstK();
            IntLLStringSet newFollowK = cache.get();
            newFollowK.addAll(blockSuffixFirstK);
            newFollowK.append(contextFollowK);
            newFollowK.addAll(blockFollowK);
            if (!newFollowK.equals(blockFollowK)) {
                printDebug(block, newFollowK);
                cache.put(block.getFollowK());
                block.setFollowK(newFollowK);
                modified = true;
            }
            else {
                cache.put(newFollowK);
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
                IntLLStringSet termSuffixFirst = term.getSuffixFirstK();
                termSuffixFirst.clear();
                if (termsIt.hasNext()) {
                    int position = termsIt.nextIndex();
                    ListIterator<Term> suffix = terms.listIterator(position);
                    while (suffix.hasNext()) {
                        Term other = suffix.next();
                        termSuffixFirst.append(other.getFirstK());
                    }
                    if (term instanceof Terminal) {
                        ((Terminal) term).getRule().getFollowK()
                            .addAll(termSuffixFirst);
                    }
                    else if (term instanceof Nonterminal) {
                        ((Nonterminal) term).getRule().getFollowK()
                            .addAll(termSuffixFirst);
                    }
                    else {
                        // must be block
                        ((Block) term).getFollowK().addAll(termSuffixFirst);
                    }
                }
            }
        }
        else if (pass == 3) {
            IntLLStringSet alternativeFollowK = alternative.getFollowK();
            IntLLStringSet parentFollowK =
                alternative.getParent().getFollowK();
            alternativeFollowK.clear();
            alternativeFollowK.addAll(parentFollowK);
            for (Term term : terms) {
                if (!(term instanceof Block)) {
                    IntLLStringSet termFollowK = term.getFollowK();
                    termFollowK.clear();
                    termFollowK.addAll(term.getSuffixFirstK());
                    termFollowK.append(parentFollowK);
                    termFollowK.addAll(parentFollowK);
                }
            }
        }
    }

    private void printDebug(GrammarNode node, IntLLStringSet newFollowK)
    {
        if (debug) {
            IntLLStringSet nodeFollowK = node.getFollowK();
            boolean error = !newFollowK.containsAll(nodeFollowK);
            String header = "INFO : ";
            if (error) {
                header = "ERROR: ";
            }
            if (error || moreDebug) {
                System.out
                    .println(header + node.getDisplayName() + " : " +
                            nodeFollowK + " => " + newFollowK);
            }
        }
    }
}
