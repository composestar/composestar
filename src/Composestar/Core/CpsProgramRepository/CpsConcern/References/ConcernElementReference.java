/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id: ConcernElementReference.java,v 1.1 2006/02/16 23:03:50 pascal_durr Exp $
 */
package Composestar.Core.CpsProgramRepository.CpsConcern.References;


public class ConcernElementReference extends Reference {
  public String concern;


  /**
   * @roseuid 401FAA5701DD
   */
  public ConcernElementReference() {
    super();
  }


  /**
   * @return java.lang.String
   *
   * @roseuid 401FAA5701DE
   */
  public String getConcern() {
    return concern;
  }


  /**
   * @param concernValue
   * @roseuid 401FAA5701E7
   */
  public void setConcern(String concernValue) {
    this.concern = concernValue;
	  this.updateRepositoryReference();
  }


  public String getQualifiedName() {
    String fname = "";
    int i;

    for (i = 0; i < pack.size(); i++) {
      fname += pack.elementAt(i);
      fname += ".";
    }
    fname += concern;
    fname += "." + name;
    return (fname);
  }
}
