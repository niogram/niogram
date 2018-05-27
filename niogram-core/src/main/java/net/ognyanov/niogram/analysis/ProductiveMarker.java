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
import net.ognyanov.niogram.ast.GrammarVisitor;
import net.ognyanov.niogram.ast.NonterminalRule;
import net.ognyanov.niogram.ast.Term;

class ProductiveMarker
    extends GrammarVisitor
{
    private boolean modified = false;

    @Override
    public void visitGrammar(Grammar grammar)
    {
        do {
            modified = false;
            super.visitGrammar(grammar);
        } while (modified);

        List<NonterminalRule> nonproducvive = grammar.getNonProductive();
        for (NonterminalRule rule : grammar.getNonterminalRules()) {
            if (!rule.isProductive()) {
                nonproducvive.add(rule);
            }
        }
    }

    @Override
    public void visitNonterminalRule(NonterminalRule rule)
    {
        super.visitNonterminalRule(rule);
        if (!rule.isProductive()) {
            boolean isProductive = false;
            for (Alternative alternative : rule.getAlternatives()) {
                if (alternative.isProductive()) {
                    isProductive = true;
                    break;
                }
            }
            if (isProductive) {
                rule.setProductive(true);
                modified = true;
            }
        }
    }

    @Override
    public void visitBlock(Block block)
    {
        super.visitBlock(block);
        if (!block.isProductive()) {
            boolean isProductive = false;
            for (Alternative alternative : block.getAlternatives()) {
                if (alternative.isProductive()) {
                    isProductive = true;
                    break;
                }
            }
            if (isProductive) {
                block.setProductive(true);
                modified = true;
            }
        }
    }

    @Override
    public void visitAlternative(Alternative alternative)
    {
        super.visitAlternative(alternative);
        if (!alternative.isProductive()) {
            boolean isProductive = true;
            for (Term term : alternative.getTerms()) {
                if (!term.isProductive()) {
                    isProductive = false;
                    break;
                }
            }
            if (isProductive) {
                alternative.setProductive(true);
                modified = true;
            }
        }
    }
}
