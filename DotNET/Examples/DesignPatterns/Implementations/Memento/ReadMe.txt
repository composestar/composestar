This example demonstrates the Memento design pattern.

Counter implements the role Originator. CounterMemento implements the role of Memento.

The "SetMemento(Memento m)" and "CreateMemento()" in the Originator crosscut and are placed in concern MementoCreation. The concern MementoAccess makes sure that only the Originator that belongs to the Memento can access state in the Memento.

A problem is that the state of Counter is protected in order for the concern MementoCreation to access it. This breaks encapsulation of the Originators state. This is the same state that concern MementoAccess tries protect. Compose* can be modified to allow it to access private variables in order to solve this problem.


