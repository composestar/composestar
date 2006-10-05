/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Core.CpsProgramRepository.CpsConcern.References;


public class FilterModuleElementReference extends ConcernElementReference {
  public String filterModule;


  /**
   * @roseuid 401FAA6402FE
   */
  public FilterModuleElementReference() {
    super();
  }


  /**
   * @return java.lang.String
   *
   * @roseuid 401FAA6402FF
   */
  public String getFilterModule() {
    return filterModule;
  }


  /**
   * @param filterModuleValue
   * @roseuid 401FAA640308
   */
  public void setFilterModule(String filterModuleValue) {
    this.filterModule = filterModuleValue;
	this.updateRepositoryReference();
  }


  public String getQualifiedName() {
    String fname = "";
    int i;

    for (i = 0; i < pack.size(); i++) {
      fname += pack.elementAt(i);
      fname += ".";
    }
    if(concern != null) fname += concern;
    if(concern != null && filterModule != null) fname += "::";
    if(filterModule != null) fname += filterModule;
    if(filterModule != null && name != null) fname += ":";
    if(name != null) fname += name;
    return (fname);
  }
}
