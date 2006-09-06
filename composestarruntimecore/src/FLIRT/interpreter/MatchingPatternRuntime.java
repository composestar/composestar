package Composestar.RuntimeCore.FLIRT.Interpreter;

import Composestar.RuntimeCore.FLIRT.Message.Message;
import Composestar.RuntimeCore.FLIRT.Message.MessageList;
import Composestar.RuntimeCore.FLIRT.MessageHandlingFacility;
import Composestar.RuntimeCore.Utils.Debug;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.*;

import java.util.Dictionary;
import java.util.ArrayList;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 * $Id: MatchingPatternRuntime.java,v 1.3 2006/09/05 12:31:01 doornenbal Exp $
 */
public class MatchingPatternRuntime extends ReferenceEntityRuntime implements Interpretable 
{
    //public MatchingPartRuntime theMatchingPartRuntime;
    //public SubstitutionPartRuntime theSubstitutionPartRuntime;
	public ArrayList matchingParts, substitutionParts;
	public FilterElementRuntime theFilterElement;
    
    /**
     * @roseuid 40DD68840299
     */
    public MatchingPatternRuntime() {
		matchingParts = new ArrayList();
		substitutionParts = new ArrayList();
    }
    
    /**
     * @param m
     * @param context
     * @return boolean
     * @roseuid 40DD688402A3
     */
    public boolean interpret(MessageList m, Dictionary context) {
		// TODO WM: Iterate over messagelist here
		// temporary
		/*
		MatchingPartRuntime theMatchingPartRuntime = null;
		if( matchingParts.size() > 0 )
			theMatchingPartRuntime = (MatchingPartRuntime) matchingParts.get( 0 );
		SubstitutionPartRuntime theSubstitutionPartRuntime = null;
		if( substitutionParts.size() > 0 )
			theSubstitutionPartRuntime = (SubstitutionPartRuntime) substitutionParts.get( 0 );

		
		if(Debug.SHOULD_DEBUG) Debug.out(Debug.MODE_INFORMATION,"FLIRT","\t\t\tInterpreting matchingPattern '"+theSubstitutionPartRuntime.getReference()+"'..." );
		boolean returnvalue = theMatchingPartRuntime.interpret(m,context);
		if(returnvalue && theSubstitutionPartRuntime != null && theSubstitutionPartRuntime.getReference() != null)
		{
			returnvalue = theSubstitutionPartRuntime.interpret(m,context);
		}
		return returnvalue;
		*/
		boolean returnvalue = this.match( m, context );
		if( returnvalue && !substitutionParts.isEmpty() )
		{
			returnvalue = substitute( m, context );
		}
		return returnvalue;
    }

	public void addMatchingPart( MatchingPartRuntime mp ) 
	{
		matchingParts.add( mp );
	}

	public void addSubstitutionPart( SubstitutionPartRuntime sp ) 
	{
		substitutionParts.add( sp );
	}

	private boolean match( MessageList modml, Dictionary context ) 
	{
		MessageList ml = modml.getOriginalMessageList();
		if( ml == null )
			ml = modml;

		ml.resetMatches();

		// Default Dispatch to Inner Filter always matches
		// broke up the code into smaller, more debugable code
		FilterRuntime fr = this.theFilterElement.theFilter;
		Filter temp = (Filter) fr.getReference();
		FilterAST astTemp = temp.getFilterAST();
		String name = astTemp.getName();
		if (name.equals("CpsDefaultInnerDispatchFilter")) //( ((Filter)this.theFilterElement.theFilter.getReference()).getFilterAST().getName().equals( "CpsDefaultInnerDispatchFilter" ) ) 
		{
			ml.matchAll();
			return true;
		}

		if( ml.getMessages().size() != matchingParts.size() ) 
		{
			if( Debug.SHOULD_DEBUG )
				Debug.out( Debug.MODE_DEBUG, "FLIRT", "No match: the number of messages differs" );
			return false;
		}
		
		for( int i = 0; i < matchingParts.size(); i++ ) 
		{
			if( !((MatchingPartRuntime)matchingParts.get(i)).interpret( (Message) ml.getMessages().get( i ), context ) ) 
			{
				return false;
			}

		}
		ml.matchAll();
		return true;
	}

	private boolean substitute( MessageList ml, Dictionary context ) 
	{
		for( int i = 0; i < substitutionParts.size(); i++ ) 
		{
			Message m;
			if( i == 0 )
				m = ml.reduceToOne();
			else
				m = ml.duplicateOne();

			if( ((SubstitutionPartRuntime)substitutionParts.get( i )).getReference() == null ) 
			{
				return false;
			}
			if( !((SubstitutionPartRuntime)substitutionParts.get( i )).interpret( m, context ) ) 
			{
				return false;
			}
		}
		return true;
	}
}
