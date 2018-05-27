/*
 * Copyright (c) 2018 by Nikolay Ognyanov. All rights reserved.
 * 
 * Use of this file is governed by the licensing conditions   
 * in the LICENSE.txt file in the root folder of the project.
 */
package net.ognyanov.niogram.ast;

/**
 * A dummy grammar node. Used in railroad diagrams.
 *
 * @author Nikolay Ognyanov
 */
class DummyNode
    extends GrammarNode
{
    private static final long serialVersionUID = 1L;

    DummyNode(int type)
    {
        super(type);
    }

    DummyNode()
    {
        this(BuiltInTypes.DUMMY);
    }
}
