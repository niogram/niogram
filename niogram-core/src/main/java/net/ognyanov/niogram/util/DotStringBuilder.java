/*
 * Copyright (c) 2018 by Nikolay Ognyanov. All rights reserved.
 * 
 * Use of this file is governed by the licensing conditions   
 * in the LICENSE.txt file in the root folder of the project.
 */
package net.ognyanov.niogram.util;

/**
 * A string builder which supports the DOT language character
 * and string escaping.
 * 
 * <p>This class is not suitable for general purpose use. It is 
 * specifically fitted to work with other core NioGram code.
 *
 * @author Nikolay Ognyanov
 */
public final class DotStringBuilder
    extends BaseStringBuilder
{
    @Override
    public BaseStringBuilder appendEscaped(char c)
    {
        if (!skip(c)) {
            append(c);
        }
        return this;
    }

    @Override
    public BaseStringBuilder appendEscaped(CharSequence string)
    {
        if (string == null) {
            append(NULL);
        }
        else if (string.length() > 1 && string.charAt(0) == '\"'
                && string.charAt(string.length() - 1) == '\"') {
            append('"');
            for (int i = 1; i < string.length() - 1; i++) {
                char c = string.charAt(i);
                if (c == '"') {
                    append('\\');
                }
                append(c);
            }
            append('"');
        }
        else if (string.length() > 1 && string.charAt(0) == '<'
                && string.charAt(string.length() - 1) == '>') {
            append('"');
            for (int i = 1; i < string.length() - 1; i++) {
                char c = string.charAt(i);
                if (c == '"') {
                    append('\\');
                }
                append(c);
            }
            append('"');
        }
        else {
            super.appendEscaped(string);
        }
        return this;
    }

    private boolean skip(char c)
    {
        boolean result = true;
        if ('a' <= c && c <= 'z') {
            result = false;
        }
        else if ('A' <= c && c <= 'Z') {
            result = false;
        }
        else if ('0' <= c && c <= '9') {
            result = false;
        }
        else if (128 <= c && c <= 255) {
            result = false;
        }
        else if ('_' == c || '+' == c || '-' == c || '.' == 'c') {
            result = false;
        }

        return result;
    }
}
