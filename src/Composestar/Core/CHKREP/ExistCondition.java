package Composestar.Core.CHKREP;

import java.util.Iterator;

import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.BinaryOperator;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Condition;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.ConditionExpression;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.ConditionVariable;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Filter;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterElement;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterModule;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.UnaryOperator;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Utils.Debug;

/**
 * Checks whether a Condtion which is used in a Input or outpurfilter is
 * declared in Condition. It does filter on "True", "False", "true" and "false"
 * as standard boolean values
 * 
 * @author DoornenbalD
 */

public class ExistCondition implements BaseChecker
{
	// Standard private DataStore
	private DataStore ds;

	/**
	 * Performs the checks, sends the check to both input and outputfilters uses
	 * checkConditionInFilter twice
	 */
	public boolean performCheck()
	{
		// standard false
		boolean nonFatal = true;
		// first get all filtermodules
		Iterator filterModuleIterator = ds.getAllInstancesOf(FilterModule.class);
		while (filterModuleIterator.hasNext() && nonFatal)
		{
			FilterModule fm = (FilterModule) filterModuleIterator.next();
			// then perform the checks on both inputfilters and outputfilters
			// also sends the paranet filtermodule to save parent calls further
			// on
			nonFatal = checkConditionInFilter(fm.getInputFilterIterator(), fm)
					&& checkConditionInFilter(fm.getOutputFilterIterator(), fm);
		}

		return nonFatal;
	}

	/**
	 * Standard entry, calls performCheck and checks on a fatal error. Because a
	 * missing condition lets the run-time crash an exception is cast.
	 */
	public void check(DataStore newDs) throws ModuleException
	{
		ds = newDs;
		boolean nonFatal = performCheck();

		if (!nonFatal)
		{
			throw new ModuleException("One or more Conditions in the input/outputfilters are not declared", "CHKREP");
		}
	}

	/**
	 * Puts the check down the filters
	 * 
	 * @param filterIterator
	 * @param fm the filetrModule
	 * @return
	 */
	private boolean checkConditionInFilter(Iterator filterIterator, FilterModule fm)
	{
		// standard true;
		boolean nonFatal = true;

		if (filterIterator != null)
		{
			while (filterIterator.hasNext())
			{
				Filter filter = (Filter) filterIterator.next();
				Iterator filterElementIterator = filter.getFilterElementIterator();
				while (filterElementIterator.hasNext())
				{
					FilterElement fe = (FilterElement) filterElementIterator.next();

					ConditionExpression ce = fe.getConditionPart();
					if (ce != null)
					{
						// puts the check in the root of the condition tree
						boolean temp = checkConditionInConditionExpression(ce, fm);

						// if one tree gives a fatal then nonFatal = false
						if (!temp)
						{
							nonFatal = false;
						}
					}
				}
			}
		}
		return nonFatal;
	}

	/**
	 * Checks the tree. Searches recrusively for Literals, if the Literals are
	 * "True", "False", "true" or "false" then it does nothing otherwise it does
	 * call doesConditionExists with the conditionname and filtermodule. Also
	 * calls the debugger.
	 * 
	 * @param ce
	 * @param fm
	 * @return
	 */
	private boolean checkConditionInConditionExpression(ConditionExpression ce, FilterModule fm)
	{
		// standard non fatal
		boolean nonFatal = true;

		if (ce instanceof ConditionVariable)
		{
			ConditionVariable cl = (ConditionVariable) ce;
			String conditionName = cl.getCondition().getName();
			if (!doesConditionExists(conditionName, fm))
			{
				ConditionVariable tempCe = (ConditionVariable) ce;
				Debug.out(Debug.MODE_ERROR, "CHKREP", "Condition " + conditionName + " is not declared in Conditions", tempCe);
				nonFatal = false;
			}
		}

		// Checks on a UnaryOperators
		if (ce instanceof UnaryOperator)
		{
			nonFatal = checkConditionInConditionExpression(((UnaryOperator) ce).getOperand(), fm);
		}

		// checks in BinaryOperators
		if (ce instanceof BinaryOperator)
		{
			nonFatal = checkConditionInConditionExpression(((BinaryOperator) ce).getLeft(), fm)
					&& checkConditionInConditionExpression(((BinaryOperator) ce).getRight(), fm);
		}

		return nonFatal;
	}

	/**
	 * Checks wheter a condtionname exists in the parent filtermodule. Since the
	 * filtermodule is given from the beign no parent calls are needed.
	 * 
	 * @param name
	 * @param fm
	 * @return
	 */
	private boolean doesConditionExists(String name, FilterModule fm)
	{
		// standra it does not exist
		boolean exists = false;

		Iterator conditionIterator = fm.getConditionIterator();

		if (conditionIterator != null)
		{
			while (conditionIterator.hasNext())
			{
				Condition condition = (Condition) conditionIterator.next();
				if (condition.getName().equals(name))
				{
					exists = true;
				}
			}
		}

		return exists;
	}

}
