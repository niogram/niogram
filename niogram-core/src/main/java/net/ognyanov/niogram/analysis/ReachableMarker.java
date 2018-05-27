/*
 * Copyright (c) 2018 by Nikolay Ognyanov. All rights reserved.
 * 
 * Use of this file is governed by the licensing conditions   
 * in the LICENSE.txt file in the root folder of the project.
 */
package net.ognyanov.niogram.analysis;

import java.util.List;

import net.ognyanov.niogram.ast.Alternative;
import net.ognyanov.niogram.ast.Block;
import net.ognyanov.niogram.ast.Grammar;
import net.ognyanov.niogram.ast.GrammarVisitor;
import net.ognyanov.niogram.ast.Nonterminal;
import net.ognyanov.niogram.ast.NonterminalRule;
import net.ognyanov.niogram.ast.Term;
import net.ognyanov.niogram.ast.Terminal;
import net.ognyanov.niogram.ast.TerminalRule;

class ReachableMarker
    extends GrammarVisitor
{
    private boolean modified = false;

    @Override
    public void visitGrammar(Grammar grammar)
    {
        if (grammar.getNonterminalRules().size() == 0) {
            return;
        }
        grammar.getNonterminalRules().get(0).setReachable(true);
        do {
            modified = false;
            super.visitGrammar(grammar);
        } while (modified);

        List<NonterminalRule> unreachable = grammar.getUnreachable();
        for (NonterminalRule rule : grammar.getNonterminalRules()) {
            if (!rule.isReachable()) {
                unreachable.add(rule);
            }
        }
    }

    @Override
    public void visitNonterminalRule(NonterminalRule rule)
    {
        super.visitNonterminalRule(rule);
        List<Alternative> alternatives = rule.getAlternatives();
        if (rule.isReachable() && !alternatives.isEmpty()) {
            if (!alternatives.get(0).isReachable()) {
                for (Alternative alternative : alternatives) {
                    alternative.setReachable(true);
                }
                modified = true;
            }
        }
    }

    @Override
    public void visitBlock(Block block)
    {
        super.visitBlock(block);
        List<Alternative> alternatives = block.getAlternatives();
        if (block.isReachable() && !alternatives.isEmpty()) {
            if (!alternatives.get(0).isReachable()) {
                for (Alternative alternative : alternatives) {
                    alternative.setReachable(true);
                }
                modified = true;
            }
        }
    }

    @Override
    public void visitAlternative(Alternative alternative)
    {
        super.visitAlternative(alternative);
        List<Term> terms = alternative.getTerms();
        if (alternative.isReachable() && !terms.isEmpty()) {
            for (Term term : alternative.getTerms()) {
                if (term instanceof Block) {
                    if (!term.isReachable()) {
                        term.setReachable(true);
                        modified = true;
                    }
                }
                else if (term instanceof Nonterminal) {
                    NonterminalRule rule = ((Nonterminal) term).getRule();
                    if (!rule.isReachable()) {
                        rule.setReachable(true);
                        modified = true;
                    }
                }
                else if (term instanceof Terminal) {
                    TerminalRule rule = ((Terminal) term).getRule();
                    if (!rule.isReachable()) {
                        rule.setReachable(true);
                        modified = true;
                    }
                }
            }
        }
    }
}
