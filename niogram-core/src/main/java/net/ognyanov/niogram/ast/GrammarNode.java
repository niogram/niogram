/*
 * Copyright (c) 2018 by Nikolay Ognyanov. All rights reserved.
 * 
 * Use of this file is governed by the licensing conditions   
 * in the LICENSE.txt file in the root folder of the project.
 */
package net.ognyanov.niogram.ast;

import java.io.Serializable;
import java.util.List;

import net.ognyanov.niogram.util.BiasedBitSet;
import net.ognyanov.niogram.util.BitSetLLString;
import net.ognyanov.niogram.util.DotStringBuilder;
import net.ognyanov.niogram.util.IntLLStringSet;
import net.ognyanov.niogram.util.XmlStringBuilder;

/**
 * The abstract root of the AST type hierarchy.<p>
 * 
 * Has the following attributes:
 * <ul>
 * <li><strong>parent</strong>
 *  - The parent node of this one or null if none.</li>
 * <li><strong>id</strong>
 *  - An unique ID.</li>
 * <li><strong>type</strong>
 *  - Node type.</li>
 * <li><strong>symbolicName</strong>
 *  - A symbolic name for the node as defined in the target grammar.</li>
 * <li><strong>displayName</strong>
 *  - A display name for the node.</li>
 * <li><strong>nullable</strong>
 *  - A flag for nullable nodes.</li>
 * <li><strong>productive</strong>
 *  - A flag for productive nodes.</li>
 * <li><strong>reachable</strong>
 *  - A flag for reachable nodes.</li>
 * <li><strong>first</strong>
 *  - The First set of the node.</li>
 * <li><strong>follow</strong>
 *  - The Follow set of the node.</li>
 * <li><strong>firstK</strong>
 *  - The FirstK set of the node.</li>
 * <li><strong>followK</strong>
 *  - The FollowK set of the node.</li>
 * <li><strong>firstKL</strong>
 * - The linearized FirstK set of the node.</li>
 * <li><strong>followKL</strong>
 * - The linearized FollowK set of the node.</li>
 * <li><strong>sourceContext</strong>
 * - The grammar source context of the node.
 *   Actual type depends on the specific parser
 *   which produced the AST. A transient property.</li>
 * <li><strong>payload</strong>
 * - Utility payload facility for use by clients.
 *   Not used by the NioGram core code. A transient property</li>
 * </ul>
 * 
 * @author Nikolay Ognyanov
 */
