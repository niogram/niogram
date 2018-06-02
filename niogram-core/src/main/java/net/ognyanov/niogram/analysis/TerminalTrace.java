/*
 * Copyright (c) 2018 by Nikolay Ognyanov. All rights reserved.
 * 
 * Use of this file is governed by the licensing conditions   
 * in the LICENSE.txt file in the root folder of the project.
 */
package net.ognyanov.niogram.analysis;

import static net.ognyanov.niogram.analysis.TerminalTrace.TraceType.FIRST;

import java.util.ArrayList;
import java.util.List;

import net.ognyanov.niogram.ast.GrammarNode;
import net.ognyanov.niogram.ast.Nonterminal;
import net.ognyanov.niogram.ast.NonterminalRule;
import net.ognyanov.niogram.ast.Terminal;
import net.ognyanov.niogram.util.DotStringBuilder;

/**
 * A terminal occurrence trace.
 *
 *<p>Has the following attributes:
 *<ul>
 *<li><strong><code>id</code></strong>
 * - an unique id of the trace.
 * </li>
 *<li><strong><code>type</code></strong>
 * - the type of set for which the 
 * trace has been built.
 * </li>
 *<li><strong><code>terminalType</code></strong>
 * - the terminal which is being traced.
 * </li>
 *<li><strong><code>start</code></strong>
 * - the start AST node of the trace.
 * </li>
 *<li><strong><code>parent</code></strong>
 * - the parent trace of this object or null
 *   if the current object has no parent.
 * </li>
 *<li><strong><code>children</code></strong>
 * - a list of child traces. Possibly empty
 * but never null.
 * </li>
 *</ul>
 * @author Nikolay Ognyanov
 */
public class TerminalTrace
{
    /**
     * The type of set for which a trace is built.
     *
     * @author Nikolay Ognyanov
     */
    public enum TraceType
    {
        FIRST, FOLLOW
    }

    private static final Object lock     = new Object();
    private static int          counter  = 0;

    private final int           id;
    private TraceType           type;
    private int                 terminalType;
    private GrammarNode         start    = null;
    private TerminalTrace       parent   = null;
    private List<TerminalTrace> children = new ArrayList<TerminalTrace>();

    TerminalTrace(TraceType type, int terminalType, GrammarNode start)
    {
        synchronized (lock) {
            id = counter++;
        }
        this.type = type;
        this.terminalType = terminalType;
        this.start = start;
    }

    public int getId()
    {
        return id;
    }

    public TraceType getType()
    {
        return type;
    }

    public int getTerminalType()
    {
        return terminalType;
    }

    public GrammarNode getStart()
    {
        return start;
    }

    public TerminalTrace getParent()
    {
        return parent;
    }

    void setParent(TerminalTrace parent)
    {
        this.parent = parent;
    }

    public List<TerminalTrace> getChildren()
    {
        return children;
    }

    /**
     * Builds a DOT language representation of the trace.
     * 
     * @return the DOT language representation of the trace
     */
    public String toDotString()
    {
        DotStringBuilder stringBuilder = new DotStringBuilder();
        startDotGraph(stringBuilder, this);
        defineDotNodes(stringBuilder, this);
        toDotString(stringBuilder);
        endDotGraph(stringBuilder);
        return stringBuilder.toString();
    }

    private void toDotString(DotStringBuilder stringBuilder)
    {
        for (TerminalTrace child : getChildren()) {
            defineDotNodes(stringBuilder, child);
            connectDotNodes(stringBuilder, this, child);
        }
        for (TerminalTrace child : getChildren()) {
            child.toDotString(stringBuilder);
        }
    }

    private static void startDotGraph(DotStringBuilder stringBuilder,
                                      TerminalTrace trace)
    {
        stringBuilder.append("digraph \"")
            .append(trace.getStart().getDisplayName()).append("\" {\n");

    }

    private static void endDotGraph(DotStringBuilder stringBuilder)
    {
        stringBuilder.append("}\n");
    }

    private static void defineDotNodes(DotStringBuilder stringBuilder,
                                       TerminalTrace... nodes)
    {
        for (TerminalTrace trace : nodes) {
            GrammarNode node = trace.getStart();
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
            stringBuilder.append(trace.getId()).append(" [label=\"");
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

    private static void connectDotNodes(DotStringBuilder stringBuilder,
                                        TerminalTrace source,
                                        TerminalTrace target)
    {
        TraceType sourceType = source.getType();
        TraceType targetType = target.getType();
        stringBuilder.append(source.getId()).append("->")
            .append(target.getId());
        if (sourceType == FIRST) {
            if (targetType == FIRST) {
            }
            else {
                // should never happen
                throw new IllegalStateException("internal error");
            }
        }
        else {
            if (targetType == FIRST) {
                stringBuilder.append(" [color=blue; style=dashed]");
            }
            else {
                stringBuilder.append(" [color=blue]");
            }
        }
        stringBuilder.append(";\n");
    }
}
