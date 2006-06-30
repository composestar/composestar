This example demonstrates the Decorator design pattern.

ConcreteOutput is the ConcreteComponent and prints a String to screen. Every Decorator adds something to the String. The BracketDecorator adds brackets, the DollarDecorator adds dollar signs, and the StarDecorator adds stars.

This pattern is directly supported by Compose*.  Decorated behavior is implemented by meta filters. Behavior can be dynamically turned on or off by conditions. A disadvantage compared to the OO version is that dynamic reordering of behavior is not possible.