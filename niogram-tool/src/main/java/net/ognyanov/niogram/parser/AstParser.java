/*
 * Copyright (c) 2018 by Nikolay Ognyanov. All rights reserved.
 * 
 * Use of this file is governed by the licensing conditions   
 * in the LICENSE.txt file in the root folder of the project.
 */
package net.ognyanov.niogram.parser;

import net.ognyanov.niogram.ast.Grammar;

/**
 * A common interface for translators from grammar
 * specification languages to the NioGram AST.
 * <p>So far mostly a placeholder because there
 * is only one supported grammar specification
 * language - ANTLR 4.
 *
 * @author Nikolay Ognyanov
 */
public interface AstParser
    extends ErrorDispatcher
{
    /**
     * The main method of the translator.
     * 
     * @return the AST of the parsed grammar
     */
    public Grammar grammar();
}
