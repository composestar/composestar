package Composestar.Core.FITER;

import java.util.*;

import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterType;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.INCRE.INCRE;
import Composestar.Core.LAMA.ProgramElement;
import Composestar.Core.LAMA.Type;
import Composestar.Core.LAMA.TypeMap;
import Composestar.Core.LAMA.UnitResult;
import Composestar.Core.LOLA.LOLA;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Utils.Debug;

/**
 * This Compose* module checks whether the used filter types can acutally be found! 
 *
 * @author pascal
 */
public class FITER implements CTCommonModule 
{
	public static final String MODULE_NAME = "FITER";
	
	public FITER()
	{
	}
	
	public void run(CommonResources resources) throws ModuleException
	{
		Debug.out(Debug.MODE_INFORMATION,MODULE_NAME,"Verifying Filter Types...");
		
		//turned off for starlight compatibility
//		ArrayList customfilters = getCustomFilterTypes();
//		resolveCustomFilterTypes(customfilters);
	}
	
//	private ArrayList getCustomFilterTypes()
//	{
//		ArrayList customfilters = new ArrayList();
//		DataStore ds = DataStore.instance();
//		Iterator it = ds.getAllInstancesOf(FilterType.class);
//		while(it.hasNext())
//		{
//			FilterType type = (FilterType)it.next();
//			if(type.getType().equals(FilterType.CUSTOM))
//			{
//				customfilters.add(type);
//			}
//		}
//		return customfilters;
//	}
//	
//	private void resolveCustomFilterTypes(ArrayList customfilters) throws ModuleException
//	{
//		ArrayList working = new ArrayList(customfilters);
//		ArrayList result = new ArrayList(customfilters);
//		LOLA lola = (LOLA)INCRE.instance().getModuleByName(LOLA.MODULE_NAME);
//		String filterSuperClass = "Composestar.RuntimeCore.FLIRT.Filtertypes.CustomFilter";
//		if(lola.unitDict != null)
//		{
//			UnitResult ur = lola.unitDict.getByName(filterSuperClass, "Class");
//			if(ur != null && ur.singleValue() != null)
//			{
//				ProgramElement filterType = ur.singleValue();
//				HashSet allFilterTypes = getChildsofClass(filterType);
//				Iterator allFilterTypesIt = allFilterTypes.iterator();
//				while(allFilterTypesIt.hasNext())
//				{
//					Object obj = allFilterTypesIt.next();
//					if(obj instanceof ProgramElement)
//					{
//						ProgramElement customFilterType = (ProgramElement)obj;
//						Iterator definedCustomFilters = working.iterator();
//						while(definedCustomFilters.hasNext())
//						{
//							FilterType ftype =(FilterType)definedCustomFilters.next();
//							
//							if(customFilterType.getUnitName().indexOf('.') < 0)
//							{
//								if(customFilterType.getUnitName().endsWith(ftype.getName()))
//								{
//									Debug.out(Debug.MODE_INFORMATION, MODULE_NAME, "Resolved filter type: "+ftype.getName()+" to "+customFilterType.getUnitName());
//									result.remove(ftype);
//								}
//							}
//							else
//							{
//								if(customFilterType.getUnitName().endsWith("."+ftype.getName()))
//								{
//									Debug.out(Debug.MODE_INFORMATION, MODULE_NAME, "Resolved filter type: "+ftype.getName()+" to "+customFilterType.getUnitName());
//									result.remove(ftype);
//								}
//							}
//						}
//					}
//				}
//			}
//		}
//		for(int i=0; i<result.size(); i++)
//		{
//			FilterType ftype = (FilterType)result.get(i);
//			Debug.out(Debug.MODE_ERROR, MODULE_NAME, "Unable to resolve filter type: "+ftype.getName()+"!");
//			throw new ModuleException("Unable to resolve filter type: "+ftype.getName()+"!",MODULE_NAME,ftype);
//		}
//	}
//	
//	private HashSet getChildsofClass(ProgramElement filterType)
//	{
//		HashSet total = new HashSet();
//		HashSet hashset = filterType.getUnitRelation("ChildClasses").multiValue();
//		total.addAll(hashset);
//		Iterator it = hashset.iterator();
//		while(it.hasNext())
//		{
//			ProgramElement customFilterType = (ProgramElement)it.next();
//			HashSet subset = this.getChildsofClass(customFilterType);
//			total.add(subset);
//		}
//		return total;
//	}

}
