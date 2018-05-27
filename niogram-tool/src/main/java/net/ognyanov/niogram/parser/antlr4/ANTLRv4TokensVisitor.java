// Generated from ANTLRv4Tokens.g4 by ANTLR 4.7.1
package net.ognyanov.niogram.parser.antlr4;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link ANTLRv4TokensParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
interface ANTLRv4TokensVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link ANTLRv4TokensParser#tokenSpecs}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTokenSpecs(ANTLRv4TokensParser.TokenSpecsContext ctx);
	/**
	 * Visit a parse tree produced by {@link ANTLRv4TokensParser#line}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLine(ANTLRv4TokensParser.LineContext ctx);
	/**
	 * Visit a parse tree produced by {@link ANTLRv4TokensParser#name}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitName(ANTLRv4TokensParser.NameContext ctx);
}