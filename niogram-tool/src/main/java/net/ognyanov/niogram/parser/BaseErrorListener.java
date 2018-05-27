/*
 * Copyright (c) 2018 by Nikolay Ognyanov. All rights reserved.
 * 
 * Use of this file is governed by the licensing conditions   
 * in the LICENSE.txt file in the root folder of the project.
 */
package net.ognyanov.niogram.parser;

/**
 * A base implementation of error listener.
 *
 * @author Nikolay Ognyanov
 */
public class BaseErrorListener
    implements ErrorListener
{
    private static final String ERROR_PREFIX   = "[ Error ]";
    private static final String WARNING_PREFIX = "[Warning]";

    /**
     * {@inheritDoc}
     */
    @Override
    public void reportError(ErrorDispatcher errorDispatcher,
                            ErrorDispatcher.ErrorType errorType,
                            int line, int position, String message)
    {
        String prefix = ERROR_PREFIX;
        String fileName = errorDispatcher.getFileName();
        if (fileName != null) {
            prefix = prefix + fileName + ":";
        }
        else if (line >= 0) {
            prefix = prefix + "line ";
        }
        String errorMessage =
            line >= 0 ? prefix + line + ":" + position + " " + message
                      : prefix + " " + message;
        System.err.println(errorMessage);
        errorDispatcher.registerError(errorType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reportWarning(ErrorDispatcher errorDispatcher,
                              ErrorDispatcher.ErrorType warningType,
                              int line, int position, String message)
    {
        String prefix = WARNING_PREFIX;
        String fileName = errorDispatcher.getFileName();
        if (fileName != null) {
            prefix = prefix + fileName + ":";
        }
        else if (line >= 0) {
            prefix = prefix + "line ";
        }
        String warningMessage =
            line >= 0 ? prefix + line + ":" + position + " " + message
                      : prefix + " " + message;
        System.err.println(warningMessage);
        errorDispatcher.registerWarning(warningType);
    }
}
