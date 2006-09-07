This example demonstrates the Adapter design pattern.

For simple adaptations it is not needed to implement a separate Adapter class. Here the method "write()" is adapted to method "printToSystemOut()". Because the only difference is the method name, this is automatically adapted by the dispatch filter.

Now that multiple messages are implemented a single method call can be adapted to several. A call to "printSquare" is adapted to calls to "printVertical" and "printHorizontal". 

For more complex adaptations an Adapter class (e.g. as an internal) is still needed e.g. changing arguments. The Adapter class specifies an ACT that changes the argument that is send to "printText". 





