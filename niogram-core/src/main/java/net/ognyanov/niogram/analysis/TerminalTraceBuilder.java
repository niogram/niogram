/*
 * Copyright (c) 2018 by Nikolay Ognyanov. All rights reserved.
 * 
 * Use of this file is governed by the licensing conditions   
 * in the LICENSE.txt file in the root folder of the project.
 */
package net.ognyanov.niogram.analysis;

import static net.ognyanov.niogram.analysis.TerminalTrace.TraceType.FIRST;
import static net.ognyanov.niogram.analysis.TerminalTrace.TraceType.FOLLOW;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.ognyanov.niogram.ast.Alternative;
import net.ognyanov.niogram.ast.Block;
import net.ognyanov.niogram.ast.BuiltInTypes;
import net.ognyanov.niogram.ast.Grammar;
import net.ognyanov.niogram.ast.GrammarNode;
import net.ognyanov.niogram.ast.Nonterminal;
import net.ognyanov.niogram.ast.NonterminalRule;
import net.ognyanov.niogram.ast.Term;
import net.ognyanov.niogram.ast.Terminal;
import net.ognyanov.niogram.ast.TerminalRule;

class TerminalTraceBuilder
{
    public enum SetType
    {
        SET, SETKL, SETK
    }

    private int              terminalType;
    private SetType          setType;
    private Set<GrammarNode> visitedFirst  = new HashSet<GrammarNode>();
    private Set<GrammarNode> visitedFollow = new HashSet<GrammarNode>();
    private boolean          debug         = false;

    public TerminalTrace buildFirstTrace(GrammarNode start, int terminalType)
    {
        this.setType = SetType.SET;
        TerminalTrace result = buildFirst(start, terminalType);
        this.setType = null;
        return result;
    }

    public TerminalTrace buildFirstTraceKL(GrammarNode start, int terminalType)
    {
        this.setType = SetType.SETKL;
        TerminalTrace result = buildFirst(start, terminalType);
        this.setType = null;
        return result;
    }

    public TerminalTrace buildFirstTraceK(GrammarNode start, int terminalType)
    {
        this.setType = SetType.SETK;
        TerminalTrace result = buildFirst(start, terminalType);
        this.setType = null;
        return result;
    }

    public TerminalTrace buildFollowTrace(GrammarNode start, int terminalType)
    {
        this.setType = SetType.SET;
        TerminalTrace result = buildFollow(start, terminalType);
        this.setType = null;
        return result;
    }

    public TerminalTrace buildFollowTraceKL(GrammarNode start, int terminalType)
    {
        this.setType = SetType.SETKL;
        TerminalTrace result = buildFollow(start, terminalType);
        this.setType = null;
        return result;
    }

    public TerminalTrace buildFollowTraceK(GrammarNode start, int terminalType)
    {
        this.setType = SetType.SETK;
        TerminalTrace result = buildFollow(start, terminalType);
        this.setType = null;
        return result;
    }

    private TerminalTrace buildFirst(GrammarNode start, int terminalType)
    {
        if (start == null || terminalType < BuiltInTypes.MIN_TYPE) {
            throw new IllegalArgumentException();
        }
        printDebug(start);
        TerminalTrace result = null;

        this.terminalType = terminalType;
        this.visitedFirst.clear();
        this.visitedFollow.clear();

        if (firstHasIt(start)) {
            result = dispatchFirst(start);
        }

        this.terminalType = 0;
        this.visitedFirst.clear();
        this.visitedFollow.clear();

        return result;
    }

    private TerminalTrace dispatchFirst(GrammarNode start)
    {
        printDebug(start);
        TerminalTrace result = null;
        if (start instanceof Grammar) {
            result = buildFirst((Grammar) start);
        }
        else if (start instanceof NonterminalRule) {
            result = buildFirst((NonterminalRule) start);
        }
        else if (start instanceof Alternative) {
            result = buildFirst((Alternative) start);
        }
        else if (start instanceof Block) {
            result = buildFirst((Block) start);
        }
        else if (start instanceof Nonterminal) {
            result = buildFirst((Nonterminal) start);
        }
        else if (start instanceof Terminal) {
            result = buildFirst((Terminal) start);
        }
        else if (start instanceof TerminalRule) {
            result = buildFirst((TerminalRule) start);
        }
        else {
            // should never happen
            throw new IllegalStateException("internal error");
        }
        return result;
    }

    private TerminalTrace buildFirst(Grammar start)
    {
        TerminalTrace result = null;
        if (firstHasIt(start)) {
            for (NonterminalRule rule : start.getNonterminalRules()) {
                TerminalTrace ruleTrace = dispatchFirst(rule);
                if (ruleTrace != null) {
                    if (result == null) {
                        result =
                            new TerminalTrace(FIRST, terminalType, start);
                    }
                    ruleTrace.setParent(result);
                    result.getChildren().add(ruleTrace);
                }
            }
        }
        return result;
    }

