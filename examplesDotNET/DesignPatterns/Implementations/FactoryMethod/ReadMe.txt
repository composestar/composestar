This example demonstrates the FactoryMethod design pattern.

GUIComponentCreator is a Creator. ButtonCreator and LabelCreator are ConcreteCreators. The method "showFrame" is the AnOperation in the pattern. This method is moved from the Creator to a concern that superimposes it. 

Now the Creator only specifies the constraint that ConcreteCreators must implements its methods. This implementation is similar to the TemplateMethod pattern. The "AnOperation"  method can be considered to tangle with constraints imposed on ConcreteCreators by the Creator. This is a minor change to the OO version.