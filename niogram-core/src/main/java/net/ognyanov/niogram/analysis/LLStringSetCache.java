/*
 * Copyright (c) 2018 by Nikolay Ognyanov. All rights reserved.
 * 
 * Use of this file is governed by the licensing conditions   
 * in the LICENSE.txt file in the root folder of the project.
 */
package net.ognyanov.niogram.analysis;

import java.util.ArrayDeque;
import java.util.Deque;

import net.ognyanov.niogram.ast.Grammar;
import net.ognyanov.niogram.util.IntLLStringSet;

class LLStringSetCache
{
    private static final int   MAX_BAG_SIZE = 100;
    private Deque<IntLLStringSet> bag          =
        new ArrayDeque<IntLLStringSet>();
    private Grammar            grammar      = null;
    private int                k            = 0;
    private int                created      = 0;
    private int                retrieved    = 0;
    private int                maxSize      = 0;

    public LLStringSetCache(Grammar grammar)
    {
        this.grammar = grammar;
        this.k = grammar.getK();
    }

    public IntLLStringSet get()
    {
        IntLLStringSet result = null;
        if (bag.size() == 0) {
            result = new IntLLStringSet(k, grammar);
            created++;
        }
        else {
            result = bag.pop();
        }
        retrieved++;
        return result;
    }

    public void put(IntLLStringSet llStringSet)
    {
        llStringSet.clear();
        if (bag.size() < MAX_BAG_SIZE) {
            bag.push(llStringSet);
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
        System.out.println("Created   LLSS: " + getCreatedLLSS());
        System.out.println("Retrieved LLSS: " + getRetrievedLLSS());
        System.out.println("Maximym size : " + maxSize);
    }
}
