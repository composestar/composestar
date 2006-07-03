This example demonstrates the TemplateMethod design pattern.

DecoratedStringGenerator implements the role AbstractClass. FancyGenerator and SimpleGenerator inherit from DecoratedStringGenerator and implement the role ConcreteClass.

The difference with the object-oriented version is that the template method of the AbstractClass is not part of the class but superimposed by concern TemplateMethod.This separates defining an algorithm (the template method) from the primitive operations of the algorithm. This is not necessarily a better implementation but is an alternative that depends on a matter of taste.


