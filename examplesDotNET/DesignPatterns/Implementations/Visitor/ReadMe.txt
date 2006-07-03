This example demonstrates the Visitor design pattern.

The pattern groups related operations in a Visitor class that would otherwise be scattered in classes of an object structure. Because Compose* can also group related operations, this pattern can directly be expressed and the structure disappears. 

There is no "accept(Visitor)" operation on the object structure. The Visitors have a "VisitConcreteElement()" operation, but without the need to pass the concrete element. The only connection to the Visitor pattern is that the concerns that implement the operations are still called Visitors. The Visitors (operations) are superimposed on the object structure. The SummationVisitor adds the values in the tree. The TraversalVisitor shows the tree.

The classes BinaryTreeLeaf and BinaryTreeNode create an object structure. They inherit from the class BinaryTreeElement in order to abstract if an element in the tree is a leaf or node. Because Visitors need access to state of the object structure the variables are protected.







