// Generated from ANTLRv4Tokens.g4 by ANTLR 4.7.1
package net.ognyanov.niogram.parser.antlr4;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link ANTLRv4TokensParser}.
 */
interface ANTLRv4TokensListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link ANTLRv4TokensParser#tokenSpecs}.
	 * @param ctx the parse tree
	 */
	void enterTokenSpecs(ANTLRv4TokensParser.TokenSpecsContext ctx);
	/**
	 * Exit a parse tree produced by {@link ANTLRv4TokensParser#tokenSpecs}.
	 * @param ctx the parse tree
	 */
	void exitTokenSpecs(ANTLRv4TokensParser.TokenSpecsContext ctx);
	/**
	 * Enter a parse tree produced by {@link ANTLRv4TokensParser#line}.
	 * @param ctx the parse tree
	 */
	void enterLine(ANTLRv4TokensParser.LineContext ctx);
	/**
	 * Exit a parse tree produced by {@link ANTLRv4TokensParser#line}.
	 * @param ctx the parse tree
	 */
	void exitLine(ANTLRv4TokensParser.LineContext ctx);
	/**
	 * Enter a parse tree produced by {@link ANTLRv4TokensParser#name}.
	 * @param ctx the parse tree
	 */
	void enterName(ANTLRv4TokensParser.NameContext ctx);
	/**
	 * Exit a parse tree produced by {@link ANTLRv4TokensParser#name}.
	 * @param ctx the parse tree
	 */
	void exitName(ANTLRv4TokensParser.NameContext ctx);
}