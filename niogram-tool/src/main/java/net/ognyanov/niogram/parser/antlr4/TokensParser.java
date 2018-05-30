/*
 * Copyright (c) 2018 by Nikolay Ognyanov. All rights reserved.
 * 
 * Use of this file is governed by the licensing conditions   
 * in the LICENSE.txt file in the root folder of the project.
 */
package net.ognyanov.niogram.parser.antlr4;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import net.ognyanov.niogram.parser.BaseErrorListener;
import net.ognyanov.niogram.parser.ErrorDispatcher;
import net.ognyanov.niogram.parser.ErrorListener;
import net.ognyanov.niogram.parser.antlr4.ANTLRv4TokensParser.LineContext;
import net.ognyanov.niogram.parser.antlr4.ANTLRv4TokensParser.TokenSpecsContext;
import net.ognyanov.niogram.util.BidirectionalMap;
import net.ognyanov.niogram.util.Pair;

class TokensParser
    extends ANTLRv4TokensBaseVisitor<Object>
    implements ErrorDispatcher
{
    private String                                 fileName            = null;
    private boolean                                errors              =
        false;
    private boolean                                warnings            =
        false;
    private Set<ErrorListener>                     errorListeners      =
        new HashSet<ErrorListener>();
    private Map<ErrorListener, RelayErrorListener> relayErrorListeners =
        new HashMap<ErrorListener, RelayErrorListener>();

    private Antlr4ToAstParser                          grammarParser;
    private ANTLRv4TokensParser                    tokenParser         = null;
    private ANTLRv4TokensLexer                     tokenLexer          = null;

    private BidirectionalMap<String, Integer>      nameToType          =
        new BidirectionalMap<String, Integer>();

    public TokensParser(String fileName, Antlr4ToAstParser grammarParser)
        throws IOException
    {
        this.fileName = fileName;
        this.grammarParser = grammarParser;
        InputStream is =
            grammarParser.getResourceLocator().getResourceAsStream(fileName);
        if (is == null) {
            throw new IOException("file not found");
        }
        CharStream input = CharStreams.fromStream(is);
        tokenLexer = new ANTLRv4TokensLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(tokenLexer);
        tokenParser = new ANTLRv4TokensParser(tokens);
        tokenParser.removeErrorListeners();
        tokenLexer.removeErrorListeners();
        RelayErrorListener relayEL =
            new RelayErrorListener(new BaseErrorListener(), grammarParser);
        tokenParser.addErrorListener(relayEL);
        tokenLexer.addErrorListener(relayEL);
    }

    public void parse()
    {
        ParseTree parseTree = tokenParser.tokenSpecs();
        visitTokenSpecs((TokenSpecsContext) parseTree);
    }

    @Override
    public Object visitLine(LineContext ctx)
    {
        String name = null;
        String intText = null;
        if (ctx.name() != null &&
                ctx.INTEGER() != null &&
                (ctx.name().IDENTIFIER() != null
                        || ctx.name().STRING_LITERAL() != null)) {
            int line = 0;
            int position = 0;
            boolean isIdentifier = false;
            if (ctx.name().IDENTIFIER() != null) {
                name = ctx.name().IDENTIFIER().getText();
                isIdentifier = true;
                line = ctx.name().IDENTIFIER().getSymbol().getLine();
                position = ctx.name().IDENTIFIER().getSymbol()
                    .getCharPositionInLine();
            }
            else if (ctx.name().STRING_LITERAL() != null) {
                name = ctx.name().STRING_LITERAL().getText();
                line = ctx.name().STRING_LITERAL().getSymbol().getLine();
                position = ctx.name().STRING_LITERAL().getSymbol()
                    .getCharPositionInLine();
            }
            else {
                // must be syntax error
                // already reported by the parse tree parser
            }
            intText = ctx.INTEGER().getText();
            int intValue = Integer.valueOf(intText);
            char c = name.charAt(0);
            if (c != '\'' && !Character.isUpperCase(c)) {
                String message =
                    "terminal name starting with a lower case letter ignored : "
                            + name + "=" + intValue;
                notifyErrorListeners(ErrorType.SyntaxErrors, line, position,
                    message);
            }
            else {
                if (nameToType.containsFirst(name)) {
                    grammarParser.getDuplicateTerminalNames().add(name);
                    String message =
                        "duplicate terminal name ignored : " + name + "="
                                + intValue;
                    notifyErrorListeners(ErrorType.DuplicateTerminals, line,
                        position, message);
                }
                else if (isIdentifier && nameToType.containsSecond(intValue)) {
                    String message =
                        "duplicate terminal type ignored : " + name + "="
                                + intValue;
                    notifyErrorListeners(ErrorType.SyntaxErrors, line, position,
                        message);
                }
                else if (isIdentifier) {
                    grammarParser.getImportedTerminalNames()
                        .add(new Pair<String, Integer>(name, intValue));
                    grammarParser.getKnownTerminalNames().add(name);
                    nameToType.put(name, intValue);
                }
                else {
                    // literal
                    String idName = nameToType.getFirst(intValue);
                    if (idName == null) {
                        String message =
                            "invalid literal type ignored : " + name + "="
                                    + intValue;
                        notifyErrorListeners(ErrorType.SyntaxErrors, line,
                            position, message);
                    }
                    else {
                        grammarParser.getLiteralToTerminal().put(name, idName);
                    }
                }
            }
        }
        /*
        else {
            // must be syntax error
            // already reported by the parse tree parser
        }
        */
        return null;

    }

    @Override
    public String getFileName()
    {
        return fileName;
    }

    public boolean hasErrors()
    {
        return errors;
    }

    public boolean hasWarnings()
    {
        return warnings;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<ErrorListener> getErrorListeners()
    {
        return errorListeners;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addErrorListener(ErrorListener errorListener)
    {
        errorListeners.add(errorListener);
        RelayErrorListener relayEL =
            new RelayErrorListener(errorListener, grammarParser);
        relayErrorListeners.put(errorListener, relayEL);
        tokenParser.addErrorListener(relayEL);
        tokenLexer.addErrorListener(relayEL);
        relayErrorListeners.put(errorListener, relayEL);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeErrorListener(ErrorListener errorListener)
    {
        if (errorListener == null) {
            throw new IllegalArgumentException("null listener");
        }
        RelayErrorListener relayErrorListener =
            relayErrorListeners.get(errorListener);
        errorListeners.remove(errorListener);
        if (relayErrorListener != null) {
            tokenParser.removeErrorListener(relayErrorListener);
            tokenLexer.removeErrorListener(relayErrorListener);
            relayErrorListeners.remove(errorListener);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeErrorListeners()
    {
        errorListeners.clear();
        relayErrorListeners.clear();
        tokenParser.removeErrorListeners();
        tokenLexer.removeErrorListeners();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void notifyErrorListeners(ErrorType errorType, int line,
                                     int position, String message)
    {
        for (ErrorListener errorListener : errorListeners) {
            errorListener.reportError(this, errorType, line, position, message);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void warnErrorListeners(ErrorType errorType, int line, int position,
                                   String message)
    {
        for (ErrorListener errorListener : errorListeners) {
            errorListener.reportWarning(this, errorType, line, position,
                message);
        }
    }

    @Override
    public void registerError(ErrorType error)
    {
        // never called - listeners report to GrammarParser

    }

    @Override
    public void registerWarning(ErrorType warning)
    {
        // never called - listeners report to GramamrParser
    }
}
