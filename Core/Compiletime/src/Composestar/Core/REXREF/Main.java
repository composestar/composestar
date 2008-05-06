package Composestar.Core.REXREF;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.CpsConcern.CpsConcern;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.CORfilterElementCompOper;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Condition;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.External;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Filter;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterElement;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterElementAST;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterModule;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterModuleAST;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterModuleParameter;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterModuleParameterValue;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Internal;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.LiteralFilterModuleParameterValue;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.MatchingPart;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.MatchingPartAST;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.MatchingPattern;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.MatchingPatternAST;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.MessageSelectorAST;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.NameMatchingType;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.ParameterizedInternal;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.ParameterizedMessageSelector;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Target;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.ConcernReference;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.ConditionReference;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.DeclaredObjectReference;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.FilterModuleReference;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.ProgramElementReference;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.Reference;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.SelectorReference;
import Composestar.Core.CpsProgramRepository.CpsConcern.SuperImposition.FilterModuleBinding;
import Composestar.Core.CpsProgramRepository.CpsConcern.SuperImposition.SelectorDefinition;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.LAMA.MethodInfo;
import Composestar.Core.LAMA.Type;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Core.Resources.CommonResources;
import Composestar.Utils.Logging.CPSLogger;

/**
 * Resolves references to objects in the repository
 */
public class Main implements CTCommonModule
{
	public static final String MODULE_NAME = "REXREF";

	protected static final CPSLogger logger = CPSLogger.getCPSLogger(Main.MODULE_NAME);

	public static boolean debug = true; // display debugging information?

	protected boolean hasErrors = false;

	protected DataStore ds;

	/**
	 * Constructor
	 */
	public Main()
	{}

	/**
	 * Function called by Master
	 * 
	 * @param resources Common resources supplied by Master
	 */
	public void run(CommonResources resources) throws ModuleException
	{
		// DoResolve dr = new DoResolve();
		// dr.go(DataStore.instance());

		ds = resources.repository();
		resolveReferences();
		if (hasErrors)
		{
			throw new ModuleException("There are unresolved references", MODULE_NAME);
		}
	}

	protected void resolveReferences()
	{
		// Phase 1: expand filter module parameters
		logger.info("Phase 1: Expand filter module parameters");
		List<Object> dsCopy = new ArrayList<Object>(ds.getObjects());
		for (Object o : dsCopy)
		{
			if (o instanceof FilterModuleReference)
			{
				resolveReference((FilterModuleReference) o);
			}
		}
		// Phase 2: resolve references
		logger.info("Phase 2: Resolve references");
		for (Object o : ds.getObjects())
		{
			if (o instanceof Reference)
			{
				if (((Reference) o).getResolved())
				{
					continue;
				}
			}
			else
			{
				continue;
			}

			if (o instanceof ConcernReference)
			{
				resolveReference((ConcernReference) o);
			}
			else if (o instanceof FilterModuleReference)
			{
				// already done
			}
			else if (o instanceof SelectorReference)
			{
				resolveReference((SelectorReference) o);
			}
			else if (o instanceof ConditionReference)
			{
				resolveReference((ConditionReference) o);
			}
			else if (o instanceof DeclaredObjectReference)
			{
				resolveReference((DeclaredObjectReference) o);
			}
			else
			{
				hasErrors = true;
				logger.error(String.format("Unhandled reference %s: %s", o.getClass().getName(), ((Reference) o)
						.getQualifiedName()));
			}
		}
	}

	protected void resolveReference(ConcernReference ref)
	{
		Concern reffedConcern = (Concern) ds.getObjectByID(ref.getQualifiedName());
		if (reffedConcern != null)
		{
			ref.setRef(reffedConcern);
			ref.setResolved(true);
			logger.debug(String.format("Resolved reference %s to %s", ref.getQualifiedName(), reffedConcern
					.getQualifiedName()), reffedConcern);
		}
		else
		{
			logger.error(String.format("Unable to resolve concern reference %s", ref.getQualifiedName()), ref);
			hasErrors = true;
		}
	}

	protected int fmCounter;

