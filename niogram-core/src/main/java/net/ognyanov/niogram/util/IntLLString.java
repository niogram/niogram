/*
 * Copyright (c) 2018 by Nikolay Ognyanov. All rights reserved.
 * 
 * Use of this file is governed by the licensing conditions   
 * in the LICENSE.txt file in the root folder of the project.
 */
package net.ognyanov.niogram.util;

import java.io.Serializable;
import java.util.Iterator;

/**
 * A limited length string of integers. Used in LL(k) analysis.
 *
 * @author Nikolay Ognyanov
 */
public final class IntLLString
    implements Cloneable, Serializable, Comparable<IntLLString>,
    Iterable<Integer>
{
    private static final long   serialVersionUID = 1L;

    private static final char   SEQ_START        = '[';
    private static final char   SEQ_END          = ']';
    private static final String SEQ_SEPARATOR    = ",";

    private int[]               string           = null;
    private int                 limit            = 0;
    private int                 length           = 0;
    private TypeNameProvider    nameProvider     = null;

    /**
     * Create a new object with a specified limit of the string length.
     * 
     * @param limit the limit
     */
    public IntLLString(int limit)
    {
        if (limit < 1) {
            throw new IllegalArgumentException();
        }
        string = new int[limit];
        this.limit = limit;
        length = 0;
    }

    /**
     * Create a new object with a specified limit of the string length
     * which uses a specified name provider for string representation.
     * 
     * @param limit the limit
     * @param nameProvider the name providers
     */
    public IntLLString(int limit, TypeNameProvider nameProvider)
    {
        this(limit);
        this.nameProvider = nameProvider;
    }

    /**
     * Create a new object which is a copy of another one.
     * 
     * @param other the object to be copied
     */
    public IntLLString(IntLLString other)
    {
        if (other == null) {
            throw new IllegalArgumentException();
        }

        string = new int[other.string.length];
        System.arraycopy(other.string, 0, string, 0, string.length);
        limit = other.limit;
        length = other.length;
        nameProvider = other.nameProvider;
    }

    /**
     * Retrieves the limit to the length of the sting.
     * 
     * @return the limit
     */
    public int limit()
    {
        return limit;
    }

    /**
     * Retrieves the current length of the string.
     * 
     * @return the current length
     */
    public int length()
    {
        return length;
    }

    /**
     * Tests whether the object is empty, i.e. - of zero length.
     * 
     * @return the result of the test
     */
    public boolean isEmpty()
    {
        return length == 0;
    }

    /**
     * Tests whether the object is empty,
     * i.e. - its length equals its limit.
     * 
     * @return the result of the test
     */
    public boolean isFull()
    {
        return length == limit;
    }

    public void add(int... elements)
    {
        @SuppressWarnings("unused")
        boolean result = false;
        for (int element : elements) {
            if (length < limit) {
                string[length++] = element;
                result = true;
            }
        }
    }

    /**
     * Appends another string at the end of this one.
     * 
     * @param other the string to be appended
     */
    public void append(IntLLString other)
    {
        if (other == null) {
            throw new IllegalArgumentException();
        }
        if (other.limit != limit) {
            throw new IllegalArgumentException("argument has different limit");
        }
        @SuppressWarnings("unused")
        boolean result = false;
        if (length < limit && other.length > 0) {
            result = true;
            int newLength = Math.min(length + other.length, limit);
            System.arraycopy(other.string, length - length, string, length,
                newLength - length);
            length = newLength;
        }
    }

    /**
     * Retrieves the string element at given position.
     * 
     * @param position the position
     * @return the element at the position
     */
    public int get(int position)
    {
        if (position < 0 || position >= length) {
            throw new IllegalArgumentException();
        }
        return string[position];
    }

    /**
     * Tests whether the object contains a specified value.
     * 
     * @param value the value to be looked for
     * @return true if the vaule is contained in the string; false otherwise
     */
    public boolean contains(int value)
    {
        boolean result = false;
        for (int i = 0; i < length; i++) {
            if (string[i] == value) {
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * Tests whether the object contains a specified value
     * at a specified position.
     * 
     * @param value the value to be looked for
     * @return true if the vaule is contained in the string; false otherwise
     */
    public boolean containsAt(int value, int position)
    {
        boolean result = false;
        if (position >= length) {
            result = false;
        }
        else {
            result = string[position] == value;
        }
        return result;
    }

    /**
     * Tests whether another string is a prefix to this.
     * 
     * @param other the other string to be tested
     * @return true if the other string is a prefix to
     * this; false otherwise
     */
    public boolean startsWith(IntLLString other)
    {
        if (other == null) {
            throw new IllegalArgumentException("null argument");
        }

        boolean result = true;
        if (other.length() > length()) {
            result = false;
        }
        else {
            for (int i = 0; i < other.length(); i++) {
                if (other.get(i) != this.get(i)) {
                    result = false;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Tests whether the prefix of length k of
     * another string (or the whole other string
     * if its length is less than k) is a prefix
     * of this one.
     * 
     * @param other the other string
     * @param k the length of the other string prefix
     * @return true if test passes; false otherwise
     */
    boolean equalsTo(IntLLString other, int k)
    {
        if (other == null) {
            throw new IllegalArgumentException();
        }
        if (other.limit != limit) {
            throw new IllegalArgumentException("argument has different limit");
        }

        boolean result = true;
        int cmpLength = Math.min(k, length);
        if (this.length != other.length) {
            result = false;
        }
        else if (other == this) {
            result = true;
        }
        else {
            for (int i = 0; i < cmpLength; i++) {
                if (string[i] != other.string[i]) {
                    result = false;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Clears the content of the object.
     */
    public void clear()
    {
        length = 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(IntLLString other)
    {
        if (other == null) {
            throw new IllegalArgumentException("null argument");
        }
        if (other.limit != this.limit) {
            throw new IllegalArgumentException("strings have different limits");
        }
        int t;
        int o;
        int result = 0;
        int cmpLength = Math.min(length, other.length);

        for (int i = 0; i < cmpLength; i++) {
            t = string[i];
            o = other.string[i];
            if (t < o) {
                result = -1;
                break;
            }
            else if (t > o) {
                result = 1;
                break;
            }
        }
        if (result == 0) {
            if (length < other.length) {
                result = -1;
            }
            else if (length > other.length) {
                result = 1;
            }
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<Integer> iterator()
    {
        return new TSIterator();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return toString(null);
    }

    /**
     * Convert the object to string using the specified type
     * name provider.
     * 
     * @param nameProvider the name provider to be used.
     * @return a string representation of the object
     */
    public String toString(TypeNameProvider nameProvider)
    {
        TypeNameProvider theNameProvider = nameProvider;
        if (theNameProvider == null) {
            theNameProvider = this.nameProvider;
        }
        StringBuilder sb = new StringBuilder();

        boolean first = true;
        sb.append(SEQ_START);
        for (int i = 0; i < length; i++) {
            if (first) {
                first = false;
            }
            else {
                sb.append(SEQ_SEPARATOR);
            }
            int terminal = string[i];
            String name =
                theNameProvider != null ? theNameProvider.getTypeName(terminal)
                                        : Integer.toString(terminal);
            sb.append(name);
        }
        sb.append(SEQ_END);
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object clone()
    {
        return new IntLLString(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + stringHashCode();
        return result;
    }

    private int stringHashCode()
    {
        int result = 1;
        for (int i = 0; i < length; i++) {
            result = 31 * result + string[i];
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        return compareTo((IntLLString) obj) == 0;
    }

    private class TSIterator
        implements Iterator<Integer>
    {
        private int position = 0;

        @Override
        public boolean hasNext()
        {
            return position < length;
        }

        @Override
        public Integer next()
        {
            if (!hasNext()) {
                throw new ArrayIndexOutOfBoundsException();
            }
            return string[position++];
        }

        @Override
        public void remove()
        {
            throw new UnsupportedOperationException();
        }
    }
}
