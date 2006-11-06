package Composestar.DotNET.SEMTEX;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.LAMA.CallToOtherMethod;
import Composestar.Core.LAMA.MethodInfo;
import Composestar.Core.LAMA.ResourceUsage;
import Composestar.Core.LAMA.Type;
import Composestar.Core.RepositoryImplementation.DataStore;

/**
 * Extracts the semtex information from the XML file and stores it in the
 * repository.
 * 
 * @author Michiel van Oudheusden
 */
public class SemanticContainerHandler extends DefaultHandler implements ContentHandler
{
	private String SemanticContainerName = "";

	private String SemanticClassName = "";

	private String SemanticMethodName = "";

	private HashSet callsToOtherMethods = new HashSet();

	private HashSet resourceUsages = new HashSet();

	private HashSet reifiedMessageBehaviour = new HashSet();

	private DataStore datastore;

	public SemanticContainerHandler()
	{
		// Get a local instance of the datastore
		datastore = DataStore.instance();
	}

	public void startElement(String uri, String local_name, String raw_name, Attributes amap) throws SAXException
	{
		if ("SemanticContainer".equals(raw_name))
		{
			if (!amap.getValue("name").equals(SemanticContainerName))
			{
				// New semantic container, save the previous one
				AddSemTexInfo();
			}
			SemanticContainerName = amap.getValue("name");
		}
		else if ("SemanticClass".equals(raw_name))
		{
			if (!amap.getValue("name").equals(SemanticClassName))
			{
				// New semantic class, save the previous one
				AddSemTexInfo();
			}
			SemanticClassName = amap.getValue("name");
		}
		else if ("SemanticMethod".equals(raw_name))
		{
			if (!amap.getValue("name").equals(SemanticMethodName))
			{
				// New semantic method, save the previous one
				AddSemTexInfo();
			}
			SemanticMethodName = amap.getValue("name");
		}
		else if ("Call".equals(raw_name))
		{
			String operationName = amap.getValue("operationName");
			String className = amap.getValue("className");

			CallToOtherMethod ctom = new CallToOtherMethod();
			ctom.OperationName = operationName;
			ctom.ClassName = className;

			callsToOtherMethods.add(ctom);
		}
		else if ("ResourceUsage".equals(raw_name))
		{
			String resourceName = amap.getValue("name");
			String accessType = amap.getValue("accessType");
			String accessOccurence = amap.getValue("accessOccurence");

			ResourceUsage ru = new ResourceUsage();
			ru.ResourceName = resourceName;
			ru.AccessType = accessType;
			ru.AccessOccurence = accessOccurence;

			resourceUsages.add(ru);
		}
		else if ("Semantic".equals(raw_name))
		{
			String v = amap.getValue("value");

			reifiedMessageBehaviour.add(v);
		}
	}

	/**
	 * Add the collected information to the repository.
	 */
	private void AddSemTexInfo()
	{
		// Check if we have all the data, if not, quit the method
		if (SemanticContainerName.length() == 0 || SemanticClassName.length() == 0 || SemanticMethodName.length() == 0) return;

		// Get the MethodInfo from the repository
		MethodInfo mi = getMethodLocation(SemanticMethodName, SemanticClassName);

		// It is possible the method is not in the datastore
		if (mi != null)
		{
			mi.getCallsToOtherMethods().addAll(callsToOtherMethods);
			mi.getReifiedMessageBehavior().addAll(reifiedMessageBehaviour);
			mi.getResourceUsage().addAll(resourceUsages);
		}

		// Clear the data for the next store action
		ClearCollectedInfo();
	}

	/**
	 * Clears the collected info so a new method can be analysed.
	 */
	private void ClearCollectedInfo()
	{
		reifiedMessageBehaviour.clear();
		callsToOtherMethods.clear();
		resourceUsages.clear();
	}

	public Type getTypeLocation(String location)
	{
		Concern c = (Concern) datastore.getObjectByID(location);
		if (c != null && c.getPlatformRepresentation() instanceof Type)
		{
			return (Type) c.getPlatformRepresentation();
		}
		return null;
	}

	public MethodInfo getMethodLocation(String methodName, String typeName)
	{
		Type type = getTypeLocation(typeName);

		if (type != null)
		{
			List methods = type.getMethods();
			Iterator i = methods.iterator();
			while (i.hasNext())
			{
				MethodInfo method = (MethodInfo) i.next();
				if (method.name().equals(methodName)) return method;
			}
		}
		return null;
	}

	public void endElement(String uri, String local_name, String raw_name) throws SAXException
	{}

	public void startDocument()
	{}

	public void endDocument()
	{
		// Save the last information. Have to do this in endDocument
		// since the StartElement is not triggered anymore after the last
		// container.
		AddSemTexInfo();
	}
}
