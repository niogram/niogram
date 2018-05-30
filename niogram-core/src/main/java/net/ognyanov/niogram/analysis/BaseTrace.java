/*
 * Copyright (c) 2018 by Nikolay Ognyanov. All rights reserved.
 * 
 * Use of this file is governed by the licensing conditions   
 * in the LICENSE.txt file in the root folder of the project.
 */
package net.ognyanov.niogram.analysis;

import net.ognyanov.niogram.ast.GrammarNode;
import net.ognyanov.niogram.ast.Nonterminal;
import net.ognyanov.niogram.ast.NonterminalRule;
import net.ognyanov.niogram.ast.Terminal;
import net.ognyanov.niogram.util.BaseStringBuilder;
import net.ognyanov.niogram.util.DotStringBuilder;

/**
 * A base class for terminal occurrence traces.
 * Provides an export to DOT capability.
 *
 * @author Nikolay Ognyanov
 */
public abstract class BaseTrace
{
    protected GrammarNode node;

    /**
     * Creates a DOT language representation of the trace.
     * 
     * @return the dot language representation of the trace
     */
    public String toDotString()
    {
        DotStringBuilder stringBuilder = new DotStringBuilder();
        stringBuilder.append("digraph ").append(node.getDisplayName())
            .append("{\n");
        toDotString(stringBuilder);
        stringBuilder.append("}\n");
        return stringBuilder.toString();
    }

    protected abstract void toDotString(DotStringBuilder stringBuilder);

    protected void define(BaseStringBuilder stringBuilder,
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

    protected void connect(BaseStringBuilder stringBuilder,
                           GrammarNode... nodes)
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
