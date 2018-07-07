/*
 * Copyright (c) 2018 by Nikolay Ognyanov. All rights reserved.
 * 
 * Use of this file is governed by the licensing conditions   
 * in the LICENSE.txt file in the root folder of the project.
 */
package net.ognyanov.niogram.ast;

import java.util.ArrayList;
import java.util.List;

/**
 * The AST node for rule and block alternatives.<p>
 * 
 * Has the following own attributes:
 * <ul>
 * <li><strong>terms</strong>
 * - The list of terms of the alternative.
 * </li>
 * </ul>
 * @author Nikolay Ognyanov
 */
public final class Alternative
    extends GrammarNode
{
    private static final long serialVersionUID = 1L;

    private List<Term>        terms            = new ArrayList<Term>();

    public Alternative(int type)
    {
        super(type);
    }

    public List<Term> getTerms()
    {
        return terms;
    }

}
