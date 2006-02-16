/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id: Filter.java,v 1.3 2006/02/16 12:51:21 composer Exp $
 */
package Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules;

import Composestar.Core.RepositoryImplementation.*;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.*;
import Composestar.Utils.*;

import java.util.*;

/**
 * @modelguid {EF28A1E2-BD65-444F-810F-8B5D590B28AA}
 */
public class Filter extends DeclaredRepositoryEntity {

  /**
   * extra, reference to type
   */
  public ConcernReference typeImplementation;

  /**
   * @modelguid {BA6E0095-9527-4D09-9DBB-C6C6D14F98CF}
   */
  public FilterType type;
  public Vector filterElements;
  public FilterCompOper rightOperator;
  public Vector parameters;

  /**
   * @modelguid {C7DDAADC-9E56-486C-B4B0-8CDF99C09BA9}
   * @roseuid 401FAA6300CC
   */
  public Filter() {
    super();
    filterElements = new Vector();
    parameters = new Vector();
  }


  /**
   * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterType
   *
   * @roseuid 401FAA6300E1
   */
  public FilterType getFilterType() {
    return type;
  }


  /**
   * @param typeValue
   * @roseuid 401FAA6300F4
   */
  public void setFilterType(FilterType typeValue) {
    this.type = typeValue;
  }


  /**
   * filterelements
   *
   * @param filterelement
   * @return boolean
   *
   * @modelguid {C3A32E86-43B0-40A3-A470-33F577AFCB58}
   * @roseuid 401FAA6300FF
   */
  public boolean addFilterElement(FilterElement filterelement) {
    filterElements.addElement(filterelement);
    return (true);
  }


  /**
   * @param index
   * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterElement
   *
   * @modelguid {62CC6BF1-5B8B-4956-A74C-2627546D8E08}
   * @roseuid 401FAA630126
   */
  public FilterElement removeFilterElement(int index) {
    Object o = filterElements.elementAt(index);
    filterElements.removeElementAt(index);
    return ((FilterElement) o);
  }


  /**
   * @param index
   * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterElement
   *
   * @modelguid {D5097D6E-9299-4538-A3BB-F43E543C6B30}
   * @roseuid 401FAA63013A
   */
  public FilterElement getFilterElement(int index) {
    return ((FilterElement) filterElements.elementAt(index));
  }


  /**
   * @return java.util.Iterator
   *
   * @modelguid {90E85BF8-4998-47B7-A91F-4B319BFA0D10}
   * @roseuid 401FAA63014E
   */
  public Iterator getFilterElementIterator() {
    return (new CPSIterator(filterElements));
  }


  /**
   * parameters
   *
   * @param parameter
   * @return boolean
   *
   * @modelguid {9AF48E0B-1D38-49D0-8FB5-99DCDA06CCDC}
   * @roseuid 401FAA630158
   */
  public boolean addParameter(String parameter) {
    parameters.addElement(parameter);
    return (true);
  }


  /**
   * @param index
   * @return Composestar.Core.CpsProgramRepository.CpsConcern.References.LabeledConcernRe
   *         ference
   *
   * @modelguid {739D2466-FF3E-409E-8856-CC86FB2B0114}
   * @roseuid 401FAA63016D
   */
  public String removeParameter(int index) {
    Object o = parameters.elementAt(index);
    parameters.removeElementAt(index);
    return ((String) o);
  }


  /**
   * @param index
   * @return Composestar.Core.CpsProgramRepository.CpsConcern.References.LabeledConcernRe
   *         ference
   *
   * @modelguid {E5A2DBE4-A124-47F1-B588-CCCD703B19B0}
   * @roseuid 401FAA630181
   */
  public String getParameter(int index) {
    return ((String) parameters.elementAt(index));
  }


  /**
   * @return java.util.Iterator
   *
   * @modelguid {DA253686-4890-45EE-9D5F-4D74644F27F0}
   * @roseuid 401FAA630195
   */
  public Iterator getParameterIterator() {
    return (new CPSIterator(parameters));
  }


  /**
   * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterCompOpe
   *         r
   *
   * @roseuid 402AA7530225
   */
  public FilterCompOper getRightOperator() {
    return rightOperator;
  }


  /**
   * @param rightOperatorValue
   * @roseuid 402AA7790310
   */
  public void setRightOperator(FilterCompOper rightOperatorValue) {
    this.rightOperator = rightOperatorValue;
  }


  /**
   * @return Composestar.Core.CpsProgramRepository.CpsConcern.References.ConcernReference
   *
   * @roseuid 40ADD67500CA
   */
  public ConcernReference getTypeImplementation() {
    return typeImplementation;
  }


  /**
   * @param typeImplementationValue
   * @roseuid 40ADD68103CA
   */
  public void setTypeImplementation(ConcernReference typeImplementationValue) {
    this.typeImplementation = typeImplementationValue;
  }
}
