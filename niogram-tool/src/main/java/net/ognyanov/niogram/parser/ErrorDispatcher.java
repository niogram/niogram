/*
 * Copyright (c) 2018 by Nikolay Ognyanov. All rights reserved.
 * 
 * Use of this file is governed by the licensing conditions   
 * in the LICENSE.txt file in the root folder of the project.
 */
package net.ognyanov.niogram.parser;

import java.util.Set;

/**
 * An interface for error message dispatchers.
 *
 * @author Nikolay Ognyanov
 */
public interface ErrorDispatcher
{
    /**
     * Defines types of errors registered by the grammar:<p>
     * <ul>
     *  <li><strong>SyntaxErrors</strong>
     * - a syntax error flag</li>
     * <li><strong>FailedTokenImports</strong>
     *  - signals failure to open an imported tokens file</li>
     * <li><strong>FailedGrammarImports</strong>
     * - signals failure to open an imported grammar file</li>
     * <li><strong>DuplicateTerminals</strong>
     * - signals the presence of duplicate terminals</li>
     * <li><strong>UnknownTerminals</strong>
     * - signals the presence of unknown terminals</li>
     * <li><strong>InvalidTerminalNames</strong>
     * - signals the presence of invalid terminal names</li>
     * <li><strong>InvalidTerminalTypes</strong>
     * - signals the presence of invalid terminal types</li>
     * <li><strong>DuplicateNonterminals</strong>
     * - signals the presence of duplicate nonterminals</li>
     * <li><strong>UnknownNonterminals</strong>
     * - signals the presence of unknown nonterminals</li>
     * <li><strong>NonterminalLiterals</strong>
     * - signals the presence of string literals
     *   in nonterminal rules</li>
     * <li><strong>DotExpressions</strong>
     * - signals the presence of dot expressions in nonterminal rules</li>
     * <li><strong>NotSets</strong>
     * - signals the presence of not set expressions in nonterminal rules</li>
     * <li><strong>LazyEBNF</strong>
     * - signals the presence of lazy EBNF occurrence indicators
     *   in nonterminal rules</li>
     * </ul>
     *
     * @author Nikolay Ognyanov
     */
    public enum ErrorType
    {
        SyntaxErrors, FailedTokenImports, FailedGrammarImports,
        DuplicateTerminals, UnknownTerminals, InvalidTerminalNames,
        InvalidTerminalTypes, DuplicateNonterminals, UnknownNonterminals,
        NonterminalLiterals, DotExpressions, NotSets, LazyEBNF
    };

    /**
     * Retrieves the name of the file currently being processed.
     * 
     * @return the file name
     */
    public String getFileName();

    /**
     * Adds a new error listener to the dispatcher.
     * 
     * @param errorListener the listener.
     */
    public void addErrorListener(ErrorListener errorListener);

    /**
     * Retrieves the set of all error listeners
     * 
     * @return the set
     */
    public Set<ErrorListener> getErrorListeners();

    /**
     * Remove an error listener from the dispatcher.
     * 
     * @param errorListener the error listener
     */
    public void removeErrorListener(ErrorListener errorListener);

    /**
     * Removes all error listeners from the error source;
     */
    public void removeErrorListeners();

    /**
     * Notifies all error listeners of the dispatcher of an error.
     * 
     * @param errorType the type of the error
     * @param line the line where a problem was encountered
     * @param position the position in line
     * @param message the error message
     */
    public void notifyErrorListeners(ErrorType errorType, int line,
                                     int position, String message);

    /**
     * Notifies all error listeners of the dispatcher of an warning.
     * 
     * @param errorType the type of the error
     * @param line the line where a problem was encountered
     * @param position the position in line
     * @param message the error message
     */
    public void warnErrorListeners(ErrorType errorType, int line,
                                   int position, String message);

    /**
     * Register that an error has occurred
     * 
     * @param error the error type
     */
    public void registerError(ErrorType error);

    /**
     * Registers that an warning has occurred
     * 
     * @param warning the warning type
     */
    public void registerWarning(ErrorType warning);
}
