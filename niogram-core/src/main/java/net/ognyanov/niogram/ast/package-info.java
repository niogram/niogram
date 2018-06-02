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
* <p>For the sake of convenience alternatives and blocks in AST instances
* are assigned display names by the following scheme:
* <ul>
* <li>alternative number n of a rule or block is assigned a display name
* <br><code>&lt;rule or block name&gt;/an</code> </li>
* <li>block number n in an enclosing alternative is assigned a display name
* <br><code>&lt;alternative name&gt;.bn</code> </li>
* </ul>
* When grammar nodes are printed out in dot format, the names of
* alternatives are reduced to just the last "an" in order to improve
* readability. In other types of graph printouts where alternatives
* are present their names are presented in full.
* <p>Implementation Note:<br>
* NioGram does not invest much code and performance into making
* the AST model fool-proof. Client code is technically capable of
* corrupting the structure and the data content of the AST. Analysis
* though is only guaranteed to work correctly (or work at all) if the
* following rules are observed:
* <ol>
* <li>Client code never changes the structure of the AST.</li>
* <li>Client code invokes directly only the following data mutation methods:
* <ol>
* <li><code>GrammarNode.setSourceContext()</code></li>
* <li><code>GrammarNode.setPayload()</code></li>
* <li><code>Grammar.setK()</code></li>
* <li><code>Grammar.setKL()</code></li>
* <li><code>Grammar.clearFlags()</code></li>
* <li><code>Grammar.clearFF()</code></li>
* <li><code>Grammar.clearFFK()</code></li>
* <li><code>Grammar.clearFFKL()</code></li>
* </ol>
* </li>
* </ol>
*/
package net.ognyanov.niogram.ast;