	protected void resolveReference(FilterModuleReference ref)
	{
		FilterModuleAST fmAst = (FilterModuleAST) ds.getObjectByID(ref.getQualifiedName());
		if (fmAst != null)
		{
			String uniqueID = "" + fmCounter++;

			// Create an unique ID for this FM instance, based on the
			// SelectorReference for the FilterModuleReference
			CpsConcern concern = (CpsConcern) fmAst.getParent();
			if (concern.getSuperImposition() != null)
			{
				Iterator<FilterModuleBinding> fmBindingIter = concern.getSuperImposition()
						.getFilterModuleBindingIterator();
				while (fmBindingIter.hasNext())
				{
					FilterModuleBinding fmBinding = fmBindingIter.next();
					SelectorReference selRef = fmBinding.getSelector();

					Iterator<FilterModuleReference> fmIter = fmBinding.getFilterModuleIterator();
					while (fmIter.hasNext())
					{
						FilterModuleReference fm_bound = fmIter.next();
						if (ref == fm_bound)
						{
							uniqueID = selRef.getName();
							break;
						}
					}
				}
			}

			FilterModule fm = new FilterModule(fmAst, ref.getArgs(), uniqueID);
			concern = (CpsConcern) fm.getParent();

			concern.addFilterModule(fm);
			ds.addObject(fm);

			// add all the newly created internal instances to the
			// repository
			Iterator<Internal> iter = fm.getInternalIterator();
			while (iter.hasNext())
			{
				Internal o = iter.next();

				if (o instanceof ParameterizedInternal)
				{
					ParameterizedInternal pi = (ParameterizedInternal) o;

					Vector<FilterModuleParameterValue> values = fm.getParameter(pi.getParameter()).getValue();
					List<String> selectors = new ArrayList<String>();
					for (FilterModuleParameterValue val : values)
					{
						if (val instanceof LiteralFilterModuleParameterValue)
						{
							selectors.addAll(val.getValues());
						}
						else
						{
							for (ProgramElementReference per : (Collection<ProgramElementReference>) val.getValues())
							{
								if (per.getRef() instanceof Type)
								{
									selectors.add(((Type) per.getRef()).getFullName());
								}
							}
						}
					}

					String[] prepack = selectors.get(0).split("\\.");
					List<String> pack = Arrays.asList(prepack);

					ConcernReference cref = new ConcernReference();
					cref.setName(pack.get(pack.size() - 1));
					cref.setPackage(pack.subList(0, pack.size() - 1));
					resolveReference(cref);
					pi.setType(cref);
					ds.addObject(cref);
					ds.addObject(pi.getType());
				}
			}
			// adding the new parameters instances to the repository
			Iterator<FilterModuleParameter> iterParams = fm.getParameterIterator();
			while (iterParams.hasNext())
			{
				FilterModuleParameter o = iterParams.next();
				ds.addObject(o);
			}

			expandFilterModuleParameters(fm, fm.getInputFilterIterator());
			expandFilterModuleParameters(fm, fm.getOutputFilterIterator());

			logger.debug(String.format("Resolved reference %s to %s", ref.getQualifiedName(), fm.getQualifiedName()),
					fm);
			ref.setRef(fm);
			ref.setResolved(true);
		}
		else
		{
			logger.error(String.format("Unable to resolve filter module reference %s", ref.getQualifiedName()), ref);
			hasErrors = true;
		}
	}

	protected void resolveReference(SelectorReference ref)
	{
		StringBuffer qn = new StringBuffer();
		for (String s : (Vector<String>) ref.getPackage())
		{
			if (qn.length() > 0)
			{
				qn.append(".");
			}
			qn.append(s);
		}
		if (qn.length() > 0)
		{
			qn.append(".");
		}
		qn.append(ref.getConcern());
		CpsConcern concern = (CpsConcern) ds.getObjectByID(qn.toString());
		if (concern != null)
		{
			Iterator<SelectorDefinition> it = concern.getSuperImposition().getSelectorIterator();
			while (it.hasNext())
			{
				SelectorDefinition sd = it.next();
				if (sd.getName().equals(ref.getName()))
				{
					logger.debug(String.format("Resolved reference %s to %s", ref.getQualifiedName(), sd
							.getQualifiedName()), sd);
					ref.setRef(sd);
					ref.setResolved(true);
					return;
				}
			}
		}
		logger
				.error(String.format("Unable to resolve selector \"%s\" in concern \"%s\"", ref.getName(), qn
						.toString()), ref);
		hasErrors = true;
	}

