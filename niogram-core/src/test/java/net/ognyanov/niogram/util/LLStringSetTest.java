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

import net.ognyanov.niogram.util.IntLLString;
import net.ognyanov.niogram.util.IntLLStringSet;

public class LLStringSetTest
{
    private static int k = 5;

    @Test
    public void test()
    {
        testaddClone();
        testAddAll();
        testAppend();
        testRepeat();
        testIsFull();
        testContainsEmpty();
        testintersects();
        testIntersect();
    }

    private void testaddClone()
    {
        IntLLStringSet st = new IntLLStringSet(k);
        IntLLString s = new IntLLString(k);
        s.add(1);

        assertTrue(st.add(s));
        assertTrue(st.contains(s));
        s.add(2);
        assertFalse(st.contains(s));
        assertTrue(st.add(s));
        assertTrue(st.contains(s));
    }

    private void testAddAll()
    {
        IntLLString s1 = new IntLLString(k);
        IntLLString s2 = new IntLLString(k);

        s1.add(1, 2);
        s2.add(3, 4, -1);

        IntLLStringSet st1 = new IntLLStringSet(k);
        IntLLStringSet st2 = new IntLLStringSet(k);

        st2.add(s1);
        st2.add(s2);
        st1.addAll(st2);
        assertTrue(st1.contains(s1));
        assertTrue(st1.contains(s2));
        s1.clear();
        assertFalse(st1.contains(st1));
    }

    private void testAppend()
    {
        IntLLStringSet st1 = new IntLLStringSet(k);
        IntLLStringSet st2 = new IntLLStringSet(k);
        IntLLString s1 = new IntLLString(k);
        IntLLString s2 = new IntLLString(k);
        IntLLString s3 = new IntLLString(k);
        IntLLString s4 = new IntLLString(k);

        s1.add(1, 2);
        s1.add(3, 4);
        st1.add(s1);
        st1.add(s2);
        st2.addAll(st1);
        st1.append(st2);
        s3.append(s1);
        s3.append(s2);
        s4.append(s2);
        s4.append(s1);
        s1.append(s1);
        s2.append(s2);
        assertTrue(st1.contains(s1));
        assertTrue(st1.contains(s2));
        assertTrue(st1.contains(s3));
        assertTrue(st1.contains(s4));

        st1.clear();
        st2.clear();
        s1.clear();
        s2.clear();
        s3.clear();
        s4.clear();
        s1.add(1, 2);
        s2.add(3, 4);
        st1.add(s1);
        st1.add(s2);
        assertTrue(st1.contains(s1));
        st2.addAll(st1);
        st1.append(st2);
        assertFalse(st1.contains(s2));
        assertFalse(st1.contains(s3));
        s3.append(s1);
        s3.append(s2);
        s4.append(s2);
        s4.append(s1);
        s1.append(s1);
        s2.append(s2);
        assertTrue(st1.contains(s1));
        assertTrue(st1.contains(s2));
        assertTrue(st1.contains(s3));
        assertTrue(st1.contains(s4));
    }

    private void testRepeat()
    {
        IntLLStringSet st = new IntLLStringSet(k);
        IntLLString s1 = new IntLLString(k);
        IntLLString s2 = new IntLLString(k);
        IntLLString s3 = new IntLLString(k);
        IntLLString s4 = new IntLLString(k);

        s1.add(1, 2);
        s1.add(3, 4);
        st.add(s1);
        st.add(s2);
        st.append(st);
        st.append(st);
        s3.append(s1);
        s3.append(s2);
        s4.append(s2);
        s4.append(s1);
        s1.append(s1);
        s2.append(s2);
        assertTrue(st.contains(s1));
        assertTrue(st.contains(s2));
        assertTrue(st.contains(s3));
        assertTrue(st.contains(s4));

        st.clear();
        s1.clear();
        s2.clear();
        s3.clear();
        s4.clear();
        st.add(s1);
        s2.add(1, 2);
        s3.add(3, 4);
        st.add(s2);
        st.add(s3);
        st.append(st);
        assertTrue(st.contains(s1));
        assertTrue(st.contains(s2));
        assertTrue(st.contains(s3));
        s3.append(s1);
        s3.append(s2);
        s4.append(s2);
        s4.append(s1);
        s1.append(s1);
        s2.append(s2);
        assertTrue(st.contains(s1));
        assertTrue(st.contains(s2));
        assertTrue(st.contains(s3));
        assertTrue(st.contains(s4));
    }

    private void testIsFull()
    {
        IntLLString s1 = new IntLLString(k);
        IntLLString s2 = new IntLLString(k);
        IntLLStringSet st = new IntLLStringSet(k);
        s1.clear();
        for (int i = 0; i < k; i++) {
            s1.add(i);
        }
        st.clear();
        assertFalse(st.isFull());
        st.add(s1);
        assertTrue(st.isFull());
        s2.clear();
        for (int i = 0; i < k; i++) {
            s2.add(i + 1);
        }
        st.add(s2);
        assertTrue(st.isFull());
        st.add(new IntLLString(k));
        assertFalse(st.isFull());
    }

    private void testContainsEmpty()
    {
        IntLLStringSet st = new IntLLStringSet(k);
        assertTrue(!st.containsEmpty());
        IntLLString s1 = new IntLLString(k);
        s1.add(1);
        st.add(s1);
        assertTrue(!st.containsEmpty());
        IntLLString s2 = new IntLLString(k);
        st.add(s2);
        assertTrue(st.containsEmpty());
        st.remove(s1);
        assertTrue(st.containsEmpty());
    }

    private void testintersects()
    {
        IntLLStringSet st1 = new IntLLStringSet(k);
        IntLLStringSet st2 = new IntLLStringSet(k);
        IntLLString s1 = new IntLLString(k);
        IntLLString s2 = new IntLLString(k);
        IntLLString s3 = new IntLLString(k);

        assertFalse(st1.intersects(st2));
        s1.add(1, 2);
        s2.add(3, 4);
        s3.add(4, 5);
        st1.add(s1);
        st1.add(s2);
        assertFalse(st1.intersects(st2));
        assertFalse(st2.intersects(st1));
        assertTrue(st1.intersects(st1));
        st2.add(s3);
        assertFalse(st1.intersects(st2));
        st2.add(s1);
        assertTrue(st1.intersects(st2));
    }

    private void testIntersect()
    {
        IntLLStringSet st1 = new IntLLStringSet(k);
        IntLLStringSet st2 = new IntLLStringSet(k);
        IntLLString s1 = new IntLLString(k);
        IntLLString s2 = new IntLLString(k);
        IntLLString s3 = new IntLLString(k);

        s1.add(1, 2);
        s2.add(3, 4);
        s3.add(4, 5);
        assertTrue(st1.intersect(st2).isEmpty());
        st1.add(s1);
        st1.add(s2);
        assertTrue(st1.intersect(st2).isEmpty());
        assertTrue(st2.intersect(st1).isEmpty());
        st2.add(s3);
        assertTrue(st1.intersect(st2).isEmpty());
        st2.add(s1);
        IntLLStringSet st = st1.intersect(st2);
        assertTrue(st.size() == 1);
        assertTrue(st.contains(s1));
    }
}
