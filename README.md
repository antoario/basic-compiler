# Basic-compiler
Creation of a basic compiler for university exam of Formal Languages and Translators.

The objective of the translator is the recognition of a simple programming language shown in class and the generation of bytecode executable by the Java Virtual Machine. To do this we transform the input file with the .lft extension into a file with the .j extension thanks to the translator we have created. Inside this file there will be the header, the code translated by us and the footer. We compile with jasmin.jar (assembler for JVM) thus creating the .class file, and finally we execute with the classic java command.

We have created 3 translators:
- Traslator.java: it's a simple translator that recognizes only the RELOP tag as <,<=,>,>=,<>,== ;
- BooleanTranslator.java: it's the most complete of the three translators, in addition to RELOP tags it also recognizes logical connectives such as AND, OR and ! ;
- NoGotoTranslator.java: it's used to not print "useless" goto instructions, you pass a boolean called jump and check if it is true, if it is, you invert the conditions.

Line of code to compile with assembler: java -jar jasmin.jar Output.j
