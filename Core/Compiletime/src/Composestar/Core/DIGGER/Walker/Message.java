/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2006 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */

package Composestar.Core.DIGGER.Walker;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.MatchingPart;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.SignatureMatchingType;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.ConcernReference;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.DeclaredObjectReference;
import Composestar.Core.DIGGER.NOBBIN;
import Composestar.Core.DIGGER.Graph.AbstractConcernNode;
import Composestar.Core.DIGGER.Graph.CondMatchEdge;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.LAMA.MethodInfo;
import Composestar.Core.LAMA.Type;
import Composestar.Core.RepositoryImplementation.TypedDeclaration;

/**
 * Message used during path walking
 * 
 * @author Michiel Hendriks
 */
public class Message
{
	protected AbstractConcernNode concernNode; 
	/**
	 * The selector of the message
	 */
	protected String selector;

	/**
	 * Where did this message come from. Will be NULL in case of initial
	 * messages. But can be set in a later stage when this message is
	 * encountered again.
	 */
	protected List messageTrace;

	/**
	 * Indication of the certenty of the message. < 0 message will probably
	 * never be executed. = 0 message will be executed. > 0 this number of
	 * conditions must be valid.
	 */
	protected int certenty;
	
	/**
	 * If true this message is recursive
	 */
	protected boolean recursive;
	
	public Message(AbstractConcernNode inConcernNode, String inSelector)
	{
		concernNode = inConcernNode;
		selector = inSelector;
		messageTrace = new ArrayList();
	}
	
	public Message(Message base)
	{
		concernNode = base.getConcernNode();
		selector = base.getSelector();
		certenty = base.getCertenty();
		messageTrace = new ArrayList();
		messageTrace.addAll(base.getMessageTrace());
	}
	
	public AbstractConcernNode getConcernNode()
	{
		return concernNode;
	}
	
	public Concern getConcern()
	{
		return concernNode.getConcern();
	}
	
	public void setConcernNode(AbstractConcernNode innode)
	{
		concernNode = innode;		
	}
	
	public String getSelector()
	{
		return selector;
	}
	
	public void setSelector(String inval)
	{
		selector = inval;
	}
	
	public void setCertenty(int inval)
	{
		certenty = inval;
	}
	
	public int getCertenty()
	{
		return certenty;
	}
		
	public List getMessageTrace()
	{
		return messageTrace;
	}
	
	public void addTrace(Message msg)
	{
		messageTrace.add(msg);
	}
	
	public boolean isRecursive()
	{
		return recursive;
	}
	
	public void setRecursive(boolean inval)
	{
		recursive = inval;
	}
	
	/**
	 * Returns true when this message matches
	 * @param edge
	 * @return
	 */
	public boolean matches(CondMatchEdge edge) throws ModuleException
	{
		boolean res = false;
		if (!edge.getIsMessageList())
		{
			Iterator it = edge.getMatchingParts();
			while (it.hasNext())
			{
				MatchingPart mp = (MatchingPart) it.next();				
				
				if (mp.getMatchType() instanceof SignatureMatchingType)
				{
					if (matchesSignature(mp))
					{
						res = true;
						break;
					}
				}
				else if (matchesName(mp)) {
					res = true;
					break;
				}
			}
		}
		else
		{
			//TODO: ...
		}
		if (edge.getEnabler())
		{
			return res;
		}
		else
		{
			return !res;
		}
	}
	
	/**
	 * Check for a normal name match
	 * @param mp
	 * @return
	 */
	protected boolean matchesName(MatchingPart mp)
	{
		String mpselector = mp.getSelector().getName();
		return mpselector.equals(selector) || mpselector.equals("*");
	}
	
	/**
	 * Check for signature matching
	 * @param mp
	 * @return
	 */
	protected boolean matchesSignature(MatchingPart mp) throws ModuleException
	{
		Type type = null;
		String mpselector = mp.getSelector().getName();
		// same names, should match (right?)
		if (mpselector.equals(selector))
		{
			return true;
		}
		// otherwise look it up
		if (mp.getTarget().getName().equals("inner") || mp.getTarget().getName().equals("*"))
		{
			type = (Type) concernNode.getConcern().getPlatformRepresentation();
		}
		else 
		{
			DeclaredObjectReference ref = (DeclaredObjectReference) mp.getTarget().getRef();
			if ((ref != null) && ref.getResolved())
			{
				TypedDeclaration typeDecl = ref.getRef();
				ConcernReference concernRef = typeDecl.getType();
				type = (Type) concernRef.getRef().getPlatformRepresentation();
			}
			else
			{
				throw new ModuleException("Unresolved internal/external: " + mp.getTarget().getName(), NOBBIN.MODULE_NAME);
			}
		}
		Iterator it = type.getMethods().iterator();
		while (it.hasNext())
		{
			MethodInfo mi = (MethodInfo) it.next();
			if (mi.name().equals(selector))
			{
				return true;
			}
		}
		return false;
	}
	
	public void checkRecursion()
	{
		if (recursive) return;
		// TODO: easier to check if the same message is listed
		Iterator it = messageTrace.iterator();
		while (it.hasNext())
		{
			Message msg = (Message) it.next();
			if ((msg.getConcernNode() == concernNode) && (msg.getSelector().equals(selector)))
			{
				recursive = true;
				return;
			}
		}
	}
	
	public String toString()
	{
		return concernNode.getLabel()+"->"+selector+" ["+certenty+"]";
	}
}