	protected void resolveReference(ConditionReference ref)
	{
		StringBuffer qn = new StringBuffer();
		for (String s : (Vector<String>) ref.getPackage())
		{
			if (qn.length() > 0)
			{
				qn.append(".");
			}
			qn.append(s);
		}
		if (qn.length() > 0)
		{
			qn.append(".");
		}
		qn.append(ref.getConcern());
		CpsConcern concern = (CpsConcern) ds.getObjectByID(qn.toString());
		if (concern != null)
		{
			FilterModuleAST fmAst = concern.getFilterModuleAST(ref.getFilterModule());
			if (fmAst != null)
			{
				Iterator<Condition> cit = fmAst.getConditionIterator();
				while (cit.hasNext())
				{
					Condition cond = cit.next();
					if (cond.getName().equals(ref.getName()))
					{
						logger.debug(String.format("Resolved reference %s to %s", ref.getQualifiedName(), cond
								.getQualifiedName()), cond);
						ref.setRef(cond);
						ref.setResolved(true);
						return;
					}
				}
			}
		}
		logger.error(String.format("Unable to resolve condition \"%s\" in filter module \"%s\" of concern \"%s\"", ref
				.getName(), ref.getFilterModule(), qn.toString()), ref);
		hasErrors = true;
	}

	protected void resolveReference(DeclaredObjectReference ref)
	{
		if (ref.getName().equals(Target.INNER))
		{
			return;
		}
		StringBuffer qn = new StringBuffer();
		for (String s : (Vector<String>) ref.getPackage())
		{
			if (qn.length() > 0)
			{
				qn.append(".");
			}
			qn.append(s);
		}
		if (qn.length() > 0)
		{
			qn.append(".");
		}
		qn.append(ref.getConcern());
		CpsConcern concern = (CpsConcern) ds.getObjectByID(qn.toString());
		if (concern != null)
		{
			Iterator<FilterModule> fmit = concern.getFilterModuleIterator();
			while (fmit.hasNext())
			{
				FilterModule fm = fmit.next();
				if (fm.getOriginalName().equals(ref.getFilterModule()))
				{
					Iterator<Internal> iit = fm.getInternalIterator();
					while (iit.hasNext())
					{
						Internal inter = iit.next();
						if (inter.getName().equals(ref.getName()))
						{
							logger.debug(String.format("Resolved reference %s to %s", ref.getQualifiedName(), inter
									.getQualifiedName()), inter);
							ref.setRef(inter);
							ref.setResolved(true);
							return;
						}
					}
					Iterator<External> eit = fm.getExternalIterator();
					while (eit.hasNext())
					{
						External inter = eit.next();
						if (inter.getName().equals(ref.getName()))
						{
							logger.debug(String.format("Resolved reference %s to %s", ref.getQualifiedName(), inter
									.getQualifiedName()), inter);
							ref.setRef(inter);
							ref.setResolved(true);
							return;
						}
					}
				}
			}
		}
		logger.error(String.format("Unable to resolve object \"%s\" in filter module \"%s\" of concern \"%s\"", ref
				.getName(), ref.getFilterModule(), qn.toString()), ref);
		hasErrors = true;
	}

	protected void expandFilterModuleParameters(FilterModule fm, Iterator<Filter> filters)
	{
		while (filters.hasNext())
		{
			Filter filter = filters.next();
			Iterator<FilterElement> fes = filter.getFilterElementIterator();
			while (fes.hasNext())
			{
				FilterElement fe = fes.next();
				MatchingPattern ma = fe.getMatchingPattern();
				Iterator<MatchingPart> mps = ma.getMatchingPartsIterator();
				while (mps.hasNext())
				{
					MatchingPart mp = mps.next();
					if (mp.getSelector() instanceof ParameterizedMessageSelector)
					{
						ParameterizedMessageSelector pms = (ParameterizedMessageSelector) mp.getSelector();

						Vector<FilterModuleParameterValue> values = fm.getParameter(pms.getName()).getValue();
						List<String> selectors = new ArrayList<String>();
						for (FilterModuleParameterValue val : values)
						{
							if (val instanceof LiteralFilterModuleParameterValue)
							{
								selectors.addAll(val.getValues());
							}
							else
							{
								for (ProgramElementReference per : (Collection<ProgramElementReference>) val
										.getValues())
								{
									if (per.getRef() instanceof MethodInfo)
									{
										selectors.add(((MethodInfo) per.getRef()).getName());
									}
								}
							}
						}
						if (selectors.size() == 0)
						{
							logger.warn(String.format("Paramater %s contains no values", pms.getName()), pms);
						}
						else
						{
							if (pms.isList())
							{
								if (mp.getMatchType() instanceof NameMatchingType)
								{
									Iterator<String> sel = selectors.iterator();
									String s = sel.next();
									pms.setName(s);

									// the second one out of the vector and the
									// rest
									while (sel.hasNext())
									{
										FilterElement copy = copyFilterElement(fe);

										// placing and and afetr the first one
										CORfilterElementCompOper cfeco = new CORfilterElementCompOper();
										cfeco.setDescriptionFileName(fe.getDescriptionFileName());
										cfeco.setDescriptionLineNumber(fe.getDescriptionLineNumber());
										cfeco.setParent(fe.getFilterElementAST());
										fe.setRightOperator(cfeco);
										ds.addObject(cfeco);

										filter.addFilterElement(copy);
										ds.addObject(copy);

										// setting the selector
										MatchingPattern mp2 = copy.getMatchingPattern();
										Iterator<MatchingPart> match = mp2.getMatchingPartsIterator();
										while (match.hasNext())
										{
											MatchingPart matchpart = match.next();
											matchpart.getSelector().setName(sel.next());
										}

										// this for getting , , between them
										fe = copy;
									}
								}
								else
								{
									logger.error("Parameter list is only allowed in name matching", pms);
								}
							}
							else
							{
								String paraValue = selectors.get(0);
								pms.setName(paraValue);
							}
						}
					}
				}
			}
		}
	}

