package Composestar.Java.LOLA;

import Composestar.Core.LOLA.*;
import Composestar.Core.LOLA.metamodel.UnitDictionary;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Java.LOLA.metamodel.*;

import java.util.ArrayList;

public class JavaLOLA extends LOLA
{
  /**
   * Default constructor; uses the .NET language model
   */
  public JavaLOLA()
  {
    this (JavaLanguageModel.instance());
  }
  
  /**
   * Constructor 
   * @param model the language model to be used by this instance of the logic language
   */
  public JavaLOLA(JavaLanguageModel model)
  {
    this.initialized = false;
    this.langModel = model;
    this.dataStore = DataStore.instance();
    this.unitDict = new UnitDictionary(model);
    selectors = new ArrayList();
  }
}

