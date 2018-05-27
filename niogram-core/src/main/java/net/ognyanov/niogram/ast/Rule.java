/*
 * Copyright (c) 2018 by Nikolay Ognyanov. All rights reserved.
 * 
 * Use of this file is governed by the licensing conditions   
 * in the LICENSE.txt file in the root folder of the project.
 */
package net.ognyanov.niogram.ast;

/**
 * Common interface of TerminalRule and NonteriminalRule.<p>
 * 
 * Has the following attributes:
 * <ul>
 * <li><strong>used</strong>
 * - A flag marking that the rule is used by other rules.</li>
 * </ul>
 *
 * @author Nikolay Ognyanov
 */
public interface Rule
{
    public boolean isUsed();

    public void setUsed(boolean used);

}
