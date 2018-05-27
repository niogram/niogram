/*
 * Copyright (c) 2018 by Nikolay Ognyanov. All rights reserved.
 * 
 * Use of this file is governed by the licensing conditions   
 * in the LICENSE.txt file in the root folder of the project.
 */
package net.ognyanov.niogram.parser;

/**
 * An error listener interface for the parser.
 *
 * @author Nikolay Ognyanov
 */
public interface ErrorListener
{
    /**
     * Reports an error.
     * 
     * @param errorDispatcher the source of the error report
     * @param errorType the type of the error
     * @param line the line in source code where the error occurred
     * @param position the position in line where the error occurred
     * @param message the error message
     */
    public void reportError(ErrorDispatcher errorDispatcher,
                            ErrorDispatcher.ErrorType errorType,
                            int line, int position, String message);

    /**
     * Reports a warning.
     * 
     * @param errorDispatcher the source of the error report
     * @param errorType the type of the error
     * @param line the line in source code where the error occurred
     * @param position the position in line where the error occurred
     * @param message the error message
     */
    public void reportWarning(ErrorDispatcher errorDispatcher,
                              ErrorDispatcher.ErrorType errorType,
                              int line, int position, String message);
}
