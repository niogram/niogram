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
import net.ognyanov.niogram.util.BitSetLLString;

class FirstKLVisitor
    extends InterruptableGrammarVisitor
{
    private boolean             modified  = false;
    private BitSetLLStringCache cache     = null;
    private boolean             debug     = false;
    private boolean             moreDebug = false;

    @Override
    public void visitGrammar(Grammar grammar)
    {
        if (debug) {
            System.out.println("Start FirstKL");
        }
        cache = new BitSetLLStringCache(grammar);
        do {
            modified = false;
            super.visitGrammar(grammar);
        } while (modified);
        BitSetLLString grammarFirstKL = grammar.getFirstKL();
        grammarFirstKL.clear();
        for (NonterminalRule rule : grammar.getNonterminalRules()) {
            grammarFirstKL.addAll(rule.getFirstKL());
        }
        cache.clear();
        if (debug) {
            System.out.println("End   FirstKL");
        }
    }

    @Override
    public void visitNonterminalRule(NonterminalRule rule)
    {
        super.visitNonterminalRule(rule);
        BitSetLLString ruleFirstKL = rule.getFirstKL();
        BitSetLLString newFirstKL = cache.get();
        for (Alternative alternative : rule.getAlternatives()) {
            newFirstKL.addAll(alternative.getFirstKL());
        }
        if (!newFirstKL.equals(ruleFirstKL)) {
            printDebug(rule, newFirstKL);
            cache.put(ruleFirstKL);
            rule.setFirstKL(newFirstKL);
            modified = true;
        }
        else {
            cache.put(newFirstKL);
        }
    }

    @Override
    public void visitBlock(Block block)
    {
        super.visitBlock(block);
        List<Alternative> alternatives = block.getAlternatives();
        BitSetLLString blockFirstKL = block.getFirstKL();
        BitSetLLString newFirstKL = cache.get();
        for (Alternative alternative : alternatives) {
            newFirstKL.addAll(alternative.getFirstKL());
        }
        if (!newFirstKL.isEmpty()) {
            if (block.isRepeatable()) {
                if (blockFirstKL.isEmpty()) {
                    printDebug(block, newFirstKL);
                    blockFirstKL.addAll(newFirstKL);
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
                    BitSetLLString repeatedFirstKL = cache.get();
                    repeatedFirstKL.addAll(newFirstKL); // c
                    repeatedFirstKL.append(blockFirstKL); // b
                    repeatedFirstKL.addAll(newFirstKL); // c
                    if (!repeatedFirstKL.equals(blockFirstKL)) {
                        printDebug(block, repeatedFirstKL);
                        cache.put(blockFirstKL);
                        block.setFirstKL(repeatedFirstKL);
                        blockFirstKL = repeatedFirstKL;
                        modified = true;
                    }
                }
            }
            else {
                if (!newFirstKL.equals(blockFirstKL)) {
                    printDebug(block, newFirstKL);
                    cache.put(blockFirstKL);
                    block.setFirstKL(newFirstKL);
                    modified = true;
                }
                else {
                    cache.put(newFirstKL);
                }
            }
        }
    }

    @Override
    public void visitAlternative(Alternative alternative)
    {
        super.visitAlternative(alternative);
        BitSetLLString altFirstKL = alternative.getFirstKL();
        List<Term> terms = alternative.getTerms();
        if (terms.size() == 0) {
            if (!altFirstKL.containsEmpty()) {
                altFirstKL.addEmpty();
                modified = true;
            }
        }
        else {
            boolean allKnown = true;
            for (Term term : terms) {
                if (term.getFirstKL().isEmpty()) {
                    allKnown = false;
                    break;
                }
            }
            if (allKnown) {
                BitSetLLString newFirstKL = cache.get();
                for (Term term : terms) {
                    BitSetLLString termFirstkL = term.getFirstKL();
                    newFirstKL.append(termFirstkL);
                }
                if (!newFirstKL.equals(altFirstKL)) {
                    printDebug(alternative, newFirstKL);
                    cache.put(altFirstKL);
                    alternative.setFirstKL(newFirstKL);
                    modified = true;
                }
                else {
                    cache.put(newFirstKL);
                }
            }
        }
    }

    private void printDebug(GrammarNode node, BitSetLLString newFirstKL)
    {
        if (debug) {
            BitSetLLString nodeFirstKL = node.getFirstKL();
            boolean error = !newFirstKL.containsAll(nodeFirstKL);
            String header = "INFO : ";
            if (error) {
                header = "ERROR: ";
            }
            if (error || moreDebug) {
                System.out
                    .println(header + node.getDisplayName() + " : " +
                            nodeFirstKL + " => " + newFirstKL);
            }
        }
    }
}
