/*
 * Copyright (c) 2018 by Nikolay Ognyanov. All rights reserved.
 * 
 * Use of this file is governed by the licensing conditions   
 * in the LICENSE.txt file in the root folder of the project.
 */
package net.ognyanov.niogram.util;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * A limited length string of bitsets. Used to represent
 * linearized FirstKL/FollowKL "sets". In these strings
 * the terminal sets at each position contain all the
 * terminals (and only the terminals) which occur at the
 * same position in some string of the corresponding 
 * FirstK/FollowK set. <p>From mathematical standpoint the
 * FirstK and the FollowK sets are solutions to some systems
 * of functional equations. The operations on strings of sets
 * are defined so as to be isomorphic to the operations
 * on sets of strings used in those systems of equations.
 * Implementation details of individual operations  are
 * described in the methods documentation. The main
 * implementation idea is to associate with every string
 * of sets a list of string lengths which appear in an isomorphic
 * set of strings.<p>
 * 
 * @author Nikolay Ognyanov
 */
public final class BitSetLLString
    implements Cloneable, Serializable, Iterable<BiasedBitSet>

{
    private static final long   serialVersionUID = 1L;

    private static final char   SEQ_START        = '[';
    private static final char   SEQ_END          = ']';
    private static final char   SET_START        = '{';
    private static final char   SET_END          = '}';
    private static final String SEQ_SEPARATOR    = ",";

    private BiasedBitSet[]      string           = null;
    private int                 length           = 0;
    private int                 limit            = 0;
    private Set<Integer>        stringLengths    = new HashSet<Integer>();
    private TypeNameProvider    nameProvider     = null;

    /**
     * Create a new object with a specified limit of the string length.
     * 
     * @param limit the limit
     */
    public BitSetLLString(int limit)
    {
        this(limit, null);
    }

    /**
     * Create a new object with a specified limit of the string length
     * which uses a specified name provider for string representation.
     * 
     * @param limit the limit
     * @param nameProvider the name providers
     */
    public BitSetLLString(int limit, TypeNameProvider nameProvider)
    {
        if (limit < 1) {
            throw new IllegalArgumentException();
        }
        string = new BiasedBitSet[limit];
        this.limit = limit;
        this.nameProvider = nameProvider;
    }

    /**
     * Create a new object which is a deep copy of another one.
     * 
     * @param other the object to be copied
     */
    public BitSetLLString(BitSetLLString other)
    {
        if (other == null) {
            throw new IllegalArgumentException();
        }

        limit = other.limit;
        length = other.length;
        nameProvider = other.nameProvider;
        string = new BiasedBitSet[other.string.length];
        for (int i = 0; i < length; i++) {
            string[i] = new BiasedBitSet(other.string[i]);
        }
        stringLengths.addAll(other.stringLengths);
    }

    /**
     * Create an object which is an isomorphic projection
     * of a set of string.
     * 
     * @param stringSet the source set of strings
     * @param start the start index of the bitsets
     * to be produced.
     */
    public BitSetLLString(IntLLStringSet stringSet, int start)
    {
        if (stringSet == null) {
            throw new IllegalArgumentException("null argument");
        }
        if (start > 0) {
            throw new IllegalArgumentException("start index can not be positive");
        }
        this.limit = stringSet.limit();
        this.length = 0;
        this.string = new BiasedBitSet[limit];
        this.nameProvider = stringSet.getNameProvider();
        int maxLength = 0;
        for (IntLLString s : stringSet) {
            int len = s.length();
            if (len > maxLength) {
                maxLength = len;
            }
        }
        this.length = maxLength;
        for (int i = 0; i < maxLength; i++) {
            string[i] = new BiasedBitSet(start, nameProvider);
        }
        for (int position = 0; position < length; position++) {
            string[position] = new BiasedBitSet(start);
        }
        for (IntLLString s : stringSet) {
            for (int i = 0; i < s.length(); i++) {
                string[i].set(s.get(i));
            }
            stringLengths.add(s.length());
        }
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
     * Retrieves an unmodifiable set of the string lengths for this object.
     * 
     * @return the set of string lengths
     */
    public Set<Integer> stringLengths()
    {
        return stringLengths;
    }

    /**
     * Tests whether the object is empty. This means that that
     * the both the underlying sequence of {@link BiasedBitSet}
     * and the set of string lengths are empty. If the sequence
     * of {@link BiasedBitSet} is empty the set of string lengths
     * may still contain the zero length which simulates the presence
     * of an empty string in a "dual" {@link IntLLStringSet}.
     * 
     * @return the result of the test
     */
    public boolean isEmpty()
    {
        return length == 0 && !stringLengths.contains(0);
    }

    /**
     * Tests whether the current lenght of the string is
     * equal to its limit.
     * 
     * @return true if the length is equal to the limit;
     * false otherwise
     */
    public boolean isFull()
    {
        return length == limit;
    }

    /**
     * Appends copies of argument {@link BiasedBitSet}s
     * at the beginning of the string. The length of the
     * argument sequence is added to the set of string
     * lengths of this object. Adding a zero-length 
     * argument sequence is treated as an addition of
     * an empty string to the pseudo "set". Thus the
     * method is isomorphic to LLStringSet.add(IntLLString)}.
     * In practice this does not matter because the NioGram
     * core code only uses this method to initialize the
     * firstKL/followKL fields content of terminal rules
     * with a single bitset containing a single terminal symbol.
     * 
     * @param bitSets the sequence of bitsets to be added.
     */
    public void add(BiasedBitSet... bitSets)
    {
        if (bitSets == null) {
            throw new IllegalArgumentException("null argument");
        }
        if (bitSets.length == 0) {
            stringLengths.add(0);
            return;
        }
        int otherLength = bitSets.length;
        addAt(bitSets, 0, otherLength);
        stringLengths.add(otherLength);
    }

    /**
     * Adds the elements of the other string at the beginning of
     * this string. Adds all string lengths of the other string
     * to the set of lengths of this one.
     * 
     * @param other the string to be added to this
     */
    public void addAll(BitSetLLString other)
    {
        if (other == null) {
            throw new IllegalArgumentException("null argument");
        }
        if (other.limit != limit) {
            throw new IllegalArgumentException("limits differ");
        }
        try {
            addAt(other.string, 0, other.length);
        }
        catch (IllegalArgumentException e) {
            throw e;
        }
        stringLengths.addAll(other.stringLengths);
    }

    private void addAt(BiasedBitSet[] other, int start, int otherLenght)
    {
        int newLength = Math.max(Math.min(start + otherLenght, limit), length);
        int firstStop = Math.min(start + otherLenght, length);

        for (int i = start; i < firstStop; i++) {
            string[i].or(other[i - start]);
        }
        for (int i = length; i < newLength; i++) {
            BiasedBitSet bbs = other[i - start];
            if (bbs == null) {
                throw new IllegalArgumentException();
            }
            string[i] = new BiasedBitSet(bbs);
        }
        length = newLength;
    }

    /**
     * Adds the other string at all positions in the set of string
     * lengths of this string. Then creates a new list of string
     * lengths which consists of all sums of a string length from
     * this string and a string length from the other string.
     * 
     * @param other the string to be appended to this one
     */
    public void append(BitSetLLString other)
    {
        if (other == null) {
            throw new IllegalArgumentException("null argument");
        }
        if (other.limit != limit) {
            throw new IllegalArgumentException("limits differ");
        }

        if (other.isEmpty()) {
        }
        else if (isEmpty()) {
            addAt(other.string, 0, other.length);
            stringLengths.addAll(other.stringLengths);
        }
        else {
            for (int len : stringLengths) {
                addAt(other.string, len, other.length);
            }
            Set<Integer> newLengths = new HashSet<Integer>();
            for (int thisLength : stringLengths) {
                for (int otherLenght : other.stringLengths) {
                    int newLenght = thisLength + otherLenght;
                    newLengths.add(Math.min(newLenght, limit));
                }
            }
            stringLengths = newLengths;
        }
    }

    /**
     * Retrieves the {@link BiasedBitSet} at given position
     * in the string. Throws IndexOutOfBoundsException if
     * the specified position is invalid.
     * 
     * @param position the position
     * @return the bitset at the specified position
     */
    public BiasedBitSet get(int position)
    {
        if (position < 0 || position >= length) {
            throw new IndexOutOfBoundsException();
        }
        return string[position];
    }

    /**
     * Tests whether the object contains a specified value.
     * 
     * @param value the value to be looked for
     * @return true if the vaule is contained in the string; false otherwise
     */
    public boolean contains(BiasedBitSet value)
    {
        if (value == null) {
            throw new IllegalArgumentException("null argument");
        }
        boolean result = false;
        for (int i = 0; i < length; i++) {
            if (string[i].equals(value)) {
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * Tests whether the bitset at the specified position
     * (if any) contains the specified value.
     * 
     * @param position the position at which the test is performed
     * @param value the value for which the test is performed
     * @return true if the test succeeds; false otherwise
     */
    public boolean containsAt(int position, int value)
    {
        boolean result = false;
        if (position < length) {
            result = get(position).get(value);
        }
        return result;
    }

    public boolean startsWith(BitSetLLString other)
    {
        if (other == null) {
            throw new IllegalArgumentException();
        }
        if (other.limit != limit) {
            throw new IllegalArgumentException("argument has different limit");
        }

        boolean result = true;
        if (other == this) {
        }
        else if (other.length == 0) {
        }
        else if (this.length == 0) {
            result = false;
        }
        else if (other.length > this.length) {
            result = false;
        }
        else {
            for (int i = 0; i < other.length; i++) {
                if (!string[i].equals(other.string[i])) {
                    result = false;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Tests whether this set is less than or equal in relation to
     * another set. The definition of the partial ordering relation
     * being tested is: X <= Y if and only X is not longer than Y
     * and the bit set at every position in X is a subset of the
     * bitset at the same position in Y.
     * 
     * @param other the set against which this one is tested
     * @return true if the ordering relation holds; otherwise false
     */
    public boolean isLE(BitSetLLString other)
    {
        if (other == null) {
            throw new IllegalArgumentException();
        }
        if (other.limit() != limit) {
            throw new IllegalArgumentException("sets have different K");
        }

        boolean result = true;
        if (isEmpty()) {
        }
        else if (length > other.length()) {
            result = false;
        }
        else {
            for (int i = 0; i < length; i++) {
                if (!other.string[i].contains(string[i])) {
                    result = false;
                    break;
                }
            }
        }
        return result;

    }

    /**
     * Calculates and returns the conflict between this
     * string and the other specified.
     * 
     * @param other the second argument of the operation
     * @return the conflict string
     */
    public BitSetLLString conflict(BitSetLLString other, int k)
    {
        if (other == null) {
            throw new IllegalArgumentException("null argument");
        }
        if (other.limit != limit) {
            throw new IllegalArgumentException("limits differ");
        }

        BitSetLLString result = new BitSetLLString(limit, nameProvider);

        if (this.length != other.length
                || this.containsEmpty() != other.containsEmpty()) {
            return new BitSetLLString(limit, nameProvider);
        }

        boolean thisHasEmpty = this.containsEmpty();
        boolean otherHasEmpty = other.containsEmpty();
        int andLength = Math.min(length, k);
        for (int i = 0; i < andLength; i++) {
            result.string[i] =
                new BiasedBitSet(this.string[i]);
        }
        result.length = andLength;
        result.stringLengths.add(andLength);
        for (int i = 0; i < andLength; i++) {
            result.string[i].and(other.string[i]);
            if (result.string[i].isEmpty()) {
                result.clear();
                break;
            }
        }
        if (thisHasEmpty && otherHasEmpty) {
            result.addEmpty();
        }
        else {
            result.removeEmpty();
        }
        return result;
    }

    /**
     * Tests whether this object contains the representation of the
     * empty string. That is - a length of zero in the set of string
     * lengths.
     * 
     * @return the result of the test
     */
    public boolean containsEmpty()
    {
        return stringLengths.contains(0);
    }

    /**
     * Adds the representation of the empty string to this object.
     * 
     * @return true if the object has changed; false otherwise
     */
    public boolean addEmpty()
    {
        return stringLengths.add(0);
    }

    /**
     * Removes the representation of the empty string to this object.
     * 
     * @return true if the object has changed; false otherwise
     */
    public boolean removeEmpty()
    {
        return stringLengths.remove(0);
    }

    /**
     * Clears the content of the object.
     */
    public void clear()
    {
        for (int i = 0; i < limit; i++) {
            string[i] = null;
        }
        length = 0;
        stringLengths.clear();
    }

    /**
     * {@inheritDoc}
     */
    public Iterator<BiasedBitSet> iterator()
    {
        return new TSIterator();
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
        if (containsEmpty()) {
            sb.append(SET_START);
            sb.append(SET_END);
            sb.append('|');
        }
        for (int i = 0; i < length; i++) {
            if (first) {
                first = false;
            }
            else {
                sb.append(SEQ_SEPARATOR);
            }
            sb.append(string[i].toString(theNameProvider));
        }
        sb.append(SEQ_END);
        return sb.toString();
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
     * {@inheritDoc}
     */
    @Override
    public Object clone()
    {
        return new BitSetLLString(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + length;
        result = prime * result + limit;
        result = prime * result + Arrays.hashCode(string);
        result = prime * result
                + ((stringLengths == null) ? 0 : stringLengths.hashCode());
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
        BitSetLLString other = (BitSetLLString) obj;
        if (length != other.length)
            return false;
        if (limit != other.limit)
            return false;
        if (!Arrays.equals(string, other.string))
            return false;
        if (stringLengths == null) {
            if (other.stringLengths != null)
                return false;
        }
        else if (!stringLengths.equals(other.stringLengths))
            return false;
        return true;
    }

    private class TSIterator
        implements Iterator<BiasedBitSet>
    {
        private int position = 0;

        @Override
        public boolean hasNext()
        {
            return position < length;
        }

        @Override
        public BiasedBitSet next()
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
