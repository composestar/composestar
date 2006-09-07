This example demonstrates the Prototype design pattern.

StringPrototypeA and StringPrototypeB are two prototypes that represent Strings.
In the object-oriented version cloning crosscuts the prototypes.

Here the concern "Clone" implements the clone operations modularly. The clone operation is type dependent and therefore a filtermodule is defined and superimposed per Prototype class. If objects of different classes can be cloned the same way then the same filtermodule can be used.

