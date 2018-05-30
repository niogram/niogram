/*
 * Copyright (c) 2018 by Nikolay Ognyanov. All rights reserved.
 * 
 * Use of this file is governed by the licensing conditions   
 * in the LICENSE.txt file in the root folder of the project.
 */
package net.ognyanov.niogram.parser.antlr4;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import net.ognyanov.niogram.ast.Grammar;
import net.ognyanov.niogram.util.ClassPathLocator;

public class GrammarParserTest
{
    @Test
    public void test()
    {
        ClassPathLocator locator = new ClassPathLocator();
        List<String> testGrammars = locator
            .findResources("/grammars/.*\\.g4");
        for (String grammarFile : testGrammars) {
            Antlr4ToAstParser parser = null;
            try {
                parser = new Antlr4ToAstParser(grammarFile, locator);
            }
            catch (IOException e) {
                // should never happen
                assertTrue(false);
            }
            Grammar grammar = parser.grammar();
            /*
            grammar.setK(1);
            FlagsCalculator flagsCalculator = new FlagsCalculator();
            flagsCalculator.calculate(grammar);
            FirstFollowCalculator calculator =
                new FirstFollowCalculator();
            calculator.calculate(grammar);
            int type = 0;
            for (TerminalRule rule : grammar.getTerminalRules()) {
                if (rule.getDisplayName().equals("A")) {
                    type = rule.getType();
                }
            }
            FirstTrace trace = null;
            for (NonterminalRule rule : grammar.getNonterminalRules()) {
                if (rule.getDisplayName().equals("a")) {
                    trace = new FirstTrace(rule, type);
                    System.out.println(trace.toDotString());
                }
            }
            calculator.calculate(grammar);
            //FirstKTrace trace = new FirstKTrace(rule, terminalType, position)
             */
            assertFalse(parser.hasErrors());
        }
    }
}
