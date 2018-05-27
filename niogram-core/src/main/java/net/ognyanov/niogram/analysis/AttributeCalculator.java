package net.ognyanov.niogram.analysis;

import net.ognyanov.niogram.ast.Grammar;
import net.ognyanov.niogram.util.Interruptable;

/*
 * Copyright (c) 2018 by Nikolay Ognyanov. All rights reserved.
 * 
 * Use of this file is governed by the licensing conditions   
 * in the LICENSE.txt file in the root folder of the project.
 */

/**
 * An interface for classes which calculate attributes of a grammar.
 *
 * @author Nikolay Ognyanov
 */
public interface AttributeCalculator
    extends Interruptable
{
    public void calculate(Grammar grammar);
}
