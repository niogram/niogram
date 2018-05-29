package net.ognyanov.niogram.analysis;
/*
 * Copyright (c) 2018 by Nikolay Ognyanov. All rights reserved.
 * 
 * Use of this file is governed by the licensing conditions   
 * in the LICENSE.txt file in the root folder of the project.
 */

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.alg.GabowStrongConnectivityInspector;
import org.jgrapht.alg.cycle.SzwarcfiterLauerSimpleCycles;
import org.jgrapht.graph.ClassBasedEdgeFactory;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import net.ognyanov.niogram.ast.Alternative;
import net.ognyanov.niogram.ast.Block;
import net.ognyanov.niogram.ast.Grammar;
import net.ognyanov.niogram.ast.GrammarNode;
import net.ognyanov.niogram.ast.GrammarVisitor;
import net.ognyanov.niogram.ast.Nonterminal;
import net.ognyanov.niogram.ast.NonterminalRule;
import net.ognyanov.niogram.ast.Term;
import net.ognyanov.niogram.util.DotStringBuilder;

/**
 * A facility for building and analyzing dependency graphs of grammars.
 *
 * @author Nikolay Ognyanov
 */
public class GraphAnalysis
{
    private GraphAnalysis()
    {
    }

    /**
     * Build a dependency graph for a grammar
     * 
     * @param grammar the grammar to be processed
     * @return the dependency graph
     */
    public static Graph<NonterminalRule, DefaultEdge> toGraph(Grammar grammar)
    {
        TheBuilder graphBuilder = new TheBuilder();
        graphBuilder.visitGrammar(grammar);
        Graph<NonterminalRule, DefaultEdge> graph =
            graphBuilder.getGraph();
        return graph;
    }

    /**
     * Build a reduced dependency graph for a grammar.
     * This graph is suitable for discovery of left
     * recursive cycles. These could in principle be
     * found in the general dependency graph too but
     * the task is much more computationally expensive.
     * 
     * @param grammar the grammar to be processed
     * @return the dependency graph
     */
    public static Graph<NonterminalRule, DefaultEdge> toLRGraph(Grammar grammar)
    {
        LRGraphBuilder graphBuilder = new LRGraphBuilder();
        graphBuilder.visitGrammar(grammar);
        Graph<NonterminalRule, DefaultEdge> graph =
            graphBuilder.getGraph();
        return graph;
    }

    /**
     * Generate a dot format representation of the graph.
     * 
     * @param graph the graph to be processed
     * @return dot format representation of the graph
     */
    public static String toDotString(Graph<NonterminalRule, DefaultEdge> graph)
    {
        DotEmitter dotEmitter = new DotEmitter();
        return dotEmitter.toDot(graph);
    }

    /**
     * Find all simple cycles in a dependency graph.
     * 
     * <p>Note that for typical grammar dependency graphs
     * (but not reduced dependency graphs) this call can
     * be very computationally expensive.
     * 
     * @param dependencyGraph the dependency graph
     * 
     * @return the list of simple cycles
     */
    public static List<List<NonterminalRule>> findCycles(Graph<NonterminalRule, DefaultEdge> dependencyGraph)
    {
        SzwarcfiterLauerSimpleCycles<NonterminalRule, DefaultEdge> cycleFinder =
            new SzwarcfiterLauerSimpleCycles<NonterminalRule, DefaultEdge>(dependencyGraph);
        List<List<NonterminalRule>> cycles = cycleFinder.findSimpleCycles();
        sortCycles(cycles);
        return cycles;
    }

    /**
     * Find all strongly connected components in a dependency graph.
     * 
     * @param dependencyGraph the dependency graph
     * 
     * @return the list of strongly connected components
     */
    public static List<Set<NonterminalRule>> findSCCs(Graph<NonterminalRule, DefaultEdge> dependencyGraph)
    {
        GabowStrongConnectivityInspector<NonterminalRule, DefaultEdge> GabowStrongConnectivityInspector =
            new GabowStrongConnectivityInspector<NonterminalRule, DefaultEdge>(dependencyGraph);
        List<Set<NonterminalRule>> sccs = GabowStrongConnectivityInspector
            .stronglyConnectedSets();
        return sccs;
    }

    private static void sortCycles(List<List<NonterminalRule>> cycles)
    {
        Collections.sort(cycles, new CycleComparator());
    }

    @SuppressWarnings("unused")
    private static class RuleComparator
        implements Comparator<NonterminalRule>
    {

        @Override
        public int compare(NonterminalRule arg0, NonterminalRule arg1)
        {
            return arg0.getDisplayName().compareTo(arg1.getDisplayName());
        }

    }

    private static class CycleComparator
        implements Comparator<List<NonterminalRule>>
    {
        @Override
        public int compare(List<NonterminalRule> o1,
                           List<NonterminalRule> o2)
        {
            GrammarNode n1 =
                o1.isEmpty() ? null : o1.iterator().next();
            GrammarNode n2 =
                o2.isEmpty() ? null : o2.iterator().next();
            if (n1 == null && n2 == null) {
                return 0;
            }
            else if (n1 == null) {
                return -1;
            }
            else if (n2 == null) {
                return 1;
            }
            else {
                return n1.getDisplayName().compareTo(n2.getDisplayName());
            }
        }
    }