    private TerminalTrace buildFirst(NonterminalRule start)
    {
        TerminalTrace result = null;
        if (!visitedFirst.contains(start)) {
            visitedFirst.add(start);
            if (firstHasIt(start)) {
                for (Alternative alternative : start.getAlternatives()) {
                    TerminalTrace altTrace = dispatchFirst(alternative);
                    if (altTrace != null) {
                        if (result == null) {
                            result =
                                new TerminalTrace(FIRST, terminalType, start);
                        }
                        altTrace.setParent(result);
                        result.getChildren().add(altTrace);
                    }
                }
            }
            visitedFirst.remove(start);
        }
        return result;
    }

    private TerminalTrace buildFirst(Alternative start)
    {
        TerminalTrace result = null;
        if (!visitedFirst.contains(start)) {
            visitedFirst.add(start);
            if (firstHasIt(start)) {
                Term firstTerm = start.getTerms().get(0);
                TerminalTrace firstTermTrace = dispatchFirst(firstTerm);
                if (firstTermTrace != null) {
                    result = new TerminalTrace(FIRST, terminalType, start);
                    firstTermTrace.setParent(result);
                    result.getChildren().add(firstTermTrace);
                }
            }
            visitedFirst.remove(start);
        }
        return result;
    }

    private TerminalTrace buildFirst(Block start)
    {
        TerminalTrace result = null;
        if (!visitedFirst.contains(start)) {
            visitedFirst.add(start);
            if (firstHasIt(start)) {
                for (Alternative alternative : start.getAlternatives()) {
                    TerminalTrace altTrace = dispatchFirst(alternative);
                    if (altTrace != null) {
                        if (result == null) {
                            result =
                                new TerminalTrace(FIRST, terminalType, start);
                        }
                        altTrace.setParent(result);
                        result.getChildren().add(altTrace);
                    }
                }
            }
            if (start.isNullable()) {
                Term nextTerm = nextTerm(start);
                if (nextTerm != null) {
                    TerminalTrace nextTermTrace = dispatchFirst(nextTerm);
                    if (nextTermTrace != null) {
                        if (result == null) {
                            result =
                                new TerminalTrace(FIRST, terminalType, start);
                        }
                        nextTermTrace.setParent(result);
                        result.getChildren().add(nextTermTrace);
                    }
                }
            }
            visitedFirst.remove(start);
        }
        return result;
    }

    private TerminalTrace buildFirst(Nonterminal start)
    {
        TerminalTrace result = null;
        if (!visitedFirst.contains(start)) {
            visitedFirst.add(start);
            if (firstHasIt(start)) {
                TerminalTrace ruleTrace = dispatchFirst(start.getRule());
                if (ruleTrace != null) {
                    result = new TerminalTrace(FIRST, terminalType, start);
                    ruleTrace.setParent(result);
                    result.getChildren().add(ruleTrace);
                }
            }
            if (start.isNullable()) {
                Term nextTerm = nextTerm(start);
                if (nextTerm != null) {
                    TerminalTrace nextTermTrace = dispatchFirst(nextTerm);
                    if (nextTermTrace != null) {
                        if (result == null) {
                            result =
                                new TerminalTrace(FIRST, terminalType, start);
                        }
                        nextTermTrace.setParent(result);
                        result.getChildren().add(nextTermTrace);
                    }
                }
            }
            visitedFirst.remove(start);
        }
        return result;
    }

    private TerminalTrace buildFirst(Terminal start)
    {
        TerminalTrace result = null;
        if (!visitedFirst.contains(start)) {
            visitedFirst.add(start);
            if (firstHasIt(start)) {
                result = new TerminalTrace(FIRST, terminalType, start);
            }
            if (start.isNullable()) {
                Term nextTerm = nextTerm(start);
                if (nextTerm != null) {
                    TerminalTrace nextTermTrace = dispatchFirst(nextTerm);
                    if (nextTermTrace != null) {
                        if (result == null) {
                            result =
                                new TerminalTrace(FIRST, terminalType, start);
                        }
                        nextTermTrace.setParent(result);
                        result.getChildren().add(nextTermTrace);
                    }
                }
            }
            visitedFirst.remove(start);
        }
        return result;
    }

    private TerminalTrace buildFirst(TerminalRule start)
    {
        return null;
    }

    private TerminalTrace buildFollow(GrammarNode start, int terminalType)
    {
        if (start == null || terminalType < BuiltInTypes.MIN_TYPE) {
            throw new IllegalArgumentException();
        }
        printDebug(start);
        TerminalTrace result = null;

        this.terminalType = terminalType;
        this.visitedFirst.clear();
        this.visitedFollow.clear();

        if (followHasIt(start)) {
            result = dispatchFollow(start);
        }

        this.terminalType = 0;
        this.visitedFirst.clear();
        this.visitedFollow.clear();

        return result;
    }

