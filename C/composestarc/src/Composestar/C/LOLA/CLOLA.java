package Composestar.C.LOLA;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;

import tarau.jinni.Builtins;
import tarau.jinni.Init;
import Composestar.Core.LAMA.*;
import Composestar.Core.LOLA.*;
import Composestar.Core.LOLA.connector.ComposestarBuiltins;
import Composestar.Core.LOLA.connector.ModelGenerator;
import Composestar.Core.LOLA.metamodel.ModelException;
import Composestar.Core.LOLA.metamodel.UnitDictionary;
import Composestar.Core.INCRE.INCRE;
import Composestar.Core.INCRE.INCRETimer;
import Composestar.Core.INCRE.Module;
import Composestar.Core.INCRE.MyComparator;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.Master.Config.Configuration;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Utils.Debug;
import Composestar.Utils.FileUtils;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.ConcernReference;
import Composestar.Core.CpsProgramRepository.CpsConcern.SuperImposition.AnnotationBinding;
import Composestar.Core.CpsProgramRepository.CpsConcern.SuperImposition.SelectorDefinition;
import Composestar.Core.CpsProgramRepository.CpsConcern.SuperImposition.SimpleSelectorDef.PredicateSelector;
import Composestar.Core.CpsProgramRepository.CpsConcern.SuperImposition.SimpleSelectorDef.SimpleSelExpression;
import Composestar.Core.Exception.ModuleException;
import Composestar.C.LOLA.metamodel.*;

public class CLOLA extends LOLA
{
  //CLanguageModel langModel;
  /**
   * Default constructor; uses the .NET language model
   */
  public CLOLA()
  {
    this (CLanguageModel.instance());
  }
  
  /**
   * Constructor 
   * @param model the language model to be used by this instance of the logic language
   */
  public CLOLA(CLanguageModel model)
  {
    this.initialized = false;
    this.langModel = model;
    this.dataStore = DataStore.instance();
    this.unitDict = new UnitDictionary(model);
    selectors = new ArrayList();
  }
}
