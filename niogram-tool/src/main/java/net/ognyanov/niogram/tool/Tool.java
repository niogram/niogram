/*
 * Copyright (c) 2018 by Nikolay Ognyanov. All rights reserved.
 * 
 * Use of this file is governed by the licensing conditions   
 * in the LICENSE.txt file in the root folder of the project.
 */
package net.ognyanov.niogram.tool;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import net.ognyanov.niogram.analysis.FirstFollowCalculator;
import net.ognyanov.niogram.analysis.FirstKFollowKCalculator;
import net.ognyanov.niogram.analysis.FirstKLFollowKLCalculator;
import net.ognyanov.niogram.analysis.FirstKLTrace;
import net.ognyanov.niogram.analysis.FirstKTrace;
import net.ognyanov.niogram.analysis.FirstTrace;
import net.ognyanov.niogram.analysis.FlagsCalculator;
import net.ognyanov.niogram.analysis.GraphAnalysis;
import net.ognyanov.niogram.ast.Block;
import net.ognyanov.niogram.ast.BuiltInTypes;
import net.ognyanov.niogram.ast.Grammar;
import net.ognyanov.niogram.ast.GrammarNode;
import net.ognyanov.niogram.ast.Multiplex;
import net.ognyanov.niogram.ast.Multiplex.Conflict;
import net.ognyanov.niogram.ast.Multiplex.ConflictK;
import net.ognyanov.niogram.ast.Multiplex.ConflictKL;
import net.ognyanov.niogram.ast.NonterminalRule;
import net.ognyanov.niogram.parser.ErrorDispatcher.ErrorType;
import net.ognyanov.niogram.parser.antlr4.Antlr4ToAstParser;
import net.ognyanov.niogram.util.BiasedBitSet;
import net.ognyanov.niogram.util.BitSetLLString;
import net.ognyanov.niogram.util.IntLLString;
import net.ognyanov.niogram.util.IntLLStringSet;

/**
 * The main class of the NioGram command line tool.
 *
 * @author Nikolay Ognyanov
 */
public class Tool
{
    private static final String USAGE             =
        "Usage : niogram [options] <grammar-file>\n"
                + "        -q     quiet mode - do not print error messages\n"
                + "        -nm    parse the grammar in NioGram mode\n"
                + "        -sg    store the grammar serialized object\n"
                + "        -pd    print the parsing diagnostic information\n"
                + "        -pb    print the grammar basic information\n"
                + "        -psx   print the grammar AST in XML\n"
                + "        -psd   print the grammar AST in DOT\n"
                + "        -psr   print the grammar railroad diagrams in DOT\n"
                + "        -pfd   print the grammar full    dependency graph in DOT\n"
                + "        -prd   print the grammar reduced dependency graph in DOT\n"
                + "        -ppx   print the grammar parse tree in XML\n"
                + "        -ppd   print the grammar parse tree in DOT\n"
                + "        -pff   print the firstX/followX sets\n"
                + "        -pffc  print the LL(k) conflict information\n"
                + "        -pct   print the conflict traces in DOT\n"
                + "        -ff    calculate the first   / follow   sets\n"
                + "        -ffk   calculate the firstK  / followK  sets\n"
                + "        -ffkl  calculate the firstKL / followKL sets\n"
                + "        -ffall calculate all firstX  / followX  sets\n"
                //+ "      -ffc   compare the calculated first/follow sets\n"
                + "        -k=n   set the k parameter for the LL(k) analysis";
    private static boolean      doNioGram         = false;
    private static boolean      doQuiet           = false;
    private static boolean      printBasic        = false;
    private static boolean      storeGrammar      = false;
    private static boolean      printDiagnostic   = false;
    private static boolean      printASTXML       = false;
    private static boolean      printASTDOT       = false;
    private static boolean      printParseTreeXML = false;
    private static boolean      printParseTreeDOT = false;
    private static boolean      printDgDOT        = false;
    private static boolean      printLrDgDOT      = false;
    private static boolean      printSets         = false;
    private static boolean      printConflicts    = false;
    private static boolean      printTraces;
    private static boolean      doFF              = false;
    private static boolean      doFFKL            = false;
    private static boolean      doFFK             = false;
    private static boolean      doFFALL           = false;
    private static boolean      doFFCMP           = false;
    private static int          llK               = -1;

