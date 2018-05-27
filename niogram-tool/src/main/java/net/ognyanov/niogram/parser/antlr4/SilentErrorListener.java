/*
 * Copyright (c) 2018 by Nikolay Ognyanov. All rights reserved.
 * 
 * Use of this file is governed by the licensing conditions   
 * in the LICENSE.txt file in the root folder of the project.
 */
package net.ognyanov.niogram.parser.antlr4;

import net.ognyanov.niogram.parser.ErrorDispatcher;
import net.ognyanov.niogram.parser.ErrorListener;
import net.ognyanov.niogram.parser.ErrorDispatcher.ErrorType;

class SilentErrorListener
    implements ErrorListener
{
    @Override
    public void reportError(ErrorDispatcher errorDispatcher,
                            ErrorType errorType, int line, int position,
                            String message)
    {
        errorDispatcher.registerError(errorType);
    }

    @Override
    public void reportWarning(ErrorDispatcher errorDispatcher,
                              ErrorType errorType, int line, int position,
                              String message)
    {
        errorDispatcher.registerWarning(errorType);
    }

}
