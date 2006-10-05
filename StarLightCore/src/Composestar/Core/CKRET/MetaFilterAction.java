/*
 * Created on Dec 9, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package Composestar.Core.CKRET;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import Composestar.Core.LAMA.*;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Utils.*;
import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.PlatformRepresentation;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.External;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Filter;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterType;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Internal;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.SubstitutionPart;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.DeclaredObjectReference;
import Composestar.Core.Exception.ModuleException;

/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class MetaFilterAction extends FilterAction {

	private Map metaSemantics;

	public MetaFilterAction() throws ModuleException
	{
		super("meta");
		metaSemantics = new HashMap();
		this.fetchAnnotations();
	}

	private void fetchAnnotations() throws ModuleException
	{
		Pattern grammer = Pattern.compile("[a-zA-Z]+\\.[a-zA-Z]+(,[a-zA-Z]+\\.[a-zA-Z]+)*");
		DataStore datastore = DataStore.instance();
		for( Iterator it = datastore.getAllInstancesOf(Filter.class); it.hasNext(); )
		{
			Filter filter = (Filter) it.next();
			if( !filter.getFilterType().getType().equals(FilterType.META) )
				continue;
			
			Concern target = null;
			SubstitutionPart sp = (SubstitutionPart) filter.getFilterElement(0).getMatchingPattern(0).getSubstitutionParts().firstElement();
			DeclaredObjectReference dor = (DeclaredObjectReference) sp.getTarget().getRef();
			Object o = dor.getRef();
			if( o instanceof Internal )
			{
				target = ((Internal) o).getType().getRef();
			}
			else if( o instanceof External )
			{
				target = ((External) o).getType().getRef();
			}
			
			if( target != null )
			{
				PlatformRepresentation pr = target.getPlatformRepresentation();
				if( pr != null && pr instanceof Type )
				{
					Type dnt = (Type) pr;
					String selector = sp.getSelector().getName();
					
					String[] params = {"Composestar.RuntimeCore.FLIRT.Message.ReifiedMessage"};
					MethodInfo method = dnt.getMethod(selector,params);
					if( method != null )
					{
						List attributes = method.getAnnotations();
						for( int i = 0; i < attributes.size(); i++ )
						{
							Annotation dna = (Annotation) attributes.get(i);
							if( dna.getType().fullName().startsWith("Composestar.") &&
									dna.getType().fullName().endsWith("Semantics"))
							{
								//System.err.println(dna.getValue());
								String spec = dna.getValue().replaceAll("\"","");
								if( !grammer.matcher(spec).matches() )
								{
									MethodInfo dnmi = ((MethodInfo)dna.getTarget());
									String fullMethodName = dnmi.parent().m_fullName+ '.' + dnmi.name();
									throw new ModuleException("Error in annotation semantics of method " + fullMethodName + " for filter " + filter.getQualifiedName(),"SECRET");
								}
								StringTokenizer st = new StringTokenizer(dna.getValue().replaceAll("\"",""),",");
								List metaOperations = new ArrayList();
								
								while( st.hasMoreTokens() )
								{
									String token = st.nextToken();
									StringTokenizer ost = new StringTokenizer(token ,".()");
									//try
									//{
									String resource = ost.nextToken();
									String operation  = ost.nextToken();
									if( ost.hasMoreTokens() )
									{
										String argument = ost.nextToken();
									}
									metaOperations.add(new Operation(operation,resource));
									//}
									//catch(Exception e)
									//{
									//	throw new ModuleException("SECRET","Error in annotation semantics of filter " + filter.getQualifiedName());
									//}
								}
								
								// TODO: this overrides a previously found scenario for the same filter
								//       which should obviously never happen...
								this.metaSemantics.put(filter, metaOperations);
							}
						}
						Iterator ReifiedMessageBehaviour = method.getReifiedMessageBehavior().iterator() ;
						while (ReifiedMessageBehaviour.hasNext())
						{
							String refMes = (String)(ReifiedMessageBehaviour.next());
							
							StringTokenizer st = new StringTokenizer(refMes.replaceAll("\"",""),",");
							
							List metaOperations = new ArrayList();
							
							while( st.hasMoreTokens() )
							{
								String token = st.nextToken();
								StringTokenizer ost = new StringTokenizer(token ,".()");
								//try
								//{
								String resource = ost.nextToken();
								String operation  = ost.nextToken();
								if( ost.hasMoreTokens() )
								{
									String argument = ost.nextToken();
								}
								metaOperations.add(new Operation(operation,resource));
								//}
								//catch(Exception e)
								//{
								//	throw new ModuleException("SECRET","Error in annotation semantics of filter " + filter.getQualifiedName());
								//}
							}
							
							// TODO: this overrides a previously found scenario for the same filter
							//       which should obviously never happen...
							this.metaSemantics.put(filter, metaOperations);
						}
					}
				}
			}
		}
	}
	

	public List getOperations(Filter filter)
	{
		List l = new ArrayList(super.getOperations(filter));
	
		List ml = (List) metaSemantics.get(filter);
		if( ml != null )
		{
			l.addAll(ml);			
		}
		return l;
	}
}
