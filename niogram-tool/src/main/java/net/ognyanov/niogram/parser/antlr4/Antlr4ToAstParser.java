/*
 * Copyright (c) 2018 by Nikolay Ognyanov. All rights reserved.
 * 
 * Use of this file is governed by the licensing conditions   
 * in the LICENSE.txt file in the root folder of the project.
 */
package net.ognyanov.niogram.parser.antlr4;

import static net.ognyanov.niogram.ast.BuiltInTypes.ALTERNATIVE;
import static net.ognyanov.niogram.ast.BuiltInTypes.ALTERNATIVE_NAME;
import static net.ognyanov.niogram.ast.BuiltInTypes.BLOCK_NAME;
import static net.ognyanov.niogram.ast.BuiltInTypes.DOT;
import static net.ognyanov.niogram.ast.BuiltInTypes.DOT_NAME;
import static net.ognyanov.niogram.ast.BuiltInTypes.EOF;
import static net.ognyanov.niogram.ast.BuiltInTypes.EOF_NAME;
import static net.ognyanov.niogram.ast.BuiltInTypes.GRAMMAR;
import static net.ognyanov.niogram.ast.BuiltInTypes.GRAMMAR_NAME;
import static net.ognyanov.niogram.ast.BuiltInTypes.INVALID;
import static net.ognyanov.niogram.ast.BuiltInTypes.INVALID_NAME;
import static net.ognyanov.niogram.ast.BuiltInTypes.NOT;
import static net.ognyanov.niogram.ast.BuiltInTypes.NOT_NAME;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;

import net.ognyanov.niogram.ast.Alternative;
import net.ognyanov.niogram.ast.Block;
import net.ognyanov.niogram.ast.BuiltInTypes;
import net.ognyanov.niogram.ast.Grammar;
import net.ognyanov.niogram.ast.GrammarNode;
import net.ognyanov.niogram.ast.GrammarVisitor;
import net.ognyanov.niogram.ast.Nonterminal;
import net.ognyanov.niogram.ast.NonterminalRule;
import net.ognyanov.niogram.ast.Term;
import net.ognyanov.niogram.ast.Terminal;
import net.ognyanov.niogram.ast.TerminalRule;
import net.ognyanov.niogram.parser.AstParser;
import net.ognyanov.niogram.parser.BaseErrorListener;
import net.ognyanov.niogram.parser.ErrorDispatcher;
import net.ognyanov.niogram.parser.ErrorListener;
import net.ognyanov.niogram.parser.antlr4.ANTLRv4Parser.AlternativeContext;
import net.ognyanov.niogram.parser.antlr4.ANTLRv4Parser.AtomContext;
import net.ognyanov.niogram.parser.antlr4.ANTLRv4Parser.BlockContext;
import net.ognyanov.niogram.parser.antlr4.ANTLRv4Parser.EbnfSuffixContext;
import net.ognyanov.niogram.parser.antlr4.ANTLRv4Parser.ElementContext;
import net.ognyanov.niogram.parser.antlr4.ANTLRv4Parser.GrammarSpecContext;
import net.ognyanov.niogram.parser.antlr4.ANTLRv4Parser.LexerRuleSpecContext;
import net.ognyanov.niogram.parser.antlr4.ANTLRv4Parser.ParserRuleSpecContext;
import net.ognyanov.niogram.util.BidirectionalMap;
import net.ognyanov.niogram.util.FileSystemLocator;
import net.ognyanov.niogram.util.Pair;
import net.ognyanov.niogram.util.ResourceLocator;

/**
 * The NioGram parser for ANTLR 4 grammars.<p>
 * Parses grammar text into AST.<p>
 * The general strategy of the parser is to try to produce an
 * AST even if errors are encountered during parsing.
 * It is up to the client to decide whether and
 * for what purposes the result AST is usable in the presence of
 * errors. Diagnostic information is available in this class.<p>
 * The parser is invoked by a call to the {@link #grammar()} method.
 * This method may be invoked only once and on subsequent call
 * throws IllegalStateException<p>
 * Note that the parser uses a {@link ResourceLocator}
 * in order to retrieve the following resources :
 * <ol>
 * <li> Grammar source code.</li>
 * <li> Source code of imported grammars.</li>
 * <li> Token vocabulary files.</li>
 * </ol><p>
 * The default locator is a {@link FileSystemLocator}.<p>
 * 
 * After parsing a grammar the parser provides the following
 * diagnostic information:
 * <ul>
 * <li><strong>errors</strong>
 * - a general error flag. More detailed information about
 *   errors is provided by {@link #getErrors()} as a map
 *   from error types to boolean values.</li>
 * <li><strong>warnings</strong>
 * - a general warnings flag. More detailed information about
 *   warnings is provided by {@link #getWarnings()} as a map
 *   from error types to boolean values.</li></li>
 * <li><strong>UnknownTerminalNames</strong>
 * - the set of unknown terminals in the grammar (if any)</li>
 * <li><strong>DuplicateTerminalNames</strong>
 * - the set of duplicate terminals in the grammar (if any)</li>
 * <li><strong>UnknownNonterminalNames</strong>
 * - the set of unknown nonterminals in the grammar (if any)</li>
 * <li><strong>DuplicateNonterminalNames</strong>
 * - the set of duplicate terminals in the grammar (if any)</li>
 * </ul>
 * 
 * <p>In the NioGram mode of the parser all encountered problems are
 * considered errors. In the ANTLR mode the following problems are 
 * considered warnings rather than errors:
 * <ul>
 * <li><strong>NonterminalLiterals</strong></li>
 * <li><strong>DotExpressions</strong></li>
 * <li><strong>NotSets</strong></li>
 * <li><strong>LazyEBNF</strong></li>
 * </ul>
 * 
 * @author Nikolay Ognyanov
 */
