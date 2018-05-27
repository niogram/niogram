package net.ognyanov.niogram.util;
/*
 * Copyright (c) 2018 by Nikolay Ognyanov. All rights reserved.
 * 
 * Use of this file is governed by the licensing conditions   
 * in the LICENSE.txt file in the root folder of the project.
 */

/**
 * An interface for interruption of long running computational tasks.
 *
 * @author Nikolay Ognyanov
 */
public interface Interruptable
{
    /**
     * Interrupts this task.
     */
    public void interrupt();

    /**
     * Tests whether this task has been interrupted. 
     * The interrupted status of the task is unaffected by this method. 
     * 
     * @return true if this task has been interrupted; false otherwise.
     */
    public boolean isInterrupted();

    /**
     * Tests whether this task has been interrupted. 
     * The interrupted status of the task is cleared. 
     * 
     * @return true if this thread has been interrupted; false otherwise.
     */
    public boolean interrupted();
}
