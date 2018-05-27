/*
 * Copyright (c) 2018 by Nikolay Ognyanov. All rights reserved.
 * 
 * Use of this file is governed by the licensing conditions   
 * in the LICENSE.txt file in the root folder of the project.
 */
package net.ognyanov.niogram.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import net.ognyanov.niogram.util.BiasedBitSet;

public class BiasedBitSetTest
{
    @Test
    public void test()
    {
        BiasedBitSet bs = new BiasedBitSet(-1);
        bs.set(-1, true);
        assertTrue(bs.get(-1));
        bs.flip(-1);
        assertFalse(bs.get(-1));
        bs.flip(-1);
        int idx = bs.getStart();
        idx = bs.nextSetBit(idx);
        assertTrue(idx == -1);
        assertTrue(bs.nextSetBit(0) == bs.getNone());
        bs.flip(-1);
        assertTrue(bs.isEmpty());

        bs.set(1, true);
        assertTrue(bs.get(1));
        bs.flip(1);
        assertFalse(bs.get(1));
        bs.flip(1);
        idx = bs.getStart();
        idx = bs.nextSetBit(idx);
        assertTrue(idx == 1);
        assertTrue(bs.nextSetBit(2) == bs.getNone());
        bs.flip(1);
        assertTrue(bs.isEmpty());

        BiasedBitSet bs1 = new BiasedBitSet(-1);
        bs1.set(5);
        bs.or(bs1);
        assertTrue(bs.get(5));

        BiasedBitSet bs2 = (BiasedBitSet) bs1.clone();
        assertTrue(bs2.get(5));
        bs2.set(3);
        assertFalse(bs1.get(3));
    }
}
