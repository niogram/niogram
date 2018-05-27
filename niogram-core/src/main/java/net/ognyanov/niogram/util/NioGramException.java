/*
 * Copyright (c) 2018 by Nikolay Ognyanov. All rights reserved.
 * 
 * Use of this file is governed by the licensing conditions   
 * in the LICENSE.txt file in the root folder of the project.
 */
package net.ognyanov.niogram.util;

/**
 * A base class for NioGram exceptions.
 *
 * @author Nikolay Ognyanov
 */
public class NioGramException
    extends RuntimeException
{
    private static final long serialVersionUID = 1L;

    public NioGramException()
    {
        super();
    }

    public NioGramException(Throwable e)
    {
        super(e);
    }

    public NioGramException(String msg)
    {
        super(msg);
    }

    public NioGramException(String msg, Throwable e)
    {
        super(msg, e);
    }
}
