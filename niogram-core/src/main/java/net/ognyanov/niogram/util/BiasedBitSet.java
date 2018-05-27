package net.ognyanov.niogram.util;
/*
 * Copyright (c) 2018 by Nikolay Ognyanov. All rights reserved.
 * 
 * Use of this file is governed by the licensing conditions   
 * in the LICENSE.txt file in the root folder of the project.
 */

import java.io.Serializable;
import java.util.BitSet;

/**
 * A biased bitset utility class.<p>
 * Wraps a java.util.BitSet object and  relays calls to it.
 * A fixed bias is added to index arguments  and subtracted
 * from index results. Thus the bitset can have a limited number
 * of negative indices. The desired value of the negative start
 * index of the bitset is set at object creation time with
 * the default being -16. The method {@link #getStart()}
 * retrieves the start index and the method {@link #getNone()}
 * retrieves an index value which plays the same role as -1 in
 * the API of java.util.BitSet.<p>
 * For description of the API see java.util.BitSet. Constructors
 * and static methods have variations which take the start index
 * of the bitset as an argument. Those which have no start argument
 * use the default value of -16 for start.<p>
 * Note that binary operations between objects of this class will
 * only succeed if the two objects have the same bias. Otherwise
 * an IllegalArgumentException will be thrown.
 * 
 * @author Nikolay Ognyanov
 */
