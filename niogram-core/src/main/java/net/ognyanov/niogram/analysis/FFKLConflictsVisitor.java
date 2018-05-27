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
import net.ognyanov.niogram.util.BitSetLLString;

class FFKLConflictsVisitor
    extends InterruptableGrammarVisitor
{
    private int            kL       = 0;
    private BitSetLLString conflict = null;
    private int            minKL    = 0;

    @Override
    public void visitGrammar(Grammar grammar)
    {
        this.kL = grammar.getKL();
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
        calculateFfConflict(multiplex, kL);
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
                calculateConflict(current, other, kL);
                recordConflict(multiplex, current, other);
            }
        }
    }

    private void calculateFfConflict(Multiplex multiplex, int currentK)
    {
        if (currentK < 0) {
            minKL = 0;
            return;
        }
        GrammarNode node = (GrammarNode) multiplex;
        BitSetLLString currentFirst = node.getFirstKL();
        BitSetLLString currentFollow = node.getFollowKL();
        BitSetLLString currentConflict =
            currentFirst.conflict(currentFollow, currentK);
        if (currentK == kL) {
            // the end result for conflict
            conflict = currentConflict;
        }
        if (currentConflict.isEmpty()) {
            // look for a conflict at smaller k
            calculateFfConflict(multiplex, currentK - 1);
        }
        else {
            // the end result for k
            minKL = currentK + 1;
        }
    }

    private void calculateConflict(Alternative current,
                                   Alternative other,
                                   int currentK)
    {
        if (currentK < 0) {
            minKL = 0;
            return;
        }
        BitSetLLString currentFirst = current.getFirstKL();
        BitSetLLString otherFirst = other.getFirstKL();
        BitSetLLString currentConflict =
            currentFirst.conflict(otherFirst, currentK);
        if (currentK == kL) {
            // the end result for conflict
            conflict = currentConflict;
        }
        if (currentConflict.isEmpty()) {
            // look for a conflict at smaller k
            calculateConflict(current, other, currentK - 1);
        }
        else {
            // the end result for k
            minKL = currentK + 1;
        }
    }

    private void recordFfConflict(Multiplex multiplex)
    {
        if (multiplex instanceof NonterminalRule) {
            NonterminalRule rule = (NonterminalRule) multiplex;
            rule.setFfConflictKL(conflict);
            if (conflict.isEmpty()) {
                rule.setMinFfKL(minKL);
            }
            else {
                rule.setMinFfKL(-1);
            }
        }
        else if (multiplex instanceof Block) {
            Block block = (Block) multiplex;
            block.setFfConflictKL(conflict);
            if (conflict.isEmpty()) {
                block.setMinFfKL(minKL);
            }
            else {
                block.setMinFfKL(-1);
            }
        }
    }

    private void recordConflict(Multiplex multiplex, Alternative source,
                                Alternative target)
    {
        if (!conflict.isEmpty()) {
            Multiplex.ConflictKL newConflictKL =
                new Multiplex.ConflictKL(source, target, conflict);

            if (!multiplex.getConflictsKL().contains(newConflictKL)) {
                multiplex.getConflictsKL().add(newConflictKL);
            }
        }
        int muxMinKL = multiplex.getMinKL();
        if (multiplex instanceof NonterminalRule) {
            NonterminalRule rule = (NonterminalRule) multiplex;
            if (conflict.isEmpty()) {
                if (minKL > muxMinKL && muxMinKL != -1) {
                    rule.setMinKL(minKL);
                }
            }
            else {
                rule.setMinKL(-1);
            }
        }
        else if (multiplex instanceof Block) {
            Block block = (Block) multiplex;
            if (conflict.isEmpty()) {
                if (minKL > muxMinKL && muxMinKL != -1) {
                    block.setMinKL(minKL);
                }
            }
            else {
                block.setMinKL(-1);
            }
        }
    }
}
