/*
 * Copyright (c) 2018 by Nikolay Ognyanov. All rights reserved.
 *
 * Use of this file is governed by the licensing conditions
 * in the LICENSE.txt file in the root folder of the project.
 */
package net.ognyanov.niogram.parser.antlr4;

import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.Vocabulary;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import net.ognyanov.niogram.util.XmlStringBuilder;

class XmlStringVisitor
    extends BaseParseTreeVisitor
{
    private static final String TERMINAL_NODE = "TerminalNode";
    private static final String ERROR_NODE    = "ErrorNode";
    private final Vocabulary    vocabulary;
    private XmlStringBuilder    builder       = null;

    public XmlStringVisitor(Parser parser)
    {
        super(parser);
        vocabulary = parser.getVocabulary();
    }

    public String toXmlString(ParseTree tree)
    {
        builder = new XmlStringBuilder();
        builder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        visit(tree);
        String result = builder.toString();
        builder = null;
        return result;
    }

    @Override
    protected void preVisit(ParseTree tree)
    {
        if (tree instanceof ParserRuleContext) {
            ParserRuleContext ctx = (ParserRuleContext) tree;
            int ruleIdx = ctx.getRuleIndex();
            String ruleName = parser.getRuleNames()[ruleIdx];
            startNode(ruleName);
            appendAttribute("class", tree.getClass().getSimpleName());
            closeTag();
        }
        else if (tree instanceof ErrorNode) {
            ErrorNode err = (ErrorNode) tree;
            Token token = err.getSymbol();
            int tokenType = token.getType();
            String displayName = vocabulary.getDisplayName(tokenType);
            String literalName = vocabulary.getLiteralName(tokenType);
            String symbolicName = vocabulary.getSymbolicName(tokenType);
            String tokenText = token.getText();
            startNode(ERROR_NODE);
            appendAttribute("class", tree.getClass().getSimpleName());
            appendAttribute("index", Integer.toString(tokenType));
            appendAttribute("symbolicName", symbolicName);
            appendAttribute("displayName", displayName);
            appendAttribute("literalName", literalName);
            appendAttribute("text", tokenText);
            endTag();
        }
        else if (tree instanceof TerminalNode) {
            TerminalNode terminal = (TerminalNode) tree;
            Token token = terminal.getSymbol();
            int tokenType = token.getType();
            String displayName = vocabulary.getDisplayName(tokenType);
            String literalName = vocabulary.getLiteralName(tokenType);
            String symbolicName = vocabulary.getSymbolicName(tokenType);
            String tokenText = token.getText();
            startNode(TERMINAL_NODE);
            appendAttribute("class", tree.getClass().getSimpleName());
            appendAttribute("index", Integer.toString(tokenType));
            appendAttribute("symbolicName", symbolicName);
            appendAttribute("displayName", displayName);
            appendAttribute("literalName", literalName);
            appendAttribute("text", tokenText);
            endTag();
        }
        else {
            String message = "Unknown node type " + tree.getClass().getName();
            throw new IllegalArgumentException(message);
        }
    }

    @Override
    protected void postVisit(ParseTree tree)
    {
        if (tree instanceof ParserRuleContext) {
            ParserRuleContext ctx = (ParserRuleContext) tree;
            int ruleIdx = ctx.getRuleIndex();
            String ruleName = parser.getRuleNames()[ruleIdx];
            endNode(ruleName);
        }
        /*
        else if (tree instanceof ErrorNode) {
            endNode(ERROR_NODE);
        }
        else if (tree instanceof TerminalNode) {
            endNode(TERMINAL_NODE);
        }
        */
    }

    private void startNode(String name)
    {
        openStartTag();
        builder.append(name);
    }

    private void appendAttribute(String name, String value)
    {
        builder.append(' ');
        builder.append(name);
        builder.append("=\"");
        builder.appendEscaped(value);
        builder.append('\"');
    }

    private void endNode(String name)
    {
        openCloseTag();
        builder.append(name);
        closeTag();
    }

    private void openStartTag()
    {
        builder.append('<');
    }

    private void openCloseTag()
    {
        builder.append("</");
    }

    private void closeTag()
    {
        builder.append('>');
    }

    private void endTag()
    {
        builder.append("/>");
    }
}
