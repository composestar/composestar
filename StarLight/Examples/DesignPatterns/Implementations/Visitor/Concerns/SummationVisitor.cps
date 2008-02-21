concern SummationVisitor {
	filtermodule Summation{
		internals
			summation : Composestar.Patterns.Visitor.SummationVisitor;

		inputfilters
			sum : Dispatch = {[*.getSum] summation.getSum }
	}
	filtermodule SummationNode{
		internals
			summation : Composestar.Patterns.Visitor.SummationVisitor;

		inputfilters
			sum : Dispatch = {[*.getSum] summation.getSumNode }
	}
	filtermodule SummationLeaf{
		internals
			summation : Composestar.Patterns.Visitor.SummationVisitor;

		inputfilters
			sum : Dispatch = {[*.getSum] summation.getSumLeaf }
	}
	superimposition{
		selectors
			leaf = { C | isClassWithName(C,'Composestar.Patterns.Visitor.BinaryTreeLeaf') };
			node = { C | isClassWithName(C,'Composestar.Patterns.Visitor.BinaryTreeNode') };
			binaryTreeElement = { C | isClassWithName(C,'Composestar.Patterns.Visitor.BinaryTreeElement') };
		
		filtermodules
			leaf <- SummationLeaf;
			node <- SummationNode;
			binaryTreeElement <- Summation;
	}
	implementation in JSharp by	Composestar.Patterns.Visitor.SummationVisitor as	"SummationVisitor.jsl"
	{
		package Composestar.Patterns.Visitor;

		public class SummationVisitor{

			public int getSum()
			{
				System.out.println("getSum() should never be called");
				return 0;
			}

			public int getSumNode()
			{
				BinaryTreeNode rnode = (BinaryTreeNode) ((Object)this); 
				return rnode.left.getSum() + rnode.right.getSum();
			}

			public int getSumLeaf()
			{
				BinaryTreeLeaf leaf = (BinaryTreeLeaf) ((Object)this); 
				return leaf.getValue();
			}
		}
	}
}
