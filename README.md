﻿# **NioGram**
## Grammar Analysis Toolkit

**Project Sites**

* [Home](https://niogram.github.io/niogram/)
* [API](https://niogram.github.io/niogram/apidocs)
* [User Manual](https://niogram.github.io/niogram/NioGram_User_Manual.pdf)
* [GitHub Repository](https://github.com/niogram/niogram)

---

**NioGram** is a tool for **LL(k)** syntax analysis of 
context free grammars. Such analysis can be benefitial
in the process of language and grammar design and for
the process of hand-coded parser implementation.

At present the grammar specification language directly
supported by NioGram is the language of the
[**ANTLR 4**](http://www.antlr.org/) parser generator.
The grammar model and the analysis methods of NioGram
however are not dependent on the grammar specification
language. If an appropriate parser to the NioGram AST
model is implemented then NioGram will be able to process
grammars specified in other languages such as e.g. JavaCC,
YACC, Bison etc.

---

### Use Cases
* **ANTLR 4 IDEs**  
In ANTLR 4 IDEs the data from NioGram analysis can be used
to signal possible grammar inefficiencies and bugs. In the
author's opinion this would be extremely helpful for
ANTLR 4 grammar developers.
* **Command Line Tool**  
NioGram provides a command line tool which servers the
same purpose as the potential ANTLR 4 IDE enhancements
but in a less convenient way.
* **Hand-Coded Parsers**  
NioGram simplifies to a great extent the collection
of analysis data needed for the implementation of
hand-coded parsers and generates related programming
artefacts.

---

### ANTLR 4 Grammar Development Support
ANTLR 4 implements an extremely powerful parsing strategy.
It can deal with almost any grammar which lacks indirect
left recursion. For the grammar developers this power is
both a blessing and a curse. During grammar development
ANTLR 4 provides no diagnostics of possible grammar
inefficiencies and even of outright bugs. NioGram
mitigates this problem by providing tools for traditional
**LL(k)** syntax analysis of ANTLR 4 grammars. The
information computed in the process of NioGram analysis is
as follows:

* Nonterminal productivity
* Nonterminal reachability
* Nonterminal use
* Nonterminal nullability
* Grammar dependency graph
* Simple cycles in the grammar dependency graph
* Strongly connected components of the grammar depencency graph
* Left-recursive cycles in the grammar
* First/Follow sets
* FirstK/FollowK sets
* Linearized FirstK/FollowK sets
* Conflicts
* Terminal occurrence traces

Perhaps most important for ANTLR 4 grammar development is
the information about LL(k) conflicts. Even though ANTLR will
typically deal with those automatically, the grammar author
will be prompted to look into the corresponded rules for
inefficiencies, ambiguities and bugs. Furthermore, if the
author develops not just a grammar but a language then the
**LL(k)** properties of the grammar are important for the ease
of comprehension of the language.

---

### Hand-Coded Parser Development Support

Despite the existence of excellent parser generator tools,
hand-coded parsers are still being developed even for
A-list languages such as Java. More often than not though
the analysis data needed for hand coding is
difficult to collect by hand. Unfortunately there
appear to be no publicly available tools for automatic
computation of the needed data. NioGram fills this gap by
providing the analysis information described above. Furthermore, since NioGram supports the ANTLR grammar
specification language, hand-coded parsers can be
validated against parsers generated by ANTLR.
NioGram also facilitates integration of hand-coded
parsers with ANTLR generated lexers.

In a bit more detail :

First order of business in grammar development is to clean
up the grammar of (usually buggy) non-productive 
nonterminals and left recursion. Doing this by hand is
usually feasible but with NioGram analysis the task is
considerably easier to accomplish and verify. Then the
typical situation will be :

1. Most rules are **LL(1)**
2. Some rules are **LL(k)** with small k > 1.
3. A few rules may be not **LL(k)** for any k.

If the situation is worse than this then the language
is either old, influential and bloated or poorly designed
(or both). As already noted, apart from the subject of
anguage implementation LL(k) properties of the grammar
are very important from the standpoint of ease of comprehension
of the language by its "speakers". Finding out which rules
belong to which of the above categories is a crucially
important task in parser development. Doing this by hand
though is far from trivial. Even talented and experienced
developers can easily make mistakes. NioGram on the other
hand fully authomates the task and thus makes it cheap and
error free.

Parsing of the **LL(k)** rules can be easily implemented
by hand in a recursive descent parser **if** the
FirstK/FollowK sets are known. This is often a
really big "**IF**" since the FirstK/FollowK sets for
higher level nonterminals tend to be quite sizeable.
Collecting the information by hand is tedious and
error prone. It is questionable whether the task
is even feasible for "serious" language grammars. NioGram
fully automates the process. Thus the feasibility is
always guaranteed and a lot of time for development and
even more time for testing and debugging is saved.

The non-**LL(k)** rules (if any)  have to be resolved by
one or more of the following:

1. Context dependency
2. Temporary switch to a different parsing stragegy
3. Temporary switch to a different grammar
4. Ad hoc tricks

NioGram strives to facilitate these solutions by means of
grammar analysis. For example - by computing terminal
occurrence traces.

---

### Development Roadmap and Status
1. AST model for the ANTLR 4 grammar language. **Done**
2. Parsing of ANTLR grammars to AST. **Done**
3. Grammar analysis **Done**
4. General code and API cleanup. **In Progress**
5. Documentation. **Ongoing** 
6. Initial acceptance testing. **TBD**
7. First release to public. **TBD**

**General Status : Under Development**

---
