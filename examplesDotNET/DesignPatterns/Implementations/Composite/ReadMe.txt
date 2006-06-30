This example demonstrates the Composite design pattern. 

A directory structure is implemented as a Composite. Here Files are Leafs and Directories are Composites.

The concern implements the crosscutting concerns of child management operations and printing the structure. An internal Composite and Leaf implement pattern specific code for respectively Directory and File.

We want to treat Files and Directories uniformly in order to call the "printStructure" method. They are cast to FileSystemComponent.

In the OO version a choice between safety and transparancy must be made. Here a cast allows to treat Leafs and Composites partly uniform ("printStructure" method), while calls to child management operations to Leafs are caught at compile-time.




