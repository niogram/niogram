/*
 * Copyright (c) 2018 by Nikolay Ognyanov. All rights reserved.
 * 
 * Use of this file is governed by the licensing conditions   
 * in the LICENSE.txt file in the root folder of the project.
 */
package net.ognyanov.niogram.util;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

/**
 * A set of limited length strings. Used to represent FirstK/FollowK sets.
 *
 * @author Nikolay Ognyanov
 */
public final class IntLLStringSet
    extends HashSet<IntLLString>
    implements Serializable, Cloneable
{
    private static final long   serialVersionUID = 1L;
    private static final String SET_OPEN         = "{";
    private static final String SET_CLOSE        = "}";
    private static final String SET_SEPARATOR    = ".";
    private static final String STRING_SEPARATOR = SET_SEPARATOR;

    private IntLLString         empty            = null;
    private int                 limit            = 0;
    private TypeNameProvider    nameProvider     = null;

    /**
     * Create a new set.
     * 
     * @param limit - the length limit for {@link IntLLString}s 
     * contained in the set.
     */
    public IntLLStringSet(int limit)
    {
        if (limit <= 0) {
            throw new IllegalArgumentException("K must be positive");
        }
        this.limit = limit;
        this.empty = new IntLLString(limit);
    }

    /**
     * Create a new set.
     * 
     * @param limit - the length limit for {@link IntLLString}s contained in the set.
     * @param nameProvider a name provider for the set
     */
    public IntLLStringSet(int limit, TypeNameProvider nameProvider)
    {
        this(limit);
        this.nameProvider = nameProvider;
    }

    public IntLLStringSet(IntLLStringSet other)
    {
        this(other.limit, other.nameProvider);
        this.addAll(other);
    }

    /**
     * Retrieves the value of limit as set in the constructor.
     * 
     * @return the value of limit
     */
    public int limit()
    {
        return limit;
    }

    /**
     * Retrieves the name provider of this object.
     *
     * @return the name provider
     */
    public TypeNameProvider getNameProvider()
    {
        return nameProvider;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean add(IntLLString llString)
    {
        if (llString == null) {
            throw new IllegalArgumentException("null argument");
        }
        if (llString.limit() != limit) {
            throw new IllegalArgumentException("limits do not match");
        }
        return super.add(llString);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean addAll(Collection<? extends IntLLString> c)
    {
        boolean result = false;
        for (IntLLString string : c) {
            result |= add(string);
        }
        return result;
    }

    /**
     * Appends all strings of another set to all strings
     * of this object. The operation involves cloning of the content
     * strings before the appending is done.
     * 
     * @param other the set to be appended to this one
     * @return true if the operation resulted in change of the set;
     * false otherwise
     */
    public boolean append(IntLLStringSet other)
    {
        if (other == null) {
            throw new IllegalArgumentException();
        }
        if (other.limit != limit) {
            throw new IllegalArgumentException("sets have different K");
        }

        boolean result = false;

        if (other.isEmpty()) {
            result = !isEmpty();
            return result;
        }
        else if (isEmpty()) {
            result = addAll(other);
        }
        else if (other.size() == 1 && other.contains(empty)) {
            result = false;
        }
        else if (size() == 1 && containsEmpty()) {
            result = true;
            clear();
            addAll(other);
        }
        else {
            if (isEmpty()) {
                addAll(other);
            }
            else {
                IntLLStringSet newContent = new IntLLStringSet(limit);
                for (IntLLString s1 : this) {
                    for (IntLLString s2 : other) {
                        IntLLString newString = new IntLLString(s1);
                        newString.append(s2);
                        newContent.add(newString);
                    }
                }
                if (!newContent.equals(this)) {
                    clear();
                    super.addAll(newContent);
                    result = true;
                }
                newContent.clear();
            }
        }
        return result;
    }

    /**{@inheritDoc}*/
    @Override
    public void clear()
    {
        clear(limit);
    }

    private void clear(int k)
    {
        this.limit = k;
        this.empty = new IntLLString(k);
        super.clear();
    }

    boolean isFull()
    {
        boolean result = false;
        if (!isEmpty()) {
            if (!containsEmpty()) {
                result = true;
                for (IntLLString string : this) {
                    if (!string.isFull()) {
                        result = false;
                        break;
                    }
                }
            }
        }
        return result;
    }

    /**
     * Tests whether the set contains an empty string.
     * 
     * @return true if the set contains an empty string; false otherwise
     */
    public boolean containsEmpty()
    {
        return contains(empty);
    }

    /**
     * Tests whether any string in the set contains a
     * specified value at a specified position.
     * 
     * @param position the position at which the test is performed
     * @param value the value for which the test is performed
     * @return true if the test succeeds; false otherwise
     */
    public boolean containsAt(int position, int value)
    {
        boolean result = false;
        for (IntLLString string : this) {
            if (string.containsAt(value, position)) {
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * Adds to the set an empty string.
     */
    public void addEmpty()
    {
        add(empty);
    }

    /**
     * Removes from the set the empty string if present.
     */
    public void removeEmpty()
    {
        remove(empty);
    }

    /**
     * Calculates and returns the conflict set of this
     * object and the specified other object. That is -
     * the set of strings belonging to one of the two
     * sets for which a "match" exists in the other set.
     * Here a match is defined as a string of the same
     * length which coincide with the string under
     * consideration up to position k-1.
     * 
     * @param other the second argument of the operation
     * @return the conflict set
     */
    public IntLLStringSet conflict(IntLLStringSet other, int k)
    {
        if (other == null) {
            throw new IllegalArgumentException("null argument");
        }
        if (other.limit() != limit) {
            throw new IllegalArgumentException("limits do not match");
        }

        IntLLStringSet result = new IntLLStringSet(limit, nameProvider);
        for (IntLLString s1 : this) {
            for (IntLLString s2 : other) {
                if (s1.equalsTo(s2, k)) {
                    result.add(s1);
                    result.add(s2);
                }
            }
        }
        return result;
    }

    /**
     * Calculates and returns the conflict set of this
     * object and the specified other object. That is -
     * the set of strings belonging to one of the two
     * sets which are prefixes of some string in the other
     * set.
     * 
     * @param other the second argument of the operation
     * @return the conflict set
     */
    public IntLLStringSet conflict(IntLLStringSet other)
    {
        return conflict(other, other.limit());
    }

    boolean intersects(IntLLStringSet other)
    {
        if (other == null) {
            throw new IllegalArgumentException("null argument");
        }

        boolean result = false;
        for (IntLLString s1 : this) {
            for (IntLLString s2 : other) {
                if (s1.equals(s2)) {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Tests whether this set is less than or equal in relation to
     * another set. The definition of the partial ordering relation
     * being tested is: X <= Y if and only if every string in X is a
     * prefix of some string in Y.
     * 
     * @param other the set against which this one is tested
     * @return true if the ordering relation holds; otherwise false
     */
    public boolean isLE(IntLLStringSet other)
    {
        if (other == null) {
            throw new IllegalArgumentException();
        }
        if (other.limit != limit) {
            throw new IllegalArgumentException("sets have different K");
        }

        boolean result = true;
        if (isEmpty()) {
            result = true;
        }
        else if (other.isEmpty()) {
            result = false;
        }
        else {
            result = true;
            for (IntLLString s1 : this) {
                boolean gotMatch = false;
                for (IntLLString s2 : other) {
                    if (s2.equals(s1)) {
                        gotMatch = true;
                        break;
                    }
                }
                if (!gotMatch) {
                    result = false;
                    break;

                }
            }
        }

        return result;
    }

    IntLLStringSet intersect(IntLLStringSet other)
    {
        if (other == null || other.limit() != limit) {
            throw new IllegalArgumentException();
        }

        IntLLStringSet result = new IntLLStringSet(limit);
        for (IntLLString s1 : this) {
            for (IntLLString s2 : other) {
                if (s1.equals(s2)) {
                    result.add((IntLLString) s1.clone());
                }
            }
        }
        return result;
    }

    public String toString(TypeNameProvider nameProvider)
    {
        TypeNameProvider theProvider = nameProvider;
        if (theProvider == null) {
            theProvider = this.nameProvider;
        }

        IntLLString[] strings =
            toArray(new IntLLString[size()]);
        Arrays.sort(strings);
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        sb.append(SET_OPEN);
        for (int i = 0; i < strings.length; i++) {
            if (first) {
                first = false;
            }
            else {
                sb.append(STRING_SEPARATOR);
            }
            sb.append(strings[i].toString(theProvider));
        }
        sb.append(SET_CLOSE);
        return sb.toString();
    }

    @Override
    public Object clone()
    {
        Object result = super.clone();
        ((IntLLStringSet) result).limit = limit;
        ((IntLLStringSet) result).nameProvider = nameProvider;
        ((IntLLStringSet) result).empty = new IntLLString(limit);
        return result;
    }

    @Override
    public String toString()
    {
        return toString(null);
    }
}
