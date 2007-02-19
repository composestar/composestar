concern TraversalVisitor {
	filtermodule Traversal{
		internals
			traversal : Composestar.Patterns.Visitor.TraversalVisitor;

		inputfilters
			traverse : Dispatch = {[*.getTraversal] traversal.getTraversal }
	}
	filtermodule TraversalNode{
		internals
			traversal : Composestar.Patterns.Visitor.TraversalVisitor;

		inputfilters
			traverse : Dispatch = {[*.getTraversal] traversal.getTraversalNode }
	}
	filtermodule TraversalLeaf{
		internals
			traversal : Composestar.Patterns.Visitor.TraversalVisitor;

		inputfilters
			traverse : Dispatch = {[*.getTraversal] traversal.getTraversalLeaf }
	}

	superimposition{
		selectors
			leaf = { C | isClassWithName(C,'Composestar.Patterns.Visitor.BinaryTreeLeaf') };
			node = { C | isClassWithName(C,'Composestar.Patterns.Visitor.BinaryTreeNode') };
			binaryTreeElement = { C | isClassWithName(C,'Composestar.Patterns.Visitor.BinaryTreeElement') };
		
		filtermodules
			leaf <- TraversalLeaf;
			node <- TraversalNode;
			binaryTreeElement <- Traversal;
	}
	implementation in JSharp by	Composestar.Patterns.Visitor.TraversalVisitor as	"TraversalVisitor.jsl"
	{
		package Composestar.Patterns.Visitor;

		public class TraversalVisitor{

			public String getTraversal()
			{
				System.out.println("getTraversal() should never be called");
				return "";
			}

			public String getTraversalNode()
			{
				BinaryTreeNode node = (BinaryTreeNode) ((Object)this); 
				return "{" + node.left.getTraversal() + "," + node.right.getTraversal() + "}";
			}

			public String getTraversalLeaf()
			{
				BinaryTreeLeaf leaf = (BinaryTreeLeaf) ((Object)this); 
				return ""+leaf.getValue();
			}
		}
	}
}
