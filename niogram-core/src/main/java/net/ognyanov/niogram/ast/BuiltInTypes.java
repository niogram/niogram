package net.ognyanov.niogram.ast;

/*
 * Copyright (c) 2018 by Nikolay Ognyanov. All rights reserved.
 * 
 * Use of this file is governed by the licensing conditions   
 * in the LICENSE.txt file in the root folder of the project.
 */

/**
 * Built-in AST node types of NioGram and their names.
 *
 * <p>The definitions of IVALID and EOF are consistent with
 * but independent from the ANTLR definitions of the same
 * types. This is a little bit risky but allows to free the AST
 * from dependence on the ANTLR runtime.
 * 
 * @author Nikolay Ognyanov
 */
public class BuiltInTypes
{
    public static final int     INVALID          = 0;                     //Token.INVALID_TYPE;
    public static final int     EOF              = INVALID - 1;           //Token.EOF;
    private static final int    EPSILON          = EOF - 1;               //Token.EPSILON;
    public static final int     DOT              = EPSILON - 1;
    public static final int     NOT              = DOT - 1;
    public static final int     GRAMMAR          = NOT - 1;
    public static final int     ALTERNATIVE      = GRAMMAR - 1;
    public static final int     BLOCK            = ALTERNATIVE - 1;
    public static final int     DUMMY            = BLOCK - 1;
    public static final int     MIN_TYPE         = DUMMY;

    private static final String PREFIX           = "$";
    public static final String  INVALID_NAME     = PREFIX + "INVALID";
    public static final String  EOF_NAME         = PREFIX + "EOF";
    public static final String  DOT_NAME         = PREFIX + "DOT";
    public static final String  NOT_NAME         = PREFIX + "NOT";
    public static final String  GRAMMAR_NAME     = PREFIX + "GRAMMAR";
    public static final String  ALTERNATIVE_NAME = PREFIX + "ALTERNATIVE";
    public static final String  BLOCK_NAME       = PREFIX + "BLOCK";
}
