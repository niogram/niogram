/*
 * Copyright (c) 2018 by Nikolay Ognyanov. All rights reserved.
 * 
 * Use of this file is governed by the licensing conditions   
 * in the LICENSE.txt file in the root folder of the project.
 */
package net.ognyanov.niogram.ast;

import java.util.List;
import java.util.ListIterator;

import net.ognyanov.niogram.util.DotStringBuilder;

class RailroadDrawer
{
    private static final String HEADER        = " {\n" +
            "rankdir=LR;\n" +
            "splines=ortho;\n" +
            "concentrate=true;\n" +
            "center=true;\n" +
            "node [color=black shape=box style=\"solid\"];\n";

    private DotStringBuilder    stringBuilder = new DotStringBuilder();

    public String draw(Grammar grammar)
    {
        stringBuilder.append("digraph ").append(grammar.getDisplayName())
            .append(HEADER);
        List<NonterminalRule> rules = grammar.getNonterminalRules();
        ListIterator<NonterminalRule> it = rules.listIterator(rules.size());
        while (it.hasPrevious()) {
            NonterminalRule rule = it.previous();
            renderRule(rule);
        }
        stringBuilder.append("}\n");
        return stringBuilder.toString();
    }

    private void renderRule(NonterminalRule rule)
    {
        boolean singleAlt = rule.getAlternatives().size() == 1;
        DummyNode start = new DummyNode();
        DummyNode end = new DummyNode();
        DummyNode exit = new DummyNode();
        GrammarNode left, right;

        if (singleAlt) {
            define(rule);
            define(end);
            left = rule;
            right = end;
        }
        else {
            define(rule, start, end, exit);
            connect(rule, start);
            connect(end, exit);
            left = start;
            right = end;
        }
        for (Alternative alternative : rule.getAlternatives()) {
            renderAlternative(alternative, left, right);
        }
    }

    private void renderAlternative(Alternative alternative, GrammarNode start,
                                   GrammarNode end)
    {
        List<Term> terms = alternative.getTerms();
        if (terms.isEmpty()) {
            connect(start, end);
            return;
        }
        GrammarNode previous = start;
        for (Term term : terms) {
            if (term instanceof Block) {
                DummyNode blockStart = new DummyNode();
                DummyNode blockEnd = new DummyNode();
                define(blockStart);
                define(blockEnd);
                connect(previous, blockStart);
                if (((Block) term).isRepeatable()) {
                    connect(blockEnd, blockStart);
                }
                previous = blockEnd;
                renderBlock((Block) term, blockStart, blockEnd);
            }
            else {
                define(term);
                connect(previous, term);
                previous = term;
            }
        }
        connect(previous, end);
    }

    private void renderBlock(Block block, GrammarNode blockStart,
                             GrammarNode blockEnd)
    {
        for (Alternative alternative : block.getAlternatives()) {
            renderAlternative(alternative, blockStart, blockEnd);
        }
    }

    private void define(GrammarNode... nodes)
    {
        for (GrammarNode node : nodes) {
            stringBuilder.append(node.getId()).append(" [label=\"");
            if (node instanceof Grammar || node instanceof NonterminalRule) {
                stringBuilder.append(node.getDisplayName());
                stringBuilder.append("\" shape=plaintext];\n");
            }
            else if (node instanceof Nonterminal) {
                stringBuilder.append(node.getDisplayName());
                stringBuilder.append("\"];\n");
            }
            else if (node instanceof Terminal) {
                stringBuilder.append(node.getDisplayName());
                stringBuilder.append("\" style=rounded];\n");
            }
            else if (node instanceof Terminal) {
                stringBuilder.append(node.getDisplayName());
                stringBuilder.append("\" style=rounded];\n");
            }
            else {
                stringBuilder.append("\" width=0 height=0.3];\n");
            }
        }
    }

    private void connect(GrammarNode... nodes)
    {
        boolean first = true;
        for (GrammarNode node : nodes) {
            if (first) {
                first = false;
            }
            else {
                stringBuilder.append("->");
            }
            stringBuilder.append(node.getId());
        }
        stringBuilder.append(";\n");
    }
}
