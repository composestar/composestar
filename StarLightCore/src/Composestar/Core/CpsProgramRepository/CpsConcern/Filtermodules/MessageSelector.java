/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules;

import Composestar.Core.RepositoryImplementation.*;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.*;
import Composestar.Utils.*;

import java.util.*;

/**
 * @modelguid {F5586509-49A4-471C-90EA-2C66B42B702A}
 * the message selector as used in filter patterns (nb; the term is selector is
 * sometimes used, but this is confusing with the selector used in superimposition)
 */
public class MessageSelector extends ContextRepositoryEntity {

  public MessageSelectorAST msAST;
  /*name -> get from AST -> have to copy name from the  MessageSelectorAST, FIRE2 uses the class as well and delegation gets us a null pointer
   * so from the two options: copying and creating a dummy AST, the first is preferrable 
   */
  public String name;  
  
  //public Vector typeList; -> get from AST


  /**
   * @roseuid 404DDA6F03AC
   * @deprecated
   */
  public MessageSelector() {
    super();
    //typeList = new Vector();
  }

  public MessageSelector(MessageSelectorAST amsAST){
	  super();
	  msAST = amsAST;
	  name = msAST.getName();
  }

  /**
   * @return java.lang.String
   *
   * @modelguid {AD4E0123-C75E-45AE-AACB-835F9A49FB33}
   * @roseuid 401FAA6703CB
   */
  public String getName() {
    return name;//msAST.getName();
  }


  /**
   * @param nameValue
   * @modelguid {7D1FDAAC-BA9B-4A3B-B02B-228E4A73D30C}
   * @roseuid 401FAA6703CC
   */
  public void setName(String nameValue) {
    //msAST.setName(nameValue);
	  name = nameValue;
  }


  /**
   * typeList
   *
   * @param type
   * @return boolean
   *
   * @modelguid {DDD36DA7-2C64-4269-945C-2AA0D91D457B}
   * @roseuid 401FAA6703D6
   */
  public boolean addParameterType(ConcernReference type) {
    return msAST.addParameterType(type);
  }


  /**
   * @param index
   * @return Composestar.Core.CpsProgramRepository.CpsConcern.References.ConcernReference
   *
   * @modelguid {4A770336-B0FC-4E69-8353-5D7050FD59B6}
   * @roseuid 401FAA680001
   */
  public ConcernReference removeParameterType(int index) {
    return msAST.removeParameterType(index);
  }


  /**
   * @param index
   * @return Composestar.Core.CpsProgramRepository.CpsConcern.References.ConcernReference
   *
   * @modelguid {0AA74210-29B2-4D19-A8A0-FC9277E8B436}
   * @roseuid 401FAA68000C
   */
  public ConcernReference getParameterType(int index) {
    return msAST.getParameterType(index);
  }


  /**
   * @return java.util.Iterator
   *
   * @modelguid {C106BE0D-804D-41EC-9CDF-B78F134F9AF8}
   * @roseuid 401FAA680020
   */
  public Iterator getParameterTypeIterator() {
    return msAST.getParameterTypeIterator();
  }

  public MessageSelectorAST getMsAST() {
	return msAST;
  }
}
