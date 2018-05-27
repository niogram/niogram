/*
 * Copyright (c) 2018 by Nikolay Ognyanov. All rights reserved.
 * 
 * Use of this file is governed by the licensing conditions   
 * in the LICENSE.txt file in the root folder of the project.
 */
package net.ognyanov.niogram.parser.antlr4;

import java.util.List;

import net.ognyanov.niogram.ast.Alternative;
import net.ognyanov.niogram.ast.Block;
import net.ognyanov.niogram.ast.Grammar;
import net.ognyanov.niogram.ast.GrammarVisitor;
import net.ognyanov.niogram.ast.NonterminalRule;
import net.ognyanov.niogram.ast.Term;

class NamingVisitor
    extends GrammarVisitor
{
    private List<Block> blocks = null;
    private String      prefix = "";

    public void visitGrammar(Grammar grammar)
    {
        blocks = grammar.getBlocks();
        for (NonterminalRule rule : grammar.getNonterminalRules()) {
            visitRule(rule);
        }
    }

    public void visitRule(NonterminalRule rule)
    {
        String oldPrefix = prefix;
        String name = rule.getDisplayName();
        prefix = rule.getDisplayName();
        int i = 1;
        for (Alternative alternative : rule.getAlternatives()) {
            prefix = name + "/a" + i++;
            alternative.setDisplayName(prefix);
            visitAlternative(alternative);
        }
        prefix = oldPrefix;
    }

    public void visitBlock(Block block)
    {
        String oldPrefix = prefix;
        int i = 1;
        for (Alternative alternative : block.getAlternatives()) {
            prefix = oldPrefix + "/a" + i++;
            alternative.setDisplayName(prefix);
            visitAlternative(alternative);
        }
        prefix = oldPrefix;
    }

    public void visitAlternative(Alternative alternative)
    {
        String oldPrefix = prefix;
        int i = 1;
        for (Term term : alternative.getTerms()) {
            if (term instanceof Block) {
                prefix = oldPrefix + ".b" + i++;
                term.setDisplayName(prefix);
                if (!blocks.contains((Block) term))
                    blocks.add((Block) term);
                visitBlock((Block) term);
            }
        }
        prefix = oldPrefix;
    }
}
