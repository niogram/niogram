/*
 * Copyright (c) 2018 by Nikolay Ognyanov. All rights reserved.
 * 
 * Use of this file is governed by the licensing conditions   
 * in the LICENSE.txt file in the root folder of the project.
 */
package net.ognyanov.niogram.parser.antlr4;

import org.antlr.v4.runtime.ConsoleErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

import net.ognyanov.niogram.parser.ErrorDispatcher;
import net.ognyanov.niogram.parser.ErrorListener;
import net.ognyanov.niogram.parser.ErrorDispatcher.ErrorType;

class RelayErrorListener
    extends ConsoleErrorListener
{
    ErrorListener   errorListener   = null;

    ErrorDispatcher errorDispatcher = null;

    public RelayErrorListener(ErrorListener errorListener,
                              ErrorDispatcher errorDispatcher)
    {
        if (errorListener == null || errorDispatcher == null) {
            throw new IllegalArgumentException("null argument");
        }
        this.errorListener = errorListener;
        this.errorDispatcher = errorDispatcher;
    }

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer,
                            Object offendingSymbol, int line,
                            int charPositionInLine, String msg,
                            RecognitionException e)
    {
        errorListener.reportError(
            errorDispatcher, ErrorDispatcher.ErrorType.SyntaxErrors,
            line, charPositionInLine, msg);
        errorDispatcher.registerError(ErrorType.SyntaxErrors);
    }
}
