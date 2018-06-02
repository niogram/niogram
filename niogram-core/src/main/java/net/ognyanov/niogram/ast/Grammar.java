/*
 * Copyright (c) 2018 by Nikolay Ognyanov. All rights reserved.
 * 
 * Use of this file is governed by the licensing conditions   
 * in the LICENSE.txt file in the root folder of the project.
 */
package net.ognyanov.niogram.ast;

import java.util.ArrayList;
import java.util.List;

import net.ognyanov.niogram.util.BidirectionalMap;
import net.ognyanov.niogram.util.TypeNameProvider;

/**
 * The AST node for grammars. The root of the AST.
 * <p>Has the following own (non-inherited) attributes :
 * <ul>
 * <li><strong>nonterminalRules</strong>
 * - A list of of all nonterminal rules in the grammar.</li>
 * <li><strong>terminalRules</strong>
 * - A list of of all terminal rules in the grammar.</li>
 * <li><strong>blocks</strong>
 * - A list of all blocks in the grammar.</li>
 * <li><strong>nonProductive</strong>
 * - A list of non-productive nonterminal rules.</li>
 * <li><strong>unreachable</strong>
 * - A list of nonterminal rules which are not reachable from the start rule.</li>
 * <li><strong>flags</strong>
 * - A flag marking whether productivity, reachability  and nullability of rules
 *  have been calculated.</li>
 * <li><strong>FF</strong>
 * - A flag marking whether First/Follow sets have been calculated.</li>
 * <li><strong>FFK</strong>
 * - A flag marking whether FirstK/FollowK sets have been calculated.</li>
 * <li><strong>FFKL</strong>
 * - A flag marking whether FirstKL/FollowKL sets have been calculated.</li>
 * <li><strong>K</strong>
 * - The value of K for FirstK/FollowK sets.</li>
 * <li><strong>KL</strong>
 * - The value of K for FirstKL/FollowKL sets.</li>
 * </ul>
 * @author Nikolay Ognyanov
 */
