/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Core.CpsProgramRepository.CpsConcern.Implementation;

import Composestar.Core.CpsProgramRepository.*;

import java.util.*;

/**
 * @modelguid {10359D51-3F2F-4373-AA93-EFAF9599E301}
 * this is nothing but a reference to a CompiledConcernRepr.
 */
public class CompiledImplementation extends Implementation {
  public PlatformRepresentation reference;
  public String name;
  public String className;


  /**
   * @roseuid 404DDA7902A2
   */
  public CompiledImplementation() {
    super();
  }


  /**
   * @return java.lang.String
   *
   * @modelguid {A45C9BBA-972F-4DD3-B95B-F8F4639D2B56}
   * @roseuid 401FAA5601FA
   */
  public String getName() {
    return name;
  }


  /**
   * @param nameValue
   * @modelguid {EB5AE02C-8B85-4DA9-BCC5-114834CFDADC}
   * @roseuid 401FAA560203
   */
  public void setName(String nameValue) {
    this.name = nameValue;
  }


  /**
   * @return Composestar.Core.CpsProgramRepository.PlatformRepresentation
   *
   * @roseuid 40ADE0980338
   */
  public PlatformRepresentation getReference() {
    return reference;
  }


  /**
   * @param referenceValue
   * @roseuid 40ADE0A6005D
   */
  public void setReference(PlatformRepresentation referenceValue) {
    this.reference = referenceValue;
  }


  public String getClassName() {
    return className;
  }


  public void setClassName(Vector qualifiedClass) {
    if(!qualifiedClass.isEmpty()) className = (String) qualifiedClass.elementAt(0);
    for(int i=1; i<qualifiedClass.size(); i++) {
      className += '.' + (String) qualifiedClass.elementAt(i);
    }
  }
}
