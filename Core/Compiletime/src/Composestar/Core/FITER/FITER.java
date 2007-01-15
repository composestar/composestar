package Composestar.Core.FITER;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterType;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.INCRE.INCRE;
import Composestar.Core.LAMA.ProgramElement;
import Composestar.Core.LAMA.UnitResult;
import Composestar.Core.LOLA.LOLA;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Utils.Debug;

/**
 * This Compose* module checks whether the used filter types can acutally be
 * found!
 * 
 * @author pascal
 */
public class FITER implements CTCommonModule
{
	public static final String MODULE_NAME = "FITER";

	public FITER()
	{}

	public void run(CommonResources resources) throws ModuleException
	{
		Debug.out(Debug.MODE_INFORMATION, MODULE_NAME, "Verifying Filter Types...");
		ArrayList customfilters = getCustomFilterTypes();
		resolveCustomFilterTypes(customfilters);
	}

	private ArrayList getCustomFilterTypes()
	{
		ArrayList customfilters = new ArrayList();
		DataStore ds = DataStore.instance();
		Iterator it = ds.getAllInstancesOf(FilterType.class);
		while (it.hasNext())
		{
			FilterType type = (FilterType) it.next();
			if (type.getType().equals(FilterType.CUSTOM))
			{
				customfilters.add(type);
			}
		}
		return customfilters;
	}

	private void resolveCustomFilterTypes(ArrayList customfilters) throws ModuleException
	{
		ArrayList working = new ArrayList(customfilters);
		ArrayList result = new ArrayList(customfilters);
		LOLA lola = (LOLA) INCRE.instance().getModuleByName(LOLA.MODULE_NAME);
		String filterSuperClass = "Composestar.RuntimeCore.FLIRT.Filtertypes.CustomFilter";
		if (lola.unitDict != null)
		{
			UnitResult ur = lola.unitDict.getByName(filterSuperClass, "Class");
			if (ur != null && ur.singleValue() != null)
			{
				ProgramElement filterType = ur.singleValue();
				HashSet allFilterTypes = getChildsofClass(filterType);
				Iterator allFilterTypesIt = allFilterTypes.iterator();
                for (Object obj : allFilterTypes) {
                    if (obj instanceof ProgramElement) {
                        ProgramElement customFilterType = (ProgramElement) obj;
                        Iterator definedCustomFilters = working.iterator();
                        for (Object aWorking : working) {
                            FilterType ftype = (FilterType) aWorking;

                            if (customFilterType.getUnitName().indexOf('.') < 0) {
                                if (customFilterType.getUnitName().endsWith(ftype.getName())) {
                                    Debug.out(Debug.MODE_INFORMATION, MODULE_NAME, "Resolved filter type: "
                                            + ftype.getName() + " to " + customFilterType.getUnitName());
                                    result.remove(ftype);
                                }
                            } else {
                                if (customFilterType.getUnitName().endsWith("." + ftype.getName())) {
                                    Debug.out(Debug.MODE_INFORMATION, MODULE_NAME, "Resolved filter type: "
                                            + ftype.getName() + " to " + customFilterType.getUnitName());
                                    result.remove(ftype);
                                }
                            }
                        }
                    }
                }
            }
		}
        for (Object aResult : result) {
            FilterType ftype = (FilterType) aResult;
            Debug.out(Debug.MODE_ERROR, MODULE_NAME, "Unable to resolve filter type: " + ftype.getName() + "!");
        }

        if(!result.isEmpty()){
            throw new ModuleException("Unable to resolve filter type: " + ((FilterType) result.get(0)).getName() + "!", MODULE_NAME, (FilterType) (result.get(0)));
		}
	}

	private HashSet getChildsofClass(ProgramElement filterType)
	{
		HashSet total = new HashSet();
		HashSet hashset = filterType.getUnitRelation("ChildClasses").multiValue();
		total.addAll(hashset);
		Iterator it = hashset.iterator();
        for (Object aHashset : hashset) {
            ProgramElement customFilterType = (ProgramElement) aHashset;
            HashSet subset = this.getChildsofClass(customFilterType);
            total.add(subset);
        }
        return total;
	}

}
