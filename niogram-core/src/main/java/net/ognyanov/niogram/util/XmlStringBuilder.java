/*
 * Copyright (c) 2018 by Nikolay Ognyanov. All rights reserved.
 * 
 * Use of this file is governed by the licensing conditions   
 * in the LICENSE.txt file in the root folder of the project.
 */
package net.ognyanov.niogram.util;

import java.util.HashMap;
import java.util.Map;

/**
 * A string builder which supports the XML 1.0 character escaping.
 *
 * @author Nikolay Ognyanov
 */
public class XmlStringBuilder
    extends BaseStringBuilder
{
    private static final char                EntityLow1  = 0x7f;
    private static final char                EntityHigh1 = 0x84;

    private static final char                EntityLow2  = 0x86;
    private static final char                EntityHigh2 = 0x9f;

    private static final String              EMPTY       = "";
    private static final Map<String, String> ESCAPE      =
        new HashMap<String, String>();
    static {
        ESCAPE.put("\"", "&quot;");
        ESCAPE.put("&", "&amp;");
        ESCAPE.put("<", "&lt;");
        ESCAPE.put(">", "&gt;");
        ESCAPE.put("'", "&apos;");
        ESCAPE.put("\u0000", EMPTY);
        ESCAPE.put("\u0001", EMPTY);
        ESCAPE.put("\u0002", EMPTY);
        ESCAPE.put("\u0003", EMPTY);
        ESCAPE.put("\u0004", EMPTY);
        ESCAPE.put("\u0005", EMPTY);
        ESCAPE.put("\u0006", EMPTY);
        ESCAPE.put("\u0007", EMPTY);
        ESCAPE.put("\u0008", EMPTY);
        ESCAPE.put("\u000b", EMPTY);
        ESCAPE.put("\u000c", EMPTY);
        ESCAPE.put("\u000e", EMPTY);
        ESCAPE.put("\u000f", EMPTY);
        ESCAPE.put("\u0010", EMPTY);
        ESCAPE.put("\u0011", EMPTY);
        ESCAPE.put("\u0012", EMPTY);
        ESCAPE.put("\u0013", EMPTY);
        ESCAPE.put("\u0014", EMPTY);
        ESCAPE.put("\u0015", EMPTY);
        ESCAPE.put("\u0016", EMPTY);
        ESCAPE.put("\u0017", EMPTY);
        ESCAPE.put("\u0018", EMPTY);
        ESCAPE.put("\u0019", EMPTY);
        ESCAPE.put("\u001a", EMPTY);
        ESCAPE.put("\u001b", EMPTY);
        ESCAPE.put("\u001c", EMPTY);
        ESCAPE.put("\u001d", EMPTY);
        ESCAPE.put("\u001e", EMPTY);
        ESCAPE.put("\u001f", EMPTY);
        ESCAPE.put("\ufffe", EMPTY);
        ESCAPE.put("\uffff", EMPTY);
    }

    @Override
    public XmlStringBuilder appendEscaped(char c)
    {
        if (!(skip(c) || escapeEntity(c))) {
            String string = Character.toString(c);
            String escaped = ESCAPE.get(string);
            if (escaped == null) {
                escaped = string;
            }
            append(escaped);
        }
        return this;
    }

    private boolean escapeEntity(int codepoint)
    {
        boolean result = false;
        if (EntityLow1 <= codepoint && codepoint <= EntityHigh1 ||
                EntityLow2 <= codepoint && codepoint <= EntityHigh2) {
            append("&#");
            append((Integer.toString(codepoint)));
            append(';');
            result = true;
        }
        return result;
    }

    private boolean skip(int codepoint)
    {
        boolean result = false;
        if (codepoint >= Character.MIN_SURROGATE
                && codepoint <= Character.MAX_SURROGATE) {
            result = true;
        }
        return result;
    }
}
