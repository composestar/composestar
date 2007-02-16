package Composestar.Core.SANE;

import java.util.Vector;

import Composestar.Core.CpsProgramRepository.CpsConcern.CpsConcern;
import Composestar.Utils.CPSIterator;

public interface AbstractSIinfo
{

	/**
	 * @param concern
	 */
	void bind(CpsConcern concern);

	/**
	 * get a Vector with all <things> that have been superimposed
	 * 
	 * @return java.util.Vector
	 */
	Vector getAll();

	/**
	 * @return Composestar.Utils.*;
	 */
	CPSIterator getIter();
}