public class Antlr4ToAstParser
    implements AstParser
{
    /**
     * Parser mode. In ANTLR mode the parser treats
     * as warnings rather than errors the following
     * error types:
     * <ul>
     * <li><strong>NonterminalLiterals</strong></li>
     * <li><strong>DotExpressions</strong></li>
     * <li><strong>NotSets</strong></li>
     * <li><strong>LazyEBNF</strong></li>
     * </ul>
     *
     * @author Nikolay Ognyanov
     */
    public enum Mode
    {
        ANTLR, NioGram
    };

    public enum GrammarType
    {
        PARSER, LEXER, COMBINED
    };

    private static final String                    TERMINAL_NAME_PREFIX      =
        "$T_";

    private Mode                                   mode                      =
        Mode.ANTLR;
    private String                                 fileName                  =
        null;

    private GrammarType                            grammarType               =
        GrammarType.COMBINED;
    private String                                 grammarName               =
        null;
    private List<Pair<String, String>>             options                   =
        new ArrayList<Pair<String, String>>();
    private int                                    optionK                   =
        -1;
    private List<String>                           delegateGrammars          =
        new ArrayList<String>();

    private boolean                                errors                    =
        false;
    private boolean                                warnings                  =
        false;

    private Map<ErrorType, Boolean>                errorsMap                 =
        new HashMap<ErrorDispatcher.ErrorType, Boolean>();
    private Map<ErrorType, Boolean>                warningsMap               =
        new HashMap<ErrorDispatcher.ErrorType, Boolean>();

    private List<Pair<String, Integer>>            importedTerminalNames     =
        new ArrayList<Pair<String, Integer>>();
    private List<String>                           declaredTerminalNames     =
        new ArrayList<String>();
    private List<String>                           definedTerminalNames      =
        new ArrayList<String>();
    private Set<String>                            knownTerminalNames        =
        new HashSet<String>();
    private Set<String>                            unknownTerminalNames      =
        new TreeSet<String>();
    private Set<String>                            usedTerminalNames         =
        new HashSet<String>();
    private Set<String>                            duplicateTerminalNames    =
        new HashSet<String>();

    private List<String>                           declaredNonterminalNames  =
        new ArrayList<String>();
    private Set<String>                            knownNonterminalNames     =
        new HashSet<String>();
    private Set<String>                            unknownNonterminalNames   =
        new TreeSet<String>();
    private Set<String>                            usedNonterminalNames      =
        new HashSet<String>();
    private Set<String>                            duplicateNonterminalNames =
        new HashSet<String>();
    private Set<String>                            usedStringLiterals        =
        new HashSet<String>();
    private BidirectionalMap<String, String>       literalToTerminal         =
        new BidirectionalMap<String, String>();
    private ResourceLocator                        resourceLocator           =
        new FileSystemLocator();
    private Set<ErrorListener>                     errorListeners            =
        new HashSet<ErrorListener>();
    private Map<ErrorListener, RelayErrorListener> relayErrorListeners       =
        new HashMap<ErrorListener, RelayErrorListener>();

    private Antlr4ToAstParser                        parent                    =
        null;

    private List<Antlr4ToAstParser>                  children                  =
        new ArrayList<Antlr4ToAstParser>();

    private boolean                                inSyntax                  =
        false;
    private boolean                                parsed                    =
        false;
    private ANTLRv4Lexer                           parseTreeLexer            =
        null;
    private ANTLRv4Parser                          parseTreeParser           =
        null;
    private GrammarSpecContext                     parseTree                 =
        null;
    private SilentErrorListener                    silentListener            =
        null;
    private RelayErrorListener                     silentRelayListener       =
        null;

    // AST Builder data
    private Grammar                                grammar                   =
        null;
    private boolean                                debugMe                   =
        false;
    private boolean                                isNioGram                 =
        false;
    private Set<String>                            visitedRules              =
        new HashSet<String>();
    private Map<String, TerminalRule>              nameToTerminalRule        =
        new HashMap<String, TerminalRule>();
    private Map<String, NonterminalRule>           nameToNonterminalRule     =
        new HashMap<String, NonterminalRule>();

    private Deque<GrammarNode>                     stack                     =
        new ArrayDeque<GrammarNode>();
    private int                                    currentType               =
        0;

    /**
     * Creates a new parser.
     * 
     * @param fileName the name of the grammar file
     * @param resourceLocator the resource locator to be used by the parser
     * @throws IOException if the parser fails to access the grammar file
     */
    public Antlr4ToAstParser(String fileName, ResourceLocator resourceLocator)
        throws IOException
    {
        if (resourceLocator != null) {
            this.resourceLocator = resourceLocator;
        }
        this.fileName = fileName;
        InputStream is = this.resourceLocator.getResourceAsStream(fileName);
        if (is == null) {
            throw new IOException("file not found");
        }
        CharStream input = null;
        input = CharStreams.fromStream(is);
        parseTreeLexer = new ANTLRv4Lexer(input);
        CommonTokenStream tokens = new CommonTokenStream(parseTreeLexer);
        parseTreeParser = new ANTLRv4Parser(tokens);
        initFlags();
        initErrorListeners();
    }

    /**
     * Creates a new parser. Uses the default {@link FileSystemLocator}.
     * 
     * @param fileName the name of the grammar file
     * @throws IOException if the parser fails to access the grammar file
     */
    public Antlr4ToAstParser(String fileName)
        throws IOException
    {
        this(fileName, null);
    }

    /**
     * Creates a new parser.
     * 
     * @param inputStream an input stream for the grammar text
     * @param resourceLocator the resource locator to be used by the parser
     * @throws IOException if the parser fails to access the stream
     */
    public Antlr4ToAstParser(InputStream inputStream,
                           ResourceLocator resourceLocator)
        throws IOException
    {
        if (inputStream == null) {
            throw new IOException("null stream");
        }
        if (resourceLocator != null) {
            this.resourceLocator = resourceLocator;
        }
        CharStream input = null;
        input = CharStreams.fromStream(inputStream);
        parseTreeLexer = new ANTLRv4Lexer(input);
        CommonTokenStream tokens = new CommonTokenStream(parseTreeLexer);
        parseTreeParser = new ANTLRv4Parser(tokens);
        initFlags();
        initErrorListeners();
    }

    /**
     * Creates a new parser. Uses the default {@link FileSystemLocator}.
     * 
     * @param inputStream an input stream for the grammar text
     * @throws IOException if the parser fails to access the grammar file
     */
    public Antlr4ToAstParser(InputStream inputStream)
        throws IOException
    {
        this(inputStream, null);
    }

    private void initFlags()
    {
        for (ErrorDispatcher.ErrorType errorType : ErrorDispatcher.ErrorType
            .values()) {
            errorsMap.put(errorType, false);
            warningsMap.put(errorType, false);
        }
    }

    private void initErrorListeners()
    {
        if (parent != null) {
            for (ErrorListener errorListener : parent.getErrorListeners()) {
                addErrorListener(errorListener);
            }
        }
        else {
            silentListener = new SilentErrorListener();
            silentRelayListener = new RelayErrorListener(silentListener, this);
            errorListeners.add(silentListener);
            BaseErrorListener parserErrorListener =
                new BaseErrorListener();
            errorListeners.add(silentListener);
            errorListeners.add(parserErrorListener);
            parseTreeParser.removeErrorListeners();
            parseTreeLexer.removeErrorListeners();
            RelayErrorListener relayErrorListener =
                new RelayErrorListener(parserErrorListener, this);
            parseTreeParser.addErrorListener(relayErrorListener);
            parseTreeLexer.addErrorListener(relayErrorListener);
            relayErrorListeners.put(parserErrorListener, relayErrorListener);
            parseTreeParser.addErrorListener(silentRelayListener);
            relayErrorListeners.put(silentListener, silentRelayListener);
        }
    }

    /**
     * Retrieves the mode of the parser.
     * 
     * @return the mode
     */
    public Mode getMode()
    {
        return mode;
    }

    /**
     * Sets the mode of the parser.
     * 
     * @param mode the mode to be set
     */
    public void setMode(Mode mode)
    {
        this.mode = mode;
    }

    /**
     * Retrieves the file name of the grammar.
     * 
     * @return the file name or null if it is unknown
     */
    public String getFileName()
    {
        return fileName;
    }

    /**
     * Sets the file name of the grammar
     * 
     * @param fileName the file name
     */
    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }

    /**
    * Retrieves the resource locator used by the parser.
    * 
    * @return the resource locator
    */
    public ResourceLocator getResourceLocator()
    {
        return resourceLocator;
    }

    /**
     * Sets the resource locator to be used by the parser.
     * 
     * @param resourceLocator the resource locator
     */
    public void setResourceLocator(ResourceLocator resourceLocator)
    {
        this.resourceLocator = resourceLocator;
    }

    /**
     * Invokes the parser.
     * 
     * @return an AST for the grammar or null if the
     * parsing failed because of syntax errors.
     * Note that an AST will be returned in case of
     * semantic errors such as e.g. duplicate or/and
     * missing rules.
     */
    public Grammar grammar()
    {
        if (parsed) {
            throw new IllegalStateException("already parsed");
        }
        parsed = true;
        inSyntax = true;
        parseTree = parseTreeParser.grammarSpec();
        if (parseTree == null || parseTree.identifier() == null) {
            return null;
        }
        inSyntax = false;

        SymbolCollector symbolCollector =
            new SymbolCollector(Antlr4ToAstParser.this, parseTreeParser);
        symbolCollector.visit(parseTree);

        createGrammar();
        addBuiltIns();
        fillUnknown();
        buildRules();

        ASTBuilder builder = new ASTBuilder();
        Grammar grammar = (Grammar) builder.visit(parseTree);

        NamingVisitor namingVisitor = new NamingVisitor();
        namingVisitor.visitGrammar(grammar);

        ParentSetter parentSetter = new ParentSetter();
        parentSetter.visitGrammar(grammar);

        return grammar;
    }

    private void createGrammar()
    {
        grammar = new Grammar(GRAMMAR);
        grammar.setSymbolicName(GRAMMAR_NAME);
        grammar.setDisplayName(getGrammarName());
        grammar.setSourceContext(parseTree);
        if (getOptionK() > 0) {
            grammar.setK(optionK);
            grammar.setKL(optionK);
        }
    }

    private void setCurrentType(int type)
    {
        Antlr4ToAstParser.this.currentType = type;
    }

    private int nextType()
    {
        return ++currentType;
    }

    private String terminalName(int type)
    {
        return TERMINAL_NAME_PREFIX + type;
    }

    private void addBuiltIns()
    {
        List<TerminalRule> terminalRules =
            grammar.getTerminalRules();
        BidirectionalMap<Integer, String> idToName =
            grammar.getTypeToName();

        TerminalRule not = new TerminalRule(NOT);
        not.setSymbolicName(NOT_NAME);
        not.setDisplayName(NOT_NAME);
        nameToTerminalRule.put(NOT_NAME, not);
        terminalRules.add(not);
        idToName.put(NOT, NOT_NAME);

        TerminalRule dot = new TerminalRule(DOT);
        dot.setSymbolicName(DOT_NAME);
        dot.setDisplayName(DOT_NAME);
        nameToTerminalRule.put(DOT_NAME, dot);
        terminalRules.add(dot);
        idToName.put(DOT, NOT_NAME);

        TerminalRule eof = new TerminalRule(EOF);
        eof.setSymbolicName(EOF_NAME);
        eof.setDisplayName("EOF");
        nameToTerminalRule.put(EOF_NAME, eof);
        terminalRules.add(eof);
        idToName.put(EOF, "EOF");

        TerminalRule invalid = new TerminalRule(INVALID);
        invalid.setSymbolicName(INVALID_NAME);
        invalid.setDisplayName(INVALID_NAME);
        nameToTerminalRule.put(INVALID_NAME, invalid);
        terminalRules.add(invalid);
        idToName.put(INVALID, INVALID_NAME);
    }

    private void fillUnknown()
    {
        for (String name : usedTerminalNames) {
            if (!knownTerminalNames.contains(name) && !"EOF".equals(name)) {
                unknownTerminalNames.add(name);
            }
        }
        if (unknownTerminalNames.size() > 0) {
            registerError(ErrorType.UnknownTerminals);
        }
        for (String name : usedNonterminalNames) {
            if (!knownNonterminalNames.contains(name)) {
                unknownNonterminalNames.add(name);
            }
        }
        if (unknownNonterminalNames.size() > 0) {
            registerError(ErrorType.UnknownNonterminals);
        }
    }

    private void buildRules()
    {
        List<TerminalRule> terminalRules =
            grammar.getTerminalRules();
        List<NonterminalRule> nonterminalRules =
            grammar.getNonterminalRules();
        BidirectionalMap<Integer, String> idToName =
            grammar.getTypeToName();

        int importedMaxType = Integer.MIN_VALUE;
        for (Pair<String, Integer> token : importedTerminalNames) {
            int type = token.getSecond();
            if (type > importedMaxType) {
                importedMaxType = type;
            }
        }
        if (importedMaxType < 0) {
            importedMaxType = 0;
        }
        setCurrentType(importedMaxType);

        for (Pair<String, Integer> token : importedTerminalNames) {
            String name = token.getFirst();
            int type = token.getSecond();
            // presumably literals are handled elsewhere
            if (name.charAt(0) != '\'') {
                if (nameToTerminalRule.containsKey(name)) {
                    continue;
                }
                TerminalRule terminalRule = new TerminalRule(type);
                terminalRule.setSymbolicName(name);
                terminalRule.setDisplayName(name);
                nameToTerminalRule.put(name, terminalRule);
                terminalRules.add(terminalRule);
                idToName.put(type, name);
            }
        }
        for (String name : declaredTerminalNames) {
            if (nameToTerminalRule.containsKey(name)) {
                continue;
            }
            int type = nextType();
            TerminalRule terminalRule = new TerminalRule(type);
            terminalRule.setSymbolicName(name);
            terminalRule.setDisplayName(name);
            nameToTerminalRule.put(name, terminalRule);
            terminalRules.add(terminalRule);
            idToName.put(type, name);
        }
        for (String name : definedTerminalNames) {
            if (nameToTerminalRule.containsKey(name)) {
                continue;
            }
            int type = nextType();
            TerminalRule terminalRule = new TerminalRule(type);
            terminalRule.setSymbolicName(name);
            terminalRule.setDisplayName(name);
            nameToTerminalRule.put(name, terminalRule);
            terminalRules.add(terminalRule);
            idToName.put(type, name);
        }
        for (String name : unknownTerminalNames) {
            if (nameToTerminalRule.containsKey(name)
                    || "EOF".equals(name)) {
                continue;
            }
            int type = nextType();
            TerminalRule terminalRule = new TerminalRule(type);
            terminalRule.setSymbolicName(name);
            terminalRule.setDisplayName(name);
            nameToTerminalRule.put(name, terminalRule);
            terminalRules.add(terminalRule);
            idToName.put(type, name);
        }
        for (String literal : usedStringLiterals) {
            String terminalName = literalToTerminal.getSecond(literal);
            if (nameToTerminalRule.containsKey(terminalName)) {
                continue;
            }
            int type = nextType();
            if (terminalName == null) {
                terminalName = terminalName(type);
            }
            TerminalRule terminalRule = new TerminalRule(type);
            terminalRule.setSymbolicName(terminalName);
            terminalRule.setDisplayName(literal);
            literalToTerminal.put(literal, terminalName);
            nameToTerminalRule.put(terminalName, terminalRule);
            terminalRules.add(terminalRule);
            idToName.put(type, literal);
        }

        for (String name : declaredNonterminalNames) {
            if (nameToNonterminalRule.containsKey(name)) {
                continue;
            }
            int type = nextType();
            NonterminalRule nonterminalRule =
                new NonterminalRule(type);
            nonterminalRule.setSymbolicName(name);
            nonterminalRule.setDisplayName(name);
            nameToNonterminalRule.put(name, nonterminalRule);
            nonterminalRules.add(nonterminalRule);
            idToName.put(type, name);
        }
        for (String nonterminalName : unknownNonterminalNames) {
            int type = nextType();
            NonterminalRule nonterminalRule = new NonterminalRule(type);
            nonterminalRule.setSymbolicName(nonterminalName);
            nonterminalRule.setDisplayName(nonterminalName);
            Alternative alternative = new Alternative(ALTERNATIVE);
            alternative.setSymbolicName(ALTERNATIVE_NAME);
            alternative.setDisplayName(ALTERNATIVE_NAME);
            nonterminalRule.getAlternatives().add(alternative);
            nameToNonterminalRule.put(nonterminalName, nonterminalRule);
            nonterminalRules.add(nonterminalRule);
            idToName.put(type, nonterminalName);
        }
    }

    /**
     * Retrieves the type of the parsed grammar.
     * 
     * @return the grammar type
     */
    public GrammarType getGrammarType()
    {
        return grammarType;
    }

    void setGrammarType(GrammarType grammarType)
    {
        this.grammarType = grammarType;
    }

    /**
     * Retrieves the name of the parsed grammar.
     * 
     * @return the grammar name
     */
    public String getGrammarName()
    {
        return grammarName;
    }

    void setGrammarName(String grammarName)
    {
        this.grammarName = grammarName;
    }

    /**
     * Retrieves the list of options declared in the grammar
     * 
     * @return the options
     */
    public List<Pair<String, String>> getOptions()
    {
        return options;
    }

    /**
     * Retrieves the value of the option k as
     * specified in the grammar text or -1 if
     * no option k was specified.
     * 
     * @return the value of the option or -1 if not specified
     */
    public int getOptionK()
    {
        return optionK;
    }

    /**
     * Sets the value of the option k to be
     * set as both K and KL into the created grammar.
     * 
     * @param optionK the value of k
     */
    public void setOptionK(int optionK)
    {
        this.optionK = optionK;
    }

    /**
     * Retrieves the list of delegate grammars declared in
     * the parsed grammar.
     * 
     * @return the list of delegate grammars
     */
    public List<String> getDelegateGrammars()
    {
        return delegateGrammars;
    }

    public boolean hasErrors()
    {
        return errors;
    }

    public Map<ErrorType, Boolean> getErrors()
    {
        return errorsMap;
    }

    public boolean hasWarnings()
    {
        return warnings;
    }

    public Map<ErrorType, Boolean> getWarnings()
    {
        return warningsMap;
    }

    List<Pair<String, Integer>> getImportedTerminalNames()
    {
        return importedTerminalNames;
    }

    List<String> getDeclaredTerminalNames()
    {
        return declaredTerminalNames;
    }

    List<String> getDefinedTerminalNames()
    {
        return definedTerminalNames;
    }

    Set<String> getKnownTerminalNames()
    {
        return knownTerminalNames;
    }

    /**
     * Retrieves the (possibly empty) set of unknown
     * terminals in the parsed grammar.
     * 
     * @return the set of unknown terminals
     */
    public Set<String> getUnknownTerminalNames()
    {
        return unknownTerminalNames;
    }

    /**
     * Retrieves the (possibly empty) set of duplicate
     * terminals in the parsed grammar.
     * 
     * @return the set of duplicated terminals.
     */
    public Set<String> getDuplicateTerminalNames()
    {
        return duplicateTerminalNames;
    }

    Set<String> getUsedTerminalNames()
    {
        return usedTerminalNames;
    }

    BidirectionalMap<String, String> getLiteralToTerminal()
    {
        return literalToTerminal;
    }

    List<String> getDeclaredNonterminalNames()
    {
        return declaredNonterminalNames;
    }

    Set<String> getKnownNonterminalNames()
    {
        return knownNonterminalNames;
    }

    /**
     * Retrieves the (possibly empty) set of unknown
     * nonterminals in the parsed grammar.
     * 
     * @return the set of unknown nonterminals
     */
    public Set<String> getUnknownNonterminalNames()
    {
        return unknownNonterminalNames;
    }

    /**
     * Retrieves the (possibly empty) set of duplicate
     * nonterminals in the parsed grammar.
     * 
     * @return the set of duplicate nonterminals
     */
    public Set<String> getDuplicateNonterminalNames()
    {
        return duplicateNonterminalNames;
    }

    Set<String> getUsedNonterminalNames()
    {
        return usedNonterminalNames;
    }

    Set<String> getUsedStringLiterals()
    {
        return usedStringLiterals;
    }

    /**
     * Generates an XML representation of the parse tree. 
     * 
     * @return the XML string
     */
    public String toXmlString()
    {
        if (parseTree == null) {
            throw new IllegalStateException("no parse tree");
        }
        XmlStringVisitor xmlWriter = new XmlStringVisitor(parseTreeParser);
        String xmlString = xmlWriter.toXmlString(parseTree);
        return xmlString;
    }

    /**
     * Generates a DOT language representation of the parse tree. 
     * For combined grammars the lexer rules are omitted.
     * 
     * @return the dot string
     */
    public String toDotString()
    {
        if (parseTree == null) {
            throw new IllegalStateException("no parse tree");
        }
        boolean excludeTerminals = !getGrammarType().equals(GrammarType.LEXER);
        DotStringVisitor dotWriter = new DotStringVisitor(parseTreeParser);
        String dotString = dotWriter.toDotString(parseTree, excludeTerminals);
        return dotString;
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
            new RelayErrorListener(errorListener, this);
        relayErrorListeners.put(errorListener, relayEL);
        parseTreeParser.addErrorListener(relayEL);
        parseTreeLexer.addErrorListener(relayEL);
        if (!children.isEmpty()) {
            for (Antlr4ToAstParser child : children) {
                child.addErrorListener(errorListener);
            }
        }
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
            parseTreeParser.removeErrorListener(relayErrorListener);
            parseTreeLexer.removeErrorListener(relayErrorListener);
            relayErrorListeners.remove(errorListener);
        }
        if (!children.isEmpty()) {
            for (Antlr4ToAstParser child : children) {
                child.removeErrorListener(errorListener);
            }
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
        parseTreeParser.removeErrorListeners();
        parseTreeLexer.removeErrorListeners();
        errorListeners.add(silentListener);
        parseTreeParser.addErrorListener(silentRelayListener);
        relayErrorListeners.put(silentListener, silentRelayListener);
        if (!children.isEmpty()) {
            for (Antlr4ToAstParser child : children) {
                child.removeErrorListeners();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void notifyErrorListeners(ErrorDispatcher.ErrorType errorType,
                                     int line, int position, String message)
    {
        errors = true;
        if (inSyntax) {
            errorsMap.put(ErrorType.SyntaxErrors, true);
        }
        for (ErrorListener errorListener : errorListeners) {
            errorListener.reportError(this, errorType, line, position, message);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void warnErrorListeners(ErrorDispatcher.ErrorType errorType,
                                   int line, int position, String message)
    {
        warnings = true;
        if (inSyntax) {
            errorsMap.put(ErrorType.SyntaxErrors, true);
        }
        for (ErrorListener errorListener : errorListeners) {
            errorListener.reportWarning(this, errorType, line, position,
                message);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerError(ErrorType error)
    {
        errorsMap.put(error, true);
        errors = true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerWarning(ErrorType error)
    {
        warningsMap.put(error, true);
        warnings = true;
    }

    private class ASTBuilder
        extends ANTLRv4ParserBaseVisitor<GrammarNode>
    {

        public ASTBuilder()
        {
            isNioGram = mode == Mode.NioGram;
        }

        @Override
        public GrammarNode visitGrammarSpec(GrammarSpecContext ctx)
        {
            stack.push(grammar);
            visitChildren(ctx);
            stack.pop();

            if (debugMe) {
                printDebugInfo(grammar);
            }
            return grammar;
        }

        @Override
        public GrammarNode visitParserRuleSpec(ParserRuleSpecContext ctx)
        {
            String ruleName = ctx.RULE_REF().getText();
            if (!visitedRules.contains(ruleName)) {
                visitedRules.add(ruleName);
                NonterminalRule rule = nameToNonterminalRule.get(ruleName);
                rule.setSourceContext(ctx);
                stack.push(rule);
                visitChildren(ctx);
                stack.pop();
                boolean gotEmpty = false;
                for (Alternative alternative : rule.getAlternatives()) {
                    if (alternative.getTerms().isEmpty()) {
                        if (gotEmpty) {
                            int line = ((ParserRuleContext) alternative
                                .getSourceContext()).start.getLine();
                            int position = ((ParserRuleContext) alternative
                                .getSourceContext()).start
                                    .getCharPositionInLine();
                            String message = "duplicate empty alternative";
                            notifyErrorListeners(
                                ErrorDispatcher.ErrorType.SyntaxErrors, line,
                                position, message);
                        }
                        else {
                            gotEmpty = true;
                        }
                    }
                }
            }
            else {
            }
            return null;
        }

        @Override
        public GrammarNode visitLexerRuleSpec(LexerRuleSpecContext ctx)
        {
            String ruleName = ctx.TOKEN_REF().getText();
            if (!visitedRules.contains(ruleName)
                    && !duplicateTerminalNames.contains(ruleName)) {
                visitedRules.add(ruleName);
                TerminalRule rule = nameToTerminalRule.get(ruleName);
                rule.setSourceContext(ctx);
                stack.push(rule);
                visitChildren(ctx);
                stack.pop();
            }
            else {
                visitChildren(ctx);
            }
            return null;
        }

        @Override
        public GrammarNode visitLabeledAlt(ANTLRv4Parser.LabeledAltContext ctx)
        {
            Alternative alternatiive =
                (Alternative) visitAlternative(ctx.alternative());
            alternatiive.setSourceContext(ctx);
            return null;
        }

        @Override
        public GrammarNode visitAlternative(AlternativeContext ctx)
        {
            Alternative alternative = new Alternative(ALTERNATIVE);
            alternative.setSymbolicName(ALTERNATIVE_NAME);
            alternative.setDisplayName(ALTERNATIVE_NAME);
            alternative.setSourceContext(ctx);
            if (stack.peek() instanceof NonterminalRule) {
                ((NonterminalRule) stack.peek()).getAlternatives()
                    .add(alternative);
            }
            else if (stack.peek() instanceof Block) {
                ((Block) stack.peek()).getAlternatives()
                    .add(alternative);
            }
            else {
                throw new IllegalStateException("internal error - unexpected class "
                        + stack.peek().getClass().getName());
            }
            stack.push(alternative);
            visitChildren(ctx);
            stack.pop();
            return alternative;
        }

        @Override
        public GrammarNode visitElement(ElementContext ctx)
        {
            AtomContext atomCtx = null;
            BlockContext blockCtx = null;
            EbnfSuffixContext ebnfSuffix = null;
            ParserRuleContext termCtx = null;
            Term term = null;
            boolean optional = false;
            boolean repeatable = false;
            boolean greedy = true;
            int lazyLine = 0;
            int lazyPosition = 0;

            if (ctx.labeledElement() != null) {
                atomCtx = ctx.labeledElement().atom();
                blockCtx = ctx.labeledElement().block();
                ebnfSuffix = ctx.ebnfSuffix();
            }
            else if (ctx.atom() != null) {
                atomCtx = ctx.atom();
                ebnfSuffix = ctx.ebnfSuffix();
            }
            else if (ctx.ebnf() != null) {
                blockCtx = ctx.ebnf().block();
                ebnfSuffix = null;
                if (ctx.ebnf().blockSuffix() != null) {
                    ebnfSuffix = ctx.ebnf().blockSuffix().ebnfSuffix();
                }
            }

            if (ebnfSuffix != null) {
                lazyLine = ebnfSuffix.start.getLine();
                lazyPosition = ebnfSuffix.start.getCharPositionInLine();
                if (ebnfSuffix.STAR() != null) {
                    optional = true;
                    repeatable = true;
                    greedy = ebnfSuffix.QUESTION().size() == 0;
                }
                else if (ebnfSuffix.PLUS() != null) {
                    repeatable = true;
                    greedy = ebnfSuffix.QUESTION().size() == 0;
                }
                else if (ebnfSuffix.QUESTION() != null) {
                    optional = true;
                    greedy = ebnfSuffix.QUESTION().size() == 1;
                }
                else {
                    throw new IllegalStateException("internal error");
                }
            }

            if (blockCtx != null) {
                termCtx = blockCtx;
                Block block =
                    new Block(nextType(), optional, repeatable, greedy);
                term = block;
                term.setSymbolicName(BLOCK_NAME);
                term.setDisplayName(BLOCK_NAME);
                if (optional) {
                    Alternative empty = new Alternative(ALTERNATIVE);
                    empty.setSymbolicName(ALTERNATIVE_NAME);
                    empty.setDisplayName(ALTERNATIVE_NAME);
                    block.getAlternatives().add(empty);
                }
            }
            else if (atomCtx != null) {
                termCtx = atomCtx;
                if (atomCtx.terminal() != null) {
                    if (atomCtx.terminal().TOKEN_REF() != null) {
                        String terminalName =
                            atomCtx.terminal().TOKEN_REF().getText();
                        if ("EOF".equals(terminalName)) {
                            terminalName = BuiltInTypes.EOF_NAME;
                        }
                        TerminalRule terminalRule =
                            nameToTerminalRule.get(terminalName);
                        term = new Terminal(terminalRule);
                    }
                    else if (atomCtx.terminal().STRING_LITERAL() != null) {
                        String literal =
                            atomCtx.terminal().STRING_LITERAL().getText();
                        String terminalName =
                            literalToTerminal.getSecond(literal);
                        TerminalRule terminalRule =
                            nameToTerminalRule.get(terminalName);
                        term = new Terminal(terminalRule);
                        term.setDisplayName(literal);
                    }
                    else {
                        throw new IllegalStateException("internal error");
                    }

                }
                else if (atomCtx.ruleref() != null) {
                    String nonterminalName =
                        atomCtx.ruleref().RULE_REF().getText();
                    NonterminalRule nonterminalRule =
                        nameToNonterminalRule.get(nonterminalName);
                    term = new Nonterminal(nonterminalRule);
                }
                else if (atomCtx.notSet() != null) {
                    String terminalName =
                        BuiltInTypes.NOT_NAME;
                    TerminalRule terminalRule =
                        nameToTerminalRule.get(terminalName);
                    term = new Terminal(terminalRule);

                    int line = ctx.start.getLine();
                    int position = ctx.start.getCharPositionInLine();
                    String message =
                        "not sets are not supported in NioGram";
                    if (isNioGram) {
                        notifyErrorListeners(ErrorType.NotSets, line, position,
                            message);
                    }
                    else {
                        warnErrorListeners(ErrorType.NotSets, line, position,
                            message);
                    }
                }
                else if (atomCtx.DOT() != null) {
                    String terminalName =
                        BuiltInTypes.DOT_NAME;
                    TerminalRule terminalRule =
                        nameToTerminalRule.get(terminalName);
                    term = new Terminal(terminalRule);

                    int line = ctx.start.getLine();
                    int position = ctx.start.getCharPositionInLine();
                    String message =
                        "dot expressions in parser rules are not supported in NioGram";
                    if (isNioGram) {
                        notifyErrorListeners(ErrorType.DotExpressions, line,
                            position, message);
                    }
                    else {
                        warnErrorListeners(ErrorType.DotExpressions, line,
                            position, message);
                    }
                }
                else {
                    throw new IllegalStateException("internal error");
                }
            }
            if (term != null) {
                term.setSourceContext(ctx);
                if ((optional || repeatable) && blockCtx == null) {
                    int blockType = nextType();
                    Block block =
                        new Block(blockType, optional, repeatable, greedy);
                    if (optional) {
                        Alternative empty = new Alternative(ALTERNATIVE);
                        empty.setSymbolicName(ALTERNATIVE_NAME);
                        empty.setDisplayName(ALTERNATIVE_NAME);
                        block.getAlternatives().add(empty);
                    }
                    block.setSymbolicName(BLOCK_NAME);
                    block.setDisplayName(BLOCK_NAME);
                    Alternative alternative = new Alternative(ALTERNATIVE);
                    alternative.setSymbolicName(ALTERNATIVE_NAME);
                    alternative.setDisplayName(ALTERNATIVE_NAME);
                    block.getAlternatives().add(alternative);
                    block.setSourceContext(ctx);
                    alternative
                        .setSourceContext(termCtx);
                    alternative.getTerms().add(term);
                    term = block;
                }
                if (!greedy) {
                    String message =
                        "lazy occurrence indicators are not supported in NioGram";
                    if (isNioGram) {
                        notifyErrorListeners(ErrorType.LazyEBNF, lazyLine,
                            lazyPosition, message);
                    }
                    else {
                        warnErrorListeners(ErrorType.LazyEBNF, lazyLine,
                            lazyLine, message);
                    }
                }
                ((Alternative) stack.peek()).getTerms().add(term);
                if (term instanceof Block) {
                    stack.push(term);
                    visitChildren(ctx);
                    stack.pop();
                    boolean gotEmpty = false;
                    for (Alternative alternative : ((Block) term)
                        .getAlternatives()) {
                        if (alternative.getTerms().isEmpty()) {
                            if (gotEmpty) {
                                int line = ((ParserRuleContext) alternative
                                    .getSourceContext()).start.getLine();
                                int position = ((ParserRuleContext) alternative
                                    .getSourceContext()).start
                                        .getCharPositionInLine();
                                String message = "duplicate empty alternative";
                                notifyErrorListeners(
                                    ErrorDispatcher.ErrorType.SyntaxErrors,
                                    line,
                                    position, message);
                            }
                            else {
                                gotEmpty = true;
                            }
                        }
                    }
                }
                else {
                    visitChildren(ctx);
                }
            }
            return null;
        }
    }

    private class ParentSetter
        extends GrammarVisitor
    {
        @Override
        public void visitGrammar(Grammar grammar)
        {
            super.visitGrammar(grammar);
            for (NonterminalRule rule : grammar.getNonterminalRules()) {
                rule.setParent(grammar);
            }
            for (TerminalRule rule : grammar.getTerminalRules()) {
                rule.setParent(grammar);
            }
        }

        @Override
        public void visitNonterminalRule(NonterminalRule rule)
        {
            super.visitNonterminalRule(rule);
            for (Alternative alternative : rule.getAlternatives()) {
                alternative.setParent(rule);
            }
        }

        @Override
        public void visitBlock(Block block)
        {
            super.visitBlock(block);
            for (Alternative alternative : block.getAlternatives()) {
                alternative.setParent(block);
            }
        }

        @Override
        public void visitAlternative(Alternative alternative)
        {
            super.visitAlternative(alternative);
            for (Term term : alternative.getTerms()) {
                term.setParent(alternative);
            }
        }
    }

    private void printDebugInfo(Grammar grammar)
    {
        System.out
            .println("Grammar Name           : " + getGrammarName());
        System.out
            .println("Grammar Type           : " + getGrammarType());
        System.out
            .println("Options                : " + getOptions());
        System.out.println(
            "Delegate  Grammars     : " + getDelegateGrammars());
        System.out.println(
            "Imported  Terminals    : "
                    + getImportedTerminalNames());
        System.out.println(
            "Declared  Terminals    : "
                    + getDeclaredTerminalNames());
        System.out.println(
            "Defined   Terminals    : " + getDefinedTerminalNames());
        System.out.println(
            "Known     Terminals    : " + getKnownTerminalNames());
        System.out.println(
            "Unknown   Terminals    : " + getUnknownTerminalNames());
        System.out.println(
            "Used      Terminals    : " + getUsedTerminalNames());
        System.out.println(
            "Duplicate Terminals    : " + getDuplicateTerminalNames());
        System.out.println(
            "Known     Nonterminals : "
                    + getKnownNonterminalNames());
        System.out.println(
            "Unknown   Nonterminals : "
                    + getUnknownNonterminalNames());
        System.out.println(
            "Used      Nonterminals : "
                    + getUsedNonterminalNames());
        System.out.println(
            "Duplicate Nonterminals : "
                    + getDuplicateNonterminalNames());
        System.out.println(
            "Offending Literals     : "
                    + getUsedStringLiterals());
        System.out
            .println(
                "Literal   Terminals    : "
                        + getLiteralToTerminal());
        System.out
            .println(
                "Terminal   Rules    : "
                        + nameToTerminalRule.keySet());
        System.out
            .println(
                "Nonterminal Rules    : "
                        + nameToNonterminalRule.keySet());
    }
}
