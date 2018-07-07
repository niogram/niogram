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
import net.ognyanov.niogram.util.IntLLStringSet;

class FirstKVisitor
    extends InterruptableGrammarVisitor
{
    private boolean             modified  = false;
    private IntLLStringSetCache cache     = null;
    private boolean             debug     = false;
    private boolean             moreDebug = false;

    @Override
    public void visitGrammar(Grammar grammar)
    {
        if (debug) {
            System.out.println("Start FirstK");
        }
        cache = new IntLLStringSetCache(grammar);
        do {
            modified = false;
            super.visitGrammar(grammar);
        } while (modified);
        IntLLStringSet grammarFirstK = grammar.getFirstK();
        grammarFirstK.clear();
        for (NonterminalRule rule : grammar.getNonterminalRules()) {
            grammarFirstK.addAll(rule.getFirstK());
        }
        cache.clear();
        if (debug) {
            System.out.println("End   FirstK");
        }
    }

    @Override
    public void visitNonterminalRule(NonterminalRule rule)
    {
        super.visitNonterminalRule(rule);
        IntLLStringSet ruleFirstK = rule.getFirstK();
        IntLLStringSet newFirstK = cache.get();
        for (Alternative alternative : rule.getAlternatives()) {
            newFirstK.addAll(alternative.getFirstK());
        }
        if (!newFirstK.equals(ruleFirstK)) {
            printDebug(rule, newFirstK);
            cache.put(ruleFirstK);
            rule.setFirstK(newFirstK);
            modified = true;
        }
        else {
            cache.put(newFirstK);
        }
    }

    @Override
    public void visitBlock(Block block)
    {
        super.visitBlock(block);
        List<Alternative> alternatives = block.getAlternatives();
        IntLLStringSet blockFirstK = block.getFirstK();
        IntLLStringSet newFirstK = cache.get();
        for (Alternative alternative : alternatives) {
            newFirstK.addAll(alternative.getFirstK());
        }
        if (!newFirstK.isEmpty()) {
            if (block.isRepeatable()) {
                if (blockFirstK.isEmpty()) {
                    printDebug(block, newFirstK);
                    blockFirstK.addAll(newFirstK);
                    modified = true;
                }
                else {
                    /**
                     * c+ is equivalent to:
                     * b : c b | c;
                     * The block holds b and c
                     * is computed above on the
                     * fly into newFirstK.
                     */
                    IntLLStringSet repeatedFirstK = cache.get();
                    repeatedFirstK.addAll(newFirstK); // c
                    repeatedFirstK.append(blockFirstK); // b
                    repeatedFirstK.addAll(newFirstK); // c
                    if (!repeatedFirstK.equals(blockFirstK)) {
                        printDebug(block, repeatedFirstK);
                        cache.put(blockFirstK);
                        block.setFirstK(repeatedFirstK);
                        blockFirstK = repeatedFirstK;
                        modified = true;
                    }
                }
            }
            else {
                if (!newFirstK.equals(blockFirstK)) {
                    printDebug(block, newFirstK);
                    cache.put(blockFirstK);
                    block.setFirstK(newFirstK);
                    modified = true;
                }
                else {
                    cache.put(newFirstK);
                }
            }
        }
    }

    @Override
    public void visitAlternative(Alternative alternative)
    {
        super.visitAlternative(alternative);
        IntLLStringSet altFirstk = alternative.getFirstK();
        List<Term> terms = alternative.getTerms();
        if (terms.isEmpty()) {
            if (!altFirstk.containsEmpty()) {
                altFirstk.addEmpty();
                modified = true;
            }
        }
        else {
            boolean allKnown = true;
            for (Term term : terms) {
                if (term.getFirstK().isEmpty()) {
                    allKnown = false;
                    break;
                }
            }
            if (allKnown) {
                IntLLStringSet newFirstK = cache.get();
                for (Term term : terms) {
                    IntLLStringSet termFirstk = term.getFirstK();
                    newFirstK.append(termFirstk);
                }
                if (!newFirstK.equals(altFirstk)) {
                    printDebug(alternative, newFirstK);
                    cache.put(altFirstk);
                    alternative.setFirstK(newFirstK);
                    modified = true;
                }
                else {
                    cache.put(newFirstK);
                }
            }
        }
    }

    private void printDebug(GrammarNode node, IntLLStringSet newFirstK)
    {
        if (debug) {
            IntLLStringSet nodeFirstK = node.getFirstK();
            boolean error = !newFirstK.containsAll(nodeFirstK);
            String header = "INFO : ";
            if (error) {
                header = "ERROR: ";
            }
            if (error || moreDebug) {
                System.out
                    .println(header + node.getDisplayName() + " : " +
                            nodeFirstK + " => " + newFirstK);
            }
        }
    }
}
