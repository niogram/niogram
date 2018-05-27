/**
 * ANRLR 4 token vocabulary files
 */
grammar ANTLRv4Tokens;

options {superClass = org.antlr.v4.runtime.Parser;}

import LexBasic;

tokenSpecs:
    line+
;

line:
    name EQ INTEGER EOL
;

name:
    IDENTIFIER | STRING_LITERAL
;

STRING_LITERAL:
    SQuoteLiteral
;

IDENTIFIER:
    UPPERCASE NameChar*
;

INTEGER:
    DecimalNumeral+
;

EQ:
    Equal
;

EOL:
    Vws
;

UPPERCASE:
    'A'..'Z'
;