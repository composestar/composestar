//Source file: C:\\local\\staijen\\composestar\\src\\Composestar\\core\\Exception\\ModuleException.java

/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id: ModuleException.java,v 1.1 2006/02/13 11:16:55 pascal Exp $
 */
package Composestar.Core.Exception;

import Composestar.Core.RepositoryImplementation.RepositoryEntity;

/**
 * this exception must be thrown by all modules when they encounter a fatal error.
 */
public class ModuleException extends Exception {
  private String module;
  private RepositoryEntity errorLocation; 

  /**
   * @param message
   * @param module
   * @roseuid 40FD314D00DA
   */
  public ModuleException(String message, String module) {
    super(message);
    this.module = module;
    this.errorLocation = null;
  }

  public ModuleException(String message, String module, RepositoryEntity errorLocation)
  {
	  this(message,module);
	  this.errorLocation = errorLocation;
  }

  /**
   * the <message> will be shown on std.error.
   *
   * @param message
   * @roseuid 401B8C1301A7
   */
  public ModuleException(String message) {
    super(message);
  }


  /**
   * @roseuid 401B8C0901B6
   */
  public ModuleException() {
	super("Undefined Model-Exception");
  }


  /**
   * @return java.lang.String
   *
   * @roseuid 40FD31430049
   */
  public String getModule() {
    return module;
  }

  public RepositoryEntity getErrorLocation()
  {
	  return errorLocation;
  }
  
  public String toString()
  {
	  if( module != null)
		  return module + " ERROR: " + getMessage();
	  else
		  return "UNDEFINED-MODULE ERROR: " + getMessage();
  }
}
