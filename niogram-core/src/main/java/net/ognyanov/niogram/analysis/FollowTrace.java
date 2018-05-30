/*
 * Copyright (c) 2018 by Nikolay Ognyanov. All rights reserved.
 * 
 * Use of this file is governed by the licensing conditions   
 * in the LICENSE.txt file in the root folder of the project.
 */
package net.ognyanov.niogram.analysis;

import java.util.ArrayList;
import java.util.List;

import net.ognyanov.niogram.ast.Alternative;
import net.ognyanov.niogram.ast.Block;
import net.ognyanov.niogram.ast.Grammar;
import net.ognyanov.niogram.ast.GrammarNode;
import net.ognyanov.niogram.ast.Nonterminal;
import net.ognyanov.niogram.ast.NonterminalRule;
import net.ognyanov.niogram.ast.Term;
import net.ognyanov.niogram.ast.Terminal;
import net.ognyanov.niogram.util.DotStringBuilder;

/**
 * A trace for a terminal in the Follow set of a node.
 *
 * @author Nikolay Ognyanov
 */
public class FollowTrace
    extends BaseTrace
{
    private int               terminalType;
    private FollowTrace       parent   = null;
    private List<FollowTrace> children = new ArrayList<FollowTrace>();

    private FollowTrace(GrammarNode node, int terminalType,
                        boolean buildChildren)
    {
        this.node = node;
        this.terminalType = terminalType;
        if (buildChildren) {
            buildChildren();
        }

    }

    /**
     * Create a new trace. The whole trace is built by
     * the constructor, so it can be used immediately
     * after the object creation.
     * 
     * @param node the node where the trace starts
     * @param terminalType the terminal type to be traced
     */
    public FollowTrace(GrammarNode node, int terminalType)
    {
        this(node, terminalType, true);
    }

    /**
     * Retrieves the starting node of the trace.
     * 
     * @return the node
     */
    public GrammarNode getNode()
    {
        return node;
    }

    /**
     * Retrieves the terminal type being traced
     * 
     * @return the terminal type
     */
    public int getTerminalType()
    {
        return terminalType;
    }

    /**
     * Retrieves the parent trace of this one.
     * 
     * @return the parent
     */
    public FollowTrace getParent()
    {
        return parent;
    }

    /**
     * Retrieves the child traces of this one
     * @return the children of the trace
     */
    public List<FollowTrace> getChildren()
    {
        return children;
    }

    private void buildChildren()
    {
        if (node instanceof Grammar) {
            for (NonterminalRule rule : ((Grammar) node)
                .getNonterminalRules()) {
                if (rule.getFollow().get(terminalType)) {
                    FollowTrace child =
                        new FollowTrace(rule, terminalType);
                    child.parent = this;
                    children.add(child);
                }
            }
        }
        else if (node instanceof NonterminalRule) {
            for (Alternative alternative : ((NonterminalRule) node)
                .getAlternatives()) {
                if (alternative.getFollow().get(terminalType)) {
                    FollowTrace child =
                        new FollowTrace(alternative, terminalType);
                    child.parent = this;
                    children.add(child);
                }
            }

        }
        else if (node instanceof Block) {
            for (Alternative alternative : ((Block) node)
                .getAlternatives()) {
                if (alternative.getFollow().get(terminalType)) {
                    FollowTrace child =
                        new FollowTrace(alternative, terminalType);
                    child.parent = this;
                    children.add(child);
                }
            }
        }
        else if (node instanceof Alternative) {
            List<Term> terms = ((Alternative) node).getTerms();
            FollowTrace current = this;
            for (Term term : terms) {
                if (term.getFollow().get(terminalType)) {
                    if (term instanceof Terminal) {
                        // the end of the road
                        if (term.getType() == terminalType) {
                            FollowTrace child =
                                new FollowTrace(term, terminalType,
                                                false);
                            child.parent = current;
                            current.children.add(child);
                            break;
                        }
                        else {
                            // should never happen
                            throw new IllegalStateException("internal error");
                        }
                    }
                    else if (term instanceof Nonterminal) {
                        if (term.getFollow().get(terminalType)) {
                            FollowTrace child =
                                new FollowTrace(term, terminalType,
                                                false);
                            child.parent = current;
                            current.children.add(child);
                            NonterminalRule rule =
                                ((Nonterminal) term).getRule();
                            FollowTrace nextTrace =
                                new FollowTrace(rule, terminalType);
                            nextTrace.parent = child;
                            child.getChildren().add(nextTrace);
                        }
                    }
                    else {
                        // must be a block
                        FollowTrace child =
                            new FollowTrace(term, terminalType);
                        child.parent = current;
                        current.children.add(child);
                    }
                    if (!term.isNullable()) {
                        break;
                    }
                }
                else if (term.isNullable()) {
                    FollowTrace child =
                        new FollowTrace(term, terminalType, false);
                    child.parent = current;
                    current.children.add(child);
                    current = child;
                }
                else {
                    System.out.println("!!! " + term.getDisplayName()
                            + " - " + term.isNullable());
                }
            }
        }
        else if (node instanceof Nonterminal) {
            // has no children - just carry on
        }
        else if (node instanceof Terminal) {
            // has no children - just carry on
        }
        else {
            throw new IllegalStateException("internal error");
        }
    }

    @Override
    protected void toDotString(DotStringBuilder stringBuilder)
    {
        define(stringBuilder, node);
        for (FollowTrace child : children) {
            child.toDotString(stringBuilder);
            connect(stringBuilder, node, child.node);
        }
    }
}