    private TerminalTrace dispatchFollow(GrammarNode start)
    {
        printDebug(start);
        TerminalTrace result = null;
        if (start instanceof Grammar) {
            result = buildFollow((Grammar) start);
        }
        else if (start instanceof NonterminalRule) {
            result = buildFollow((NonterminalRule) start);
        }
        else if (start instanceof Alternative) {
            result = buildFollow((Alternative) start);
        }
        else if (start instanceof Term) {
            result = buildFollow((Term) start);
        }
        else if (start instanceof TerminalRule) {
            result = buildFollow((TerminalRule) start);
        }
        else {
            // should never happen
            throw new IllegalStateException("internal error");
        }
        return result;
    }

    private TerminalTrace buildFollow(Grammar start)
    {
        TerminalTrace result = null;
        if (!visitedFollow.contains(start)) {
            visitedFollow.add(start);
            for (NonterminalRule rule : start.getNonterminalRules()) {
                TerminalTrace ruleTrace = dispatchFollow(rule);
                if (ruleTrace != null) {
                    if (result == null) {
                        result = new TerminalTrace(FOLLOW, terminalType, start);
                    }
                    ruleTrace.setParent(result);
                    result.getChildren().add(ruleTrace);
                }
            }
            visitedFollow.remove(start);
        }
        return result;
    }

    private TerminalTrace buildFollow(NonterminalRule start)
    {
        TerminalTrace result = null;
        if (!visitedFollow.contains(start)) {
            visitedFollow.add(start);
            for (Nonterminal nonterminal : start.getReferences()) {
                TerminalTrace refTrace = dispatchFollow(nonterminal);
                if (refTrace != null) {
                    if (result == null) {
                        result = new TerminalTrace(FOLLOW, terminalType, start);
                    }
                    refTrace.setParent(result);
                    result.getChildren().add(refTrace);
                }
            }
            visitedFollow.remove(start);
        }
        return result;
    }

    private TerminalTrace buildFollow(Alternative start)
    {
        TerminalTrace result = null;
        if (!visitedFollow.contains(start)) {
            visitedFollow.add(start);
            TerminalTrace muxTrace = dispatchFollow(start.getParent());
            if (muxTrace != null) {
                result = new TerminalTrace(FOLLOW, terminalType, start);
                muxTrace.setParent(result);
                result.getChildren().add(muxTrace);
            }
            visitedFollow.remove(start);
        }
        return result;
    }

    private TerminalTrace buildFollow(Term start)
    {
        TerminalTrace result = null;
        if (!visitedFollow.contains(start)) {
            visitedFollow.add(start);
            Term nextTerm = nextTerm(start);
            if (nextTerm != null) {
                TerminalTrace nextTermTrace = dispatchFirst(nextTerm);
                if (nextTermTrace != null) {
                    result = new TerminalTrace(FOLLOW, terminalType, start);
                    nextTermTrace.setParent(result);
                    result.getChildren().add(nextTermTrace);
                }
            }
            if (start.isSuffixNullable()) {
                TerminalTrace alternativeTrace =
                    dispatchFollow(start.getParent());
                if (alternativeTrace != null) {
                    if (result == null) {
                        result =
                            new TerminalTrace(FOLLOW, terminalType, start);
                        alternativeTrace.setParent(result);
                        result.getChildren().add(alternativeTrace);
                    }
                }
            }
            visitedFollow.remove(start);
        }
        return result;
    }

    private TerminalTrace buildFollow(TerminalRule start)
    {
        return null;
    }

    private Term nextTerm(Term term)
    {
        Term result = null;
        List<Term> terms = ((Alternative) term.getParent()).getTerms();
        int termIndex = terms.size();
        for (int i = 0; i < terms.size(); i++) {
            if (terms.get(i) == term) {
                termIndex = i;
                break;
            }
        }
        if (termIndex + 1 < terms.size()) {
            result = terms.get(termIndex + 1);
        }
        return result;
    }

    private boolean firstHasIt(GrammarNode node)
    {
        boolean result = false;
        switch (setType) {
            case SETK:
                if (node.getFirstK() != null)
                    result = node.getFirstK().containsAt(0, terminalType);
                break;
            case SETKL:
                if (node.getFirstKL() != null)
                    result = node.getFirstKL().containsAt(0, terminalType);
                break;
            case SET:
                if (node.getFirst() != null)
                    result = node.getFirst().get(terminalType);
                break;
        }
        return result;
    }

    private boolean followHasIt(GrammarNode node)
    {
        boolean result = false;
        switch (setType) {
            case SETK:
                if (node.getFollowK() != null)
                    result = node.getFollowK().containsAt(0, terminalType);
                break;
            case SETKL:
                if (node.getFollowKL() != null)
                    result = node.getFollowKL().containsAt(0, terminalType);
                break;
            case SET:
                if (node.getFollow() != null)
                    result = node.getFollow().get(terminalType);
                break;
        }
        return result;
    }

    private void printDebug(GrammarNode node)
    {
        if (debug) {
            System.out.println(node.getDisplayName());
        }
    }
}
