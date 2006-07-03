This example demonstrates the Mediator design patttern.

Two buttons and a label are Colleagues. They communicate through the ConcreteMediator. The client adds Colleagues to the Mediator.

A filtermodule implements communication from Colleagues to Mediator and from Mediator to Colleagues. If a button is clicked then "colleagueChanged" is called in the Mediator (communication from Colleague to Mediator). The Colleague that changed is determined and a helper method "cc", on which can be filtered, is called.

Based on this call actions are performed on Colleagues. The action depends on the changed Colleague and is determined by conditions. If button1 changes then its text is changed. If button2 changes then its text is changed by the Mediator. In both cases the text of the label changes (communication from Mediator to Colleague).