    private static String       fileName          = null;
    private static boolean      argOK             = true;
    private static long         start             = 0;
    private static long         end               = 0;
    private static boolean      printRAIL;

    public static void main(String args[])
    {
        processArgs(args);
        if (!argOK || fileName == null) {
            System.err.println(USAGE);
            System.exit(255);
        }

        Antlr4ToAstParser grammarParser = null;
        try {
            grammarParser = new Antlr4ToAstParser(fileName);
        }
        catch (IOException e) {
            System.out.println("Error: failed to open file " + fileName);
            System.exit(255);
        }
        if (doNioGram) {
            grammarParser.setMode(Antlr4ToAstParser.Mode.NioGram);
        }
        if (doQuiet) {
            grammarParser.removeErrorListeners();
        }

        start = System.currentTimeMillis();
        Grammar grammar = grammarParser.grammar();
        end = System.currentTimeMillis();
        if (grammar == null) {
            System.out.println("Error: Parsing of the grammar failed.");
            System.exit(255);
        }
        if (llK > 0) {
            grammar.setK(llK);
            grammar.setKL(llK);
        }
        else {
            if (grammar.getK() <= 0) {
                grammar.setK(1);
                grammar.setKL(1);
            }
        }
        if (printDiagnostic) {
            System.out.println("Parsing duration                : "
                    + (end - start)
                    + "ms.");
        }

        start = System.currentTimeMillis();
        new FlagsCalculator().calculate(grammar);
        end = System.currentTimeMillis();
        if (printBasic) {
            System.out.println("Flags calculation duration      : "
                    + (end - start)
                    + "ms.");
        }

        if (printDiagnostic) {
            printDiagnostic(grammarParser);
        }
        if (printBasic) {
            printBasic(grammar);
        }
        if (printParseTreeXML) {
            System.out.println(grammarParser.toXmlString());
        }
        if (printParseTreeDOT) {
            System.out.println(grammarParser.toDotString());
        }
        if (printASTXML) {
            System.out.println(grammar.toXmlString());
        }
        if (printASTDOT) {
            System.out.println(grammar.toDotString());
        }
        if (printRAIL) {
            System.out.println(grammar.toRailRoadDot());
        }
        if (printDgDOT) {
            System.out.println(GraphAnalysis
                .toDotString(GraphAnalysis.toGraph(grammar)));
        }
        if (printLrDgDOT) {
            System.out.println(GraphAnalysis
                .toDotString(GraphAnalysis.toLRGraph(grammar)));
        }
        if (doFF) {
            start = System.currentTimeMillis();
            new FirstFollowCalculator().calculate(grammar);
            end = System.currentTimeMillis();
            if (printBasic) {
                System.out.println("First/Follow duration           : "
                        + (end - start)
                        + "ms.");
            }
        }
        if (doFFK) {
            start = System.currentTimeMillis();
            new FirstKFollowKCalculator().calculate(grammar);
            end = System.currentTimeMillis();
            if (printBasic) {
                System.out.println("FirstK/FollowK duration         : "
                        + (end - start)
                        + "ms.");
            }
        }
        if (doFFKL) {
            start = System.currentTimeMillis();
            new FirstKLFollowKLCalculator().calculate(grammar);
            end = System.currentTimeMillis();
            if (printBasic) {
                System.out.println("FirstKL/FollowKL duration       : "
                        + (end - start)
                        + "ms.");
            }
        }
        if (doFFALL) {
            start = System.currentTimeMillis();
            new FirstFollowCalculator().calculate(grammar);
            new FirstKFollowKCalculator().calculate(grammar);
            new FirstKLFollowKLCalculator().calculate(grammar);
            end = System.currentTimeMillis();
            if (printBasic) {
                System.out.println("FirstX/FollowX duration         : "
                        + (end - start)
                        + "ms.");
            }
        }
        if (printSets) {
            printSets(grammar);
        }
        if (printConflicts) {
            printConflicts(grammar);
        }
        if (printTraces) {
            printTraces(grammar);
        }
        if (doFFCMP) {
            new FirstFollowComparator().compare(grammar);
        }
        if (storeGrammar) {
            // must be last, so that all
            // analysis data is stored
            storeGrammar(grammar, fileName);
        }
    }

