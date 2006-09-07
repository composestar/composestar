/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id: Concern.java,v 1.2 2006/02/16 12:51:20 composer Exp $
 */
package Composestar.Core.CpsProgramRepository;

import Composestar.Core.RepositoryImplementation.*;

/**
 * @modelguid {C0029BA1-DCA2-423D-B656-CECE522EAAB2}
 * A concern is any implementation unit; this can be a Compose* Concern, but also
 * a class, a package, a primitive type, an assembly.
 */
public class Concern extends DeclaredRepositoryEntity {
  public PlatformRepresentation platformRepr;
  public Signature theSignature;


  /**
   * @modelguid {D3EFC8F1-94CE-4BD0-8994-9801CF5ED163}
   * @roseuid 401FAA570165
   */
  public Concern() {
    super();
  }


  /**
   * @return Composestar.Core.CpsProgramRepository.PlatformRepresentation
   *
   * @roseuid 40237FEC009F
   */
  public PlatformRepresentation getPlatformRepresentation() {
    return platformRepr;
  }


  /**
   * @param plat
   * @roseuid 40237FFF0300
   */
  public void setPlatformRepresentation(PlatformRepresentation plat) {
    platformRepr = plat;
  }


  /**
   * @param sig
   * @roseuid 404C49F80196
   */
  public void setSignature(Signature sig) {
    theSignature = sig;
  }


  /**
   * @return java.security.Signature
   *
   * @roseuid 404C4A31012A
   */
  public Signature getSignature() {
    return theSignature;
  }

	public Object clone () throws CloneNotSupportedException
	{
		Concern newObject = null;
		newObject = (Concern)super.clone();

			// At this point, the newObject shares all data with the object
			// running clone. If you want newObject to have its own
			// copy of data, you must clone this data yourself.
		
		newObject.theSignature = null;
		return newObject;
	}
}