    private static class TheBuilder
        extends GrammarVisitor
    {
        private Graph<NonterminalRule, DefaultEdge> graph;
        private NonterminalRule                     currentRule;

        public Graph<NonterminalRule, DefaultEdge> getGraph()
        {
            return graph;
        }

        @Override
        public void visitGrammar(Grammar grammar)
        {
            graph = new DefaultDirectedGraph<NonterminalRule, DefaultEdge>(
                                                                           new ClassBasedEdgeFactory<NonterminalRule, DefaultEdge>(
                                                                                                                                   DefaultEdge.class));
            super.visitGrammar(grammar);
        }

        @Override
        public void visitNonterminalRule(NonterminalRule rule)
        {
            graph.addVertex(rule);
            NonterminalRule oldRule = currentRule;
            currentRule = rule;
            super.visitNonterminalRule(rule);
            currentRule = oldRule;
        }

        @Override
        public void visitAlternative(Alternative alternative)
        {
            for (Term term : alternative.getTerms()) {
                if (term instanceof Nonterminal) {
                    NonterminalRule otherRule =
                        ((Nonterminal) term).getRule();
                    graph.addVertex(otherRule);
                    graph.addEdge(currentRule, otherRule);
                }
            }
            super.visitAlternative(alternative);
        }
    }

    private static class LRGraphBuilder
        extends GrammarVisitor
    {
        private Graph<NonterminalRule, DefaultEdge> graph;
        private NonterminalRule                     currentRule;

        public Graph<NonterminalRule, DefaultEdge> getGraph()
        {
            return graph;
        }

        @Override
        public void visitGrammar(Grammar grammar)
        {
            graph = new DefaultDirectedGraph<NonterminalRule, DefaultEdge>(
                                                                           new ClassBasedEdgeFactory<NonterminalRule, DefaultEdge>(
                                                                                                                                   DefaultEdge.class));
            super.visitGrammar(grammar);
        }

        @Override
        public void visitNonterminalRule(NonterminalRule rule)
        {
            graph.addVertex(rule);
            NonterminalRule oldRule = currentRule;
            currentRule = rule;
            super.visitNonterminalRule(rule);
            currentRule = oldRule;
        }

        @Override
        public void visitAlternative(Alternative alternative)
        {
            for (Term term : alternative.getTerms()) {
                if (term instanceof Nonterminal) {
                    NonterminalRule otherRule =
                        ((Nonterminal) term).getRule();
                    graph.addVertex(otherRule);
                    graph.addEdge(currentRule, otherRule);
                }
                else if (term instanceof Block) {
                    visitBlock((Block) term);
                }
                if (!(term.isNullable() ||
                        (term instanceof Block)
                                && ((Block) term).isOptional())) {
                    break;
                }
            }
        }
    }

    private static class DotEmitter
    {
        private Deque<NonterminalRule> todo          =
            new ArrayDeque<NonterminalRule>();
        private Set<NonterminalRule>   visited       =
            new HashSet<NonterminalRule>();
        private DotStringBuilder       stringBuilder = new DotStringBuilder();

        public String toDot(Graph<NonterminalRule, DefaultEdge> graph)
        {
            Set<NonterminalRule> rules = graph.vertexSet();
            Iterator<NonterminalRule> rulesIt = rules.iterator();
            emidDotStart();
            if (rulesIt.hasNext()) {
                while (rulesIt.hasNext()) {
                    // cycle over the strongly connected components
                    NonterminalRule currentStart = rulesIt.next();
                    if (visited.contains(currentStart)) {
                        continue;
                    }
                    todo.addLast(currentStart);
                    while (!todo.isEmpty()) {
                        NonterminalRule rule = todo.removeFirst();
                        if (!visited.contains(rule)) {
                            visited.add(rule);
                            emitDotNode(rule);
                        }
                        Set<DefaultEdge> edges = graph.outgoingEdgesOf(rule);
                        for (DefaultEdge edge : edges) {
                            emitDotEdge(graph, edge);
                            NonterminalRule target = graph.getEdgeTarget(edge);
                            if (!visited.contains(target)
                                    && !todo.contains(target)) {
                                todo.addLast(target);
                            }
                        }
                    }
                }
            }
            emitDotEnd();
            return stringBuilder.toString();
        }

        private void emidDotStart()
        {
            stringBuilder.append("digraph grammar {\n");
        }

        private void emitDotEnd()
        {
            stringBuilder.append("}\n");
        }

        private void emitDotEdge(Graph<NonterminalRule, DefaultEdge> graph,
                                 DefaultEdge edge)
        {
            NonterminalRule source = graph.getEdgeSource(edge);
            NonterminalRule target = graph.getEdgeTarget(edge);
            stringBuilder.append(source.getId())
                .append("->").append(target.getId()).append(";\n");
        }

        private void emitDotNode(NonterminalRule rule)
        {
            stringBuilder.append(rule.getId()).append(" [label=");
            stringBuilder.appendEscaped("\"" + rule.getDisplayName() + "\"");
            stringBuilder.append(" shape=box];\n");
        }
    }

}
