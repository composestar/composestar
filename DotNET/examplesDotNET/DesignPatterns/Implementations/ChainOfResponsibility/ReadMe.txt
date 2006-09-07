This example demonstrates the Chain of Responsibility (CoR) design pattern.

Button, Panel and Frame are members in the chain. Which member handles a request is determined by conditions that are located in Click. The concern dispatches all CoR specific calls to the class ClickHandlers.

Setting a successor, Handler logic, and passing a request to a successor are crosscutting concerns.

All members in de chain are implemented in different classes. But you want to call CoR specific methods on a single type. Therefore all members are cast to the ClickHandler interface by the internal ClickHandlers.