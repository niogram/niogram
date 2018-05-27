package net.ognyanov.niogram.util;

import java.io.Serializable;

/*
 * Copyright (c) 2018 by Nikolay Ognyanov. All rights reserved.
 * 
 * Use of this file is governed by the licensing conditions   
 * in the LICENSE.txt file in the root folder of the project.
 */

/**
 * An utility container class for immutable object pairs.
 *
 * @param <F> Type of the first element in the pair.
 * @param <S> Type of the second element in the pair.
 */
public class Pair<F, S>
    implements Serializable
{
    private static final long serialVersionUID = 1L;

    private F                 first;
    private S                 second;

    public Pair(F first, S second)
    {
        this.first = first;
        this.second = second;
    }

    /**
     * Retrieves the first element of the pair
     * 
     * @return the first element of the pair
     */
    public F getFirst()
    {
        return first;
    }

    protected void setFirst(F first)
    {
        this.first = first;
    }

    /**
     * Retrieves the second element of the pair
     * 
     * @return the second element of the pair
     */
    public S getSecond()
    {
        return second;
    }

    protected void setSecond(S second)
    {
        this.second = second;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((first == null) ? 0 : first.hashCode());
        result = prime * result + ((second == null) ? 0 : second.hashCode());
        return result;
    }

    @SuppressWarnings("all")
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Pair other = (Pair) obj;
        if (first == null) {
            if (other.first != null)
                return false;
        }
        else if (!first.equals(other.first))
            return false;
        if (second == null) {
            if (other.second != null)
                return false;
        }
        else if (!second.equals(other.second))
            return false;
        return true;
    }

    @Override
    public String toString()
    {
        return "[" + (first != null ? first.toString() : "null") + ", "
                + (second != null ? second.toString() : "null") + "]";
    }
}
