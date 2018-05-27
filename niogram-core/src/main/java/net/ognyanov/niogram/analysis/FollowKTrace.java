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
import net.ognyanov.niogram.util.BaseStringBuilder;
import net.ognyanov.niogram.util.DotStringBuilder;

/**
 * A trace for a terminal in the FollowK set of a node.
 *
 * @author Nikolay Ognyanov
 */
public class FollowKTrace
{
    private GrammarNode        node;
    private int                terminalType;
    private int                position;
    private FollowKTrace       parent   = null;
    private List<FollowKTrace> children = new ArrayList<FollowKTrace>();

    private FollowKTrace(GrammarNode node, int terminalType, int position,
                         boolean buildChildren)
    {
        this.node = node;
        this.terminalType = terminalType;
        this.position = position;
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
     * @param position the position at which the terminal
     * type is to be traced
     */
    public FollowKTrace(GrammarNode node, int terminalType, int position)
    {
        this(node, terminalType, position, true);
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
     * Retrieves the position at which the terminal
     * type is traced.
     * 
     * @return the position
     */
    public int getPosition()
    {
        return position;
    }

    /**
     * Retrieves the parent trace of this one.
     * 
     * @return the parent
     */
    public FollowKTrace getParent()
    {
        return parent;
    }

    /**
     * Retrieves the child traces of this one
     * @return the children of the trace
     */
    public List<FollowKTrace> getChildren()
    {
        return children;
    }

    public String toDotString()
    {
        DotStringBuilder stringBuilder = new DotStringBuilder();
        stringBuilder.append("digraph ").append(node.getDisplayName())
            .append("{\n");
        toDotString(stringBuilder);
        stringBuilder.append("}\n");
        return stringBuilder.toString();
    }

    private void buildChildren()
    {
        if (node instanceof Grammar) {
            for (NonterminalRule rule : ((Grammar) node)
                .getNonterminalRules()) {
                if (rule.getFollowK().containsAt(terminalType, position)) {
                    FollowKTrace child =
                        new FollowKTrace(rule, terminalType, position);
                    child.parent = this;
                    children.add(child);
                }
            }
        }
        else if (node instanceof NonterminalRule) {
            for (Alternative alternative : ((NonterminalRule) node)
                .getAlternatives()) {
                if (alternative.getFollowK().containsAt(terminalType,
                    position)) {
                    FollowKTrace child =
                        new FollowKTrace(alternative, terminalType, position);
                    child.parent = this;
                    children.add(child);
                }
            }

        }
        else if (node instanceof Block) {
            for (Alternative alternative : ((Block) node)
                .getAlternatives()) {
                if (alternative.getFollowK().containsAt(terminalType,
                    position)) {
                    FollowKTrace child =
                        new FollowKTrace(alternative, terminalType, position);
                    child.parent = this;
                    children.add(child);
                }
            }
        }
        else if (node instanceof Alternative) {
            List<Term> terms = ((Alternative) node).getTerms();
            FollowKTrace current = this;
            for (Term term : terms) {
                if (term.getFollowK().containsAt(terminalType, position)) {
                    if (term instanceof Terminal) {
                        // the end of the road
                        if (term.getType() == terminalType) {
                            FollowKTrace child =
                                new FollowKTrace(term, terminalType, position,
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
                        if (term.getFollowK().containsAt(terminalType,
                            position)) {
                            FollowKTrace child =
                                new FollowKTrace(term, terminalType, position,
                                                 false);
                            child.parent = current;
                            current.children.add(child);
                            NonterminalRule rule =
                                ((Nonterminal) term).getRule();
                            FollowKTrace nextTrace =
                                new FollowKTrace(rule, terminalType, position);
                            nextTrace.parent = child;
                            child.getChildren().add(nextTrace);
                        }
                    }
                    else {
                        // must be a block
                        FollowKTrace child =
                            new FollowKTrace(term, terminalType, position);
                        child.parent = current;
                        current.children.add(child);
                    }
                    if (!term.isNullable()) {
                        break;
                    }
                }
                else if (term.isNullable()) {
                    FollowKTrace child =
                        new FollowKTrace(term, terminalType,
                                         position, false);
                    child.parent = current;
                    current.children.add(child);
                    current = child;
                }
                else {
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

    private void toDotString(DotStringBuilder stringBuilder)
    {
        define(stringBuilder, node);
        for (FollowKTrace child : children) {
            child.toDotString(stringBuilder);
            connect(stringBuilder, node, child.node);
        }
    }

    private void define(BaseStringBuilder stringBuilder,
                        GrammarNode... nodes)
    {
        for (GrammarNode node : nodes) {
            String shape = null;
            String style = null;
            if (node.isNullable() && !(node instanceof NonterminalRule)) {
                style = "dashed";
            }
            if (node instanceof Nonterminal || node instanceof Terminal) {
                shape = "box";
            }
            if (node instanceof Terminal) {
                if (style != null) {
                    style = style + ",rounded";
                }
                else {
                    style = "rounded";
                }
            }
            stringBuilder.append(node.getId()).append(" [label=\"");
            stringBuilder.append(node.getDisplayName()).append("\"");
            if (shape != null) {
                stringBuilder.append(" shape=").append(shape);
            }
            if (style != null) {
                stringBuilder.append(" style=\"").append(style).append("\"");
            }
            stringBuilder.append("];\n");
        }
    }

    private void connect(BaseStringBuilder stringBuilder,
                         GrammarNode... nodes)
    {
        boolean follow = true;
        for (GrammarNode node : nodes) {
            if (follow) {
                follow = false;
            }
            else {
                stringBuilder.append("->");
            }
            stringBuilder.append(node.getId());
        }
        stringBuilder.append(";\n");
    }
}
