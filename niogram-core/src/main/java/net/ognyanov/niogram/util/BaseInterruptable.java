package net.ognyanov.niogram.util;
/*
 * Copyright (c) 2018 by Nikolay Ognyanov. All rights reserved.
 * 
 * Use of this file is governed by the licensing conditions   
 * in the LICENSE.txt file in the root folder of the project.
 */

/**
 * A base implementation of the interface {@link Interruptable}
 *
 * @author Nikolay Ognyanov
 */
public class BaseInterruptable
    implements Interruptable
{
    private volatile boolean       interrupted;
    private volatile Interruptable relayTarget;

    /**
     * {@inheritDoc}
     */
    @Override
    public void interrupt()
    {
        interrupted = true;
        if (relayTarget != null) {
            relayTarget.interrupt();
        }
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

    /**
     * Retrieves the current interruptable subtask if any.
     * 
     * @return the subtask if any or null otherwise
     */
    protected Interruptable getRelayTarget()
    {
        return relayTarget;
    }

    /**
     * Sets an interruptable subtask to which the 
     * {@linkplain #interrupt()} call is relayed.
     * 
     * @param relayTarget the subtask to be set
     */
    protected void setRelayTarget(Interruptable relayTarget)
    {
        this.relayTarget = relayTarget;
    }
}
