This example demonstrates the Observer design patttern.

Point implements the Subject role.
Screen implements the Subject and Observer role. The Subject role currently does not work. Pattern specific code is removed from Point and Screen and is placed in concerns.

The concern NotifyObservers implements a case-specific observer notification.
The concern ObserverUpdate implements a case-specific observer update.
The concern AddRemoveObservers implements a generic Subject-Observer mapping.
The concern Superimpose superimposed all concerns on classes.