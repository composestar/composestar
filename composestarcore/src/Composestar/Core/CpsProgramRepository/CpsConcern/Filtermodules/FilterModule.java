/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id: FilterModule.java,v 1.3 2006/02/16 12:51:21 composer Exp $
 */
package Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules;

import Composestar.Core.RepositoryImplementation.*;
import Composestar.Utils.*;

import java.util.*;

/**
 * @modelguid {A1F21307-81AA-41BE-8D4C-79216E1F0EC6}
 */
public class FilterModule extends DeclaredRepositoryEntity {
    public Vector conditions;
    public Vector internals;
    public  Vector externals;
    public Vector methods;
    public Vector inputFilters;
    public Vector outputFilters;
    

    /**
     * @modelguid {04F87A00-10B0-476E-8C87-A6BD604DEF19}
     * @roseuid 401FAA6303DA
     */
    public FilterModule() 
	{
    super();
    conditions = new Vector();
    internals = new Vector();
    externals = new Vector();
    methods = new Vector();
    inputFilters = new Vector();
    outputFilters = new Vector();
  }


  /**
   * conditions
   *
   * @param condition
   * @return boolean
   *
   * @modelguid {F8B3B43F-D2DC-42F4-8BB7-960E34077798}
   * @roseuid 401FAA640006
   */
  public boolean addCondition(Condition condition) {
    conditions.addElement(condition);
    return (true);
  }


  /**
   * @param index
   * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Condition
   *
   * @modelguid {E224F0A7-E476-40C6-AA91-DAAABCC9F2C8}
   * @roseuid 401FAA640010
   */
  public Condition removeCondition(int index) {
    Object o = conditions.elementAt(index);
    conditions.removeElementAt(index);
    return ((Condition) o);
  }


  /**
   * @param index
   * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Condition
   *
   * @modelguid {6101C495-ACFE-4235-BABF-072163C217F8}
   * @roseuid 401FAA640023
   */
  public Condition getCondition(int index) {
    return ((Condition) conditions.elementAt(index));
  }


  /**
   * @return java.util.Iterator
   *
   * @modelguid {C1BFB6AC-3D27-4283-A51B-1D86CD52E9DD}
   * @roseuid 401FAA64002E
   */
  public Iterator getConditionIterator() {
    return (new CPSIterator(conditions));
  }


  /**
   * internals
   *
   * @param internal
   * @return boolean
   *
   * @modelguid {638C9279-9DD7-4E4B-92D1-52A44F9D6D93}
   * @roseuid 401FAA640041
   */
  public boolean addInternal(Internal internal) {
    internals.addElement(internal);
    return (true);
  }


  /**
   * @param index
   * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Internal
   *
   * @modelguid {E1CCC976-8BB3-40C4-A7E8-024139E6EF68}
   * @roseuid 401FAA640060
   */
  public Internal removeInternal(int index) {
    Object o = internals.elementAt(index);
    internals.removeElementAt(index);
    return ((Internal) o);
  }


  /**
   * @param index
   * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Internal
   *
   * @modelguid {0A8DFDE2-FD90-47F2-8C9E-1BE78B38EE07}
   * @roseuid 401FAA640073
   */
  public Internal getInternal(int index) {
    return ((Internal) internals.elementAt(index));
  }


  /**
   * @return java.util.Iterator
   *
   * @modelguid {0DD565EF-8A52-4205-AF2F-45EBC462C6DC}
   * @roseuid 401FAA64007E
   */
  public Iterator getInternalIterator() {
    return (new CPSIterator(internals));
  }


  /**
   * externals
   *
   * @param external
   * @return boolean
   *
   * @modelguid {66A6FE94-6148-4AEA-8398-9AA8194866B2}
   * @roseuid 401FAA640091
   */
  public boolean addExternal(External external) {
    externals.addElement(external);
    return (true);
  }


  /**
   * @param index
   * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.External
   *
   * @modelguid {E43D6A89-5769-4307-93CF-0C25F68FD0B6}
   * @roseuid 401FAA64009C
   */
  public External removeExternal(int index) {
    Object o = externals.elementAt(index);
    externals.removeElementAt(index);
    return ((External) o);
  }


  /**
   * @param index
   * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.External
   *
   * @modelguid {DC6CEBA4-836A-40A4-BD34-CCDCDE04AE80}
   * @roseuid 401FAA6400B0
   */
  public External getExternal(int index) {
    return ((External) externals.elementAt(index));
  }


