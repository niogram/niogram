/*
 * Copyright (c) 2018 by Nikolay Ognyanov. All rights reserved.
 * 
 * Use of this file is governed by the licensing conditions   
 * in the LICENSE.txt file in the root folder of the project.
 */
package net.ognyanov.niogram.parser.antlr4;

import java.util.HashMap;
import java.util.Map;

import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import net.ognyanov.niogram.parser.antlr4.ANTLRv4Parser.GrammarSpecContext;
import net.ognyanov.niogram.util.DotStringBuilder;

class DotStringVisitor
    extends BaseParseTreeVisitor
{
    boolean                         excludeTerminals = false;
    String[]                        ruleNames        = null;
    private Map<ParseTree, Integer> treeId           =
        new HashMap<ParseTree, Integer>();
    private DotStringBuilder        sb               = null;
    private boolean                 firstPass        = true;
    private int                     counter          = 0;
    private boolean                 skip             = false;

    public DotStringVisitor(Parser parser)
    {
        super(parser);
        ruleNames = parser.getRuleNames();
    }

    public String toDotString(ParseTree tree, boolean excludeTerminals)
    {
        this.excludeTerminals = excludeTerminals;
        sb = new DotStringBuilder();
        firstPass = true;
        visit(tree);
        firstPass = false;
        visit(tree);
        String result = sb.toString();
        sb = null;
        return result;
    }

    @Override
    protected void preVisit(ParseTree tree)
    {
        if (excludeTerminals
                && tree instanceof ANTLRv4Parser.RuleSpecContext) {
            ANTLRv4Parser.RuleSpecContext ctx =
                (ANTLRv4Parser.RuleSpecContext) tree;
            if (ctx.lexerRuleSpec() != null) {
                skip = true;
            }
        }
        if (skip) {
            return;
        }
        if (firstPass) {
            treeId.put(tree, counter++);
            return;
        }
        if (tree instanceof GrammarSpecContext) {
            sb.append("digraph Grammar {\n");
        }
        int code = treeId.get(tree);
        if (tree instanceof ParserRuleContext) {
            ParserRuleContext ctx = (ParserRuleContext) tree;
            int ruleIdx = ctx.getRuleIndex();
            String ruleName = ruleNames[ruleIdx];
            startNode(code, ruleName);
        }
        else if (tree instanceof ErrorNode) {
            ErrorNode err = (ErrorNode) tree;
            Token token = err.getSymbol();
            String tokenText = token.getText();
            startNode(code, tokenText);
            appendAttribute("style", "rounded");
            appendAttribute("color", "red");
        }
        else if (tree instanceof TerminalNode) {
            TerminalNode terminal = (TerminalNode) tree;
            Token token = terminal.getSymbol();
            String tokenText = token.getText();
            startNode(code, tokenText);
            appendAttribute("style", "rounded");
        }
        else {
            String message = "Unknown node type " + tree.getClass().getName();
            throw new IllegalArgumentException(message);
        }
        endNode();
    }

    @Override
    protected void postVisit(ParseTree tree)
    {
        if (excludeTerminals
                && tree instanceof ANTLRv4Parser.RuleSpecContext) {
            ANTLRv4Parser.RuleSpecContext ctx =
                (ANTLRv4Parser.RuleSpecContext) tree;
            if (ctx.lexerRuleSpec() != null) {
                skip = false;
                return;
            }
        }
        if (skip) {
            return;
        }
        if (firstPass) {
            return;
        }
        int treeCode = treeId.get(tree);
        int childCount = tree.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ParseTree child = tree.getChild(i);
            Integer childId = treeId.get(child);
            if (childId != null) {
                sb.append(treeCode).append(" -> ").append(childId)
                    .append(";\n");
            }
        }

        if (tree instanceof GrammarSpecContext) {
            sb.append("}\n");
        }
    }

    private void startNode(int code, String name)
    {
        sb.append(code).append(" [label=");
        sb.appendEscaped("\"" + name + "\"");
        appendAttribute("shape", "box");
    }

    private void appendAttribute(String name, String value)
    {
        sb.append(' ');
        sb.append(name);
        sb.append("=");
        sb.appendEscaped(value);
    }

    private void endNode()
    {
        sb.append("]\n");
    }

}
