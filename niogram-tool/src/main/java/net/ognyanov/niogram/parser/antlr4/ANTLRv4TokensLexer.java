// Generated from ANTLRv4Tokens.g4 by ANTLR 4.7.1
package net.ognyanov.niogram.parser.antlr4;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
class ANTLRv4TokensLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.7.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		STRING_LITERAL=1, IDENTIFIER=2, INTEGER=3, EQ=4, EOL=5, UPPERCASE=6;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"STRING_LITERAL", "IDENTIFIER", "INTEGER", "EQ", "EOL", "UPPERCASE", "Ws", 
		"Hws", "Vws", "BlockComment", "DocComment", "LineComment", "EscSeq", "EscAny", 
		"UnicodeEsc", "DecimalNumeral", "HexDigit", "DecDigit", "BoolLiteral", 
		"CharLiteral", "SQuoteLiteral", "DQuoteLiteral", "USQuoteLiteral", "NameChar", 
		"NameStartChar", "Int", "Esc", "Colon", "DColon", "SQuote", "DQuote", 
		"LParen", "RParen", "LBrace", "RBrace", "LBrack", "RBrack", "RArrow", 
		"Lt", "Gt", "Equal", "Question", "Star", "Plus", "PlusAssign", "Underscore", 
		"Pipe", "Dollar", "Comma", "Semi", "Dot", "Range", "At", "Pound", "Tilde"
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


	public ANTLRv4TokensLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "ANTLRv4Tokens.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\b\u0150\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t"+
		"\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\3\2\3\2\3\3\3\3\7\3v\n\3\f\3\16"+
		"\3y\13\3\3\4\6\4|\n\4\r\4\16\4}\3\5\3\5\3\6\3\6\3\7\3\7\3\b\3\b\5\b\u0088"+
		"\n\b\3\t\3\t\3\n\3\n\3\13\3\13\3\13\3\13\7\13\u0092\n\13\f\13\16\13\u0095"+
		"\13\13\3\13\3\13\3\13\5\13\u009a\n\13\3\f\3\f\3\f\3\f\3\f\7\f\u00a1\n"+
		"\f\f\f\16\f\u00a4\13\f\3\f\3\f\3\f\5\f\u00a9\n\f\3\r\3\r\3\r\3\r\7\r\u00af"+
		"\n\r\f\r\16\r\u00b2\13\r\3\16\3\16\3\16\3\16\3\16\5\16\u00b9\n\16\3\17"+
		"\3\17\3\17\3\20\3\20\3\20\3\20\3\20\5\20\u00c3\n\20\5\20\u00c5\n\20\5"+
		"\20\u00c7\n\20\5\20\u00c9\n\20\3\21\3\21\3\21\7\21\u00ce\n\21\f\21\16"+
		"\21\u00d1\13\21\5\21\u00d3\n\21\3\22\3\22\3\23\3\23\3\24\3\24\3\24\3\24"+
		"\3\24\3\24\3\24\3\24\3\24\5\24\u00e2\n\24\3\25\3\25\3\25\5\25\u00e7\n"+
		"\25\3\25\3\25\3\26\3\26\3\26\7\26\u00ee\n\26\f\26\16\26\u00f1\13\26\3"+
		"\26\3\26\3\27\3\27\3\27\7\27\u00f8\n\27\f\27\16\27\u00fb\13\27\3\27\3"+
		"\27\3\30\3\30\3\30\7\30\u0102\n\30\f\30\16\30\u0105\13\30\3\31\3\31\3"+
		"\31\3\31\5\31\u010b\n\31\3\32\3\32\3\33\3\33\3\33\3\33\3\34\3\34\3\35"+
		"\3\35\3\36\3\36\3\36\3\37\3\37\3 \3 \3!\3!\3\"\3\"\3#\3#\3$\3$\3%\3%\3"+
		"&\3&\3\'\3\'\3\'\3(\3(\3)\3)\3*\3*\3+\3+\3,\3,\3-\3-\3.\3.\3.\3/\3/\3"+
		"\60\3\60\3\61\3\61\3\62\3\62\3\63\3\63\3\64\3\64\3\65\3\65\3\65\3\66\3"+
		"\66\3\67\3\67\38\38\4\u0093\u00a2\29\3\3\5\4\7\5\t\6\13\7\r\b\17\2\21"+
		"\2\23\2\25\2\27\2\31\2\33\2\35\2\37\2!\2#\2%\2\'\2)\2+\2-\2/\2\61\2\63"+
		"\2\65\2\67\29\2;\2=\2?\2A\2C\2E\2G\2I\2K\2M\2O\2Q\2S\2U\2W\2Y\2[\2]\2"+
		"_\2a\2c\2e\2g\2i\2k\2m\2o\2\3\2\r\4\2\13\13\"\"\4\2\f\f\16\17\4\2\f\f"+
		"\17\17\n\2$$))^^ddhhppttvv\3\2\63;\5\2\62;CHch\3\2\62;\6\2\f\f\17\17)"+
		")^^\6\2\f\f\17\17$$^^\5\2\u00b9\u00b9\u0302\u0371\u2041\u2042\17\2C\\"+
		"c|\u00c2\u00d8\u00da\u00f8\u00fa\u0301\u0372\u037f\u0381\u2001\u200e\u200f"+
		"\u2072\u2191\u2c02\u2ff1\u3003\ud801\uf902\ufdd1\ufdf2\uffff\2\u013a\2"+
		"\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2"+
		"\2\3q\3\2\2\2\5s\3\2\2\2\7{\3\2\2\2\t\177\3\2\2\2\13\u0081\3\2\2\2\r\u0083"+
		"\3\2\2\2\17\u0087\3\2\2\2\21\u0089\3\2\2\2\23\u008b\3\2\2\2\25\u008d\3"+
		"\2\2\2\27\u009b\3\2\2\2\31\u00aa\3\2\2\2\33\u00b3\3\2\2\2\35\u00ba\3\2"+
		"\2\2\37\u00bd\3\2\2\2!\u00d2\3\2\2\2#\u00d4\3\2\2\2%\u00d6\3\2\2\2\'\u00e1"+
		"\3\2\2\2)\u00e3\3\2\2\2+\u00ea\3\2\2\2-\u00f4\3\2\2\2/\u00fe\3\2\2\2\61"+
		"\u010a\3\2\2\2\63\u010c\3\2\2\2\65\u010e\3\2\2\2\67\u0112\3\2\2\29\u0114"+
		"\3\2\2\2;\u0116\3\2\2\2=\u0119\3\2\2\2?\u011b\3\2\2\2A\u011d\3\2\2\2C"+
		"\u011f\3\2\2\2E\u0121\3\2\2\2G\u0123\3\2\2\2I\u0125\3\2\2\2K\u0127\3\2"+
		"\2\2M\u0129\3\2\2\2O\u012c\3\2\2\2Q\u012e\3\2\2\2S\u0130\3\2\2\2U\u0132"+
		"\3\2\2\2W\u0134\3\2\2\2Y\u0136\3\2\2\2[\u0138\3\2\2\2]\u013b\3\2\2\2_"+
		"\u013d\3\2\2\2a\u013f\3\2\2\2c\u0141\3\2\2\2e\u0143\3\2\2\2g\u0145\3\2"+
		"\2\2i\u0147\3\2\2\2k\u014a\3\2\2\2m\u014c\3\2\2\2o\u014e\3\2\2\2qr\5+"+
		"\26\2r\4\3\2\2\2sw\5\r\7\2tv\5\61\31\2ut\3\2\2\2vy\3\2\2\2wu\3\2\2\2w"+
		"x\3\2\2\2x\6\3\2\2\2yw\3\2\2\2z|\5!\21\2{z\3\2\2\2|}\3\2\2\2}{\3\2\2\2"+
		"}~\3\2\2\2~\b\3\2\2\2\177\u0080\5S*\2\u0080\n\3\2\2\2\u0081\u0082\5\23"+
		"\n\2\u0082\f\3\2\2\2\u0083\u0084\4C\\\2\u0084\16\3\2\2\2\u0085\u0088\5"+
		"\21\t\2\u0086\u0088\5\23\n\2\u0087\u0085\3\2\2\2\u0087\u0086\3\2\2\2\u0088"+
		"\20\3\2\2\2\u0089\u008a\t\2\2\2\u008a\22\3\2\2\2\u008b\u008c\t\3\2\2\u008c"+
		"\24\3\2\2\2\u008d\u008e\7\61\2\2\u008e\u008f\7,\2\2\u008f\u0093\3\2\2"+
		"\2\u0090\u0092\13\2\2\2\u0091\u0090\3\2\2\2\u0092\u0095\3\2\2\2\u0093"+
		"\u0094\3\2\2\2\u0093\u0091\3\2\2\2\u0094\u0099\3\2\2\2\u0095\u0093\3\2"+
		"\2\2\u0096\u0097\7,\2\2\u0097\u009a\7\61\2\2\u0098\u009a\7\2\2\3\u0099"+
		"\u0096\3\2\2\2\u0099\u0098\3\2\2\2\u009a\26\3\2\2\2\u009b\u009c\7\61\2"+
		"\2\u009c\u009d\7,\2\2\u009d\u009e\7,\2\2\u009e\u00a2\3\2\2\2\u009f\u00a1"+
		"\13\2\2\2\u00a0\u009f\3\2\2\2\u00a1\u00a4\3\2\2\2\u00a2\u00a3\3\2\2\2"+
		"\u00a2\u00a0\3\2\2\2\u00a3\u00a8\3\2\2\2\u00a4\u00a2\3\2\2\2\u00a5\u00a6"+
		"\7,\2\2\u00a6\u00a9\7\61\2\2\u00a7\u00a9\7\2\2\3\u00a8\u00a5\3\2\2\2\u00a8"+
		"\u00a7\3\2\2\2\u00a9\30\3\2\2\2\u00aa\u00ab\7\61\2\2\u00ab\u00ac\7\61"+
		"\2\2\u00ac\u00b0\3\2\2\2\u00ad\u00af\n\4\2\2\u00ae\u00ad\3\2\2\2\u00af"+
		"\u00b2\3\2\2\2\u00b0\u00ae\3\2\2\2\u00b0\u00b1\3\2\2\2\u00b1\32\3\2\2"+
		"\2\u00b2\u00b0\3\2\2\2\u00b3\u00b8\5\67\34\2\u00b4\u00b9\t\5\2\2\u00b5"+
		"\u00b9\5\37\20\2\u00b6\u00b9\13\2\2\2\u00b7\u00b9\7\2\2\3\u00b8\u00b4"+
		"\3\2\2\2\u00b8\u00b5\3\2\2\2\u00b8\u00b6\3\2\2\2\u00b8\u00b7\3\2\2\2\u00b9"+
		"\34\3\2\2\2\u00ba\u00bb\5\67\34\2\u00bb\u00bc\13\2\2\2\u00bc\36\3\2\2"+
		"\2\u00bd\u00c8\7w\2\2\u00be\u00c6\5#\22\2\u00bf\u00c4\5#\22\2\u00c0\u00c2"+
		"\5#\22\2\u00c1\u00c3\5#\22\2\u00c2\u00c1\3\2\2\2\u00c2\u00c3\3\2\2\2\u00c3"+
		"\u00c5\3\2\2\2\u00c4\u00c0\3\2\2\2\u00c4\u00c5\3\2\2\2\u00c5\u00c7\3\2"+
		"\2\2\u00c6\u00bf\3\2\2\2\u00c6\u00c7\3\2\2\2\u00c7\u00c9\3\2\2\2\u00c8"+
		"\u00be\3\2\2\2\u00c8\u00c9\3\2\2\2\u00c9 \3\2\2\2\u00ca\u00d3\7\62\2\2"+
		"\u00cb\u00cf\t\6\2\2\u00cc\u00ce\5%\23\2\u00cd\u00cc\3\2\2\2\u00ce\u00d1"+
		"\3\2\2\2\u00cf\u00cd\3\2\2\2\u00cf\u00d0\3\2\2\2\u00d0\u00d3\3\2\2\2\u00d1"+
		"\u00cf\3\2\2\2\u00d2\u00ca\3\2\2\2\u00d2\u00cb\3\2\2\2\u00d3\"\3\2\2\2"+
		"\u00d4\u00d5\t\7\2\2\u00d5$\3\2\2\2\u00d6\u00d7\t\b\2\2\u00d7&\3\2\2\2"+
		"\u00d8\u00d9\7v\2\2\u00d9\u00da\7t\2\2\u00da\u00db\7w\2\2\u00db\u00e2"+
		"\7g\2\2\u00dc\u00dd\7h\2\2\u00dd\u00de\7c\2\2\u00de\u00df\7n\2\2\u00df"+
		"\u00e0\7u\2\2\u00e0\u00e2\7g\2\2\u00e1\u00d8\3\2\2\2\u00e1\u00dc\3\2\2"+
		"\2\u00e2(\3\2\2\2\u00e3\u00e6\5=\37\2\u00e4\u00e7\5\33\16\2\u00e5\u00e7"+
		"\n\t\2\2\u00e6\u00e4\3\2\2\2\u00e6\u00e5\3\2\2\2\u00e7\u00e8\3\2\2\2\u00e8"+
		"\u00e9\5=\37\2\u00e9*\3\2\2\2\u00ea\u00ef\5=\37\2\u00eb\u00ee\5\33\16"+
		"\2\u00ec\u00ee\n\t\2\2\u00ed\u00eb\3\2\2\2\u00ed\u00ec\3\2\2\2\u00ee\u00f1"+
		"\3\2\2\2\u00ef\u00ed\3\2\2\2\u00ef\u00f0\3\2\2\2\u00f0\u00f2\3\2\2\2\u00f1"+
		"\u00ef\3\2\2\2\u00f2\u00f3\5=\37\2\u00f3,\3\2\2\2\u00f4\u00f9\5? \2\u00f5"+
		"\u00f8\5\33\16\2\u00f6\u00f8\n\n\2\2\u00f7\u00f5\3\2\2\2\u00f7\u00f6\3"+
		"\2\2\2\u00f8\u00fb\3\2\2\2\u00f9\u00f7\3\2\2\2\u00f9\u00fa\3\2\2\2\u00fa"+
		"\u00fc\3\2\2\2\u00fb\u00f9\3\2\2\2\u00fc\u00fd\5? \2\u00fd.\3\2\2\2\u00fe"+
		"\u0103\5=\37\2\u00ff\u0102\5\33\16\2\u0100\u0102\n\t\2\2\u0101\u00ff\3"+
		"\2\2\2\u0101\u0100\3\2\2\2\u0102\u0105\3\2\2\2\u0103\u0101\3\2\2\2\u0103"+
		"\u0104\3\2\2\2\u0104\60\3\2\2\2\u0105\u0103\3\2\2\2\u0106\u010b\5\63\32"+
		"\2\u0107\u010b\4\62;\2\u0108\u010b\5]/\2\u0109\u010b\t\13\2\2\u010a\u0106"+
		"\3\2\2\2\u010a\u0107\3\2\2\2\u010a\u0108\3\2\2\2\u010a\u0109\3\2\2\2\u010b"+
		"\62\3\2\2\2\u010c\u010d\t\f\2\2\u010d\64\3\2\2\2\u010e\u010f\7k\2\2\u010f"+
		"\u0110\7p\2\2\u0110\u0111\7v\2\2\u0111\66\3\2\2\2\u0112\u0113\7^\2\2\u0113"+
		"8\3\2\2\2\u0114\u0115\7<\2\2\u0115:\3\2\2\2\u0116\u0117\7<\2\2\u0117\u0118"+
		"\7<\2\2\u0118<\3\2\2\2\u0119\u011a\7)\2\2\u011a>\3\2\2\2\u011b\u011c\7"+
		"$\2\2\u011c@\3\2\2\2\u011d\u011e\7*\2\2\u011eB\3\2\2\2\u011f\u0120\7+"+
		"\2\2\u0120D\3\2\2\2\u0121\u0122\7}\2\2\u0122F\3\2\2\2\u0123\u0124\7\177"+
		"\2\2\u0124H\3\2\2\2\u0125\u0126\7]\2\2\u0126J\3\2\2\2\u0127\u0128\7_\2"+
		"\2\u0128L\3\2\2\2\u0129\u012a\7/\2\2\u012a\u012b\7@\2\2\u012bN\3\2\2\2"+
		"\u012c\u012d\7>\2\2\u012dP\3\2\2\2\u012e\u012f\7@\2\2\u012fR\3\2\2\2\u0130"+
		"\u0131\7?\2\2\u0131T\3\2\2\2\u0132\u0133\7A\2\2\u0133V\3\2\2\2\u0134\u0135"+
		"\7,\2\2\u0135X\3\2\2\2\u0136\u0137\7-\2\2\u0137Z\3\2\2\2\u0138\u0139\7"+
		"-\2\2\u0139\u013a\7?\2\2\u013a\\\3\2\2\2\u013b\u013c\7a\2\2\u013c^\3\2"+
		"\2\2\u013d\u013e\7~\2\2\u013e`\3\2\2\2\u013f\u0140\7&\2\2\u0140b\3\2\2"+
		"\2\u0141\u0142\7.\2\2\u0142d\3\2\2\2\u0143\u0144\7=\2\2\u0144f\3\2\2\2"+
		"\u0145\u0146\7\60\2\2\u0146h\3\2\2\2\u0147\u0148\7\60\2\2\u0148\u0149"+
		"\7\60\2\2\u0149j\3\2\2\2\u014a\u014b\7B\2\2\u014bl\3\2\2\2\u014c\u014d"+
		"\7%\2\2\u014dn\3\2\2\2\u014e\u014f\7\u0080\2\2\u014fp\3\2\2\2\33\2w}\u0087"+
		"\u0093\u0099\u00a2\u00a8\u00b0\u00b8\u00c2\u00c4\u00c6\u00c8\u00cf\u00d2"+
		"\u00e1\u00e6\u00ed\u00ef\u00f7\u00f9\u0101\u0103\u010a\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}