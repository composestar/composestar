/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id: SelClassAndSubClasses.java,v 1.2 2006/02/16 12:51:22 composer Exp $
 */
package Composestar.Core.CpsProgramRepository.CpsConcern.SuperImposition.SimpleSelectorDef;

import Composestar.Core.CpsProgramRepository.CpsConcern.References.ConcernReference;

import java.util.Vector;

/**
 * klasse en subklassen
 *
 * @modelguid {2B3C2C27-5B5D-44E6-B78B-555C2322A27A}
 */
public class SelClassAndSubClasses extends SimpleSelExpression 
{

  /**
   * @modelguid {18775506-0682-492E-AA1D-51D40CB5CD03}
   * @roseuid 401FAA670352
   */
  public SelClassAndSubClasses() {
    super();
  }


  /**
   * this list contains the concern designated by the classname (=concernname) and
   * all those marked as 'subclasses': the interpretation of 'subclass' is:
   * - all .NET classes that are marked as being a subclass
   * - all concerns that dispatch to this class as an internal (..) *if* they are
   * also a subtype
   * 
   * TODO currently, only returns the class itself, and no subclasses!
   * 
   * @return returns a list of ConcernReferences
   *
   * @todo rename className to concernName
   * @roseuid 404FA9900092
   */
  public Vector interpret() {
	Vector v = new Vector();
	ConcernReference rootClass = this.getRef();  
	v.addElement(rootClass);
	return(v);
  }
}
