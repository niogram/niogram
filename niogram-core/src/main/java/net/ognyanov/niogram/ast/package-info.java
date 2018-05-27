/**
* <h3>The NioGram AST model for grammars</h3>
* 
* The AST model is described by the following grammar :
* 
* <pre>
* tokens {Nonterminal, Terminal, TerminalRule}
* 
* grammar         : nonterminalRule* TerminalRule*;
* nonterminalRule : alternative+;
* alternative     : term*;
* term            : Terminal | Nonterminal | block;
* block           : alternative+;
* </pre>
*
* Here the names match up to first letter capitalization
* the names of the implementing Java classes. The tokens
* represent leaf nodes of the AST. The EBNF occurrence 
* indicators in the source text of the grammar are represented
* in the AST by attributes of the block nodes. For terminal and
* nonterminal terms in the source of the grammar which have EBNF
* occurrence indicators the NioGram parser creates enclosing 
* blocks and assigns correspondent attributes to that block.
* Thus all terms with EBNF markup are represented by blocks.
* In case that the term is EBNF-optional the parser inserts
* an empty alternative in the block.
* 
* <p>The type hierarchy of the implementation classes is as follows:
* <center>
* <p><img src="./doc-files/AST.png" width="700" alt="Diagram">
* </center>
* 
* <p>Grammar – related tools tend to have different conventions about 
* the representation of the empty strings in grammar rules. Some use
* explicit epsilon symbol for that and others do not. NioGram adheres
* to the second convention and defines no explicit epsilon terminal
* in its AST model. Empty alternatives are literally empty rather than
* containing the epsilon terminal.
* <p>EBNF occurrence indicators have a representation in AST only for
* AST blocks. Therefore if other type of term (i.e. - terminal or
* nonterminal) has an EBNF occurrence indicator in the source text
* of the grammar then it is inserted into an AST block of its own.
* If an occurrence indicator in the source is one of ‘?’, ‘??’. ‘*’
* or ‘*?’ then an empty alternative is inserted into the correspondent
* block.
* <p>For the sake of precision let us finally note that even though
* the AST is viewed mostly as a tree, technically it is a general
* directed graph. Rules contain references to all their 
* instances on the right hand side of productions and instances
* contain references to their correspondent rule.
*/
package net.ognyanov.niogram.ast;
