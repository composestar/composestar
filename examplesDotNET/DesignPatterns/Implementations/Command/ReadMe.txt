This example demonstrates the Command design pattern. It decouples an invoker and a receiver by adding a Command. The Invoker executes a Command, and the Command executes the Receiver.Button is the Invoker, PrintTarget is the Receiver, and ButtonCommand is the Command.

With simple executions a concern can immediatelly map the Invoker to a Receiver. For more complex executions a concern first maps Invoker to Command and then Command to Receiver.

The concern CommandCommunication overrides the "executeCommand" method of Command behavior and shows the mappings. The same kind of message conversions are possible as with the Adapter pattern.