/*
 * Copyright (c) 2018 by Nikolay Ognyanov. All rights reserved.
 * 
 * Use of this file is governed by the licensing conditions   
 * in the LICENSE.txt file in the root folder of the project.
 */
package net.ognyanov.niogram.parser.antlr4;

import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.tree.ParseTree;

abstract class BaseParseTreeVisitor
{
    protected final Parser parser;

    public BaseParseTreeVisitor(Parser parser)
    {
        this.parser = parser;
    }

    public void visit(ParseTree tree)
    {
        if (tree == null) {
            throw new IllegalArgumentException("null tree");
        }
        preVisit(tree);
        int numChildren = tree.getChildCount();
        for (int i = 0; i < numChildren; i++) {
            visit(tree.getChild(i));
        }
        postVisit(tree);
    }

    protected void preVisit(ParseTree tree)
    {
    }

    protected void postVisit(ParseTree tree)
    {

    }
}
