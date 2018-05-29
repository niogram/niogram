/*
 * Copyright (c) 2018 by Nikolay Ognyanov. All rights reserved.
 * 
 * Use of this file is governed by the licensing conditions   
 * in the LICENSE.txt file in the root folder of the project.
 */
package net.ognyanov.niogram.parser.antlr4;

import java.io.IOException;
import java.util.List;

import org.antlr.v4.runtime.Vocabulary;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import net.ognyanov.niogram.parser.ErrorDispatcher;
import net.ognyanov.niogram.parser.ErrorListener;
import net.ognyanov.niogram.parser.ErrorDispatcher.ErrorType;
import net.ognyanov.niogram.parser.antlr4.ANTLRv4Parser.AtomContext;
import net.ognyanov.niogram.parser.antlr4.ANTLRv4Parser.DelegateGrammarContext;
import net.ognyanov.niogram.parser.antlr4.ANTLRv4Parser.GrammarSpecContext;
import net.ognyanov.niogram.parser.antlr4.ANTLRv4Parser.IdListContext;
import net.ognyanov.niogram.parser.antlr4.ANTLRv4Parser.IdentifierContext;
import net.ognyanov.niogram.parser.antlr4.ANTLRv4Parser.LexerAltContext;
import net.ognyanov.niogram.parser.antlr4.ANTLRv4Parser.LexerAltListContext;
import net.ognyanov.niogram.parser.antlr4.ANTLRv4Parser.LexerAtomContext;
import net.ognyanov.niogram.parser.antlr4.ANTLRv4Parser.LexerElementContext;
import net.ognyanov.niogram.parser.antlr4.ANTLRv4Parser.LexerRuleSpecContext;
import net.ognyanov.niogram.parser.antlr4.ANTLRv4Parser.OptionContext;
import net.ognyanov.niogram.parser.antlr4.ANTLRv4Parser.ParserRuleSpecContext;
import net.ognyanov.niogram.parser.antlr4.ANTLRv4Parser.TerminalContext;
import net.ognyanov.niogram.parser.antlr4.ANTLRv4Parser.TokensSpecContext;
import net.ognyanov.niogram.util.Pair;