	/*
	 * Copying a filter element out of the FilterElementAST of the orginal FE
	 */
	private FilterElement copyFilterElement(FilterElement original)
	{
		// temporary check by cloning the AST strcutruee
		FilterElementAST oldAST = original.getFilterElementAST();
		FilterElementAST newAST = new FilterElementAST();
		newAST.setConditionPart(oldAST.getConditionPart());
		newAST.setDescriptionFileName(oldAST.getDescriptionFileName());
		newAST.setDescriptionLineNumber(oldAST.getDescriptionLineNumber());
		newAST.setEnableOperatorType(oldAST.getEnableOperatorType());
		newAST.setParent(oldAST.getParent());
		newAST.setRightOperator(oldAST.getRightOperator());

		// TODO: cleanup MatchingPatterns leftovers
		// Vector mps = new Vector();
		// Iterator mpi = oldAST.getMatchingPatterns().iterator();
		// while(mpi.hasNext()){
		MatchingPatternAST newmpa = new MatchingPatternAST();
		MatchingPatternAST oldmpa = oldAST.getMatchingPattern();

		newmpa.setDescriptionFileName(oldmpa.getDescriptionFileName());
		newmpa.setDescriptionLineNumber(oldmpa.getDescriptionLineNumber());
		newmpa.setParent(oldmpa.getParent());
		newmpa.setSubstitutionParts(oldmpa.getSubstitutionParts());

		Vector<MatchingPartAST> ms = new Vector<MatchingPartAST>();
		Iterator<MatchingPartAST> mi = oldmpa.getMatchingPartsIterator();
		while (mi.hasNext())
		{
			MatchingPartAST newm = new MatchingPartAST();
			MatchingPartAST oldm = mi.next();

			newm.setDescriptionFileName(oldm.getDescriptionFileName());
			newm.setDescriptionLineNumber(oldm.getDescriptionLineNumber());
			newm.setMatchType(oldm.getMatchType());
			newm.setParent(newm.getParent());
			newm.setTarget(oldm.getTarget());

			MessageSelectorAST newsel = new MessageSelectorAST();
			newsel.setDescriptionFileName(oldm.getSelector().getDescriptionFileName());
			newsel.setDescriptionLineNumber(oldm.getSelector().getDescriptionLineNumber());
			newsel.setName("@unnamed@");
			newsel.setParent(newm);
			ds.addObject(newsel);

			newm.setSelector(newsel);
			ms.add(newm);
			ds.addObject(newm);
		}

		newmpa.setMatchingParts(ms);
		// mps.add(newmpa);
		ds.addObject(newmpa);
		// }

		// newAST.setMatchingPatterns(mps);
		newAST.setMatchingPattern(newmpa);
		ds.addObject(newAST);
		// end test

		FilterElement copy = new FilterElement(newAST);
		copy.setConditionPart(original.getConditionPart());
		copy.setDescriptionFileName(original.getDescriptionFileName());
		copy.setDescriptionLineNumber(original.getDescriptionLineNumber());
		copy.setEnableOperatorType(copy.getEnableOperatorType());
		copy.setParent(copy.getParent());
		copy.setRightOperator(copy.getRightOperator());
		return copy;
	}
}
