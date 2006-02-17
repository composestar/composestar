This example demonstrates the use of prolog predicate based superimposition selectors.

The program implements a working but really simple UDP communication client. The application can
be started in 'receiver' mode or in 'sender' mode. The 'sender' can send a simple string message
to a receiver.

Structure:

MyApp/
  Main - the main program
  
  MyApp.net/
    Connection - basic handling of an UDP 'connection' to another system
      IncomingConnection - can receive messages
        SpecificIncomingConnection - can receive messages, but only from the specified sender
    OutgoingConnection - can send messages
  MyApp.logging/
    Logger - will write a message to stdout when the 'log' method is called
    Logging.cps - The logging CPS concern, where you can specify where and how to impose the Logger

  
The point is ofcourse that the app contains some classes, namespaces, and that
it uses inheritance so that we can write some interesting superimposition selectors.


How to use the program:

You can start it in 3 different modes:

Listening for messages on a certain port:
  Main <port>
  
Listening for messages on a certain port, but only for messages from the specified sender:
  Main <port> <sender-host>
  
Sending a message to a listener on host:port
  Main <hostname> <port> <message string>