    private static void processArgs(String[] args)
    {
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if ("-nm".equals(arg)) {
                doNioGram = true;
            }
            if ("-q".equals(arg)) {
                doQuiet = true;
            }
            if ("-psx".equals(arg)) {
                printASTXML = true;
            }
            if ("-psr".equals(arg)) {
                printRAIL = true;
            }
            else if ("-psd".equals(arg)) {
                printASTDOT = true;
            }
            else if ("-ppx".equals(arg)) {
                printParseTreeXML = true;
            }
            else if ("-ppd".equals(arg)) {
                printParseTreeDOT = true;
            }
            else if ("-pfd".equals(arg)) {
                printDgDOT = true;
            }
            else if ("-prd".equals(arg)) {
                printLrDgDOT = true;
            }
            else if ("-pd".equals(arg)) {
                printDiagnostic = true;
            }
            else if ("-pb".equals(arg)) {
                printBasic = true;
            }
            else if ("-sg".equals(arg)) {
                storeGrammar = true;
            }
            else if ("-pff".equals(arg)) {
                printSets = true;
            }
            else if ("-pffc".equals(arg)) {
                printConflicts = true;
            }
            else if ("-pct".equals(arg)) {
                printTraces = true;
            }
            else if ("-ff".equals(arg)) {
                doFF = true;
            }
            else if ("-ffkl".equals(arg)) {
                doFFKL = true;
            }
            else if ("-ffk".equals(arg)) {
                doFFK = true;
            }
            else if ("-ffall".equals(arg)) {
                doFFALL = true;
            }
            else if ("-ffc".equals(arg)) {
                doFFCMP = true;
            }
            else if (arg.startsWith("-k=")) {
                if (arg.length() > 3) {
                    String ks = arg.substring(3);
                    int k = 0;
                    try {
                        k = Integer.parseInt(ks);
                        llK = k;
                    }
                    catch (NumberFormatException e) {
                        argOK = false;
                    }
                }
            }
            else {
                fileName = arg;
            }
        }

    }

    private static void storeGrammar(Grammar grammar, String fileName)
    {
        String serFileName = null;
        int extIdx = fileName.lastIndexOf('.');
        if (extIdx > 0) {
            serFileName = fileName.substring(0, extIdx + 1) + "ser";
        }
        else {
            serFileName = fileName + ".ser";
        }
        try {
            FileOutputStream fileOut = new FileOutputStream(serFileName);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(grammar);
            out.close();
            fileOut.close();
        }
        catch (IOException e) {
            System.out.println("Error: failed to serialize the grammar object");
            System.out.println(e);
        }
        /* Test */
        /*
        try {
            FileInputStream fileIn = new FileInputStream(new File(serFileName));
            ObjectInputStream in = new ObjectInputStream(fileIn);
            Grammar g = (Grammar) in.readObject();
            printSets(g);
        }
        catch (IOException e) {
            System.out.println("Failed to read back the grammar.");
            System.out.println(e);
        }
        catch (ClassNotFoundException e) {
            System.out.println("Failed to read back the grammar.");
            System.out.println(e);
        }
        */
    }

    private static void printDiagnostic(Antlr4ToAstParser astParser)
    {
        int width = 22;

        System.out.println();
        printString("Grammar name", astParser.getGrammarName(), width);
        printString("Grammar type", astParser.getGrammarType().toString(),
            width);
        printString("Parsing mode", astParser.getMode().toString(), width);
        System.out.println();
        printFlag(astParser.hasWarnings(), "Warnings", width);
        printFlag(astParser.hasErrors(), "Errors", width);
        for (ErrorType errorType : ErrorType.values()) {
            printFlag(errorType, width, astParser);
        }
        if (astParser.getOptions().size() > 0) {
            System.out.println();
            System.out.println("Grammar options : ");
            System.out.println(astParser.getOptions());
        }
        if (astParser.getDelegateGrammars().size() > 0) {
            System.out.println();
            System.out.println("Grammar imports : ");
            System.out.println(astParser.getDelegateGrammars());
        }
        if (astParser.getUnknownTerminalNames().size() > 0) {
            System.out.println();
            System.out.println("Unknown termilas :");
            System.out.println(astParser.getUnknownTerminalNames());
        }
        if (astParser.getDuplicateTerminalNames().size() > 0) {
            System.out.println();
            System.out.println("Duplicate termilas :");
            System.out.println(astParser.getDuplicateTerminalNames());
        }
        if (astParser.getUnknownNonterminalNames().size() > 0) {
            System.out.println();
            System.out.println("Unknown montermilas :");
            System.out.println(astParser.getUnknownNonterminalNames());
        }
        if (astParser.getDuplicateNonterminalNames().size() > 0) {
            System.out.println();
            System.out.println("Duplicate nontermilas");
            System.out.println();
        }
        System.out.println();
    }

    private static void printBasic(Grammar grammar)
    {
        start = System.currentTimeMillis();
        Graph<NonterminalRule, DefaultEdge> lrDependencyGraph =
            GraphAnalysis.toLRGraph(grammar);
        @SuppressWarnings("unused")
        List<List<NonterminalRule>> lrCycles =
            GraphAnalysis.findCycles(lrDependencyGraph);
        end = System.currentTimeMillis();
        if (printBasic) {
            System.out.println("LR cycles duration              : "
                    + (end - start)
                    + "ms.");
        }
        System.out.println(
            "Number of Nonterminal   Rules   : "
                    + grammar.getNonterminalRules().size());

        System.out.println(
            "Number of Terminal      Rules   : "
                    + (grammar.getTerminalRules().size()
                            + BuiltInTypes.MIN_TYPE));

        System.out.println(
            "Number of Nonproductive Rules   : "
                    + grammar.getNonProductive().size());
        System.out.println(
            "Number of Unreachable   Rules   : "
                    + grammar.getUnreachable().size());
        System.out.println(
            "Number of Unused        Rules   : "
                    + grammar.getUnused().size());
        System.out.println(
            "Number of Left Recursive Cycles : "
                    + lrCycles.size());
        if (!grammar.getNonProductive().isEmpty()) {
            System.out.println("Nonproductive Rules             : ");
            printList(grammar.getNonProductive());
        }
        if (!grammar.getUnreachable().isEmpty()) {
            System.out.println("Unreachable Rules               : ");
            printList(grammar.getUnreachable());
        }
        if (!grammar.getUnused().isEmpty()) {
            System.out.println("Unused Rules                    : ");
            printList(grammar.getUnused());
        }
        if (!lrCycles.isEmpty()) {
            System.out.println("Left Rcursive Cycles            : ");
            printLRCycles(lrCycles);
        }

    }

    private static void printList(List<NonterminalRule> list)
    {
        System.out.print("   ");
        System.out.println(list);
    }

    private static void printLRCycles(List<List<NonterminalRule>> lrCycles)
    {
        for (List<NonterminalRule> cycle : lrCycles) {
            System.out.print("   ");
            System.out.println(cycle);
        }
    }

    private static void printSets(Grammar grammar)
    {
        if (grammar.hasFF()) {
            System.out.println("================");
            System.out.println("  First  Sets:  ");
            System.out.println("================");
            for (NonterminalRule rule : grammar.getNonterminalRules()) {
                printMultiplexFirst(grammar, rule);
            }
            for (Block block : grammar.getBlocks()) {
                printMultiplexFirst(grammar, block);
            }
            System.out.println("================");
            System.out.println("  Follow  Sets: ");
            System.out.println("================");
            for (NonterminalRule rule : grammar.getNonterminalRules()) {
                printMultiplexFollow(grammar, rule);
            }
            for (Block block : grammar.getBlocks()) {
                printMultiplexFollow(grammar, block);
            }
        }
        if (grammar.hasFFK()) {
            System.out.println("================");
            System.out.println("  FirstK Sets: ");
            System.out.println("================");
            for (NonterminalRule rule : grammar.getNonterminalRules()) {
                printMultiplexFirstK(grammar, rule);
            }
            for (Block block : grammar.getBlocks()) {
                printMultiplexFirstK(grammar, block);
            }
            System.out.println("================");
            System.out.println("  FollowK Sets: ");
            System.out.println("================");
            for (NonterminalRule rule : grammar.getNonterminalRules()) {
                printMultiplexFollowK(grammar, rule);
            }
            for (Block block : grammar.getBlocks()) {
                printMultiplexFollowK(grammar, block);
            }
        }
        if (grammar.hasFFKL()) {
            System.out.println("================");
            System.out.println("  FirstKL Sets: ");
            System.out.println("================");
            for (NonterminalRule rule : grammar.getNonterminalRules()) {
                printMultiplexFirstKL(grammar, rule);
            }
            for (Block block : grammar.getBlocks()) {
                printMultiplexFirstKL(grammar, block);
            }
            System.out.println("================");
            System.out.println(" FollowKL Sets: ");
            System.out.println("================");
            for (NonterminalRule rule : grammar.getNonterminalRules()) {
                printMultiplexFollowKL(grammar, rule);
            }
            for (Block block : grammar.getBlocks()) {
                printMultiplexFollowKL(grammar, block);
            }
        }

    }

    private static void printMultiplexFirst(Grammar grammar, Multiplex mtx)
    {
        GrammarNode node = (GrammarNode) mtx;
        System.out.println(node.getDisplayName() + " :\n   "
                + node.getFirst());
    }

    private static void printMultiplexFollow(Grammar grammar, Multiplex mtx)
    {
        GrammarNode node = (GrammarNode) mtx;
        System.out.println(node.getDisplayName() + " :\n   "
                + node.getFollow());
    }

    private static void printMultiplexFirstK(Grammar grammar, Multiplex mtx)
    {
        GrammarNode node = (GrammarNode) mtx;
        System.out.println(node.getDisplayName() + " :\n   "
                + node.getFirstK());
    }

    private static void printMultiplexFollowK(Grammar grammar, Multiplex mtx)
    {
        GrammarNode node = (GrammarNode) mtx;
        System.out.println(node.getDisplayName() + " :\n   "
                + node.getFollowK());
    }

    private static void printMultiplexFirstKL(Grammar grammar, Multiplex mtx)
    {
        GrammarNode node = (GrammarNode) mtx;
        System.out.println(node.getDisplayName() + " :\n   "
                + node.getFirstKL());
    }

    private static void printMultiplexFollowKL(Grammar grammar, Multiplex mtx)
    {
        GrammarNode node = (GrammarNode) mtx;
        System.out.println(node.getDisplayName() + " :\n   "
                + node.getFollowKL());
    }

    private static void printConflicts(Grammar grammar)
    {
        System.out.println("=============");
        System.out.println("  Conflicts  ");
        System.out.println("=============");
        for (NonterminalRule rule : grammar.getNonterminalRules()) {
            printMultiplexConflicts(grammar, rule);
        }
        for (Block block : grammar.getBlocks()) {
            printMultiplexConflicts(grammar, block);
        }
    }

    private static void printMultiplexConflicts(Grammar grammar, Multiplex mux)
    {
        GrammarNode node = (GrammarNode) mux;
        boolean isNullable = node.isNullable();
        //boolean hasFF = grammar.hasFF();
        boolean hasFFK = grammar.hasFFK();
        boolean hasFFKL = grammar.hasFFKL();
        boolean hasConflicts = !mux.getConflicts().isEmpty();
        boolean hasConflictsK = !mux.getConflictsK().isEmpty();
        boolean hasConflictsKL = !mux.getConflictsKL().isEmpty();
        boolean hasFfConflict =
            !(mux.getFfConflict() == null) && !mux.getFfConflict().isEmpty();
        boolean hasFfConflictK =
            !(mux.getFfConflictK() == null) && !mux.getFfConflictK().isEmpty();
        boolean hasFfConflictKL =
            !(mux.getFfConflictKL() == null)
                    && !mux.getFfConflictKL().isEmpty();

        boolean hasAny = hasConflicts || hasConflictsK || hasConflictsKL ||
                hasFfConflict || hasFfConflictK || hasFfConflictKL;
        if (!hasAny) {
            return;
        }

        System.out.println(((GrammarNode) mux).getDisplayName() + " : ");
        if (hasFFK)
            System.out.println("   minK    = " + mux.getMinK());
        if (hasFFKL)
            System.out.println("   minKL   = " + mux.getMinKL());
        if (hasFFK && isNullable)
            System.out.println("   minFfK  = " + mux.getMinFfK());
        if (hasFFKL && isNullable)
            System.out.println("   minFfKL = " + mux.getMinFfKL());

        for (Multiplex.Conflict conflict : mux.getConflicts()) {
            System.out.print("   ");
            System.out.println(conflict);
        }
        for (Multiplex.ConflictK conflict : mux.getConflictsK()) {
            System.out.print("   ");
            System.out.println(conflict);
        }
        for (Multiplex.ConflictKL conflict : mux.getConflictsKL()) {
            System.out.print("   ");
            System.out.println(conflict);
        }
        if (hasFfConflict && isNullable) {
            System.out.print("   ");
            System.out
                .println("FfConflict   [conflict=" + mux.getFfConflict() + "]");
        }
        if (hasFfConflictK && isNullable) {
            System.out.print("   ");
            System.out.println(
                "FfConflictK  [conflict=" + mux.getFfConflictK() + "]");
        }
        if (hasFfConflictKL && isNullable) {
            System.out.print("   ");
            System.out.println(
                "FfConflictKL [conflict=" + mux.getFfConflictKL() + "]");
        }
    }

    private static void printTraces(Grammar grammar)
    {
        System.out.println("//=============");
        System.out.println("//   Traces    ");
        System.out.println("//=============");
        System.out.println();
        for (NonterminalRule rule : grammar.getNonterminalRules()) {
            printMultiplexTraces(grammar, rule);
        }
        for (Block block : grammar.getBlocks()) {
            printMultiplexTraces(grammar, block);
        }
    }

    private static void printMultiplexTraces(Grammar grammar,
                                             Multiplex multiplex)
    {
        BiasedBitSet conflict = null;
        IntLLStringSet conflictK = null;
        BitSetLLString conflictKL = null;
        BiasedBitSet conflictFF = null;
        IntLLStringSet conflictKFF = null;
        BitSetLLString conflictKLFF = null;
        if (grammar.hasFF()) {
            conflict = new BiasedBitSet(BuiltInTypes.MIN_TYPE, grammar);
            for (Conflict c : multiplex.getConflicts()) {
                conflict.or(c.getConflictSet());
            }
            conflictFF = multiplex.getFfConflict();
        }
        if (grammar.hasFFK()) {
            conflictK = new IntLLStringSet(grammar.getK(), grammar);
            for (ConflictK cK : multiplex.getConflictsK()) {
                conflictK.addAll(cK.getConflictSet());
            }
            conflictKFF = multiplex.getFfConflictK();
        }
        if (grammar.hasFFKL()) {
            conflictKL = new BitSetLLString(grammar.getKL(), grammar);
            for (ConflictKL cKL : multiplex.getConflictsKL()) {
                conflictKL.addAll(cKL.getConflictSet());
            }
            conflictKLFF = multiplex.getFfConflictKL();
        }
        if (conflict != null) {
            int current = conflict.getStart();
            while ((current = conflict.nextSetBit(current)) != conflict
                .getNone()) {
                FirstTrace trace = null;
                if (multiplex instanceof NonterminalRule) {
                    trace =
                        new FirstTrace((NonterminalRule) multiplex, current);
                }
                else if (multiplex instanceof Block) {
                    trace =
                        new FirstTrace((Block) multiplex, current);
                }
                System.out.println(
                    "// First/First on " + grammar.getTypeName(current));
                System.out.println(trace.toDotString());
            }
        }
        if (conflictFF != null && multiplex.isNullable()) {
            int current = conflictFF.getStart();
            while ((current = conflictFF.nextSetBit(current)) != conflictFF
                .getNone()) {
                FirstTrace trace = null;
                if (multiplex instanceof NonterminalRule) {
                    trace =
                        new FirstTrace((NonterminalRule) multiplex, current);
                }
                else if (multiplex instanceof Block) {
                    trace =
                        new FirstTrace((Block) multiplex, current);
                }
                System.out.println(
                    "// First/Follow on " + grammar.getTypeName(current));
                System.out.println(trace.toDotString());
            }
        }
        if (conflictK != null) {
            BiasedBitSet printed =
                new BiasedBitSet(BuiltInTypes.MIN_TYPE, grammar);
            for (IntLLString string : conflictK) {
                if (string.length() > 0) {
                    FirstKTrace trace = null;
                    int type = string.get(0);
                    if (multiplex instanceof NonterminalRule) {
                        trace = new FirstKTrace((NonterminalRule) multiplex,
                                                type, 0);
                    }
                    else if (multiplex instanceof Block) {
                        trace = new FirstKTrace((Block) multiplex,
                                                type, 0);
                    }
                    if (!printed.get(type)) {
                        System.out.println(
                            "// FirstK/FirstK on "
                                    + grammar.getTypeName(type));
                        System.out.println(trace.toDotString());
                        printed.set(type);
                    }
                }
            }
        }
        if (conflictKFF != null && multiplex.isNullable()) {
            BiasedBitSet printed =
                new BiasedBitSet(BuiltInTypes.MIN_TYPE, grammar);
            for (IntLLString string : conflictKFF) {
                if (string.length() > 0) {
                    FirstKTrace trace = null;
                    int type = string.get(0);
                    if (multiplex instanceof NonterminalRule) {
                        trace = new FirstKTrace((NonterminalRule) multiplex,
                                                type, 0);
                    }
                    else if (multiplex instanceof Block) {
                        trace = new FirstKTrace((Block) multiplex,
                                                type, 0);
                    }
                    if (!printed.get(type)) {
                        System.out.println(
                            "// FirstK/FollowK on "
                                    + grammar.getTypeName(type));
                        System.out.println(trace.toDotString());
                    }
                }
            }
        }
        if (conflictKL != null) {
            for (BiasedBitSet set : conflictKL) {
                BiasedBitSet printed =
                    new BiasedBitSet(BuiltInTypes.MIN_TYPE, grammar);
                int current = set.getStart();
                FirstKLTrace trace = null;
                while ((current = set.nextSetBit(current)) != set.getNone()) {
                    if (multiplex instanceof NonterminalRule) {
                        trace = new FirstKLTrace((NonterminalRule) multiplex,
                                                 current, 0);
                    }
                    else if (multiplex instanceof Block) {
                        trace = new FirstKLTrace((Block) multiplex,
                                                 current, 0);
                    }
                    if (!printed.get(current)) {
                        System.out.println(
                            "// FirstKL/FirstKL on "
                                    + grammar.getTypeName(current));
                        System.out.println(trace.toDotString());
                        printed.set(current);
                    }
                }
            }
        }
        if (conflictKLFF != null && multiplex.isNullable()) {
            for (BiasedBitSet set : conflictKLFF) {
                BiasedBitSet printed =
                    new BiasedBitSet(BuiltInTypes.MIN_TYPE, grammar);
                int current = set.getStart();
                FirstKLTrace trace = null;
                while ((current = set.nextSetBit(current)) != set.getNone()) {
                    if (multiplex instanceof NonterminalRule) {
                        trace = new FirstKLTrace((NonterminalRule) multiplex,
                                                 current, 0);
                    }
                    else if (multiplex instanceof Block) {
                        trace = new FirstKLTrace((Block) multiplex,
                                                 current, 0);
                    }
                    if (!printed.get(current)) {
                        System.out.println(
                            "// FirstKL/FollowKL on "
                                    + grammar.getTypeName(current));
                        System.out.println(trace.toDotString());
                        printed.set(current);
                    }
                }
            }
        }
    }

    private static void printFlag(ErrorType errorType, int width,
                                  Antlr4ToAstParser parser)
    {
        boolean value = parser.getErrors().get(errorType) ||
                parser.getWarnings().get(errorType);
        String name = errorType.toString();
        int len = width - name.length();
        System.out.print(name);
        for (int i = 0; i < len; i++) {
            System.out.print(" ");
        }
        System.out.println(":  " + value);
    }

    private static void printFlag(boolean flag, String name, int width)
    {
        int len = width - name.length();
        System.out.print(name);
        for (int i = 0; i < len; i++) {
            System.out.print(" ");
        }
        System.out.println(":  " + flag);
    }

    private static void printString(String name, String value, int width)
    {
        int len = width - name.length();
        System.out.print(name);
        for (int i = 0; i < len; i++) {
            System.out.print(" ");
        }
        System.out.println(":  " + value);
    }
}
