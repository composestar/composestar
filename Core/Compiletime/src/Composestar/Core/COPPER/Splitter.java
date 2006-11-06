package Composestar.Core.COPPER;

import java.util.*;


//this is a class to help split references that are supplied in a Vector correctly

public class Splitter {
  private Vector pack;
  private String concern;
  private String concernelem;
  private String fm;
  private String fmelem;
  private int i, j;
  private CpsRepositoryBuilder bui;   //reference to a builder class (so we can get to defaults)


  public Splitter() {
    reset();
  }


  //resets all the previous work
  public void reset() {
    pack = null;
    concern = null;
    concernelem = null;
    fm = null;
    fmelem = null;
    i = 0;
    j = 0;
  }


  //split a concernReference (i.e. a.b.c.concern)
 public void splitConcernReference(Vector in) {
    reset();
    i = in.size();
    switch (i) {
      case 0:  //nothing, just null
        break;
      case 1: //we just have n
        concern = (String) in.elementAt(0);
        break;
      default: //we have a.b.c.n
        concern = (String) in.elementAt(i - 1);
        for (j = 0; j <= i - 2; j++) { //now add the package
          if (pack == null) 
          {
        	  pack = new Vector();  //only create pack if actually used
          }
          pack.add(in.elementAt(j));
        }
        break;
    }
  }


  //split a concernElementReference (i.e. a.b.c.concern::fm)
  void splitConcernElemReference(Vector in) {
    reset();
    i = in.size();
    switch (i) {
      case 0:  //nothing, just null
        break;
      case 1: //we just have n
        concernelem = (String) in.elementAt(0);
        break;
      case 2: //we have n::n
        concernelem = (String) in.elementAt(1);
        concern = (String) in.elementAt(0);
        break;
      default: //we have a.b.c.n::n
        concernelem = (String) in.elementAt(i - 1);
        concern = (String) in.elementAt(i - 2);
        for (j = 0; j <= i - 3; j++) { //now add the package
          if (pack == null)
          {
        	  pack = new Vector();  //only create pack if actually used
          }
          pack.add(in.elementAt(j));
        }
        break;
    }
  }


  //splits and fills in missing values using defaults
  void splitConcernElemReference(Vector in, boolean fillindefaults) {
    reset();
    splitConcernElemReference(in);

    if (fillindefaults) {
      if (concern == null) 
      {
    	  concern = bui.getCpsc();
      }
    }
  }


  //split a filtermoduleElementReference (i.e. a.b.c.concern::fm:fmelem)
  void splitFmElemReference(Vector in) {
    reset();
    i = in.size();
    switch (i) {
      case 0:  //nothing, just null
        break;
      case 1: //we just have n
        fmelem = (String) in.elementAt(0);
        break;
      case 2: //we have n:n
        fmelem = (String) in.elementAt(1);
        fm = (String) in.elementAt(0);
        break;
      case 3: //we have n::n:n
        fmelem = (String) in.elementAt(2);
        fm = (String) in.elementAt(1);
        concern = (String) in.elementAt(0);
        break;
      default: //we have a.b.c.n::n:n
        fmelem = (String) in.elementAt(i - 1);
        fm = (String) in.elementAt(i - 2);
        concern = (String) in.elementAt(i - 3);
        for (j = 0; j <= i - 4; j++) { //now add the package
          if (pack == null)
          {
        	  pack = new Vector();  //only create pack if actually used
          }
          pack.add(in.elementAt(j));
        }
        break;
    }
  }


  //splits and fills in missing values using defaults
  void splitFmElemReference(Vector in, boolean fillindefaults) {
    reset();
    splitFmElemReference(in);

    if (fillindefaults) {
      if (concern == null) 
      {
    	  concern = bui.getCpsc();
      }
      if (fm == null)
      {
    	  fm = bui.getFm();
      }
    }
  }


  //special version, to be used when you only have only element (i.e. a NAME) and as a string
  //instead of a Vector with one element.
  void splitFmElemReference(String in, boolean fillindefaults) {
    Vector tempvec = new Vector();
    tempvec.add(in);

    reset();
    splitFmElemReference(tempvec, fillindefaults);
  }


  public Vector getPack() {
    return pack;
  }


  public String getConcern() {
    return concern;
  }


  public String getFm() {
    return fm;
  }


  public String getFmelem() {
    return fmelem;
  }


  public String getConcernelem() {
    return concernelem;
  }


  public void setBuilder(CpsRepositoryBuilder buiValue) {
    bui = buiValue;
  }
}