public abstract class GrammarNode
    implements Serializable
{
    private static final long   serialVersionUID = -26979619545084470L;

    private static final String NULL             = "null";

    private static final Object lock             = new Object();
    private static int          counter          = 0;

    private GrammarNode         parent           = null;
    private int                 id               = 0;
    private int                 type             = 0;
    private String              symbolicName     = null;
    private String              displayName      = null;

    private boolean             nullable         = false;
    private boolean             productive       = false;
    private boolean             reachable        = false;

    private BiasedBitSet        first            = null;
    private BiasedBitSet        follow           = null;
    private IntLLStringSet      firstK           = null;
    private IntLLStringSet      followK          = null;
    private BitSetLLString      firstKL          = null;
    private BitSetLLString      followKL         = null;

    /*
     * The class could be parameterized with the types
     * of sourceContext and payload but I do not want
     * to pollute the AST model with generics which are
     * unrelated to the core functionality.
     */
    private transient Object    sourceContext    = null;
    private transient Object    payload          = null;

    public GrammarNode(int type)
    {
        synchronized (lock) {
            id = counter++;
        }
        this.type = type;
    }

    public GrammarNode getParent()
    {
        return parent;
    }

    public void setParent(GrammarNode parent)
    {
        this.parent = parent;
    }

    public int getId()
    {
        return id;
    }

    public int getType()
    {
        return type;
    }

    public String getSymbolicName()
    {
        return symbolicName;
    }

    public void setSymbolicName(String symbolicName)
    {
        this.symbolicName = symbolicName;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public void setDisplayName(String displayName)
    {
        this.displayName = displayName;
    }

    public boolean isNullable()
    {
        return nullable;
    }

    public void setNullable(boolean nullable)
    {
        this.nullable = nullable;
    }

    public boolean isProductive()
    {
        return productive;
    }

    public void setProductive(boolean productive)
    {
        this.productive = productive;
    }

    public boolean isReachable()
    {
        return reachable;
    }

    public void setReachable(boolean reachable)
    {
        this.reachable = reachable;
    }

    public BiasedBitSet getFirst()
    {
        return first;
    }

    public void setFirst(BiasedBitSet first)
    {
        this.first = first;
    }

    public BiasedBitSet getFollow()
    {
        return follow;
    }

    public void setFollow(BiasedBitSet follow)
    {
        this.follow = follow;
    }

    public IntLLStringSet getFirstK()
    {
        return firstK;
    }

    public void setFirstK(IntLLStringSet firstK)
    {
        this.firstK = firstK;
    }

    public IntLLStringSet getFollowK()
    {
        return followK;
    }

    public void setFollowK(IntLLStringSet followK)
    {
        this.followK = followK;
    }

    public BitSetLLString getFirstKL()
    {
        return firstKL;
    }

    public void setFirstKL(BitSetLLString firstKL)
    {
        this.firstKL = firstKL;
    }

    public BitSetLLString getFollowKL()
    {
        return followKL;
    }

    public void setFollowKL(BitSetLLString followKL)
    {
        this.followKL = followKL;
    }

    public Object getSourceContext()
    {
        return sourceContext;
    }

    public void setSourceContext(Object parseTreeContext)
    {
        this.sourceContext = parseTreeContext;
    }

    public Object getPayload()
    {
        return payload;
    }

    public void setPayload(Object payload)
    {
        this.payload = payload;
    }

    /**
     * Generates an XML representation of
     * the node and its descendants.
     * 
     * @return XML text for the node.
     */
    public String toXmlString()
    {
        XMLVisitor visitor = new XMLVisitor();
        visitor.visitNode(this);
        return visitor.getXMLString();
    }

    /**
     * Generates a DOT language representation
     * of the node and its descendants.
     * 
     * @return dot text for the node.
     */
    public String toDotString()
    {
        DotVisitor visitor = new DotVisitor();
        visitor.visitNode(this);
        return visitor.getDOTString();
    }

    @Override
    public String toString()
    {
        return getDisplayName();
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
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
        GrammarNode other = (GrammarNode) obj;
        if (id != other.id)
            return false;
        return true;
    }

    private class DotVisitor
        extends GrammarVisitor
    {
        private DotStringBuilder stringBuider;

        public DotVisitor()
        {
            this.stringBuider = new DotStringBuilder();
        }

        public String getDOTString()
        {
            return stringBuider.toString();
        }

        @Override
        public void visitGrammar(Grammar grammar)
        {
            stringBuider
                .append("digraph " + grammar.getDisplayName() + "{" + "\n");
            super.visitGrammar(grammar);
            stringBuider.append("}\n");
        }

        @Override
        public void preVisit(GrammarNode node)
        {
            String label = null;
            String shape = null;
            String color = null;
            String style = null;

            if (node instanceof TerminalRule) {
                return;
            }
            if (node instanceof Grammar || node instanceof NonterminalRule) {
                label = node.getDisplayName();
                shape = "oval";
                color = "black";
                style = null;
            }
            else if (node instanceof Alternative) {
                String name = node.getDisplayName();
                int l = name.lastIndexOf('/');
                label = name.substring(l + 1);
                shape = "circle";
                color = "black";
                style = null;
            }
            else if (node instanceof Block) {
                label = node.getDisplayName();
                shape = "oval";
                color = ((Block) node).isRepeatable() ? "blue" : "black";
                style = ((Block) node).isOptional() ? "dashed" : "solid";
            }
            else if (node instanceof Nonterminal) {
                label = node.getDisplayName();
                shape = "box";
                color = "black";
                style = "solid";
            }
            else if (node instanceof Terminal) {
                label = node.getDisplayName();
                shape = "box";
                color = "black";
                style = "\"rounded,solid\"";
            }
            else {
                label = node.getDisplayName();
                shape = "box";
                color = "black";
                style = null;
            }

            stringBuider.append(node.getId());
            stringBuider.append(" [");
            stringBuider.append("label=");
            stringBuider.appendEscaped("\"" + label + "\"");
            stringBuider.append(" shape=" + shape);
            stringBuider.append(" color=" + color);
            if (style != null) {
                stringBuider.append(" style=" + style);
            }
            stringBuider.append("];\n");
        }

        @Override
        public void postVisit(GrammarNode node)
        {
            if (node instanceof TerminalRule) {
                return;
            }
            if (node instanceof Grammar) {
                connectDots(node, ((Grammar) node).getNonterminalRules());
            }
            else if (node instanceof NonterminalRule) {
                connectDots(node, ((NonterminalRule) node).getAlternatives());
            }
            else if (node instanceof Block) {
                connectDots(node, ((Block) node).getAlternatives());
            }
            else if (node instanceof Alternative) {
                connectDots(node, ((Alternative) node).getTerms());
            }
        }

        @SuppressWarnings({ "unchecked", "rawtypes" })
        private void connectDots(GrammarNode parent,
                                 List children)
        {
            for (GrammarNode child : (List<GrammarNode>) children) {
                stringBuider.append(parent.getId());
                stringBuider.append(" -> ");
                stringBuider.append(child.getId());
                stringBuider.append(";\n");
            }
        }
    }

    private class XMLVisitor
        extends GrammarVisitor
    {
        private XmlStringBuilder stringBuilder;

        public String getXMLString()
        {
            return stringBuilder.toString();
        }

        public XMLVisitor()
        {
            stringBuilder = new XmlStringBuilder();
        }

        @Override
        public void visitGrammar(Grammar grammar)
        {
            stringBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
            super.visitGrammar(grammar);
        }

        @Override
        public void preVisit(GrammarNode grammarNode)
        {
            boolean leafNode = grammarNode instanceof Terminal
                    || grammarNode instanceof Nonterminal;
            String className = grammarNode.getClass().getSimpleName();
            String symbolicName = grammarNode.getSymbolicName();
            String displayName = grammarNode.getDisplayName();
            stringBuilder.append("<").append(className);
            appendEscapedProperty("symbolicName", symbolicName);
            appendEscapedProperty("displayName", displayName);
            String parentName = NULL;
            if (grammarNode.getParent() != null) {
                parentName = grammarNode.getParent().getDisplayName();
            }
            appendProperty("parent", parentName);
            appendProperty("type", Integer.toString(grammarNode.getType()));
            appendProperty("id", Integer.toString(grammarNode.getId()));
            appendProperty("nullable", grammarNode.isNullable());
            appendProperty("productive", grammarNode.isProductive());
            appendProperty("reachable", grammarNode.isReachable());

            if (grammarNode instanceof TerminalRule ||
                    grammarNode instanceof NonterminalRule) {
            }
            if (grammarNode instanceof Term) {
                Term term = (Term) grammarNode;
                appendProperty("prefixNullale", term.isPrefixNullable());
                appendProperty("suffixNullable", term.isSuffixNullable());
            }
            if (grammarNode instanceof Block) {
                Block block = (Block) grammarNode;
                appendProperty("optional", block.isOptional());
                appendProperty("repeatable", block.isRepeatable());
                appendProperty("greedy", block.isGreedy());
            }
            if (leafNode) {
                stringBuilder.append("/>");
            }
            else {
                stringBuilder.append(">");
            }
        }

        @Override
        public void postVisit(GrammarNode grammarNode)
        {
            boolean leafNode = grammarNode instanceof Terminal
                    || grammarNode instanceof Nonterminal;
            if (!leafNode) {
                String className = grammarNode.getClass().getSimpleName();
                stringBuilder.append("</").append(className).append(">");
            }
        }

        private void appendProperty(String name, String value)
        {
            if (value == null) {
                value = NULL;
            }
            stringBuilder.append(" ").append(name).append("=\"").append(value)
                .append("\"");
        }

        private void appendProperty(String name, boolean value)
        {
            appendProperty(name, Boolean.toString(value));
        }

        private void appendEscapedProperty(String name, String value)
        {
            if (value == null) {
                value = NULL;
            }
            stringBuilder.append(" ").appendEscaped(name).append("=\"")
                .appendEscaped(value)
                .append("\"");
        }
    }
}
