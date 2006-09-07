package Composestar.Java.BACO;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import Composestar.Core.Java.BACO;
import Composestar.Core.Master.Config.Dependency;
import Composestar.Core.RepositoryImplementation.DataStore;

public class JavaBACO extends BACO{

	protected boolean checkNeededDependency(Dependency dependency){
		return true;
	}
}