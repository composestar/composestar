This example demonstrates the Builder Design Pattern.

Director and ComplexDirector implement the Director role. Creator, TextCreator and XMLCreator implement the Builder role.

The concern AbstractConstruction superimposes the Director role on the Builders. As a result clients only have to create a Builder and do not create Directors and couple them to Builders. This simplifies the client but it also has no control over the coupling of a Director and Builder.

