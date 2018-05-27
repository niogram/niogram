/*
 * Copyright (c) 2018 by Nikolay Ognyanov. All rights reserved.
 * 
 * Use of this file is governed by the licensing conditions   
 * in the LICENSE.txt file in the root folder of the project.
 */
package net.ognyanov.niogram.util;

/**
 * An abstract base class for string builders which
 * support different types of character and string
 * escaping.
 *
 * @author Nikolay Ognyanov
 */
public abstract class BaseStringBuilder
{
    private StringBuilder         stringBuilder = new StringBuilder();
    protected static final String NULL          = "null";

    protected BaseStringBuilder()
    {
    }

    public BaseStringBuilder append(boolean b)
    {
        return append(Boolean.toString(b));
    }

    public BaseStringBuilder append(char c)
    {
        stringBuilder.append(c);
        return this;
    }

    public BaseStringBuilder append(int i)
    {
        return append(Integer.toString(i, 10));
    }

    public BaseStringBuilder append(long l)
    {
        return append(Long.toString(l, 10));
    }

    public BaseStringBuilder append(float f)
    {
        return append(Float.toString(f));
    }

    public BaseStringBuilder append(double d)
    {
        return append(Double.toString(d));
    }

    public BaseStringBuilder append(CharSequence string)
    {
        stringBuilder.append(string);
        return this;
    }

    public BaseStringBuilder append(Object o)
    {
        String string = NULL;
        if (o != null) {
            string = o.toString();
        }
        return append(string);
    }

    public abstract BaseStringBuilder appendEscaped(char c);

    public BaseStringBuilder appendEscaped(CharSequence string)
    {
        if (string == null) {
            append(NULL);
        }
        else {
            for (int i = 0; i < string.length(); i++) {
                char c = string.charAt(i);
                appendEscaped(c);
            }
        }
        return this;
    }

    public BaseStringBuilder appendEscaped(Object o)
    {
        String string = NULL;
        if (o != null) {
            string = o.toString();
        }
        return appendEscaped(string);
    }

    public String toString()
    {
        return stringBuilder.toString();
    }
}
