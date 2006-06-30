This example demonstrates the Bridge design pattern.

Screen, GreetingScreen and InformationScreen implement Abstractions. ScreenImplementation, CrossCapitalImplementation and StarImplementation implement Implementors.

A default Abstraction and Implementor is used. The concern Bridge overrides the used Implementor based on a random value of a condition. This shows that Compose* can change the Implementor without changing existing code.