  /**
   * @return java.util.Iterator
   *
   * @modelguid {7ED0DFC2-C26F-4AD8-930D-D8619B60A094}
   * @roseuid 401FAA6400C4
   */
  public Iterator getExternalIterator() {
    return (new CPSIterator(externals));
  }


  /**
   * methods
   *
   * @param method
   * @return boolean
   *
   * @modelguid {C26D4769-374D-4328-9EC6-1B739DEADC28}
   * @roseuid 401FAA6400D7
   */
  public boolean addMethod(Method method) {
    methods.addElement(method);
    return (true);
  }


  /**
   * @param index
   * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Method
   *
   * @modelguid {5993A474-B082-4B82-9271-BB10A261D535}
   * @roseuid 401FAA6400F6
   */
  public Method removeMethod(int index) {
    Object o = methods.elementAt(index);
    methods.removeElementAt(index);
    return ((Method) o);
  }


  /**
   * @param index
   * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Method
   *
   * @modelguid {251921D1-08F7-40BD-BE2F-EA2E20B28B83}
   * @roseuid 401FAA640109
   */
  public Method getMethod(int index) {
    return ((Method) methods.elementAt(index));
  }


  /**
   * @return java.util.Iterator
   *
   * @modelguid {55FF6623-0ACA-4974-A6F7-CABA99F1CB93}
   * @roseuid 401FAA64011D
   */
  public Iterator getMethodIterator() {
    return (new CPSIterator(methods));
  }


  /**
   * inputfilters
   *
   * @param inputfilter
   * @return boolean
   *
   * @modelguid {057E1939-BF1B-488B-9433-D9AFDDE5F3DC}
   * @roseuid 401FAA640127
   */
  public boolean addInputFilter(Filter inputfilter) {
    inputFilters.addElement(inputfilter);
    return (true);
  }


  /**
   * @param index
   * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Filter
   *
   * @modelguid {8BAC3FDA-92BB-4806-8B21-497E6E09CC1E}
   * @roseuid 401FAA64013C
   */
  public Filter removeInputFilter(int index) {
    Object o = inputFilters.elementAt(index);
    inputFilters.removeElementAt(index);
    return ((Filter) o);
  }


  /**
   * @param index
   * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Filter
   *
   * @modelguid {92657675-90E7-4A7E-AEA9-D13717A78D94}
   * @roseuid 401FAA64014F
   */
  public Filter getInputFilter(int index) {
    return ((Filter) inputFilters.elementAt(index));
  }


  /**
   * @return java.util.Iterator
   *
   * @modelguid {C4063FB6-952E-433E-8B4B-464D5ECC562C}
   * @roseuid 401FAA640163
   */
  public Iterator getInputFilterIterator() {
    return (new CPSIterator(inputFilters));
  }


  /**
   * outputfilters
   *
   * @param outputfilter
   * @return boolean
   *
   * @modelguid {33854751-CB00-46A6-BAEE-EE06C8E13F29}
   * @roseuid 401FAA640177
   */
  public boolean addOutputFilter(Filter outputfilter) {
    outputFilters.addElement(outputfilter);
    return (true);
  }


  /**
   * @param index
   * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Filter
   *
   * @modelguid {7C532F57-5948-453E-AF4B-5E9BEC0C30A2}
   * @roseuid 401FAA64018B
   */
  public Filter removeOutputFilter(int index) {
    Object o = outputFilters.elementAt(index);
    outputFilters.removeElementAt(index);
    return ((Filter) o);
  }


  /**
   * @param index
   * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Filter
   *
   * @modelguid {4F30E1EA-4EAA-4352-B0F6-E72F3297C18B}
   * @roseuid 401FAA64019F
   */
  public Filter getOutputFilter(int index) {
    return ((Filter) outputFilters.elementAt(index));
  }


  /**
   * @return java.util.Iterator
   *
   * @modelguid {4BDED6A5-B80F-401A-82B6-6CED9F185669}
   * @roseuid 401FAA6401B4
   */
  public Iterator getOutputFilterIterator() {
    return (new CPSIterator(outputFilters));
  }
}

/**
 * void FilterModule.accept(RepositoryVisitor){
 *     visitor.visit(this);
 *   }
 */
