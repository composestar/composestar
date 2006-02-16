package Composestar.Core.FILTH;
/*
 * Created on 2-sep-2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

/**
 * @author nagyist
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
import java.util.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;

import Composestar.Core.FILTH.Core.*;
import Composestar.Core.FILTH.XMLSpecification.*;

import java.io.*;

public class Run {
	public void test1(){
		Action c=new Action("c",new Boolean(true),true);
		Action p=new Action("p",new Boolean(true),true);
		Action m=new Action("m",new Boolean(true),true);
		Action u=new Action("u",new Boolean(true),true);
		Rule p1=new SoftPreRule((Parameter)c, (Parameter)p);
		Rule p2=new SoftPreRule((Parameter)m, (Parameter)u);
		
		Graph g=new Graph();g.setRoot(new Node((Object)"root"));
		Action.insert(c,g);
		Action.insert(p,g);
		Action.insert(m,g);
		Action.insert(u,g);
		p1.insert(g);
		p2.insert(g); 
		OrderTraverser ot=new OrderTraverser();
		LinkedList order=ot.traverse(g);
		//for (Iterator i=order.iterator();i.hasNext();)
		//	System.out.println(((Node)i.next()).getElement());
	}

	/* testing cycles */
	public void test2(){
		Action c=new Action("c",new Boolean(true),true);
		Action p=new Action("p",new Boolean(true),true);
		Action m=new Action("m",new Boolean(true),true);
		Action u=new Action("u",new Boolean(true),true);

		/* 1. cycle */
		Rule p1=new SoftPreRule((Parameter)c, (Parameter)p);
		Rule p2=new SoftPreRule((Parameter)p, (Parameter)m);
		Rule p3=new SoftPreRule((Parameter)m, (Parameter)c); 
		/* 2. cycle */
		Rule p4=new SoftPreRule((Parameter)m, (Parameter)p); 
		/* 3. cycle */
		Rule p5=new SoftPreRule((Parameter)u, (Parameter)m); 
		Rule p6=new SoftPreRule((Parameter)p, (Parameter)u); 
		/* 4. cycle */
		Rule p7=new SoftPreRule((Parameter)p, (Parameter)p); 
		
		Graph g=new Graph();g.setRoot(new Node((Object)"root"));
		Action.insert(c,g);
		Action.insert(p,g);
		Action.insert(m,g);
		Action.insert(u,g);

		p1.insert(g);
		p2.insert(g); 
		p3.insert(g); 
		p4.insert(g); 
		p5.insert(g); 
		p6.insert(g); 
		p7.insert(g); 

		OrderTraverser ot=new OrderTraverser();
		LinkedList order=ot.traverse(g);
		//for (Iterator i=order.iterator();i.hasNext();)
		//	System.out.println(((Node)i.next()).getElement());
	}
	
	/* the example of the paper */	
	public void test3(){
		Action c=new Action("c",null,true);
		Action p=new Action("p",new Boolean(true),true);
		Action m=new Action("m",new Boolean(true),true);
		Action u=new Action("u",new Boolean(true),true);
		Rule p1=new SoftCondRule((Parameter)c, (Parameter)p);
		Rule p2=new SoftSkipRule((Parameter)p, (Parameter)u, new True());
		Rule p3=new SoftPreRule((Parameter)p, (Parameter)m);


//		PreRule p2=new PreRule((Parameter)m, (Parameter)u);
		
		Graph g=new Graph();g.setRoot(new Node((Object)"root"));
		Action.insert(c,g);
		Action.insert(p,g);
		Action.insert(m,g);
		Action.insert(u,g);
		p1.insert(g);
		p2.insert(g);
		p3.insert(g); 
 
		OrderTraverser ot=new OrderTraverser();
		LinkedList order=ot.traverse(g);
		
		//System.out.print("\norder>> ");
		//for (Iterator i=order.iterator();i.hasNext();)
		//	System.out.print(((Node)i.next()).getElement()+" ");
		//System.out.println("\n");
		
		ExecutionManager em=new ExecutionManager(order,g);
		em.execute();
	}

	public void test33()
	{
		Action c=new Action("c",new Boolean(true),true);
		Action p=new Action("p",new Boolean(true),true);
		Action m=new Action("m",new Boolean(true),true);
		Action u=new Action("u",new Boolean(true),true);
		Rule p1=new IncludeRule((Parameter)c, (Parameter)p);
		Rule p2=new IncludeRule((Parameter)p, (Parameter)m);
		Graph g=new Graph();g.setRoot(new Node((Object)"root"));
		Action.insert(c,g);
		Action.insert(p,g);
		Action.insert(m,g);
		Action.insert(u,g);
		p1.insert(g);
		p2.insert(g); 
		OrderTraverser ot=new OrderTraverser();
		LinkedList order=ot.traverse(g);
		for (Iterator i=order.iterator();i.hasNext();)
			System.out.println(((Node)i.next()).getElement());
		}


	/* testing cond-cond */
	public void test4()
	{
		Action a=new Action("a",new Boolean(true),true);
		Action b=new Action("b",new Boolean(false),true);
		Action c=new Action("c",new Boolean(true),true);
		Rule p1=new HardCondRule((Parameter)a, (Parameter)c);
		Rule p2=new HardCondRule((Parameter)b, (Parameter)c);


//		PreRule p2=new PreRule((Parameter)m, (Parameter)u);
		
		Graph g=new Graph();g.setRoot(new Node((Object)"root"));
		Action.insert(a,g);
		Action.insert(b,g);
		Action.insert(c,g);
		p1.insert(g);
		p2.insert(g);
 
		OrderTraverser ot=new OrderTraverser();
		LinkedList order=ot.traverse(g);
		
		//System.out.print("\norder>> ");
		//for (Iterator i=order.iterator();i.hasNext();)
		//	System.out.print(((Node)i.next()).getElement()+" ");
		//System.out.println("\n");
		
		ExecutionManager em=new ExecutionManager(order,g);
		em.execute();
			
	}

	/* testing cond-skip */
	public void test5(){
		Action a=new Action("a",new Boolean(true),true);
		Action b=new Action("b",new Boolean(false),true);
		Action c=new Action("c",new Boolean(true),true);
		Rule p1=new HardSkipRule((Parameter)a, (Parameter)c, new False());
		Rule p2=new HardCondRule((Parameter)b, (Parameter)c);


//		PreRule p2=new PreRule((Parameter)m, (Parameter)u);
		
		Graph g=new Graph();g.setRoot(new Node((Object)"root"));
		Action.insert(a,g);
		Action.insert(b,g);
		Action.insert(c,g);
		p1.insert(g);
		p2.insert(g);
 
		OrderTraverser ot=new OrderTraverser();
		LinkedList order=ot.traverse(g);
		
		//System.out.print("\norder>> ");
		//for (Iterator i=order.iterator();i.hasNext();)
		//	System.out.print(((Node)i.next()).getElement()+" ");
		//System.out.println("\n");
		
		ExecutionManager em=new ExecutionManager(order,g);
		em.execute();
			
	}	

	/* testing skip-skip conflict*/
	public void test6(){
		Action a=new Action("a",new Boolean(true),true);
		Action b=new Action("b",new Boolean(true),true);
		Action c=new Action("c",new Boolean(true),true);
		Rule p1=new HardSkipRule((Parameter)a, (Parameter)c, new False());
		Rule p2=new HardSkipRule((Parameter)b, (Parameter)c, new True());


//		PreRule p2=new PreRule((Parameter)m, (Parameter)u);
		
		Graph g=new Graph();g.setRoot(new Node((Object)"root"));
		Action.insert(a,g);
		Action.insert(b,g);
		Action.insert(c,g);
		p1.insert(g);
		p2.insert(g);
 
		OrderTraverser ot=new OrderTraverser();
		LinkedList order=ot.traverse(g);
		
		//System.out.print("\norder>> ");
		//for (Iterator i=order.iterator();i.hasNext();)
		//	System.out.print(((Node)i.next()).getElement()+" ");
		//System.out.println("\n");
		
		ExecutionManager em=new ExecutionManager(order,g);
		em.execute();
			
	}	

	public void test7(){
		Action a=new Action("a",new Boolean(true),true);
		Action b=new Action("b",new Boolean(true),false);
		Action c=new Action("c",new Boolean(true),true);
		Rule p1=new HardPreRule((Parameter)b, (Parameter)c);
//		Rule p2=new HardSkipRule((Parameter)b, (Parameter)c, new True());


//		PreRule p2=new PreRule((Parameter)m, (Parameter)u);
		
		Graph g=new Graph();g.setRoot(new Node((Object)"root"));
		Action.insert(a,g);
		Action.insert(b,g);
		Action.insert(c,g);
		p1.insert(g);
//		p2.insert(g);
 
		OrderTraverser ot=new OrderTraverser();
		LinkedList order=ot.traverse(g);
		
		//System.out.print("\norder>> ");
		//for (Iterator i=order.iterator();i.hasNext();)
		//	System.out.print(((Node)i.next()).getElement()+" ");
		//System.out.println("\n");
		
		ExecutionManager em=new ExecutionManager(order,g);
		em.execute();
			
	}	
	
	//test for providing all the possible orders
	public void test8(){
		Action c=new Action("c",new Boolean(true),true);
		Action p=new Action("p",new Boolean(true),true);
		Action m=new Action("m",new Boolean(true),true);
		Action u=new Action("u",new Boolean(true),true);
		Rule p1=new SoftPreRule((Parameter)c, (Parameter)p);
		Rule p2=new SoftPreRule((Parameter)m, (Parameter)u);
		
		Graph g=new Graph();g.setRoot(new Node((Object)"root"));
		Action.insert(c,g);
		Action.insert(p,g);
		Action.insert(m,g);
		Action.insert(u,g);
		p1.insert(g);
		p2.insert(g); 
		OrderTraverser ot=new OrderTraverser();
		LinkedList multiOrder=ot.multiTraverse(g);
		//System.out.println("<<multiple-orders>>");
		//for (Iterator j=multiOrder.iterator();j.hasNext();){
		//	for (Iterator i=((LinkedList)j.next()).iterator();i.hasNext();)
		//		System.out.println(((Node)i.next()).getElement());
			//System.out.println("/n-----------");
		//}

	}

	//test for providing all the possible orders (without any ordering spec.)
	public void test9(){
		Action c=new Action("c",new Boolean(true),true);
		Action p=new Action("p",new Boolean(true),true);
		Action m=new Action("m",new Boolean(true),true);
//		Action u=new Action("u",new Boolean(true),true);
//		Rule p1=new SoftPreRule((Parameter)c, (Parameter)p);
//		Rule p2=new SoftPreRule((Parameter)m, (Parameter)u);
		
		Graph g=new Graph();g.setRoot(new Node((Object)"root"));
		Action.insert(c,g);
		Action.insert(p,g);
		Action.insert(m,g);
//		Action.insert(u,g);
//		p1.insert(g);
//		p2.insert(g); 
		OrderTraverser ot=new OrderTraverser();
		LinkedList multiOrder=ot.multiTraverse(g);
		//System.out.println("<<multiple-orders>>");
		//for (Iterator j=multiOrder.iterator();j.hasNext();){
		//	for (Iterator i=((LinkedList)j.next()).iterator();i.hasNext();)
		//		System.out.println(((Node)i.next()).getElement());
		//	System.out.println("/n-----------");
		//}

	}

	//test for providing all the possible orders (with CYCLE)
	public void test10(){
		Action c=new Action("c",new Boolean(true),true);
		Action p=new Action("p",new Boolean(true),true);
		Action m=new Action("m",new Boolean(true),true);
		Action u=new Action("u",new Boolean(true),true);
		Rule p1=new SoftPreRule((Parameter)c, (Parameter)p);
		Rule p2=new SoftPreRule((Parameter)m, (Parameter)u);
		Rule p3=new SoftPreRule((Parameter)u, (Parameter)m); //cycle
		
		Graph g=new Graph();g.setRoot(new Node((Object)"root"));
		Action.insert(c,g);
		Action.insert(p,g);
		Action.insert(m,g);
		Action.insert(u,g);
		p1.insert(g);
		p2.insert(g); 
		p3.insert(g); 
		OrderTraverser ot=new OrderTraverser();
		LinkedList multiOrder=ot.multiTraverse(g);
		//System.out.println("<<multiple-orders>>");
		//for (Iterator j=multiOrder.iterator();j.hasNext();){
		//	for (Iterator i=((LinkedList)j.next()).iterator();i.hasNext();)
		//		System.out.println(((Node)i.next()).getElement());
		//	System.out.println("/n-----------");
		//}

	}
	public void test11(){

		Graph g=new Graph();g.setRoot(new Node((Object)"root"));
		/* TODO: augment the active filtermodules in memory and in the graph */
		
		/* process XML specification */
		try{
			XMLReader xr = XMLReaderFactory.createXMLReader();		
			ConstraintFilter of = new ConstraintFilter(g);
			of.setParent(xr);
			FileReader r = new FileReader("XMLTest.xml");
			of.parse(new InputSource(r));
		}catch (Exception e){
			e.getStackTrace();
		}
		/* process XML specification */

		OrderTraverser ot=new OrderTraverser();
		LinkedList multiOrder=ot.multiTraverse(g);
		//System.out.println("<<multiple-orders>>");
		//for (Iterator j=multiOrder.iterator();j.hasNext();){
		//	for (Iterator i=((LinkedList)j.next()).iterator();i.hasNext();)
		//		System.out.println(((Node)i.next()).getElement());
		//	System.out.println("/n-----------");
		//}

	}
	public void test12(){
		Graph g=new Graph();g.setRoot(new Node((Object)"root"));
		
		Action a=new Action("a", new Boolean(true), true);		
		Action.insert(a,g);
		
		//System.out.println(Action.lookupByName("a",g));
		
	}
	
	public static void main(String[] args) {
		Run r=new Run();
		r.test11();
	}
}
