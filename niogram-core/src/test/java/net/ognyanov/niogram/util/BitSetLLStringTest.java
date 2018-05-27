/*
 * Copyright (c) 2018 by Nikolay Ognyanov. All rights reserved.
 * 
 * Use of this file is governed by the licensing conditions   
 * in the LICENSE.txt file in the root folder of the project.
 */
package net.ognyanov.niogram.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Test;

import net.ognyanov.niogram.util.BiasedBitSet;
import net.ognyanov.niogram.util.BitSetLLString;

public class BitSetLLStringTest
{
    @Test
    public void test()
    {
        testConstructors();
        testAddGet();
        testContains();
        testAddAll();
        testAppend();
    }

    private void testConstructors()
    {
        BitSetLLString s = new BitSetLLString(5);
        BiasedBitSet bs = new BiasedBitSet();
        bs.set(4);
        assertTrue(s.limit() == 5);
        assertTrue(s.length() == 0);
        s.add(bs);
        assertTrue(s.equals(new BitSetLLString(s)));
        assertTrue(s.equals(s.clone()));
    }

    private void testAddGet()
    {
        Random r = new Random();
        int k = 10;
        int l = 5;
        BiasedBitSet bbs1[] = new BiasedBitSet[l];
        BiasedBitSet bbs2[] = new BiasedBitSet[l];
        BitSetLLString s1 = new BitSetLLString(k);
        BitSetLLString s2 = new BitSetLLString(k);
        for (int i = 0; i < l; i++) {
            bbs1[i] = new BiasedBitSet();
            bbs2[i] = new BiasedBitSet();
            for (int j = 0; j < 10; j++) {
                bbs1[i].set(r.nextInt(20));
                bbs2[i].set(r.nextInt(20));
            }
        }
        s1.add(bbs1);
        assertTrue(s1.length() == l);
        assertTrue(s1.stringLengths().size() == 1);
        assertTrue(s1.stringLengths().contains(l));
        for (int i = 0; i < l; i++) {
            assertTrue(s1.get(i).equals(bbs1[i]));
        }
        s2.add(bbs2);
        assertTrue(s2.length() == l);
        s1.stringLengths().add(1);
        s2.stringLengths().add(2);
        s1.addAll(s2);
        assertTrue(s1.length() == l);
        assertTrue(s1.stringLengths().size() == 3);
        assertTrue(s1.stringLengths().contains(1));
        assertTrue(s1.stringLengths().contains(2));
        assertTrue(s1.stringLengths().contains(l));
        for (int i = 0; i < l; i++) {
            BiasedBitSet bs = new BiasedBitSet(bbs1[i]);
            bs.or(bbs2[i]);
            assertTrue(bs.equals(s1.get(i)));
        }
    }

    private void testContains()
    {
        BitSetLLString s = new BitSetLLString(2);
        BiasedBitSet bs1 = new BiasedBitSet();
        BiasedBitSet bs2 = new BiasedBitSet();
        bs1.set(1);
        bs2.set(2);

        assertFalse(s.containsEmpty());
        s.addEmpty();
        assertTrue(s.containsEmpty());
        assertFalse(s.isEmpty());
        s.add(bs1, bs2);
        assertTrue(s.contains(bs1));
        assertTrue(s.contains(bs2));
    }

    private void testAddAll()
    {
        Random r = new Random();
        int k = 5;
        BiasedBitSet bbs1[] = new BiasedBitSet[k];
        BiasedBitSet bbs2[] = new BiasedBitSet[k];
        BitSetLLString s1 = new BitSetLLString(k);
        BitSetLLString s2 = new BitSetLLString(k);
        for (int i = 0; i < k; i++) {
            bbs1[i] = new BiasedBitSet();
            bbs2[i] = new BiasedBitSet();
            for (int j = 0; j < 10; j++) {
                bbs1[i].set(r.nextInt(20));
                bbs2[i].set(r.nextInt(20));
            }
        }
        s1.add(bbs1);
        assertTrue(s1.length() == k);
        s2.add(bbs2);
        assertTrue(s2.length() == k);
        s1.stringLengths().add(1);
        s2.stringLengths().add(2);
        s1.addAll(s2);
        assertTrue(s1.length() == k);
        assertTrue(s1.stringLengths().contains(1));
        assertTrue(s1.stringLengths().contains(2));
        for (int i = 0; i < k; i++) {
            BiasedBitSet b = new BiasedBitSet(bbs1[i]);
            b.or(bbs2[i]);
            assertTrue(s1.get(i).equals(b));
        }
    }

    private void testAppend()
    {
        Random r = new Random();
        int k = 10;
        int l = 5;
        int m = 3;

        BiasedBitSet bbs1[] = new BiasedBitSet[l];
        BiasedBitSet bbs2[] = new BiasedBitSet[l];
        BitSetLLString s1 = new BitSetLLString(k);
        BitSetLLString s2 = new BitSetLLString(k);
        for (int i = 0; i < l; i++) {
            bbs1[i] = new BiasedBitSet();
            bbs2[i] = new BiasedBitSet();
            for (int j = 0; j < 10; j++) {
                bbs1[i].set(r.nextInt(20));
                bbs2[i].set(r.nextInt(20));
            }
        }
        s1.add(bbs1);
        s2.add(bbs2);
        s1.stringLengths().add(m);
        assertTrue(bbs1.length == l);
        assertTrue(bbs2.length == l);
        assertTrue(s1.stringLengths().contains(l));
        assertTrue(s2.stringLengths().contains(l));
        s1.stringLengths().add(m);
        s1.append(s2);
        assertTrue(s1.length() == 2 * l);
        assertTrue(s1.stringLengths().contains(l + m));
        for (int i = m; i < l; i++) {
            BiasedBitSet b = new BiasedBitSet(bbs1[i]);
            b.or(bbs2[i - m]);
            assertTrue(b.equals(s1.get(i)));
        }
    }

}
