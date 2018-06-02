/*
 * Copyright (c) 2018 by Nikolay Ognyanov. All rights reserved.
 * 
 * Use of this file is governed by the licensing conditions   
 * in the LICENSE.txt file in the root folder of the project.
 */
package net.ognyanov.niogram.analysis;

import net.ognyanov.niogram.ast.GrammarNode;

/**
 * A static method facility for building of terminal occurrence traces.
 *
 * @author Nikolay Ognyanov
 */
public class TerminalTraceFactory
{
    private TerminalTraceFactory()
    {
    }

    /**
     * Builds an occurrence trace for a terminal
     * type in the First set of an AST node.
     * 
     * @param start the start AST node of the trace
     * @param terminalType the terminal type to be traced
     * @return the trace or null if the specified terminal
     * type is not in the First set.
     */
    public static TerminalTrace buildFirstTrace(GrammarNode start,
                                                int terminalType)
    {
        TerminalTrace result =
            new TerminalTraceBuilder().buildFirstTrace(start, terminalType);
        return result;
    }

    /**
     * Builds an occurrence trace for a terminal type
     * at position 0 in the FirstKL set of an AST node.
     * 
     * @param start the start AST node of the trace
     * @param terminalType the terminal type to be traced
     * @return the trace or null if the specified terminal
     * type is not in the FirstKL set.
     */
    public static TerminalTrace buildFirstTraceKL(GrammarNode start,
                                                  int terminalType)
    {
        TerminalTrace result =
            new TerminalTraceBuilder().buildFirstTraceKL(start, terminalType);
        return result;
    }

    /**
     * Builds an occurrence trace for a terminal type
     * at position 0 in the FirstK set of an AST node.
     * 
     * @param start the start AST node of the trace
     * @param terminalType the terminal type to be traced
     * @return the trace or null if the specified terminal
     * type is not in the FirstK set.
     */
    public static TerminalTrace buildFirstTraceK(GrammarNode start,
                                                 int terminalType)
    {
        TerminalTrace result =
            new TerminalTraceBuilder().buildFirstTraceK(start, terminalType);
        return result;
    }

    /**
     * Builds an occurrence trace for a terminal type
     * in the Follow set of an AST node.
     * 
     * @param start the start AST node of the trace
     * @param terminalType the terminal type to be traced
     * @return the trace or null if the specified terminal
     * type is not in the Follow set.
     */
    public static TerminalTrace buildFollowTrace(GrammarNode start,
                                                 int terminalType)
    {
        TerminalTrace result =
            new TerminalTraceBuilder().buildFollowTrace(start, terminalType);
        return result;
    }

    /**
     * Builds an occurrence trace for a terminal type
     * at position 0 in the FollowKL set of an AST node.
     * 
     * @param start the start AST node of the trace
     * @param terminalType the terminal type to be traced
     * @return the trace or null if the specified terminal
     * type is not in the FollowKL set.
     */
    public static TerminalTrace buildFollowTraceKL(GrammarNode start,
                                                   int terminalType)
    {
        TerminalTrace result =
            new TerminalTraceBuilder().buildFollowTraceKL(start, terminalType);
        return result;
    }

    /**
     * Builds an occurrence trace for a terminal type
     * at position 0 in the FollowK set of an AST node.
     * 
     * @param start the start AST node of the trace
     * @param terminalType the terminal type to be traced
     * @return the trace or null if the specified terminal
     * type is not in the FollowK set.
     */
    public static TerminalTrace buildFollowTraceK(GrammarNode start,
                                                  int terminalType)
    {
        TerminalTrace result =
            new TerminalTraceBuilder().buildFollowTraceK(start, terminalType);
        return result;
    }
}
