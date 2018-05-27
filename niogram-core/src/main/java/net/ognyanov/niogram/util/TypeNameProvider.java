package net.ognyanov.niogram.util;
/*
 * Copyright (c) 2018 by Nikolay Ognyanov. All rights reserved.
 * 
 * Use of this file is governed by the licensing conditions   
 * in the LICENSE.txt file in the root folder of the project.
 */

/**
 * An interface for classes which provide names for
 * the elements of a BiasedBitSet.
 *
 * @author Nikolay Ognyanov
 */
public interface TypeNameProvider
{
    public String getTypeName(int index);
}
