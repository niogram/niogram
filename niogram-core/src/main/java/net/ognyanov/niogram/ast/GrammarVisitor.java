/*
 * Copyright (c) 2018 by Nikolay Ognyanov. All rights reserved.
 *
 * Use of this file is governed by the licensing conditions
 * in the LICENSE.txt file in the root folder of the project.
 */
package net.ognyanov.niogram.ast;

import net.ognyanov.niogram.util.NioGramException;

/**
 * A visitor for the AST.
 * 
 * @author Nikolay Ognyanov
 */
public abstract class GrammarVisitor
{
    public void visitGrammar(Grammar grammar)
    {
        if (grammar == null) {
            throw new IllegalArgumentException("null grammar");
        }
        preVisit(grammar);
        for (NonterminalRule rule : grammar.getNonterminalRules()) {
            visitNonterminalRule(rule);
        }
        for (TerminalRule rule : grammar.getTerminalRules()) {
            visitTerminalRule(rule);
        }
        postVisit(grammar);
    }

    public void visitNonterminalRule(NonterminalRule rule)
    {
        preVisit(rule);
        for (Alternative alternative : rule.getAlternatives()) {
            visitAlternative(alternative);
        }
        postVisit(rule);
    }

    public void visitAlternative(Alternative alternative)
    {
        preVisit(alternative);
        for (Term term : alternative.getTerms()) {
            if (term instanceof Terminal) {
                visitTerminal((Terminal) term);
            }
            else if (term instanceof Nonterminal) {
                visitNonterminal((Nonterminal) term);
            }
            else if (term instanceof Block) {
                visitBlock((Block) term);
            }
        }
        postVisit(alternative);
    }

    public void visitTerminal(Terminal terminal)
    {
        preVisit(terminal);
        postVisit(terminal);
    }

    public void visitNonterminal(Nonterminal nonterminal)
    {
        preVisit(nonterminal);
        postVisit(nonterminal);
    }

    public void visitBlock(Block block)
    {
        preVisit(block);
        for (Alternative alternative : block.getAlternatives()) {
            visitAlternative(alternative);
        }
        postVisit(block);
    }

    public void visitTerminalRule(TerminalRule rule)
    {
        preVisit(rule);
        postVisit(rule);
    }

    public void visitNode(GrammarNode node)
    {
        if (node instanceof Grammar) {
            visitGrammar(((Grammar) node));
        }
        else if (node instanceof NonterminalRule) {
            visitNonterminalRule(((NonterminalRule) node));
        }
        else if (node instanceof TerminalRule) {
            visitTerminalRule((TerminalRule) node);
        }
        else if (node instanceof Alternative) {
            visitAlternative(((Alternative) node));
        }
        else if (node instanceof Block) {
            visitBlock(((Block) node));
        }
        else if (node instanceof Nonterminal) {
            visitNonterminal(((Nonterminal) node));
        }
        else if (node instanceof Terminal) {
            visitTerminal(((Terminal) node));
        }
        else {
            throw new NioGramException("Unknown node type "
                    + node.getClass().getName());
        }
    }

    public void preVisit(GrammarNode grammarNode)
    {
    }

    public void postVisit(GrammarNode grammarNode)
    {
    }
}
