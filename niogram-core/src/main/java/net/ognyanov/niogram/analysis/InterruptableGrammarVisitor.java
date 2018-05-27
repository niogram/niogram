/*
 * Copyright (c) 2018 by Nikolay Ognyanov. All rights reserved.
 * 
 * Use of this file is governed by the licensing conditions   
 * in the LICENSE.txt file in the root folder of the project.
 */
package net.ognyanov.niogram.analysis;

import net.ognyanov.niogram.ast.Grammar;
import net.ognyanov.niogram.ast.GrammarVisitor;
import net.ognyanov.niogram.util.Interruptable;
import net.ognyanov.niogram.util.NioGramException;

/**
 * A grammar visitor which tests automatically its
 * interrupted status and terminates if interrupted.
 * The test is performed upon each invocation of the
 * {@link #visitGrammar(Grammar)} method. If an
 * interrupt request is pending then an 
 * {@link NioGramException} is thrown.
 *
 * @author Nikolay Ognyanov
 */
class InterruptableGrammarVisitor
    extends GrammarVisitor
    implements Interruptable
{
    private volatile boolean interrupted;

    /**
     * {@inheritDoc}
     * 
     * @throws NioGramException if the object is interrupted
     */
    @Override
    public void visitGrammar(Grammar grammar)
    {
        if (isInterrupted()) {
            throw new NioGramException();
        }
        super.visitGrammar(grammar);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void interrupt()
    {
        interrupted = true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isInterrupted()
    {
        return interrupted;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean interrupted()
    {
        boolean result = interrupted;
        interrupted = false;
        return result;
    }
}
