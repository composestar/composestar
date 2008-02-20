This example demonstrates the Iterator design pattern.

The interface SimpleList implements Aggregate. The class OpenList implements the ConcreteAggregate. The class ReverseIterator implements the Iterator.

The factory method that creates the Iterator can be considered to tangle with the aggregate. To resolve this the method is superimposed on the Aggregate by the concern CreateIterator.