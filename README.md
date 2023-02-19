# Basic-compiler
Creation of a basic compiler for university exam of Formal Languages and Translators.

The objective of the translator is the recognition of a simple programming language shown in class and the generation of bytecode executable by the Java Virtual Machine. To do this we transform the input file with the .lft extension into a file with the .j extension thanks to the translator we have created. Inside this file there will be the header, the code translated by us and the footer. We compile with jasmin.jar (assembler for JVM) thus creating the .class file, and finally we execute with the classic java command.

Line of code to compile with assembler: java -jar jasmin.jar Output.j
