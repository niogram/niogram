/*
 * Copyright (c) 2018 by Nikolay Ognyanov. All rights reserved.
 * 
 * Use of this file is governed by the licensing conditions   
 * in the LICENSE.txt file in the root folder of the project.
 */
package net.ognyanov.niogram.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.junit.Test;

import net.ognyanov.niogram.util.IntLLString;

public class LLStringTest
{
    private static int k = 5;

    @Test
    public void test()
    {
        IntLLString s1 = new IntLLString(k);
        IntLLString s2 = new IntLLString(k);
        IntLLString empty = new IntLLString(k);
        int j = 0;

        // constructors, equals
        assertTrue(s1.equals(new IntLLString(empty)));
        assertTrue(s1.equals(empty.clone()));
        assertTrue(s1.equals(new IntLLString(s1)));
        assertTrue(s1.equals(s1.clone()));

        // limit, length, isEmpty, get
        assertTrue(s1.limit() == k);
        assertTrue(s1.length() == 0);
        assertTrue(s1.isEmpty());
        assertTrue(s1.limit() == k);
        assertTrue(s1.length() == 1);
        assertFalse(s1.isEmpty());
        assertTrue(s1.get(0) == 0);
        assertTrue(s1.limit() == k);
        assertTrue(s1.length() == 2);
        assertFalse(s1.isEmpty());
        assertTrue(s1.get(1) == 1);

        // contains, isFull
        s1.clear();
        for (int i = 0; i < k; i++) {
            assertFalse(s1.isFull());
            s1.add(i);
            assertTrue(s1.contains(i));
        }
        assertTrue(s1.isFull());

        // append(int...), append(LLString)
        s1.clear();
        s2.clear();
        s1.add(0, 1);
        assertTrue(s1.contains(0));
        assertTrue(s1.contains(1));
        s2.add(2, 3);
        assertTrue(s2.contains(2));
        assertTrue(s2.contains(3));

        // toString
        assertTrue(s1.toString().equals("[0,1,2,3,-1]"));
        assertTrue(s2.toString().equals("[2,3]"));

        // equals
        assertTrue(s1.equals(s1));
        assertTrue(s1.equals(s1.clone()));
        assertFalse(s1.equals(s2));

        // iterator
        s1.clear();
        assertFalse(s1.iterator().hasNext());
        s1.add(0, 1, 2, 3, 4, 5);
        j = 0;
        Iterator<Integer> it = s1.iterator();
        while (it.hasNext()) {
            assertTrue(it.next() == s1.get(j++));
        }

        // hashCode
        assertTrue(s1.hashCode() == s1.clone().hashCode());

        // compareTo
        assertTrue(s1.compareTo(s1) == 0);
        assertTrue(s1.compareTo(new IntLLString(s1)) == 0);
        assertTrue(s1.compareTo(s2) == -1);
        assertTrue(s2.compareTo(s1) == 1);
    }
}