public final class Grammar
    extends GrammarNode
    implements TypeNameProvider
{
    private static final long                 serialVersionUID = 1L;

    private List<NonterminalRule>             nonterminalRules =
        new ArrayList<NonterminalRule>();
    private List<Block>                       blocks           =
        new ArrayList<Block>();
    private List<TerminalRule>                terminalRules    =
        new ArrayList<TerminalRule>();

    private List<NonterminalRule>             nonProductive    =
        new ArrayList<NonterminalRule>();
    private List<NonterminalRule>             unreachable      =
        new ArrayList<NonterminalRule>();
    private List<NonterminalRule>             unused           =
        new ArrayList<NonterminalRule>();

    private boolean                           flags            = false;
    private boolean                           fF               = false;
    private boolean                           fFK              = false;
    private boolean                           fFKL             = false;
    private int                               k                = 1;
    private int                               kL               = 1;

    private BidirectionalMap<Integer, String> typeToName       =
        new BidirectionalMap<Integer, String>();

    public Grammar(int type)
    {
        super(type);
    }

    public List<NonterminalRule> getNonterminalRules()
    {
        return nonterminalRules;
    }

    public List<TerminalRule> getTerminalRules()
    {
        return terminalRules;
    }

    public List<NonterminalRule> getNonProductive()
    {
        return nonProductive;
    }

    public List<Block> getBlocks()
    {
        return blocks;
    }

    public List<NonterminalRule> getUnreachable()
    {
        return unreachable;
    }

    public List<NonterminalRule> getUnused()
    {
        return unused;
    }

    public void setK(int k)
    {
        if (k < 1) {
            throw new IllegalArgumentException("k must be positive");
        }
        this.k = k;
    }

    public void setKL(int kL)
    {
        if (k < 1) {
            throw new IllegalArgumentException("k must be positive");
        }
        this.kL = kL;
    }

    public boolean hasFlags()
    {
        return flags;
    }

    public void setFlags(boolean flags)
    {
        this.flags = flags;
    }

    public boolean hasFF()
    {
        return fF;
    }

    public void setFF(boolean value)
    {
        this.fF = value;
    }

    public boolean hasFFK()
    {
        return fFK;
    }

    public void setFFK(boolean value)
    {
        this.fFK = value;
    }

    public boolean hasFFKL()
    {
        return fFKL;
    }

    public void setFFKL(boolean value)
    {
        fFKL = value;
    }

    public int getK()
    {
        return k;
    }

    public int getKL()
    {
        return kL;
    }

    /**
     * Clear the grammar of analysis flags analysis data.
     */
    public void clearFlags()
    {
        new ClearFlagsVisitor().visitGrammar(this);
        setFlags(false);
    }

    /**
     * Clear the grammar of First/Follow analysis data.
     */
    public void clearFF()
    {
        new ClearFFVisitor().visitGrammar(this);
        setFF(false);
    }

    /**
     * Clear the grammar of FirstK/FollowK analysis data.
     */
    public void clearFFK()
    {
        new ClearFFKVisitor().visitGrammar(this);
        setFFK(false);
    }

    /**
     * Clear the grammar of FirstKL/FollowKL analysis data.
     */
    public void clearFFKL()
    {
        new ClearFFKLVisitor().visitGrammar(this);
        setFFKL(false);
    }

    /**
     * Retrieves a mapping between rule node types and names
     * 
     * @return the mapping
     */
    public BidirectionalMap<Integer, String> getTypeToName()
    {
        return typeToName;
    }

    @Override
    public String getTypeName(int index)
    {
        return typeToName.getSecond(index);
    }

    /**
     * Generates a DOT language representation
     * of the railroad diagrams of the grammar
     * rules.
     * <p>Note that a DOT language printout of
     * the grammar AST is inherited from  {@link GrammarNode}
     * (and so is an XML printout).
     * 
     * @return dot language description of the
     * railroad diagrams
     */
    public String toRailRoadDot()
    {
        RailroadDrawer drawer = new RailroadDrawer();
        return drawer.draw(this);
    }

    private static class ClearFlagsVisitor
        extends GrammarVisitor
    {
        @Override
        public void visitGrammar(Grammar grammar)
        {
            grammar.getNonProductive().clear();
            grammar.getUnreachable().clear();
            grammar.getUnused().clear();
            super.visitGrammar(grammar);
        }

        @Override
        public void preVisit(GrammarNode node)
        {
            if (!((node instanceof Terminal) ||
                    (node instanceof Nonterminal) ||
                    (node instanceof TerminalRule))) {
                node.setNullable(false);
                node.setProductive(false);
                node.setReachable(false);
            }
            if (node instanceof Rule) {
                ((Rule) node).setUsed(false);
            }
            if (node instanceof Term) {
                Term term = (Term) node;
                term.setPrefixNullable(false);
                term.setSuffixNullable(false);
            }
        }
    }

    private static class ClearFFVisitor
        extends GrammarVisitor
    {
        @Override
        public void preVisit(GrammarNode node)
        {
            if (!(node instanceof Terminal || node instanceof Nonterminal)) {
                node.setFirst(null);
            }
            node.setFollow(null);
            if (node instanceof Term) {
                ((Term) node).setSuffixFirst(null);
            }
            if (node instanceof NonterminalRule) {
                NonterminalRule rule = (NonterminalRule) node;
                rule.getConflicts().clear();
                rule.setFfConflict(null);
            }
            else if (node instanceof Block) {
                Block block = (Block) node;
                block.getConflicts().clear();
                block.setFfConflict(null);
            }
        }
    }

    private static class ClearFFKVisitor
        extends GrammarVisitor
    {
        @Override
        public void preVisit(GrammarNode node)
        {
            if (!(node instanceof Terminal || node instanceof Nonterminal)) {
                node.setFirstK(null);
            }
            node.setFollowK(null);
            if (node instanceof Term) {
                ((Term) node).setSuffixFirstK(null);
            }
            if (node instanceof NonterminalRule) {
                NonterminalRule rule = (NonterminalRule) node;
                rule.getConflictsK().clear();
                rule.setFfConflictK(null);
                rule.setMinK(0);
                rule.setMinFfK(0);
            }
            else if (node instanceof Block) {
                Block block = (Block) node;
                block.getConflictsK().clear();
                block.setFfConflictK(null);
                block.setMinK(0);
                block.setMinFfK(0);
            }
        }
    }

    private static class ClearFFKLVisitor
        extends GrammarVisitor
    {
        @Override
        public void preVisit(GrammarNode node)
        {
            if (!(node instanceof Terminal || node instanceof Nonterminal)) {
                node.setFirstKL(null);
            }
            node.setFollowKL(null);
            if (node instanceof Term) {
                ((Term) node).setSuffixFirstKL(null);
            }
            if (node instanceof NonterminalRule) {
                NonterminalRule rule = (NonterminalRule) node;
                rule.getConflictsKL().clear();
                rule.setFfConflictKL(null);
                rule.setMinKL(0);
                rule.setMinFfKL(0);
            }
            else if (node instanceof Block) {
                Block block = (Block) node;
                block.getConflictsKL().clear();
                block.setFfConflictKL(null);
                block.setMinK(0);
                block.setMinFfK(0);
            }
        }
    }
}
