package net.ognyanov.niogram.util;
/*
 * Copyright (c) 2018 by Nikolay Ognyanov. All rights reserved.
 * 
 * Use of this file is governed by the licensing conditions   
 * in the LICENSE.txt file in the root folder of the project.
 */

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A bidirectional map utility class.<p>
 * Null entries are not allowed.
 *
 * @author Nikolay Ognyanov
 */
public final class BidirectionalMap<F, S>
    implements Serializable
{
    private static final long serialVersionUID = 1L;

    private Map<F, S>         fToS             = new HashMap<F, S>();
    private Map<S, F>         sToF             = new HashMap<S, F>();

    /**
     * Stores the mapping of two values in the map
     * 
     * @param first first element
     * @param second second element
     */
    public void put(F first, S second)
    {
        if (first == null || second == null) {
            throw new IllegalArgumentException("null argument");
        }
        if (fToS.containsKey(first)) {
            sToF.remove(fToS.get(first));
            fToS.remove(first);
        }
        if (sToF.containsKey(second)) {
            fToS.remove(sToF.get(second));
            sToF.remove(second);
        }
        fToS.put(first, second);
        sToF.put(second, first);
        if (fToS.size() != sToF.size()) {
            throw new IllegalStateException("internal error");
        }
    }

    /**
     * Stores a mapping between the two elements of a pair object.
     * 
     * @param pair the pair to be stored
     */
    public void put(Pair<F, S> pair)
    {
        put(pair.getFirst(), pair.getSecond());
    }

    /**
     * Tests whether the map contains a mapping by first element
     * 
     * @param first the key to be checked
     * 
     * @return true if the map contains the specified key
     */
    public boolean containsFirst(F first)
    {
        if (first == null) {
            throw new IllegalArgumentException("null argument");
        }
        return fToS.containsKey(first);
    }

    /**
     * Tests whether the map contains a mapping by second element
     * 
     * @param second the key to be checked
     * @return true if the map contains the specified key
     */
    public boolean containsSecond(S second)
    {
        if (second == null) {
            throw new IllegalArgumentException("null argument");
        }
        return sToF.containsKey(second);
    }

    /**
     * Retrieves the second element of a mapped pair
     * 
     * @param first the key
     * @return the value if any or null
     */
    public S getSecond(F first)
    {
        if (first == null) {
            throw new IllegalArgumentException("null argument");
        }
        return this.fToS.get(first);
    }

    /**
     * Retrieves the first element of a mapped pair
     * 
     * @param second the key
     * @return the value if any or null
     */
    public F getFirst(S second)
    {
        if (second == null) {
            throw new IllegalArgumentException("null argument");
        }
        return this.sToF.get(second);
    }

    /**
     * Retrieves the set the first elements of the pairs in the map.
     * 
     * @return an unmodifiable set of the first elements
     */
    public Set<F> firstSet()
    {
        return Collections.unmodifiableSet(fToS.keySet());
    }

    /**
     *  Retrieves the set the second elements of the pairs in the map.
     * 
     * @return an unmodifiable set of the second elements
     */
    public Set<S> secondSet()
    {
        return Collections.unmodifiableSet(sToF.keySet());
    }

    /**
     * Removes from the map a pair (if any) with given first element
     * 
     * @param first the key
     */
    public void removeFirst(F first)
    {
        if (first == null) {
            throw new IllegalArgumentException("null argument");
        }
        S second = fToS.get(first);
        fToS.remove(first);
        sToF.remove(second);
    }

    /**
     * Removes from the map a pair (if any) with given second element
     * 
     * @param second the key
     */
    public void removeSecond(S second)
    {
        if (second == null) {
            throw new IllegalArgumentException("null argument");
        }
        F first = sToF.get(second);
        fToS.remove(first);
        sToF.remove(second);
    }

    /**
     * Removes all mappings
     */
    public void clear()
    {
        fToS.clear();
        sToF.clear();
    }

    /**
     * Retrieves the size of the map
     * 
     * @return the size
     */
    public int size()
    {
        return fToS.size();
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((fToS == null) ? 0 : fToS.hashCode());
        result = prime * result + ((sToF == null) ? 0 : sToF.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        @SuppressWarnings("rawtypes")
        BidirectionalMap other = (BidirectionalMap) obj;
        if (fToS == null) {
            if (other.fToS != null)
                return false;
        }
        else if (!fToS.equals(other.fToS))
            return false;
        if (sToF == null) {
            if (other.sToF != null)
                return false;
        }
        else if (!sToF.equals(other.sToF))
            return false;
        return true;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        sb.append(fToS.toString());
        sb.append(',');
        sb.append(sToF.toString());
        sb.append(']');
        return sb.toString();
    }
}
