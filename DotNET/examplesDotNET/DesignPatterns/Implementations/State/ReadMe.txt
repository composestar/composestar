This example demonstrates the State design pattern.

A queue is implemented using the pattern.
QueueState implements State.
QueueEmpty, QueueNormal, and QueueFull implement ConcreteStates.
Queue implements the Context.

State transitions can be placed in the Context or in each State class.
In the first situation state transitions crosscut with forward methods. In the second case state transitions crosscuts with behavior in State classes. The concern StateTransitions implements the state transitions and they are modularized in the class StateTransition.

The concern Forwarding forwards calls to Sorter to the current State.
The concern Superimpose applies the two other concerns to the Context.

