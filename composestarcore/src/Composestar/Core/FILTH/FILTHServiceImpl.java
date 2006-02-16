/*
 * Created on Mar 16, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package Composestar.Core.FILTH;

/**
 * @author Isti
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.FilterModuleReference;
import Composestar.Utils.*;
import Composestar.Core.FILTH.Core.Action;
import Composestar.Core.FILTH.Core.Graph;
import Composestar.Core.FILTH.Core.Node;
import Composestar.Core.FILTH.Core.OrderTraverser;
import Composestar.Core.FILTH.XMLSpecification.ConstraintFilter;
import Composestar.Core.INCRE.INCRE;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Core.SANE.FilterModSIinfo;
import Composestar.Core.SANE.SIinfo;

public class FILTHServiceImpl extends FILTHService{
	private DataStore _ds;
	private String _specfile;
	private CommonResources _cr;
		
	protected FILTHServiceImpl(CommonResources cr){
		Properties props = (Properties)DataStore.instance().getObjectByID("config");
		_specfile = props.getProperty("FILTH_INPUT");
		_ds = (DataStore)cr.getResource("TheRepository");
		
		try
		{
			this.setLog(cr.ProjectConfiguration.getProperty("TempFolder") + "filth.html");
		} catch(Exception e)
		{
		}
	}
	
	public List getOrder(Concern c){
		FilterModuleOrder fo = (FilterModuleOrder)c.getDynObject("SingleOrder");
		if (fo==null)
			return new LinkedList();
		//	getMultipleOrder(c);
			// TODO: calling getMultipleOrder(c) generates lot's of exceptions for CONE-IS (filenotfound & nullpointer)
		
		return ((FilterModuleOrder)c.getDynObject("SingleOrder")).orderAsList(); 
	}
	
	public List getMultipleOrder(Concern c){ 
		try{					
			Properties props = (Properties)DataStore.instance().getObjectByID("config");
			String filthoutput = props.getProperty("FILTH_output_pattern");

			FILTHService.setLog(new PrintStream(new FileOutputStream(filthoutput+c.getName()+".html")));
		}catch(Exception e){
			e.getMessage();
			//e.printStackTrace();
		}
			 
		FILTHService.log.print("<html>\n");
		FILTHService.log.print("\t<head>\n");
		FILTHService.log.print("\t\t<title>Filter Composition and Checking report</title>\n");
		FILTHService.log.print("<link id=\"css_color\" rel=\"stylesheet\" type=\"text/css\" href=\"blackvoid.css\"/>\n");
		FILTHService.log.print("</head>\n");
		FILTHService.log.print("<body>\n");
		FILTHService.log.print("<div align=\"center\">\n");
		FILTHService.log.print("<TABLE width=\"100%\" bgcolor=\"lightblue\" border=\"0\">\n");
		FILTHService.log.print("<div id=\"headerbox\" class=\"headerbox\"><h2><a name=\"_TRESE_Compose_\"> <img src=\"./logo.gif\"/>  /TRESE/Compose*/FILTH</a></h2></div>\n");
		FILTHService.log.print("<h3>Report generated on:  "+new Date().toString()+"</h3>\n");

		FILTHService.print("\n");

		FILTHService.print("<---FILTH::BEGIN::Concern{"+c.getName()+"}--->\n");
		LinkedList forders = new LinkedList();
		
		LinkedList modulrefs;
		
		Graph g=new Graph(); g.setRoot(new Node((Object)"root"));

		modulrefs = processModules(c,g);
		processXML(c,g);
		
		FILTHService.print("FILTH::generating alternatives\n");
		OrderTraverser ot=new OrderTraverser();
		LinkedList orders=ot.multiTraverse(g);
		/*
		System.out.println("<<INNER multiple-orders>>");
					for (Iterator j=orders.iterator();j.hasNext();){
						for (Iterator i=((LinkedList)j.next()).iterator();i.hasNext();)
							System.out.println(((Node)i.next()).getElement());
						System.out.println("/n-----------");
					}
		
		*/		
		FilterModuleReference fr=null;
		for (Iterator k=orders.iterator();k.hasNext();){
			Iterator i=((List)k.next()).iterator();
			/* skip the root */
			i.next();
			LinkedList anOrder = new LinkedList();
			while (i.hasNext()){
				Action a =(Action)((Node)i.next()).getElement();

				for (Iterator j=modulrefs.iterator();j.hasNext();){
					fr = ((FilterModuleReference)j.next()); 				
					//System.out.println("FILTH ordering>>>"+a+"::"+fr.getName() ); 
					if (a.getName().equals(fr.getName()))
						break;
					
				}				
				anOrder.addLast(fr);	
				//System.out.println(count++);
			}
			anOrder.addLast(InnerDispatcher.getInnerDispatchReference());
			forders.addLast(anOrder);
		}
		
		/* DEBUG info about the orders */
		int alt= 1;
		
		for (Iterator i=forders.iterator();i.hasNext();){
			FILTHService.printTab(3,"<-- Alternative ("+ alt++ +") -->\n");
			for (Iterator j=((List)i.next()).iterator();j.hasNext();){
				FILTHService.printTab(6,""+((FilterModuleReference)j.next()).getName()+"\n");
			}
			FILTHService.printTab(3,"<--->\n");
			
		}
		/* DEBUG info end */
		
		/*** attaching all orders to the concern in DataStore */
		c.addDynObject("FilterModuleOrders",forders);
		
		/* attaching the first order with different key to be dumped in the repository
		 * the list is encapsulated in FilterModuleOrder for the XML generator */  
		FilterModuleOrder fmorder = new FilterModuleOrder( (LinkedList)forders.getFirst());
		c.addDynObject("SingleOrder",fmorder);

		
		FILTHService.print("FILTH::order (1) added to the repository {"+c.getName()+"}\n");
		FILTHService.print("<---FILTH::END--->\n");

		FILTHService.log.print("</body>\n");

		FILTHService.log.flush(); 
		FILTHService.log.close();		

		return forders; //arrange this according to the output required!!
	
	}
	
	private LinkedList processModules(Concern c, Graph g){
		LinkedList modulerefs = new LinkedList();
		
		/* get the superimposition from the repository */
		SIinfo sinfo = (SIinfo)c.getDynObject("superImpInfo");
		/* get the firt altnernative */
		Vector msalts = sinfo.getFilterModSIAlts();
		
		/*
		System.out.print(">>>"+c.getName());
		for (Iterator i=msalts.iterator();i.hasNext();)
			System.out.println("@@@@"+i.next());
		*/
		/* get the vector of the superimposed filtermodules */
		FilterModSIinfo fmsi = (FilterModSIinfo)msalts.get(0);
		
		/* add the name of each filtermodule into the graph */
		for (Iterator i=fmsi.getIter();i.hasNext();){
			FilterModuleReference fr = (FilterModuleReference)i.next();
			Action a = new Action(fr.getName(), new Boolean(true),true);
			Action.insert(a,g);
			
			FILTHService.print("FILTH::adding module> "+fr.getName()+"|");
				
			modulerefs.add(fr);
		}
		return modulerefs;
	}
	
	private void processXML(Concern c, Graph g){
		
		/* process XML specification, build the rules into the graph */
		try{
			XMLReader xr = XMLReaderFactory.createXMLReader();		
			ConstraintFilter of = new ConstraintFilter(g);
			of.setParent(xr);
			//System.out.println(_specfile);
			FileReader r = new FileReader(_specfile);
			of.parse(new InputSource(r));

		}catch (SAXException se){
			Debug.out(Debug.MODE_WARNING, "FILTH", "xml parsing problem");
			se.printStackTrace();
		}catch (Exception ioe){
			Debug.out(Debug.MODE_WARNING, "FILTH", "Order specification (" + _specfile + ") not found. ",c.getName());
		}
		
	}
	/* 
	public List getOrder(CpsConcern c){

		LinkedList forder = new LinkedList();
		LinkedList modulrefs;
		
		Graph g=new Graph(); g.setRoot(new Node((Object)"root"));

		modulrefs = processModules(c,g);
		processXML(c,g);
		
		OrderTraverser ot=new OrderTraverser();
		LinkedList order=ot.traverse(g);
		
		Action a;Iterator i=order.iterator();

		i.next();
		while (i.hasNext()){
			a =(Action)((Node)i.next()).getElement();
			for (Iterator j=modulrefs.iterator();j.hasNext();){
				FilterModuleReference fr = ((FilterModuleReference)j.next()); 				
				System.out.println("FILTH ordering>>>"+a+"::"+fr.getName() ); 
				if (a.getName().equals(fr.getName()))
					forder.addLast(fr);	
			}				
			
		} 
		
		return forder; 
	}
	*/
	
	public void copyOperation(Concern c,INCRE inc){
		/* Copy dynamic objects 'FilterModuleOrders' and 'SingleOrder' */	
		Concern cop = (Concern)inc.findHistoryObject(c);
		
		LinkedList forders = (LinkedList)cop.getDynObject("FilterModuleOrders");
		
		c.addDynObject("FilterModuleOrders",forders);
		FilterModuleOrder fmorder = new FilterModuleOrder( (LinkedList)forders.getFirst());
		c.addDynObject("SingleOrder",fmorder);
	}
}
