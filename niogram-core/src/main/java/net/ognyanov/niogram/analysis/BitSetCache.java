/*
 * Copyright (c) 2018 by Nikolay Ognyanov. All rights reserved.
 * 
 * Use of this file is governed by the licensing conditions   
 * in the LICENSE.txt file in the root folder of the project.
 */
package net.ognyanov.niogram.analysis;

import java.util.ArrayDeque;
import java.util.Deque;

import net.ognyanov.niogram.ast.BuiltInTypes;
import net.ognyanov.niogram.ast.Grammar;
import net.ognyanov.niogram.util.BiasedBitSet;

class BitSetCache
{
    private static final int    MAX_BAG_SIZE = 100;
    private Deque<BiasedBitSet> bag          =
        new ArrayDeque<BiasedBitSet>();
    private Grammar             grammar      = null;
    private int                 created      = 0;
    private int                 retrieved    = 0;
    private int                 maxSize      = 0;

    public BitSetCache(Grammar grammar)
    {
        this.grammar = grammar;
    }

    public BiasedBitSet get()
    {
        BiasedBitSet result = null;
        if (bag.size() == 0) {
            result = new BiasedBitSet(BuiltInTypes.MIN_TYPE, grammar);
            created++;
        }
        else {
            result = bag.pop();
        }
        retrieved++;
        return result;
    }

    public void put(BiasedBitSet bitSet)
    {
        bitSet.clear();
        if (bag.size() < MAX_BAG_SIZE) {
            bag.push(bitSet);
            int size = bag.size();
            if (size > maxSize) {
                maxSize = size;
            }
        }
    }

    public void clear()
    {
        bag.clear();
        created = 0;
        retrieved = 0;
    }

    public int getCreatedLLSS()
    {
        return created;
    }

    public int getRetrievedLLSS()
    {
        return retrieved;
    }

    public void printDiagnostics()
    {
        System.out.println("Created    BS: " + getCreatedLLSS());
        System.out.println("Retrieved  BS: " + getRetrievedLLSS());
        System.out.println("Maximym size : " + maxSize);
    }
}
