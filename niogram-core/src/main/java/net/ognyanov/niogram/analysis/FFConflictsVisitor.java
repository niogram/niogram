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
import net.ognyanov.niogram.util.BiasedBitSet;

class FFConflictsVisitor
    extends InterruptableGrammarVisitor
{
    private BiasedBitSet conflict = null;

    @Override
    public void visitGrammar(Grammar grammar)
    {
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
        calculateFfConflict(multiplex);
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
                calculateConflict(current, other);
                recordConflict(multiplex, current, other);
            }
        }
    }

    private void calculateFfConflict(Multiplex multiplex)
    {
        GrammarNode node = (GrammarNode) multiplex;
        BiasedBitSet currentFirst = node.getFirst();
        BiasedBitSet currentFollow = node.getFollow();
        conflict = currentFirst.conflict(currentFollow);
    }

    private void calculateConflict(Alternative current,
                                   Alternative other)
    {
        BiasedBitSet currentFirst = current.getFirst();
        BiasedBitSet otherFirst = other.getFirst();
        conflict = currentFirst.conflict(otherFirst);
    }

    private void recordFfConflict(Multiplex multiplex)
    {
        if (multiplex instanceof NonterminalRule) {
            NonterminalRule rule = (NonterminalRule) multiplex;
            rule.setFfConflict(conflict);
        }
        else if (multiplex instanceof Block) {
            Block block = (Block) multiplex;
            block.setFfConflict(conflict);
        }
    }

    private void recordConflict(Multiplex multiplex, Alternative source,
                                Alternative target)
    {
        if (!conflict.isEmpty()) {
            Multiplex.Conflict newConflictK =
                new Multiplex.Conflict(source, target, conflict);
            if (!multiplex.getConflicts().contains(newConflictK)) {
                multiplex.getConflicts().add(newConflictK);
            }
        }
    }

}
