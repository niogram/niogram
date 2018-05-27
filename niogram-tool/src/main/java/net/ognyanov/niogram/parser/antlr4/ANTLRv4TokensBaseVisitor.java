// Generated from ANTLRv4Tokens.g4 by ANTLR 4.7.1
package net.ognyanov.niogram.parser.antlr4;
import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;

/**
 * This class provides an empty implementation of {@link ANTLRv4TokensVisitor},
 * which can be extended to create a visitor which only needs to handle a subset
 * of the available methods.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
class ANTLRv4TokensBaseVisitor<T> extends AbstractParseTreeVisitor<T> implements ANTLRv4TokensVisitor<T> {
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public T visitTokenSpecs(ANTLRv4TokensParser.TokenSpecsContext ctx) { return visitChildren(ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public T visitLine(ANTLRv4TokensParser.LineContext ctx) { return visitChildren(ctx); }
	/**
	 * {@inheritDoc}
	 *
	 * <p>The default implementation returns the result of calling
	 * {@link #visitChildren} on {@code ctx}.</p>
	 */
	@Override public T visitName(ANTLRv4TokensParser.NameContext ctx) { return visitChildren(ctx); }
}