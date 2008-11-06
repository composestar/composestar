package Composestar.Core.SANE;

import java.util.Iterator;
import java.util.Vector;

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.CpsConcern.CpsConcern;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Condition;
import Composestar.Core.CpsProgramRepository.CpsConcern.SuperImposition.SelectorDefinition;
import Composestar.Core.RepositoryImplementation.ContextRepositoryEntity;

/**
 * [this aggregate is necessary to enable the inclusion of alternatives]
 */
@Deprecated
public class SIinfo extends ContextRepositoryEntity
{
	public static final String DATAMAP_KEY = "superImpInfo";

	private static final long serialVersionUID = -5485428622848735525L;

	public Vector theFmSIinfo = new Vector();

	/**
	 *
	 */
	public SIinfo()
	{}

	/**
	 * @param parent
	 */
	public SIinfo(Concern parent)
	{
		super(parent); // set the ref to the concern
	}

	/**
	 * this interprets the selectors and attaches a list with ConcernReferences
	 * that represents the expanded list of concerns that are selected by the
	 * selector
	 * 
	 * @param sel
	 */
	public void resolveSelectors(SelectorDefinition sel)
	{
		sel.interpret(); // because this method is now in the repository
		// object.
	}

	/**
	 * @param concern resolve SuperImposition for this single concern.
	 * @param concern
	 */
	public void resolveSI(CpsConcern concern)
	{
	// TODO finish up
	}

	/**
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.CpsConcern
	 */
	public CpsConcern myConcern()
	{
		return (CpsConcern) this.getParent();
	}

	/**
	 * get a vector with all the alternative superimpositions of filtermodules
	 * (perhaps just one)
	 * 
	 * @return java.util.Vector
	 */
	public Vector getFilterModSIAlts()
	{
		return theFmSIinfo;
	}

	/**
	 * @param fms
	 * @param index
	 */
	public void addFMsAt(Iterator fms, Condition fmCondition, int index)
	{
		// get (create if necessary) the FilterModSIinfo instance
		FilterModSIinfo fmSIinfo;
		if (theFmSIinfo.isEmpty())
		{
			// always add one alternative
			fmSIinfo = new FilterModSIinfo();
			theFmSIinfo.addElement(fmSIinfo);
		}
		else
		{
			fmSIinfo = (FilterModSIinfo) theFmSIinfo.get(index);
		}
		fmSIinfo.addFMs(fms, fmCondition);
	}
}
