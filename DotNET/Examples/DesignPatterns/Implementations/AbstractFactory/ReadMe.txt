This example demonstrates the AbstractFactory design pattern.

ComponentFactory implements the AbstractFactory. It is an empty concrete class instead of an abstract class. Clients create an object of this factory. Two ConcreteFactories RegularFactory and FramedFactory create labels and buttons. A concern dispatches calls to the AbstractFactory to a ConcreteFactory. So Creating the ConcreteFactory is moved from the client to the concern AbstractFactory. Whether the client or a concern depends on who is intended to determine the ConcreteFactory.

Then two evolution scenarios are applied:
1) A new product inputfield is added. 
Instead of making invasive changes to all factories a concern contains the new products. This groups all inputfields in a single module. As a result no changes are needed to existing modules and all changes are localized.

2) A new factory ColoredFactory is added and used as the current ConcreteFactory
All calls to the ComponentFactory are dispatched to the ColoredFactory. Because this factory is added after addding inputfields this factory can contain a factory method directly.