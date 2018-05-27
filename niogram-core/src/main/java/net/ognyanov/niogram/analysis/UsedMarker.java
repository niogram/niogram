/*
 * Copyright (c) 2018 by Nikolay Ognyanov. All rights reserved.
 * 
 * Use of this file is governed by the licensing conditions   
 * in the LICENSE.txt file in the root folder of the project.
 */
package net.ognyanov.niogram.analysis;

import java.util.List;

import net.ognyanov.niogram.ast.Grammar;
import net.ognyanov.niogram.ast.GrammarVisitor;
import net.ognyanov.niogram.ast.Nonterminal;
import net.ognyanov.niogram.ast.NonterminalRule;
import net.ognyanov.niogram.ast.Terminal;

class UsedMarker
    extends GrammarVisitor
{
    @Override
    public void visitGrammar(Grammar grammar)
    {
        super.visitGrammar(grammar);
        List<NonterminalRule> unused = grammar.getUnused();
        for (NonterminalRule rule : grammar.getNonterminalRules()) {
            if (!rule.isUsed()) {
                unused.add(rule);
            }
        }
    }

    @Override
    public void visitTerminal(Terminal terminal)
    {
        terminal.getRule().setUsed(true);
    }

    @Override
    public void visitNonterminal(Nonterminal nonterminal)
    {
        nonterminal.getRule().setUsed(true);
    }
}
