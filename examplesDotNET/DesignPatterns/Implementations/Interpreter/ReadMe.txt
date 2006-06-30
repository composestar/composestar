This example demonstrates the Interpreter design pattern.

A grammar for boolean expressions is represented. Three operations are performed on the representation. The "evaluate" operation corresponds to the "Interpret" operation in the pattern. The "copy" and "replace" operations are used specifically in this case. 

The operations crosscut with the representation. For every operation a concern is defined. 
Because every operation is different for each representation class a filtermodule is created per operation per class. Because the operations need to access the variables of the expressions the variables are protected. The Visitor pattern can also be used to implement the operations. 




