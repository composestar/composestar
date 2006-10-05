package Composestar.Core.FIRE;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 * 
 * $Id$
 * 
**/

import java.util.HashSet;
import Composestar.Core.LAMA.*;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.MethodWrapper;
import Composestar.Core.CpsProgramRepository.PlatformRepresentation;
import Composestar.Core.CpsProgramRepository.Signature;

public class SignatureActionNode extends ActionNode
{
	protected Symbol signature = null;

	public SignatureActionNode (Symbol sig)
	{
		signature = sig;
	}

	public Node getChild (boolean truthValue)
	{
		if (numberOfChildren() == 0) return null;

		if (truthValue || numberOfChildren() == 1) return getChild(0);
		else return getChild(1);
	}

	public String toString ()
	{
		return "SignatureAction: " + signature + '[' + getTarget() + '.' + getSelector() + ']';
	}

	protected boolean subsetOfSingle (Node rhs)
	{
	/*
		if (!super.subsetOfSingle(rhs))	return false;
		
		if (rhs instanceof SignatureActionNode)	
			return (((SignatureActionNode)rhs).signature == signature);
			
		return true;
		*/
		
		return (super.subsetOfSingle(rhs) && 
				rhs instanceof SignatureActionNode &&
                ((SignatureActionNode) rhs).signature.equals(signature));
	}


	public void sort ()
	{
		// I do not sort my children.
		// but I do sort my grandchildren

		for (int i = 0; i < numberOfChildren(); i++)
		{
			getChild(i).sort();
		}
	}

	/*
	public boolean updateSignatures()
	{
		boolean result = false;
		//System.out.println ("Looking for signature: " + signature + " with selector: " + getSelector());
		
		DataStore ds = DataStore.instance();
		Concern c = (Concern) ds.getObjectByID(signature.getName());
		
		if (c != null)
		{
			Signature sig = c.getSignature();
			if (sig.hasMethod(getSelector().getName()))
			{			
				//System.out.println ("I got the signature");
				int id = parentUsesId();
				parent.replaceChild(id, getChild(true));
				result = true;
			} 
			else
			{
				//System.out.println ("Noooo signature for me :-(");
			}
					
		}
		
		result |= super.updateSignatures();
		return result;
	}
	*/

	public int search (HashSet dependencies, Node match, String selector, String concernName)
	{	
		if (numberOfChildren() != 2)
		{
			return super.search(dependencies, match, selector, concernName);
		}
	
		// Pfff search in depth.
		
		// isSignatureMatch ? 
		// true => Path 1.
		// false => Path 2.
		// Unknown => both paths. (if true or unknown return unknown.) false = false.
		
		int result = isSignatureMatch(dependencies, selector, concernName);
		
		if (result == FilterReasoningEngine.UNKNOWN)
		{
			// We try both paths. If they return both false then we are safe.
			
			for (int i = 0; i < numberOfChildren(); i++)
			{
				int localResult = getChild(i).search(dependencies, match, selector, concernName);
				
				// unknown => return.
				if (localResult == FilterReasoningEngine.UNKNOWN)				
					return localResult;
				
				// true -> add dependency, return.
				if (localResult == FilterReasoningEngine.TRUE)
				{
					// add dependency.
					dependencies.add(getDependentConcern(concernName));
					return localResult;
				}
			}

			// It's your lucky day. Both paths returned false. 
			return FilterReasoningEngine.FALSE;
		}
			
		// We know the path. Please continue.
		boolean boolResult = result == FilterReasoningEngine.TRUE;
		return getChild(boolResult).search(dependencies, match, selector, concernName);
	}
	
	public Concern getDependentConcern(String concernName)
	{
		String signatureName = signature.getName();
	
		if (signatureName.equals("inner")) 
		{
			signatureName = concernName;		
		}
		else
		{
			// TODO, can have multiple signatures?
			signatureName = getFIREInfo().getConcern(signatureName);
		}
		
		DataStore ds = DataStore.instance();
		
		Concern concern = (Concern) ds.getObjectByID(signatureName);
		if (concern == null)
		{
			System.err.println ("Cannot find the concern with the ID: " + signatureName + " in the DataStore");
			System.exit(-1);
		}
		
		
		return concern;
	}
	
	public int isSignatureMatch(HashSet dependencies, String selector, String concernName)
	{
		//System.out.println ("Looking for signature: " + signature + " with selector: " + selector);
		
		if (signature.getName().equals("*")) return FilterReasoningEngine.TRUE;
		
		Concern c = getDependentConcern(concernName);
		
		if( signature.getName().equals("inner"))
		{
			PlatformRepresentation pr = c.getPlatformRepresentation();
			if( pr instanceof Type )
			{
				Type type = (Type) pr;
				for( java.util.Iterator i = type.getMethods().iterator(); i.hasNext(); )
				{
					MethodInfo m = (MethodInfo) i.next();
					if( m.name().equals(selector) )
						return FilterReasoningEngine.TRUE;
				}
			}
			return FilterReasoningEngine.FALSE;
		}
		
		try 
		{		
			Signature sig = c.getSignature();
			
			switch (sig.getMethodStatus(selector))
			{
				case MethodWrapper.REMOVED: return FilterReasoningEngine.FALSE; 
				case MethodWrapper.UNKNOWN:
					if (c.getName().equals(concernName)) return FilterReasoningEngine.TRUE; // Checking our selves.
					return FilterReasoningEngine.UNKNOWN;
				
				default:
					return MethodWrapper.ADDED;
			}
		} catch (Exception e)
		{
			System.err.println("Cannot resolve dependency ");
			e.printStackTrace();
		}
		
		return FilterReasoningEngine.UNKNOWN;
	}

}
