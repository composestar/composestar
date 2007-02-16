/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2006 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id: Message.java 2659 2006-11-12 21:30:22Z elmuerte $
 */

package Composestar.Core.DIGGER.Walker;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.MatchingPart;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.SignatureMatchingType;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Target;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.ConcernReference;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.DeclaredObjectReference;
import Composestar.Core.DIGGER.NOBBIN;
import Composestar.Core.DIGGER.Graph.AbstractConcernNode;
import Composestar.Core.DIGGER.Graph.CondMatchEdge;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.LAMA.MethodInfo;
import Composestar.Core.LAMA.Type;
import Composestar.Core.RepositoryImplementation.RepositoryEntity;
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
	 * Indication of the certainty of the message. < 0 message will probably
	 * never be executed. = 0 message will be executed. > 0 this number of
	 * conditions must be valid.
	 */
	protected int certainty;

	/**
	 * If true this message is recursive
	 */
	protected boolean recursive;

	/**
	 * Will be set during substitution to the FilterElement
	 */
	protected RepositoryEntity re;

	/**
	 * List of cloned messages
	 */
	protected List clones;

	/**
	 * List of resulting messages. Is usually one except when a message list or
	 * alike is created.
	 */
	protected List results;

	/**
	 * Don't call this constructor directly. Use
	 * MessageGenerator.getMessageFor()
	 * 
	 * @param inConcernNode
	 * @param inSelector
	 */
	public Message(AbstractConcernNode inConcernNode, String inSelector)
	{
		concernNode = inConcernNode;
		selector = inSelector;
		clones = new ArrayList();
		results = new ArrayList();
	}

	/**
	 * Don't call this constructor directly. Use MessageGenerator.cloneMessage()
	 * 
	 * @param base
	 */
	public Message(Message base)
	{
		concernNode = base.getConcernNode();
		selector = base.getSelector();
		certainty = base.getCertainty();
		re = base.getRE();
		clones = new ArrayList();
		results = new ArrayList();
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

	public void setCertainty(int inval)
	{
		certainty = inval;
	}

	public int getCertainty()
	{
		return certainty;
	}

	public boolean isRecursive()
	{
		return recursive;
	}

	public void setRecursive(boolean inval)
	{
		recursive = inval;
	}

	public void setRE(RepositoryEntity inRe)
	{
		re = inRe;
	}

	public RepositoryEntity getRE()
	{
		return re;
	}

	public void addClone(Message clone)
	{
		clones.add(clone);
	}

	public Iterator getClones()
	{
		return clones.iterator();
	}

	public void addResult(Message res)
	{
		// to filter out duplicated
		if (!results.contains(res)) results.add(res);
	}

	public Iterator getResults()
	{
		return results.iterator();
	}

	/**
	 * Returns true when this message matches
	 * 
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
				else if (matchesName(mp))
				{
					res = true;
					break;
				}
			}
		}
		else
		{
			// TODO: check for message list matching?
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
	 * 
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
	 * 
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
		if (mp.getTarget().getName().equals(Target.INNER) || mp.getTarget().getName().equals("*"))
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
				throw new ModuleException("Unresolved internal/external: " + mp.getTarget().getName(),
						NOBBIN.MODULE_NAME);
			}
		}
		Iterator it = type.getMethods().iterator();
		for (Object o : type.getMethods())
		{
			MethodInfo mi = (MethodInfo) o;
			if (mi.getName().equals(selector))
			{
				return true;
			}
		}
		return false;
	}

	public String toString()
	{
		return concernNode.getLabel() + "->" + selector + " [" + certainty + "]";
	}

	public int hashCode()
	{
		return concernNode.hashCode() + selector.hashCode();
	}

	public boolean equals(Object obj)
	{
		if (obj == null)
		{
			return false;
		}
		if (!(obj instanceof Message))
		{
			return false;
		}
		Message msg = (Message) obj;
		return (msg.getConcernNode() == concernNode) && msg.getSelector().equals(selector);
	}
}