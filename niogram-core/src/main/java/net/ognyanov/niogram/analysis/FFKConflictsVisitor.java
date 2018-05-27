/*
 * Copyright (c) 2018 by Nikolay Ognyanov. All rights reserved.
 * 
 * Use of this file is governed by the licensing conditions   
 * in the LICENSE.txt file in the root folder of the project.
 */
package net.ognyanov.niogram.analysis;

import java.util.List;
import java.util.ListIterator;

import net.ognyanov.niogram.ast.Alternative;
import net.ognyanov.niogram.ast.Block;
import net.ognyanov.niogram.ast.Grammar;
import net.ognyanov.niogram.ast.GrammarNode;
import net.ognyanov.niogram.ast.Multiplex;
import net.ognyanov.niogram.ast.NonterminalRule;
import net.ognyanov.niogram.util.IntLLStringSet;

class FFKConflictsVisitor
    extends InterruptableGrammarVisitor
{
    private int            k        = 0;
    private IntLLStringSet conflict = null;
    private int            minK     = 0;

    @Override
    public void visitGrammar(Grammar grammar)
    {
        this.k = grammar.getK();
        super.visitGrammar(grammar);
    }

    @Override
    public void visitNonterminalRule(NonterminalRule rule)
    {
        super.visitNonterminalRule(rule);
        visitMultiplex(rule);
    }

    @Override
    public void visitBlock(Block block)
    {
        super.visitBlock(block);
        visitMultiplex(block);
    }

    private void visitMultiplex(Multiplex multiplex)
    {
        calculateFfConflict(multiplex, k);
        conflict.removeEmpty();
        recordFfConflict(multiplex);

        List<Alternative> alternatives = multiplex.getAlternatives();
        ListIterator<Alternative> currentIt = alternatives.listIterator();
        while (currentIt.hasNext()) {
            int nextIndex = currentIt.nextIndex();
            ListIterator<Alternative> otherIt =
                alternatives.listIterator(nextIndex);
            Alternative current = currentIt.next();
            otherIt.next(); // skip current
            while (otherIt.hasNext()) {
                Alternative other = otherIt.next();
                calculateConflict(current, other, k);
                recordConflict(multiplex, current, other);
            }
        }
    }

    private void calculateFfConflict(Multiplex multiplex, int currentK)
    {
        if (currentK < 0) {
            minK = 0;
            return;
        }
        GrammarNode node = (GrammarNode) multiplex;
        IntLLStringSet currentFirst = node.getFirstK();
        IntLLStringSet currentFollow = node.getFollowK();
        IntLLStringSet currentConflict =
            currentFirst.conflict(currentFollow, currentK);
        if (currentK == k) {
            // the end result for conflict
            conflict = currentConflict;
        }
        if (currentConflict.isEmpty()) {
            // look for a conflict at smaller k
            calculateFfConflict(multiplex, currentK - 1);
        }
        else {
            // the end result for k
            minK = currentK + 1;
        }
    }

    private void calculateConflict(Alternative current,
                                   Alternative other,
                                   int currentK)
    {
        if (currentK < 0) {
            minK = 0;
            return;
        }
        IntLLStringSet currentFirst = current.getFirstK();
        IntLLStringSet otherFirst = other.getFirstK();
        IntLLStringSet currentConflict =
            currentFirst.conflict(otherFirst, currentK);
        if (currentK == k) {
            // the end result for conflict
            conflict = currentConflict;
        }
        if (currentConflict.isEmpty()) {
            // look for a conflict at smaller k
            calculateConflict(current, other, currentK - 1);
        }
        else {
            // the end result for k
            minK = currentK + 1;
        }
    }

    private void recordFfConflict(Multiplex multiplex)
    {
        if (multiplex instanceof NonterminalRule) {
            NonterminalRule rule = (NonterminalRule) multiplex;
            rule.setFfConflictK(conflict);
            if (conflict.isEmpty()) {
                rule.setMinFfK(minK);
            }
            else {
                rule.setMinFfK(-1);
            }
        }
        else if (multiplex instanceof Block) {
            Block block = (Block) multiplex;
            block.setFfConflictK(conflict);
            if (conflict.isEmpty()) {
                block.setMinFfK(minK);
            }
            else {
                block.setMinFfK(-1);
            }
        }
    }

    private void recordConflict(Multiplex multiplex, Alternative source,
                                Alternative target)
    {
        if (!conflict.isEmpty()) {
            Multiplex.ConflictK newConflictK =
                new Multiplex.ConflictK(source, target, conflict);

            if (!multiplex.getConflictsK().contains(newConflictK)) {
                multiplex.getConflictsK().add(newConflictK);
            }
        }
        int muxMinK = multiplex.getMinK();
        if (multiplex instanceof NonterminalRule) {
            NonterminalRule rule = (NonterminalRule) multiplex;
            if (conflict.isEmpty()) {
                if (minK > muxMinK && muxMinK != -1) {
                    rule.setMinK(minK);
                }
            }
            else {
                rule.setMinK(-1);
            }
        }
        else if (multiplex instanceof Block) {
            Block block = (Block) multiplex;
            if (conflict.isEmpty()) {
                if (minK > muxMinK && muxMinK != -1) {
                    block.setMinK(minK);
                }
            }
            else {
                block.setMinK(-1);
            }
        }
    }
}
