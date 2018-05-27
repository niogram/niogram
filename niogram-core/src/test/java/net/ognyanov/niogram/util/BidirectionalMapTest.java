package net.ognyanov.niogram.util;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import net.ognyanov.niogram.util.BidirectionalMap;

public class BidirectionalMapTest
{
    @Test
    public void test()
    {
        BidirectionalMap<Integer, String> iToS =
            new BidirectionalMap<Integer, String>();

        iToS.put(1, "one");
        iToS.put(2, "two");

        assertTrue(iToS.containsFirst(1));
        assertTrue(iToS.containsSecond("one"));
        assertTrue(iToS.getSecond(1).equals("one"));
        assertTrue(iToS.getFirst("one") == 1);

        assertTrue(iToS.containsFirst(2));
        assertTrue(iToS.containsSecond("two"));
        assertTrue(iToS.getSecond(2).equals("two"));
        assertTrue(iToS.getFirst("two") == 2);

        iToS.removeFirst(1);
        assertTrue(iToS.size() == 1);
        assertTrue(iToS.getSecond(1) == null);
        assertTrue(iToS.getFirst("one") == null);

        iToS.removeSecond("two");
        assertTrue(iToS.size() == 0);
        assertTrue(iToS.getSecond(2) == null);
        assertTrue(iToS.getFirst("two") == null);

        iToS.put(1, "one");
        iToS.put(2, "two");
        assertTrue(iToS.size() == 2);
        iToS.clear();
        assertTrue(iToS.size() == 0);
    }
}
