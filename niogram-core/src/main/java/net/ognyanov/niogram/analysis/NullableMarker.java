/*
 * Copyright (c) 2018 by Nikolay Ognyanov. All rights reserved.
 * 
 * Use of this file is governed by the licensing conditions   
 * in the LICENSE.txt file in the root folder of the project.
 */
package net.ognyanov.niogram.analysis;

import java.util.ListIterator;

import net.ognyanov.niogram.ast.Alternative;
import net.ognyanov.niogram.ast.Block;
import net.ognyanov.niogram.ast.Grammar;
import net.ognyanov.niogram.ast.GrammarVisitor;
import net.ognyanov.niogram.ast.NonterminalRule;
import net.ognyanov.niogram.ast.Term;

class NullableMarker
    extends GrammarVisitor
{
    private boolean modified  = false;
    private boolean firstPass = false;

    @Override
    public void visitGrammar(Grammar grammar)
    {
        firstPass = true;
        do {
            modified = false;
            super.visitGrammar(grammar);
        } while (modified);
        firstPass = false;
        super.visitGrammar(grammar);
    }

    @Override
    public void visitNonterminalRule(NonterminalRule rule)
    {
        super.visitNonterminalRule(rule);
        if (firstPass) {
            if (!rule.isNullable()) {
                boolean isNullable = false;
                for (Alternative alternative : rule.getAlternatives()) {
                    if (alternative.isNullable()) {
                        isNullable = true;
                        break;
                    }
                }
                if (isNullable) {
                    rule.setNullable(true);
                    modified = true;
                }
            }
        }
    }

    @Override
    public void visitBlock(Block block)
    {
        super.visitBlock(block);
        if (firstPass) {
            if (!block.isNullable()) {
                boolean isNullable = false;
                for (Alternative alternative : block.getAlternatives()) {
                    if (alternative.isNullable()) {
                        isNullable = true;
                        break;
                    }
                }
                if (isNullable) {
                    block.setNullable(true);
                    modified = true;
                }
            }
        }
    }

    @Override
    public void visitAlternative(Alternative alternative)
    {
        super.visitAlternative(alternative);
        if (firstPass) {
            if (!alternative.isNullable()) {
                boolean isNullable = true;
                // empty alternatives fall through
                for (Term term : alternative.getTerms()) {
                    if (!term.isNullable()) {
                        isNullable = false;
                        break;
                    }
                }
                if (isNullable) {
                    alternative.setNullable(true);
                    modified = true;
                }
            }
        }
        else {
            boolean prefixNullable = true;
            boolean suffixNullable = true;
            ListIterator<Term> terms = alternative.getTerms().listIterator();
            while (terms.hasNext()) {
                Term term = terms.next();
                term.setPrefixNullable(prefixNullable);
                if (!term.isNullable()) {
                    prefixNullable = false;
                }
            }
            while (terms.hasPrevious()) {
                Term term = terms.previous();
                term.setSuffixNullable(suffixNullable);
                if (!term.isNullable()) {
                    suffixNullable = false;
                }
            }
        }
    }
}
