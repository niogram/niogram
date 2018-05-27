// Generated from ANTLRv4Tokens.g4 by ANTLR 4.7.1
package net.ognyanov.niogram.parser.antlr4;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
class ANTLRv4TokensParser extends org.antlr.v4.runtime.Parser {
	static { RuntimeMetaData.checkVersion("4.7.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		STRING_LITERAL=1, IDENTIFIER=2, INTEGER=3, EQ=4, EOL=5, UPPERCASE=6;
	public static final int
		RULE_tokenSpecs = 0, RULE_line = 1, RULE_name = 2;
	public static final String[] ruleNames = {
		"tokenSpecs", "line", "name"
	};

	private static final String[] _LITERAL_NAMES = {
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, "STRING_LITERAL", "IDENTIFIER", "INTEGER", "EQ", "EOL", "UPPERCASE"
	};
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "ANTLRv4Tokens.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public ANTLRv4TokensParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class TokenSpecsContext extends ParserRuleContext {
		public List<LineContext> line() {
			return getRuleContexts(LineContext.class);
		}
		public LineContext line(int i) {
			return getRuleContext(LineContext.class,i);
		}
		public TokenSpecsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tokenSpecs; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4TokensListener ) ((ANTLRv4TokensListener)listener).enterTokenSpecs(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4TokensListener ) ((ANTLRv4TokensListener)listener).exitTokenSpecs(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ANTLRv4TokensVisitor ) return ((ANTLRv4TokensVisitor<? extends T>)visitor).visitTokenSpecs(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TokenSpecsContext tokenSpecs() throws RecognitionException {
		TokenSpecsContext _localctx = new TokenSpecsContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_tokenSpecs);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(7); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(6);
				line();
				}
				}
				setState(9); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==STRING_LITERAL || _la==IDENTIFIER );
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class LineContext extends ParserRuleContext {
		public NameContext name() {
			return getRuleContext(NameContext.class,0);
		}
		public TerminalNode EQ() { return getToken(ANTLRv4TokensParser.EQ, 0); }
		public TerminalNode INTEGER() { return getToken(ANTLRv4TokensParser.INTEGER, 0); }
		public TerminalNode EOL() { return getToken(ANTLRv4TokensParser.EOL, 0); }
		public LineContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_line; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4TokensListener ) ((ANTLRv4TokensListener)listener).enterLine(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4TokensListener ) ((ANTLRv4TokensListener)listener).exitLine(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ANTLRv4TokensVisitor ) return ((ANTLRv4TokensVisitor<? extends T>)visitor).visitLine(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LineContext line() throws RecognitionException {
		LineContext _localctx = new LineContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_line);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(11);
			name();
			setState(12);
			match(EQ);
			setState(13);
			match(INTEGER);
			setState(14);
			match(EOL);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class NameContext extends ParserRuleContext {
		public TerminalNode IDENTIFIER() { return getToken(ANTLRv4TokensParser.IDENTIFIER, 0); }
		public TerminalNode STRING_LITERAL() { return getToken(ANTLRv4TokensParser.STRING_LITERAL, 0); }
		public NameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4TokensListener ) ((ANTLRv4TokensListener)listener).enterName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ANTLRv4TokensListener ) ((ANTLRv4TokensListener)listener).exitName(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof ANTLRv4TokensVisitor ) return ((ANTLRv4TokensVisitor<? extends T>)visitor).visitName(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NameContext name() throws RecognitionException {
		NameContext _localctx = new NameContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_name);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(16);
			_la = _input.LA(1);
			if ( !(_la==STRING_LITERAL || _la==IDENTIFIER) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\b\25\4\2\t\2\4\3"+
		"\t\3\4\4\t\4\3\2\6\2\n\n\2\r\2\16\2\13\3\3\3\3\3\3\3\3\3\3\3\4\3\4\3\4"+
		"\2\2\5\2\4\6\2\3\3\2\3\4\2\22\2\t\3\2\2\2\4\r\3\2\2\2\6\22\3\2\2\2\b\n"+
		"\5\4\3\2\t\b\3\2\2\2\n\13\3\2\2\2\13\t\3\2\2\2\13\f\3\2\2\2\f\3\3\2\2"+
		"\2\r\16\5\6\4\2\16\17\7\6\2\2\17\20\7\5\2\2\20\21\7\7\2\2\21\5\3\2\2\2"+
		"\22\23\t\2\2\2\23\7\3\2\2\2\3\13";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}