@SuppressWarnings("unused")
class SymbolCollector
    extends ANTLRv4ParserBaseVisitor<Object>
{
    private static final String TOKEN_VOCABULARY  = "tokenVocab";
    private static final String OPTION_K          = "k";
    private Antlr4AstParser     parser            = null;
    private ANTLRv4Parser       parseTreeParser   = null;
    private Vocabulary          vocabulary        = null;
    private boolean             isNioGram         = false;

    boolean                     firstPass         = false;
    private boolean             inNonterminalRule = false;
    private boolean             inTerminalRule    = false;
    String                      ruleName          = null;

    private int                 vocabularyLine    = 0;
    private int                 vocabularyPos     = 0;

    public SymbolCollector(Antlr4AstParser parser,
                           ANTLRv4Parser parseTreeParser)
    {
        this.parser = parser;
        this.parseTreeParser = parseTreeParser;
        vocabulary = parseTreeParser.getVocabulary();
        isNioGram = parser.getMode() == Antlr4AstParser.Mode.NioGram;
    }

    @Override
    public Object visitGrammarSpec(GrammarSpecContext ctx)
    {
        inNonterminalRule = false;
        inTerminalRule = false;
        if (ctx.grammarType() != null) {
            String type = ctx.grammarType().getText();
            if ("lexergrammar".equals(type)) {
                parser.setGrammarType(Antlr4AstParser.GrammarType.LEXER);
            }
            else if ("parsergrammar".equals(type)) {
                parser.setGrammarType(Antlr4AstParser.GrammarType.PARSER);
            }
        }
        String name = ctx.identifier().getText();
        parser.setGrammarName(name);
        // collect symbols
        firstPass = true;
        visitChildren(ctx);
        firstPass = false;
        // check for unknown terminals and nonterminals
        visitChildren(ctx);
        return null;
    }

    @Override
    public Object visitOption(OptionContext ctx)
    {
        if (firstPass) {
            String identifier = null;
            String optionValue = null;
            if (ctx.identifier() != null && ctx.optionValue() != null) {
                identifier = ctx.identifier().getText();
                optionValue = ctx.optionValue().getText();
                if (TOKEN_VOCABULARY.equals(identifier)) {
                    String vocabularyFile = null;
                    if (optionValue.charAt(0) == '\'') {
                        vocabularyFile = optionValue.substring(1,
                            optionValue.length() - 1);
                    }
                    else {
                        vocabularyFile = optionValue;
                    }
                    vocabularyLine = ctx.start.getLine();
                    vocabularyPos = ctx.start.getCharPositionInLine();
                    if (vocabularyFile != null) {
                        loadVocabulary(vocabularyFile);
                    }
                }
                else if (OPTION_K.equals(identifier)) {
                    int k = -1;
                    try {
                        k = Integer.parseInt(optionValue);
                    }
                    catch (NumberFormatException e) {
                    }
                    if (k <= 0) {
                        int line = ctx.start.getLine();
                        int position = ctx.start.getCharPositionInLine();
                        String message =
                            "Invalid value for option k : " + optionValue;
                        parser.notifyErrorListeners(
                            ErrorType.IvalidOptionValue, line,
                            position, message);

                    }
                    else {
                        parser.setOptionK(k);
                    }
                }
                parser.getOptions()
                    .add(new Pair<String, String>(identifier, optionValue));
            }
            return visitChildren(ctx);
        }
        else {
            return visitChildren(ctx);
        }

    }

    @Override
    public Object visitDelegateGrammars(ANTLRv4Parser.DelegateGrammarsContext ctx)
    {
        if (firstPass) {
            int line = ctx.start.getLine();
            int position = ctx.start.getCharPositionInLine();
            String message = "NioGram does not support delegate grammars";
            parser.notifyErrorListeners(ErrorType.FailedGrammarImports, line,
                position, message);
            return visitChildren(ctx);
        }
        else {
            return visitChildren(ctx);
        }
    }

    @Override
    public Object visitDelegateGrammar(DelegateGrammarContext ctx)
    {
        if (firstPass) {
            String delegateGrammar = ctx.identifier(0).getText();
            parser.getDelegateGrammars().add(delegateGrammar);
            return visitChildren(ctx);
        }
        else {
            return visitChildren(ctx);
        }
    }

    @Override
    public Object visitTokensSpec(TokensSpecContext ctx)
    {
        if (firstPass) {
            IdListContext idList = ctx.idList();
            if (idList != null) {
                for (IdentifierContext id : idList.identifier()) {
                    String idName = id.getText();
                    int line = id.start.getLine();
                    int position = id.start.getCharPositionInLine();
                    if (!Character.isUpperCase(idName.charAt(0))) {
                        String message =
                            "terminal name starting with a lower case letter ignored : "
                                    + idName;
                        parser.notifyErrorListeners(ErrorType.SyntaxErrors,
                            line,
                            position, message);
                    }
                    else {
                        if (parser.getKnownTerminalNames().contains(idName)) {
                            parser.getDuplicateTerminalNames().add(idName);
                            String message = "duplicate terminal " + idName;
                            parser.notifyErrorListeners(
                                ErrorType.DuplicateTerminals, line, position,
                                message);
                        }
                        else {
                            parser.getDeclaredTerminalNames().add(idName);
                            parser.getKnownTerminalNames().add(idName);
                        }
                    }
                }
            }
            return null;
        }
        else {
            return null;
        }
    }

    @Override
    public Object visitParserRuleSpec(ParserRuleSpecContext ctx)
    {
        if (firstPass) {
            ruleName = ctx.RULE_REF().getText();
            if (parser.getKnownNonterminalNames().contains(ruleName)) {
                parser.getDuplicateNonterminalNames().add(ruleName);
                int line = ctx.RULE_REF().getSymbol().getLine();
                int position =
                    ctx.RULE_REF().getSymbol().getCharPositionInLine();
                String message =
                    "duplicate nonterminal rule " + ruleName + " ignored.";
                parser.notifyErrorListeners(ErrorType.DuplicateNonterminals,
                    line,
                    position, message);
            }
            else {
                parser.getKnownNonterminalNames().add(ruleName);
                parser.getDeclaredNonterminalNames().add(ruleName);
            }
            inNonterminalRule = true;
            visitChildren(ctx);
            inNonterminalRule = false;
            ruleName = null;
            return null;
        }
        else {
            return visitChildren(ctx);
        }
    }

    @Override
    public Object visitLexerRuleSpec(LexerRuleSpecContext ctx)
    {
        if (firstPass) {
            inTerminalRule = true;
            ruleName = ctx.TOKEN_REF().getText();
            if (parser.getKnownTerminalNames().contains(ruleName)) {
                parser.getDuplicateTerminalNames().add(ruleName);
                int line = ctx.TOKEN_REF().getSymbol().getLine();
                int position =
                    ctx.TOKEN_REF().getSymbol().getCharPositionInLine();
                String message = "duplicate terminal " + ruleName;
                parser.notifyErrorListeners(ErrorType.DuplicateTerminals, line,
                    position, message);
            }
            else {
                parser.getKnownTerminalNames().add(ruleName);
                parser.getDefinedTerminalNames().add(ruleName);
            }

            visitChildren(ctx);
            inTerminalRule = false;
            ruleName = null;
            return null;
        }
        else {
            return null;
        }
    }

    @Override
    public Object visitLexerAltList(LexerAltListContext ctx)
    {
        if (firstPass) {
            List<LexerAltContext> alts = ctx.lexerAlt();
            if (alts != null && alts.size() == 1) {
                LexerAltContext lalt = alts.get(0);
                if (lalt != null && lalt.lexerElements() != null
                        && lalt.lexerElements().lexerElement() != null) {
                    List<LexerElementContext> lels =
                        lalt.lexerElements().lexerElement();
                    if (lels != null && lels.size() == 1) {
                        LexerAtomContext atom = lels.get(0).lexerAtom();
                        if (atom != null && atom.terminal() != null
                                && atom.terminal()
                                    .STRING_LITERAL() != null) {
                            String literal =
                                atom.terminal().STRING_LITERAL().getText();
                            parser.getLiteralToTerminal().put(literal,
                                ruleName);
                        }
                    }
                }
            }
            return visitChildren(ctx);
        }
        else {
            return visitChildren(ctx);
        }
    }

    @Override
    public Object visitAtom(AtomContext ctx)
    {
        if (firstPass) {
            if (ctx.ruleref() != null) {
                String ruleRef = null;
                if (ctx.ruleref().RULE_REF() != null) {
                    ruleRef = ctx.ruleref().RULE_REF().getText();
                }
                if (ruleRef != null) {
                    parser.getUsedNonterminalNames().add(ruleRef);
                }
            }
            else if (ctx.terminal() != null) {
                TerminalContext tctx = ctx.terminal();
                String literal = null;
                if (tctx.TOKEN_REF() != null) {
                    String tokenRef = null;
                    tokenRef = tctx.TOKEN_REF().getText();
                    if (tokenRef != null) {
                        parser.getUsedTerminalNames().add(tokenRef);
                    }
                }
                else if (tctx.STRING_LITERAL() != null) {
                    literal = tctx.STRING_LITERAL().getText();
                }
                if (inNonterminalRule && literal != null) {
                    parser.getUsedStringLiterals().add(literal);
                    int line = ctx.start.getLine();
                    int position = ctx.start.getCharPositionInLine();
                    boolean unknownLiteral =
                        parser.getLiteralToTerminal()
                            .getSecond(literal) == null;
                    if (unknownLiteral) {
                        if (isNioGram) {
                            String message =
                                "literals are not allowed in nonterminal rules";
                            parser.notifyErrorListeners(
                                ErrorType.NonterminalLiterals,
                                line,
                                position, message);
                        }
                        else {
                            if (!(Antlr4AstParser.GrammarType.COMBINED == parser
                                .getGrammarType())) {
                                String message =
                                    "literals are not allowed in nonterminal rules";
                                parser.notifyErrorListeners(
                                    ErrorType.NonterminalLiterals,
                                    line,
                                    position, message);

                            }
                            else {
                                /*
                                Seems overly irritating.
                                String message =
                                "NioGram discourages use of literals in nonterminal rules";
                                parser.warnErrorListeners(ErrorType.NonterminalLiterals,
                                line,
                                position, message);
                                */
                            }
                        }
                    }
                }
            }
            return visitChildren(ctx);
        }
        else {
            int line = ctx.start.getLine();
            int position = ctx.start.getCharPositionInLine();
            if (ctx.ruleref() != null) {
                String ruleRef = null;
                if (ctx.ruleref().RULE_REF() != null) {
                    ruleRef = ctx.ruleref().RULE_REF().getText();
                }
                if (!parser.getKnownNonterminalNames()
                    .contains(ruleRef)) {
                    String message = "undefined nonterminal " + ruleRef;
                    parser.notifyErrorListeners(
                        ErrorDispatcher.ErrorType.UnknownNonterminals,
                        line,
                        position, message);
                }

            }
            else if (ctx.terminal() != null
                    && ctx.terminal().TOKEN_REF() != null) {
                String terminalName = ctx.getText();
                if (!parser.getKnownTerminalNames().contains(terminalName) &&
                        !"EOF".equals(terminalName)) {
                    String message =
                        "undefined terminal " + terminalName;
                    parser.notifyErrorListeners(
                        ErrorDispatcher.ErrorType.UnknownTerminals,
                        line,
                        position, message);
                }
            }
            return visitChildren(ctx);
        }

    }

    @Override
    public Object visitLexerAtom(LexerAtomContext ctx)
    {
        if (firstPass) {
            return visitChildren(ctx);
        }
        else {
            if (ctx.terminal() != null
                    && ctx.terminal().TOKEN_REF() != null) {
                int line = ctx.start.getLine();
                int position = ctx.start.getCharPositionInLine();
                String terminalName = ctx.getText();
                if (!parser.getKnownTerminalNames().contains(terminalName) &&
                        !"EOF".equals(terminalName)) {
                    String message = "undefined terminal " + terminalName;
                    parser.notifyErrorListeners(
                        ErrorDispatcher.ErrorType.UnknownTerminals, line,
                        position, message);
                }
            }
            return visitChildren(ctx);
        }
    }

    @Override
    public Object visitErrorNode(ErrorNode errorNode)
    {
        if (firstPass) {
            if (false) {
                int type = errorNode.getSymbol().getType();
                String text = errorNode.getText();
                String sName = vocabulary.getSymbolicName(type);

                System.out
                    .println(text + " : " + sName);
            }
            return null;
        }
        else {
            return null;
        }
    }

    @Override
    public Object visitTerminal(TerminalNode terminalNode)
    {
        if (firstPass) {
            if (false) {
                int type = terminalNode.getSymbol().getType();
                String text = terminalNode.getText();
                String sName = vocabulary.getSymbolicName(type);

                System.out
                    .println(text + " : " + sName);
            }
            return null;
        }
        else {
            return null;
        }
    }

    private void loadVocabulary(String vocabularyFile)
    {
        TokensParser tokenParser = null;
        String fullName = vocabularyFile + ".tokens";
        try {
            tokenParser =
                new TokensParser(fullName, parser);
        }
        catch (IOException e) {
            parser.notifyErrorListeners(ErrorType.FailedTokenImports,
                vocabularyLine, vocabularyPos,
                "failed to open token vocabulary " + fullName);
        }
        if (tokenParser != null) {
            tokenParser.removeErrorListeners();
            for (ErrorListener el : parser.getErrorListeners()) {
                tokenParser.addErrorListener(el);
            }
            tokenParser.parse();
        }
    }
}
