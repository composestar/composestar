package Composestar.Core.SANE;

import java.util.Iterator;
import java.util.Vector;

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.CpsConcern.CpsConcern;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Condition;
import Composestar.Core.CpsProgramRepository.CpsConcern.SuperImposition.SelectorDefinition;
import Composestar.Utils.CPSIterator;

/**
 * [this aggregate is necessary to enable the inclusion of alternatives]
 */
public class SIinfo extends Composestar.Core.RepositoryImplementation.ContextRepositoryEntity
{
	public static final String DATAMAP_KEY = "superImpInfo";

	private static final long serialVersionUID = -5485428622848735525L;

	public Vector theFmSIinfo = new Vector();

	public Vector theMethodSIinfo = new Vector();

	public Vector theConditionSIinfo = new Vector();

	/**
	 * @roseuid 4053A9110195
	 */
	public SIinfo()
	{

	}

	/**
	 * @param parent
	 * @roseuid 404C4B660033
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
	 * @roseuid 40539E4D0228
	 */
	public void resolveSelectors(SelectorDefinition sel)
	{
		sel.interpret(); // because this method is now in the repository
		// object.
	}

	/**
	 * @param concern resolve SuperImposition for this single concern.
	 * @param concern
	 * @roseuid 4053996D03A0
	 */
	public void resolveSI(CpsConcern concern)
	{

	// TODO finish up
	}

	/**
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.CpsConcern
	 * @roseuid 4053AD6703E0
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
	 * @roseuid 405984CB0062
	 */
	public Vector getFilterModSIAlts()
	{
		return theFmSIinfo;
	}

	public CPSIterator filterModSIAltsIter()
	{
		return new CPSIterator(theFmSIinfo);
	}

	/**
	 * get a vector with all the alternative superimpositions of methods
	 * (perhaps just one)
	 * 
	 * @return java.util.Vector
	 * @roseuid 405984E50236
	 */
	public Vector getMethodSIAlts()
	{
		return theMethodSIinfo;
	}

	public CPSIterator getMethodSIAltsIter()
	{
		return new CPSIterator(theMethodSIinfo);
	}

	/**
	 * get a vector with all the alternative superimpositions of conditions
	 * (perhaps just one)
	 * 
	 * @return java.util.Vector
	 * @roseuid 405984F40332
	 */
	public Vector getConditionSIAlts()
	{
		return theConditionSIinfo;
	}

	public CPSIterator getConditionSIAltsIter()
	{
		return new CPSIterator(theConditionSIinfo);
	}

	/**
	 * @param fms
	 * @param index
	 * @roseuid 405A5EE500FC
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

	/**
	 * @param fms
	 * @param index
	 * @roseuid 405A5F1101AA
	 */
	public void addMethodsAt(Iterator fms, int index)
	{

	}

	/**
	 * @param fms
	 * @param index
	 * @roseuid 405A5F200129
	 */
	public void addCondsAt(Iterator fms, int index)
	{

	}
}