public class BiasedBitSet
    implements Serializable, Cloneable
{
    private static final long serialVersionUID  = 1L;

    public static final int   DEFAULT_START     = -16;

    private static final char SET_OPEN          = '{';
    private static final char SET_CLOSE         = '}';
    private static final char ELEMENT_SEPARATOR = '.';

    private BitSet            bitSet            = null;
    private int               start             = 0;   // 
    private int               bias              = 0;   // -start
    private int               none              = 0;   // start - 1
    private TypeNameProvider  nameProvider      = null;

    /**
     * Creates a bitset with the default start index.
     */
    public BiasedBitSet()
    {
        this(DEFAULT_START);
    }

    /**
     * Creates a bitset with the default start index.
     * 
     * @param nameProvider a name provider for the bitset
     */
    public BiasedBitSet(TypeNameProvider nameProvider)
    {
        this(DEFAULT_START);
        this.nameProvider = nameProvider;
    }

    /**
     * Creates a bit set with a specified start index.
     *
     * @param start the start index
     */
    public BiasedBitSet(int start)
    {
        if (start > 0) {
            throw new IllegalArgumentException("start may not be positive");
        }
        this.start = start;
        this.bias = -start;
        this.none = start - 1;
        bitSet = new BitSet();
    }

    /**
     * Creates a bit set with a specified start index.
     *
     * @param start the start index
     * @param nameProvider a name provider for the bitset
     */
    public BiasedBitSet(int start, TypeNameProvider nameProvider)
    {
        this(start);
        this.nameProvider = nameProvider;
    }

    /**
     * Creates a bit set whose initial size is large enough
     * to explicitly represent bits with indices in the range 0
     * through nbits-1.
     * 
     * @param nBits the initial size of the bit set
     * @param start the start index
     */
    public BiasedBitSet(int nBits, int start)
    {
        if (start > 0) {
            throw new IllegalArgumentException("start may not be positive");
        }
        this.start = start;
        this.bias = -start;
        this.none = start - 1;
        bitSet = new BitSet(nBits);
    }

    /**
     * Creates a bit set whose initial size is large enough
     * to explicitly represent bits with indices in the range 0
     * through nbits-1.
     * 
     * @param nBits the initial size of the bit set
     * @param start the start index
     * @param nameProvider a name provider for the bitset
     */
    public BiasedBitSet(int nBits, int start, TypeNameProvider nameProvider)
    {
        this(nBits, start);
        this.nameProvider = nameProvider;
    }

    /**
     * Create a bitset which is a copy of another bitset.
     * 
     * @param other the original ot be copied
     */
    public BiasedBitSet(BiasedBitSet other)
    {
        if (other == null) {
            throw new IllegalArgumentException("null argument");
        }
        bitSet = (BitSet) other.bitSet.clone();
        this.start = other.start;
        this.bias = -start;
        this.none = start - 1;
        this.nameProvider = other.nameProvider;
    }

    private BiasedBitSet(BitSet bitSet, int start)
    {
        this(start);
        this.bitSet = bitSet;
    }

    /**
     * Retrieves the start index of the bitset.
     * 
     * @return the start index
     */
    public int getStart()
    {
        return start;
    }

    /**
     * Retrieves the non-existent index which is
     * returned in cases when the regular BitSet
     * returns -1.
     * 
     * @return the index
     */
    public int getNone()
    {
        return none;
    }

    /*
    // Available since Java 1.7 and NioGram tries to stay
    // at the same code level as ANTLR which is 1.6
    public static BiasedBitSet valueOf(byte[] bytes)
    {
        return new BiasedBitSet(BitSet.valueOf(bytes), DEFAULT_START);
    }
    
    public static BiasedBitSet valueOf(ByteBuffer bb)
    {
        return new BiasedBitSet(BitSet.valueOf(bb), DEFAULT_START);
    }
    
    public static BiasedBitSet valueOf(long[] longs)
    {
        return new BiasedBitSet(BitSet.valueOf(longs), DEFAULT_START);
    }
    
    public static BiasedBitSet valueOf(LongBuffer lb)
    {
        return new BiasedBitSet(BitSet.valueOf(lb), DEFAULT_START);
    }
    
    public static BiasedBitSet valueOf(byte[] bytes, int start)
    {
        return new BiasedBitSet(BitSet.valueOf(bytes), start);
    }
    
    public static BiasedBitSet valueOf(ByteBuffer bb, int start)
    {
        return new BiasedBitSet(BitSet.valueOf(bb), start);
    }
    
    public static BiasedBitSet valueOf(long[] longs, int start)
    {
        return new BiasedBitSet(BitSet.valueOf(longs), start);
    }
    
    public static BiasedBitSet valueOf(LongBuffer lb, int start)
    {
        return new BiasedBitSet(BitSet.valueOf(lb), start);
    }
    
     public byte[] toByteArray()
    {
        return bitSet.toByteArray();
    }
    
    public long[] toLongArray()
    {
        return bitSet.toLongArray();
    }
    */

    public void flip(int bitIndex)
    {
        bitSet.flip(bitIndex + bias);
    }

    public void flip(int fromIndex, int toIndex)
    {
        bitSet.flip(fromIndex + bias, toIndex + bias);
    }

    public void set(int bitIndex)
    {
        bitSet.set(bitIndex + bias);
    }

    public void set(int bitIndex, boolean value)
    {
        bitSet.set(bitIndex + bias, value);
    }

    public void set(int fromIndex, int toIndex)
    {
        bitSet.set(fromIndex + bias, toIndex + bias);
    }

    public void set(int fromIndex, int toIndex, boolean value)
    {
        bitSet.set(fromIndex + bias, toIndex + bias, value);
    }

    public void clear(int bitIndex)
    {
        bitSet.clear(bitIndex + bias);
    }

    public void clear(int fromIndex, int toIndex)
    {
        bitSet.clear(fromIndex + bias, toIndex + bias);
    }

    public void clear()
    {
        bitSet.clear();
    }

    public boolean get(int bitIndex)
    {
        return bitSet.get(bitIndex + bias);
    }

    public BiasedBitSet get(int fromIndex, int toIndex)
    {
        BitSet newBitSet = bitSet.get(fromIndex + bias, toIndex + bias);
        BiasedBitSet result = new BiasedBitSet(newBitSet, start);
        result.nameProvider = nameProvider;
        return result;
    }

    public int nextSetBit(int fromIndex)
    {
        int result = bitSet.nextSetBit(fromIndex + bias);
        return result >= 0 ? result - bias : none;
    }

    public int nextClearBit(int fromIndex)
    {
        int result = bitSet.nextClearBit(fromIndex + bias);
        return result >= 0 ? result - bias : none;
    }

    /*
    // Available since Java 1.7
    public int previousSetBit(int fromIndex)
    {
        int result = bitSet.previousSetBit(fromIndex + bias);
        return result >= 0 ? result - bias : none;
    }
    
    public int previousClearBit(int fromIndex)
    {
        int result = bitSet.previousClearBit(fromIndex + bias);
        return result >= 0 ? result - bias : none;
    }
    */

    public int length()
    {
        return bitSet.length();
    }

    public boolean isEmpty()
    {
        return bitSet.isEmpty();
    }

    public boolean intersects(BiasedBitSet set)
    {
        if (set == null) {
            throw new IllegalArgumentException("null argument");
        }
        if (set.start != this.start) {
            throw new IllegalArgumentException("biases differ");
        }
        return bitSet.intersects(set.bitSet);
    }

    /**
     * Tests whether an argument set is a subset of this object.
     * An added method which does not exist in java.util.BitSet
     * but is needed in NioGram. Equivalent to a non-destructive
     * version of thia.and(other).xor(other).isEmpty().
     * 
     * @param set the set to test against
     * @return true if the argument is a asubtset of this one;
     * otherwise false
     */
    public boolean contains(BiasedBitSet set)
    {
        if (set == null) {
            throw new IllegalArgumentException("null argument");
        }
        if (set.start != this.start) {
            throw new IllegalArgumentException("biases differ");
        }
        boolean result = true;
        int current = start;
        while ((current = set.nextSetBit(current)) != none) {
            if (!get(current)) {
                result = false;
                break;
            }
            current++;
        }

        return result;
    }

    public int cardinality()
    {
        return bitSet.cardinality();
    }

    public void and(BiasedBitSet set)
    {
        if (set == null) {
            throw new IllegalArgumentException("null argument");
        }
        if (set.start != this.start) {
            throw new IllegalArgumentException("biases differ");
        }
        bitSet.and(set.bitSet);
    }

    public void or(BiasedBitSet set)
    {
        if (set == null) {
            throw new IllegalArgumentException("null argument");
        }
        if (set.start != this.start) {
            throw new IllegalArgumentException("biases differ");
        }
        bitSet.or(set.bitSet);
    }

    public void xor(BiasedBitSet set)
    {
        if (set == null) {
            throw new IllegalArgumentException("null argument");
        }
        if (set.start != this.start) {
            throw new IllegalArgumentException("biases differ");
        }
        bitSet.xor(set.bitSet);
    }

    public void andNot(BiasedBitSet set)
    {
        if (set == null) {
            throw new IllegalArgumentException("null argument");
        }
        if (set.start != this.start) {
            throw new IllegalArgumentException("biases differ");
        }
        bitSet.andNot(set.bitSet);
    }

    /**
     * Calculates and returns the (possibly empty) conflict set
     * between the specified argument and this set. That is -
     * creates a copy of this object and invoke on it the
     * {@link #and(BiasedBitSet)} operation on it with an argument other.
     * The result is returned.
     * 
     * @param other the target bitset
     * @return the possibly empty conflict set
     */
    public BiasedBitSet conflict(BiasedBitSet other)
    {
        if (other == null) {
            throw new IllegalArgumentException("null argument");
        }
        if (other.start != this.start) {
            throw new IllegalArgumentException("biases differ");
        }
        BiasedBitSet result = new BiasedBitSet(this);
        result.and(other);
        return result;
    }

    public int size()
    {
        return bitSet.size();
    }

    /*
    // Available since Java 1.8
    public IntStream stream()
    {
        return `bitSet.stream();
    }
    */

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + bias;
        result = prime * result + ((bitSet == null) ? 0 : bitSet.hashCode());
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
        BiasedBitSet other = (BiasedBitSet) obj;
        if (bias != other.bias)
            return false;
        if (bitSet == null) {
            if (other.bitSet != null)
                return false;
        }
        else if (!bitSet.equals(other.bitSet))
            return false;
        return true;
    }

    @Override
    public Object clone()
    {
        BiasedBitSet result = new BiasedBitSet((BitSet) bitSet.clone(), start);
        return result;
    }

    @Override
    public String toString()
    {
        return toString(null);
    }

    public String toString(TypeNameProvider nameProvider)
    {
        TypeNameProvider theNameProvider = nameProvider;
        if (theNameProvider == null) {
            theNameProvider = this.nameProvider;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(SET_OPEN);
        int current = getStart();
        int end = getNone();

        boolean first = true;
        while ((current = nextSetBit(current)) != end) {
            if (first) {
                first = false;
            }
            else {
                sb.append(ELEMENT_SEPARATOR);
            }
            String name =
                theNameProvider != null ? theNameProvider.getTypeName(current)
                                        : Integer.toHexString(current);
            sb.append(name);
            ++current;
        }
        sb.append(SET_CLOSE);
        return sb.toString();
    }
}
