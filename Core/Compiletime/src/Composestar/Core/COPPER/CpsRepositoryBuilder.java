/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Core.COPPER;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import Composestar.Core.CpsProgramRepository.CpsConcern.CpsConcern;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.And;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.CORfilterElementCompOper;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Condition;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.ConditionExpression;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.ConditionVariable;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.DisableOperator;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.EnableOperator;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.EnableOperatorType;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.External;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.False;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Filter;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterAST;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterElementAST;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterModule;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterModuleAST;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterModuleParameter;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterModuleParameterAST;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterType;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.InternalAST;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.MatchingPartAST;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.MatchingPatternAST;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.MatchingType;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.MessageSelectorAST;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Method;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.NameMatchingType;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Not;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Or;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.ParameterizedInternalAST;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.ParameterizedMessageSelectorAST;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.SEQfilterCompOper;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.SignatureMatchingType;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.SubstitutionPartAST;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Target;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.True;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.VoidFilterCompOper;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.VoidFilterElementCompOper;
import Composestar.Core.CpsProgramRepository.CpsConcern.Implementation.CompiledImplementation;
import Composestar.Core.CpsProgramRepository.CpsConcern.Implementation.Source;
import Composestar.Core.CpsProgramRepository.CpsConcern.Implementation.SourceFile;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.ConcernReference;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.ConditionReference;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.DeclaredObjectReference;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.ExternalConcernReference;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.FilterModuleReference;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.LabeledConcernReference;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.MethodReference;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.SelectorReference;
import Composestar.Core.CpsProgramRepository.CpsConcern.SuperImposition.AnnotationBinding;
import Composestar.Core.CpsProgramRepository.CpsConcern.SuperImposition.ConditionBinding;
import Composestar.Core.CpsProgramRepository.CpsConcern.SuperImposition.FilterModuleBinding;
import Composestar.Core.CpsProgramRepository.CpsConcern.SuperImposition.MethodBinding;
import Composestar.Core.CpsProgramRepository.CpsConcern.SuperImposition.SelectorDefinition;
import Composestar.Core.CpsProgramRepository.CpsConcern.SuperImposition.StarCondition;
import Composestar.Core.CpsProgramRepository.CpsConcern.SuperImposition.StarMethod;
import Composestar.Core.CpsProgramRepository.CpsConcern.SuperImposition.SuperImposition;
import Composestar.Core.CpsProgramRepository.CpsConcern.SuperImposition.SimpleSelectorDef.PredicateSelector;
import Composestar.Core.CpsProgramRepository.CpsConcern.SuperImposition.SimpleSelectorDef.SelClass;
import Composestar.Core.CpsProgramRepository.CpsConcern.SuperImposition.SimpleSelectorDef.SelClassAndSubClasses;
import Composestar.Core.CpsProgramRepository.Legacy.LegacyFilterTypes;
import Composestar.Core.FILTH.FILTH;
import Composestar.Core.FILTH.SyntacticOrderingConstraint;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Core.RepositoryImplementation.RepositoryEntity;
import Composestar.Utils.Debug;
import Composestar.Utils.StringConverter;
import antlr.SemanticException;

/**
 * Class to create objects from the parse tree
 */
public class CpsRepositoryBuilder
{
	private DataStore ds;

	private Splitter splitter;

	private String filename; // Filename of file currently being parsed, for

	// reference in case of parsing errors.

	private String embeddedCode;

	private String namespace = null;

	private boolean parsingInput = true; // whether we're parsing an input-

	// or outputfilter (needed because
	// of generalfilter)

	private boolean workingOnMatching = true; // whether we're busy creating

	// the matching or substitution
	// part in a messagepatternset

	private AnnotationBinding annotBinding;

	private ConditionBinding cb;

	private CpsConcern cpsc;

	private FilterAST inf;

	private FilterAST of;

	private FilterElementAST fe;

	private FilterModuleAST fm;

	private FilterModuleBinding fmb;

	private MatchingPartAST mp;

	private MatchingPatternAST mpat;

	private MatchingType mt;

	private MessageSelectorAST s;

	private Method m;

	private MethodBinding mb;

	private SelectorDefinition sd;

	private SubstitutionPartAST sp;

	private SuperImposition si;

	private Target ta;

	private HashMap orderingconstraints = new HashMap();

	/**
	 * Constructor
	 */
	public CpsRepositoryBuilder()
	{
		ds = DataStore.instance();
		splitter = new Splitter();
		splitter.setBuilder(this);
	}

	public void setFilename(String filename)
	{
		this.filename = filename;
	}

	public void setEmbeddedCode(String ec)
	{
		this.embeddedCode = ec;
	}

	/**
	 * Adds the filename to the object before it adds the object to the
	 * datastore Use this instead of ds.addObject for incremental runs of COPPER
	 * 
	 * @param Object
	 * @param obj
	 */
	public void addToRepository(RepositoryEntity obj)
	{
		obj.setDescriptionFileName(filename);
		ds.addObject(obj);
	}

	/**
	 * Adds a Concern object to the repository
	 * 
	 * @param name Name for the concern
	 * @param line
	 */
	public void addConcern(String name, int line)
	{
		cpsc = new CpsConcern();
		cpsc.setParent(null);
		cpsc.setName(name);
		cpsc.setDescriptionFileName(filename);
		cpsc.setDescriptionLineNumber(line);
		// ds.addObject(cpsc);
	}

	public void finalizeNamespace()
	{
		cpsc.setQualifiedName(namespace + '.' + cpsc.getName());
	}

	public void addToNamespace(String name)
	{
		if (namespace == null)
		{
			namespace = name;
		}
		else
		{
			namespace += '.' + name;
		}
		// System.out.println("Namespace: "+this.namespace);
	}

	/**
	 * Adds parameters to a concern
	 * 
	 * @param namev the names of the parameters with a common type
	 * @param typev the type shared by the parameters
	 */
	public void addConcernFormalParameters(Vector namev, Vector typev)
	{
		String name;
		int j;

		if (!namev.isEmpty())
		{ // names are optional, only specifying types is enough
			for (j = 0; j < namev.size(); j++)
			{
				name = (String) namev.elementAt(j);
				cpsc.addParameter(addLabeledConcernReference(name, typev));
			}
		}
		else
		{ // alleen type opgegeven
			cpsc.addParameter(addLabeledConcernReference(null, typev));
		}
	}

	// - FILTERMODULE STUFF
	// -------------------------------------------------------------------------------------------

	/**
	 * Adds a FilterModule object to the repository
	 * 
	 * @param name Name for the filtermodule
	 * @param lineNumber
	 */
	public void addFilterModule(String name, int lineNumber)
	{
		fm = new FilterModuleAST();
		fm.setName(name);
		fm.setParent(cpsc);
		fm.setDescriptionFileName(filename);
		fm.setDescriptionLineNumber(lineNumber);
		cpsc.addFilterModuleAST(fm);
		this.addToRepository(fm);
	}

	public void addFilterModuleParameters(Vector idv, int lineNumber) throws SemanticException
	{
		for (int j = 0; j < idv.size(); j++)
		{
			FilterModuleParameterAST fmp = new FilterModuleParameterAST();
			fmp.setDescriptionFileName(filename);
			fmp.setDescriptionLineNumber(lineNumber);
			fmp.setName((String) idv.elementAt(j));
			fmp.setParent(fm);
			if (!fm.doesParameterExists(fmp))
			{
				fm.addParameter(fmp);
				this.addToRepository(fmp);
			}
			else
			{
				throw new SemanticException("Parameter not unique within filtermodule", fmp.getDescriptionFileName(),
						fmp.descriptionLineNumber, 0);
			}
		}
	}

	/**
	 * Creates internals. Internals have a name and a type (e.g. a: int)
	 * 
	 * @param namev the names of the internals
	 * @param typev the type shared by the internals (can contain a package e.g.
	 * x.y.z.int) @ throws SemanticException when
	 *            internals/externals/inputfilters/outputfilters have duplicate
	 *            identifiers
	 * @param lineNumber
	 * @param idv
	 * @param parameterized
	 */
	public void addInternals(Vector namev, Vector idv, int lineNumber, boolean parameterized) throws SemanticException
	{
		for (int j = 0; j < namev.size(); j++)
		{
			if (!parameterized)
			{
				InternalAST in = new InternalAST();
				in.setDescriptionFileName(filename);
				in.setDescriptionLineNumber(lineNumber);
				in.setName((String) namev.elementAt(j));
				in.setType(addConcernReference(idv)); // fixme: instead of
				// recreating the
				// concernreference
				// here, we could just
				// create it once and
				// reuse for all
				// internals
				in.setParent(fm);
				if (fm.addInternal(in))
				{
					this.addToRepository(in);
				}
				else
				{
					throw new SemanticException("Identifier not unique within filtermodule", in
							.getDescriptionFileName(), in.descriptionLineNumber, 0);
				}
			}
			else
			{
				ParameterizedInternalAST parin = new ParameterizedInternalAST();
				parin.setDescriptionFileName(filename);
				parin.setDescriptionLineNumber(lineNumber);
				parin.setName((String) namev.elementAt(j));
				parin.setParameter((String) idv.elementAt(0));
				parin.setParent(fm);
				if (fm.addInternal(parin))
				{
					this.addToRepository(parin);
				}
				else
				{
					throw new SemanticException("Identifier not unique within filtermodule", parin
							.getDescriptionFileName(), parin.descriptionLineNumber, 0);
				}

			}
		}
	}

	/**
	 * Creates externals. Internals have a name and a type (e.g. a: int)
	 * 
	 * @param namev The names of the internals
	 * @param typev The type shared by the internals (can contain a package e.g.
	 *            x.y.z.int)
	 * @param init Common initialization expression (can be an internal /
	 * external) @ throws SemanticException when
	 *           internals/externals/inputfilters/outputfilters have duplicate
	 *           identifiers
	 * @param type
	 * @param lineNumber
	 */
	public void addExternals(Vector namev, Vector typev, Vector init, int type, int lineNumber)
			throws SemanticException
	{
		for (int j = 0; j < namev.size(); j++)
		{
			External ex = new External();
			ex.setName((String) namev.elementAt(j));
			ex.setType(addConcernReference(typev));
			ex.setDescriptionFileName(filename);
			ex.setDescriptionLineNumber(lineNumber);

			if (!init.isEmpty())
			{
				if (type == 0)
				{
					splitter.splitConcernReference(init);
					// ex.setShortinit((addConcernReference(split.getPack(),
					// split.getConcern())));
					ExternalConcernReference ecref = new ExternalConcernReference();
					Vector v = new Vector();
					if (init.size() >= 2)
					{
						StringBuffer sb = new StringBuffer();
						String selector = (String) init.get(init.size() - 1);
						for (int i = 0; i < (init.size() - 1); i++)
						{
							// System.out.println("Hello:
							// "+(String)init.get(i));
							if (i > 0) sb.append('.');
							sb.append((String) init.get(i));
							v.add(init.get(i));
						}
						// System.out.println("Hello: "+selector);
						ecref.setInitSelector(selector);
						ecref.setInitTarget(sb.toString());
					}
					splitter.splitConcernReference(v);
					ecref.setPackage(splitter.getPack());
					ecref.setName(splitter.getConcern());
					ex.setShortinit(ecref);
					// fixme:
					this.addToRepository(ecref);
				}
				else
				{
					splitter.splitFmElemReference(init);
					ex.setLonginit(addDeclaredObjectReference(splitter.getPack(), splitter.getConcern(), splitter
							.getFm(), splitter.getFmelem()));
				}
			}
			// String bla = null;
			// bla.toCharArray();
			ex.setParent(fm);
			if (fm.addExternal(ex))
			{
				this.addToRepository(ex);
			}
			else
			{
				throw new SemanticException("Identifier not unique within filtermodule", ex.getDescriptionFileName(),
						ex.descriptionLineNumber, 0);
			}
		}
	}

	private boolean isInternal(String name)
	{
		Iterator iterInternals;
		InternalAST internal;

		for (iterInternals = fm.getInternalIterator(); iterInternals.hasNext();)
		{
			internal = (InternalAST) iterInternals.next();
			if (internal.getName().compareTo(name) == 0)
			{
				return true;
			}
		}

		return false;
	}

	private boolean isExternal(String name)
	{
		Iterator iterExternals;
		External external;

		for (iterExternals = fm.getExternalIterator(); iterExternals.hasNext();)
		{
			external = (External) iterExternals.next();
			if (external.getName().compareTo(name) == 0)
			{
				return true;
			}
		}

		return false;
	}

	/**
	 * Adds a Condition object to the repository
	 * 
	 * @param name
	 * @param init Name of the condition
	 * @param type
	 * @param lineNumber
	 */
	public void addCondition(String name, Vector init, int type, int lineNumber)
	{
		Condition c = new Condition();
		c.setName(name);
		c.setDescriptionFileName(filename);
		c.setDescriptionLineNumber(lineNumber);

		if (type == 0)
		{ // short version
			splitter.splitConcernElemReference(init);
			c.setParent(fm);
			c.addDynObject("selector", splitter.getConcernelem());
			if (splitter.getConcern().compareTo(Target.INNER) == 0)
			{
				// Reference to 'inner'
				DeclaredObjectReference dor = new DeclaredObjectReference();
				dor.setName(Target.INNER);
				c.setShortref(dor);
			}
			else if (isInternal(splitter.getConcern()) || isExternal(splitter.getConcern()))
			{
				// Reference to an internal or an external
				DeclaredObjectReference dor = new DeclaredObjectReference();
				dor.setName(splitter.getConcern());
				dor.setConcern(cpsc.getName());
				dor.setFilterModule(fm.getName());
				c.setShortref(dor);
			}
			else
			{
				// addMethodReference(split.getPack(), cpsc.getName(),
				// fm.getName(), split.getConcernelem(), new Vector());
				// addDeclaredObjectReference(split.getPack(), cpsc.getName(),
				// fm.getName(), "a."+split.getConcernelem());

				c.setShortref(addConcernReference(splitter.getPack(), splitter.getConcern()));
			}
		}
		else
		{ // long version
			splitter.splitFmElemReference(init, false);
			c.setLongref(addConditionReference(splitter.getPack(), splitter.getConcern(), splitter.getFm(), splitter
					.getFmelem()));
			c.setParent(fm);
		}
		fm.addCondition(c);
		this.addToRepository(c);
	}

	/**
	 * Adds a Filter Module Condition object to the repository
	 * 
	 * @param name
	 * @param init Name of the condition
	 * @param type
	 * @param lineNumber
	 */
	public void addFilterModuleCondition(String name, Vector init, int type, int lineNumber) throws SemanticException
	{
		Condition c = new Condition();
		c.setName(name);
		c.setDescriptionFileName(filename);
		c.setDescriptionLineNumber(lineNumber);

		if (type == 0)
		{ // short version
			splitter.splitConcernElemReference(init);
			c.setParent(si);
			c.addDynObject("selector", splitter.getConcernelem());
			if (splitter.getConcern().compareTo("inner") == 0)
			{
				// Reference to 'inner'
				DeclaredObjectReference dor = new DeclaredObjectReference();
				dor.setName("inner");
				c.setShortref(dor);
			}
			else if (isInternal(splitter.getConcern()) || isExternal(splitter.getConcern()))
			{
				// Reference to an internal or an external
				DeclaredObjectReference dor = new DeclaredObjectReference();
				dor.setName(splitter.getConcern());
				dor.setConcern(cpsc.getName());
				dor.setFilterModule(fm.getName());
				c.setShortref(dor);
			}
			else
			{
				// addMethodReference(split.getPack(), cpsc.getName(),
				// fm.getName(), split.getConcernelem(), new Vector());
				// addDeclaredObjectReference(split.getPack(), cpsc.getName(),
				// fm.getName(), "a."+split.getConcernelem());

				c.setShortref(addConcernReference(splitter.getPack(), splitter.getConcern()));
			}
		}
		else
		{ // long version
			splitter.splitFmElemReference(init, false);
			c.setLongref(addConditionReference(splitter.getPack(), splitter.getConcern(), splitter.getFm(), splitter
					.getFmelem()));
			c.setParent(si);
		}
		if (!si.addFilterModuleCondition(c))
		{
			throw new SemanticException("Identifier of the condition not unique in the superimposition", c
					.getDescriptionFileName(), c.descriptionLineNumber, 0);
		}
		this.addToRepository(c);
	}

	public void addFilterModuleConditionOperand(FilterModuleBinding binding, String conditionId)
			throws SemanticException
	{
		Condition condition = si.getFilterModuleCondition(conditionId);

		if (condition == null)
		{
			throw new SemanticException("Condition corresponding with identifier " + conditionId + " can not be found",
					binding.getDescriptionFileName(), binding.descriptionLineNumber, 0);
		}
	}

	/**
	 * Adds a Method object to the repository
	 * 
	 * @param name Name of the method
	 * @deprecated obselete now? DD
	 * @param lineNumber
	 */
	public void addMethod(String name, int lineNumber)
	{
		m = new Method();
		m.setName(name);
		m.setParent(fm);
		m.setDescriptionFileName(filename);
		m.setDescriptionLineNumber(lineNumber);
		fm.addMethod(m);
		this.addToRepository(m);
	}

	/**
	 * Adds the returntype to a Method object
	 * 
	 * @param typev the returntype (e.g. java.lang.int)
	 * @deprecated obselete now? DD
	 */
	public void addMethodReturnType(Vector typev)
	{
		if (!typev.isEmpty())
		{ // because returntype is optional
			m.setReturnType(addConcernReference(typev));
		}
		else
		{ // default is void
			m.setReturnType(addConcernReference(null, "void"));
		}
	}

	/**
	 * Adds parameters to a method
	 * 
	 * @param namev the names of the parameters with a common type
	 * @param typev the type shared by the parameters
	 * @deprecated obselete now? DD
	 */
	public void addMethodFormalParameters(Vector namev, Vector typev)
	{
		String name;
		int j;

		if (!namev.isEmpty())
		{ // names are optional, only specifying types is enough
			for (j = 0; j < namev.size(); j++)
			{
				name = (String) namev.elementAt(j);
				m.addParameter(addLabeledConcernReference(name, typev));
			}
		}
		else
		{ // alleen type opgegeven
			m.addParameter(addLabeledConcernReference(null, typev));
		}
	}

	/**
	 * Adds an inputFilter to the repository
	 * 
	 * @param name name of the inputfilter
	 * @param type the type of the filter (e.g. a.b.c.Meta) @ throws
	 *            SemanticException when
	 *            internals/externals/inputfilters/outputfilters have duplicate
	 *            identifiers
	 * @param lineNumber
	 */
	public void addInputFilter(String name, Vector type, int lineNumber) throws SemanticException
	{
		parsingInput = true;
		inf = new FilterAST();
		inf.setName(name);
		inf.setParent(fm);
		inf.setDescriptionFileName(filename);
		inf.setDescriptionLineNumber(lineNumber);

		// inf.setTypeImplementation(addConcernReference(type)); //fixme: we do
		// the same thing twice here basically //turned of because a filtertype
		// is not a concern anymore
		addFilterType((String) type.lastElement(), lineNumber); //

		if (fm.addInputFilter(inf))
		{
			this.addToRepository(inf);
		}
		else
		{
			throw new SemanticException("Identifier not unique within filtermodule", inf.getDescriptionFileName(),
					inf.descriptionLineNumber, 0);
		}
	}

	/**
	 * Adds a FilterType to an input- or outputfilter
	 * 
	 * @param type the type of the filter (e.g. Meta)
	 * @param lineNumber
	 */
	public void addFilterType(String type, int lineNumber)
	{
		FilterType ft = FilterType.getFilterType(type);

		if ((ft == null) && LegacyFilterTypes.useLegacyFilterTypes)
		{
			Debug.out(Debug.MODE_INFORMATION, "COPPER", "Creating legacy custom filter with name: " + type);
			ft = LegacyFilterTypes.createCustomFilterType(type);
		}

		if (ft == null)
		{
			Debug.out(Debug.MODE_ERROR, "COPPER", "Undefined FilterType '" + type + "'", filename, lineNumber);
		}

		if (parsingInput)
		{
			inf.setFilterType(ft);
		}
		else
		{
			of.setFilterType(ft);
		}
	}

	/**
	 * Adds parameters to an input- or outputfilter
	 * 
	 * @param namev the actual parameters
	 */
	public void addFilterActualParameters(Vector namev)
	{
		String name;
		int j;

		for (j = 0; j < namev.size(); j++)
		{
			name = (String) namev.elementAt(j);
			if (parsingInput)
			{
				inf.addParameter(name);
			}
			else
			{
				of.addParameter(name);
			}
		}
	}

	/**
	 * Adds a FilterElement to an input- or outputfilter (e.g. C => { [t.s]t.s] })
	 * 
	 * @param filterop2 inclusion of exclusion (can be '=>' or '~>'
	 * @param lineNumber
	 */
	public void addFilterElement(String filterop2, int lineNumber, ConditionExpression fec)
	{
		fe = new FilterElementAST();
		fe.setDescriptionFileName(filename);
		fe.setDescriptionLineNumber(lineNumber);

		if (parsingInput)
		{
			inf.addFilterElement(fe);
			fe.setParent(inf);
		}
		else
		{
			of.addFilterElement(fe);
			fe.setParent(of);
		}
		this.addToRepository(fe);

		// TODO: this is not very nice
		if (filterop2 != null)
		{
			addFilterOperatorType(filterop2, lineNumber);
		}
		else
		// sugar
		{
			addFilterOperatorType("=>", lineNumber);
		}

		if (fec == null) // sugar
		{
			fec = new True();
		}
		fec.setParent(fe);
		fe.setConditionPart(fec);
		this.addToRepository(fec);
	}

	public ConditionExpression addAnd(ConditionExpression lhs, ConditionExpression rhs)
	{
		And and = new And();
		and.setLeft(lhs);
		lhs.setParent(and);
		and.setRight(rhs);
		rhs.setParent(and);
		return and;
	}

	public ConditionExpression addOr(ConditionExpression lhs, ConditionExpression rhs)
	{
		Or or = new Or();
		or.setLeft(lhs);
		lhs.setParent(or);
		or.setRight(rhs);
		rhs.setParent(or);
		return or;
	}

	public ConditionExpression addNot(ConditionExpression oper)
	{
		Not not = new Not();
		not.setOperand(oper);
		oper.setParent(not);
		return not;
	}

	public ConditionExpression addConditionOperand(String operand)
	{
		// special case if "true" or "false" are specified
		if (operand.equalsIgnoreCase("true"))
		{
			return new True();
		}
		else if (operand.equalsIgnoreCase("false"))
		{
			return new False();
		}
		else
		{
			ConditionVariable cv = new ConditionVariable();
			splitter.splitFmElemReference(operand, true);
			cv.setCondition(addConditionReference(splitter.getPack(), splitter.getConcern(), splitter.getFm(), splitter
					.getFmelem()));
			return cv;
		}
	}

	/**
	 * Adds a FilterOperatorType (i.e. an inclusion or exclusion)
	 * 
	 * @param filterop Inclusion or Exclusion (can be '=>' or '~>'
	 * @param lineNumber
	 */
	public void addFilterOperatorType(String filterop, int lineNumber)
	{
		EnableOperatorType eot;
		if (filterop.equals("=>"))
		{
			eot = new EnableOperator();
		}
		else if (filterop.equals("~>"))
		{
			eot = new DisableOperator();
		}
		else
		{
			Debug.out(Debug.MODE_ERROR, "COPPER", "Filter operation must be => or ~>");
			return;
		}
		eot.setParent(fe);
		eot.setDescriptionFileName(filename);
		eot.setDescriptionLineNumber(lineNumber);
		fe.setEnableOperatorType(eot);
		this.addToRepository(eot);
	}

	/**
	 * Adds a FilterElementCompOper (the character separating each
	 * FilterElement)
	 * 
	 * @param sep The separator: can be ',' (i.e. an OR) or null (if the last
	 *            FilterElement)
	 * @param line
	 */
	public void addFilterElementCompOper(String sep, int line)
	{ // throws ModuleException {
		if (sep == null)
		{ // null, so we're at the last element
			VoidFilterElementCompOper vfeco = new VoidFilterElementCompOper();
			vfeco.setDescriptionFileName(filename);
			vfeco.setDescriptionLineNumber(line);
			vfeco.setParent(fe);
			fe.setRightOperator(vfeco);
			this.addToRepository(vfeco);
		}
		else if (",".equals(sep))
		{ // not the last one
			CORfilterElementCompOper cfeco = new CORfilterElementCompOper();
			cfeco.setDescriptionFileName(filename);
			cfeco.setDescriptionLineNumber(line);
			cfeco.setParent(fe);
			fe.setRightOperator(cfeco);
			this.addToRepository(cfeco);
		}
		else
		{
			// throw new ModuleException("no comma");
			Debug.out(Debug.MODE_WARNING, "COPPER", "no comma"); // fixme:
			// exception
		}
	}

	/**
	 * Adds a FilterCompOper (the character separating each Filter)
	 * 
	 * @param sep The separator: can be ';' (i.e. continue to next)
	 * @param line
	 */
	public void addFilterCompOper(String sep, int line)
	{
		if (sep.equals(";"))
		{ // currently only semicolon, no way to 'see' last element
			SEQfilterCompOper sfco = new SEQfilterCompOper();
			sfco.setDescriptionFileName(filename);
			sfco.setDescriptionLineNumber(line);
			if (parsingInput)
			{
				sfco.setParent(inf);
				inf.setRightOperator(sfco);
			}
			else
			{
				sfco.setParent(of);
				of.setRightOperator(sfco);
			}
			this.addToRepository(sfco);
		}
		else
		{
			Debug.out(Debug.MODE_WARNING, "COPPER", "no semicolon"); // fixme:
			// exception
		}
	}

	/**
	 * Adds the whole [target.selctor]target.selector part
	 * 
	 * @param objv All the targets and selectors (as Strings)
	 * @param typev All the types of the first selector (e.g. method(a.b.c.int,
	 *            x.y.z.int, String). Contains Vectors with the complete
	 *            packages of the parameters
	 * @param typev2 All the types of the second selector Contains Vectors with
	 *            the complete packages of the parameters
	 * @param orgmatching Whether the matching is name or signature (0=name,
	 *            1=signature)
	 */
	public void addMessagePattern( /*
									 * Vector objv, Vector typev, Vector typev2,
									 * int orgmatching,
									 */)
	{
		// int matching;

		mpat = new MatchingPatternAST();
		mpat.setParent(fe);
		// fe.addMatchingPattern(mpat);
		fe.setMatchingPattern(mpat);
		this.addToRepository(mpat);

		/*
		 * if (orgmatching == MESSAGEP) { matching = 1; //signature matching is
		 * the default } else { matching = orgmatching; } //now create the
		 * MatchingPattern with selectors etc. switch (objv.size()) { case 1:
		 * //selector addMatchingPart("*", (String) objv.elementAt(0), typev,
		 * matching,0); //only add if no braces specified if (orgmatching ==
		 * MESSAGEP) addSubstitutionPart("*", (String) objv.elementAt(0),
		 * typev,0); break; case 2: //target.selector addMatchingPart((String)
		 * objv.elementAt(0), (String) objv.elementAt(1), typev, matching,0);
		 * //only add if no braces specified if (orgmatching == MESSAGEP)
		 * addSubstitutionPart((String) objv.elementAt(0), (String)
		 * objv.elementAt(1), typev,0); break; case 3:
		 * //[selector]target.selector or <selector>target.selector
		 * addMatchingPart("*", (String) objv.elementAt(0), typev, matching,0);
		 * addSubstitutionPart((String) objv.elementAt(1), (String)
		 * objv.elementAt(2), typev2,0); break; case 4:
		 * //[target.selector]target.selector or
		 * <target.selector>target.selector addMatchingPart((String)
		 * objv.elementAt(0), (String) objv.elementAt(1), typev, matching,0);
		 * addSubstitutionPart((String) objv.elementAt(2), (String)
		 * objv.elementAt(3), typev2,0); break; default:
		 * Debug.out(Debug.MODE_WARNING, "COPPER", "error"); //fixme: throw
		 * exception break; }
		 */

		// should not happen anymore
		/*
		 * TODO WM: can this be removed? if ((mpat.getMatchingPart() == null) &&
		 * (mpat.getSubstitutionPart() != null)) { Debug.out(Debug.MODE_WARNING,
		 * "COPPER", "Error: no matching, only substitution"); }
		 */
	}

	public void setMessagePatternList(boolean isMessageList)
	{
		mpat.setIsMessageList(isMessageList);
	}

	/**
	 * Adds a MatchingPart
	 * 
	 * @param target The target
	 * @param selector The selector
	 * @param argTypes All the types of the selector (i.e. method(int, String))
	 *            Consists of multiple vectors within this vector (each
	 *            parameter a separate vector)
	 * @param matching Whether the matching is name or signature (0=name,
	 *            1=signature)
	 * @param lineNumber
	 * @param paratype
	 */
	public void addMatchingPart(String target, String selector, Vector argTypes, int matching, int paratype,
			int lineNumber)
	{
		workingOnMatching = true; // busy with matching
		mp = new MatchingPartAST();
		mp.setParent(mpat);
		mp.setDescriptionFileName(filename);
		mp.setDescriptionLineNumber(lineNumber);
		mpat.addMatchingPart(mp);
		this.addToRepository(mp);

		if (target != null)
		{
			addTarget(target);
		}
		else
		{
			addTarget("*");
		}
		ta.setParent(mp);

		if (selector != null)
		{
			if (paratype == 1)
			{
				addSelector(selector, argTypes, lineNumber);
				s.setParent(mp);
			}
			if (paratype == 2)
			{
				addParameterizedSelector(selector, argTypes, lineNumber);
				s.setParent(mp);
			}
			if (paratype == 3)
			{ // lists are currently handled as single, and REXREF transforms
				// it to a list of matchings. Therefore the boolean
				addParameterizedSelector(selector, argTypes, lineNumber);
				s.setParent(mp);
				(((ParameterizedMessageSelectorAST) s)).setList(true);
			}
		}
		addMatchingType(matching);
		mp.setMatchType(mt);
	}

	/**
	 * Adds a MatchingType
	 * 
	 * @param matching Whether the matching is name or signature (0=name,
	 *            1=signature)
	 */
	public void addMatchingType(int matching)
	{
		if (matching == 0)
		{
			mt = new NameMatchingType();
		}
		else if (matching == 1)
		{
			mt = new SignatureMatchingType();
		}
		mt.setParent(mp);
		this.addToRepository(mt);
	}

	/**
	 * Adds a SubstitutionPart
	 * 
	 * @param target The target
	 * @param selector The selector
	 * @param argTypes All the types of the selector (i.e. method(int, String))
	 *            Consists of multiple vectors within this vector (each
	 *            parameter a separate vector)
	 * @param lineNumber
	 */
	public void addSubstitutionPart(String target, String selector, Vector argTypes, int lineNumber)
	{
		workingOnMatching = false; // busy with substitution
		sp = new SubstitutionPartAST();
		sp.setDescriptionFileName(filename);
		sp.setDescriptionLineNumber(lineNumber);
		sp.setParent(mpat);
		mpat.addSubstitutionPart(sp);
		this.addToRepository(sp);

		if (target != null)
		{
			addTarget(target);
		}
		else
		{
			addTarget("*");
		}
		if (selector != null)
		{
			addSelector(selector, argTypes, lineNumber);
		}
	}

	/**
	 * Adds a Target object to a Matching- or SubstitutionPart
	 * 
	 * @param name The target
	 */
	public void addTarget(String name)
	{
		ta = new Target();
		// fixme: do something special with inner here
		// fixme: do something special with * here
		ta.setName(name);
		// fixme: putting in the same thing twice basically
		// even though you can only specify a NAME only in the grammar, we still
		// create a reference here
		splitter.splitFmElemReference(name, true);
		// ta.setRef(addFilterModuleElementReference(splitPath.getPack(),
		// splitPath.getConcern(), splitPath.getFm(), splitPath.getFmelem()));
		ta.setRef(addDeclaredObjectReference(splitter.getPack(), splitter.getConcern(), splitter.getFm(), splitter
				.getFmelem()));
		this.addToRepository(ta);

		if (workingOnMatching)
		{
			ta.setParent(mp);
			mp.setTarget(ta);
		}
		else
		{
			ta.setParent(sp);
			sp.setTarget(ta);
		}
	}

	/**
	 * Adds a Selector object to a Matching- or SubstitutionPart
	 * 
	 * @param name The method name
	 * @param typev The types of parameters of the method Consists of Vectors
	 *            within a Vector (each parameter has a separate Vector, so it
	 *            can contain a package in front of the type)
	 * @param lineNumber
	 */
	public void addSelector(String name, Vector typev, int lineNumber)
	{
		int j;

		// fixme: do something special with * here
		s = new MessageSelectorAST();
		s.setDescriptionFileName(filename);
		s.setDescriptionLineNumber(lineNumber);
		s.setName(name);
		this.addToRepository(s);
		for (j = 0; j < typev.size(); j++)
		{
			s.addParameterType(addConcernReference((Vector) typev.elementAt(j)));
		}

		if (workingOnMatching)
		{
			mp.setSelector(s);
			s.setParent(mp);
		}
		else
		{
			sp.setSelector(s);
			s.setParent(sp);
		}
	}

	/**
	 * Adds a ParameterizedSelectorAST object to a Matching //- or
	 * SubstitutionPart
	 * 
	 * @param name The method name
	 * @param typev The types of parameters of the method Consists of Vectors
	 *            within a Vector (each parameter has a separate Vector, so it
	 *            can contain a package in front of the type)
	 * @param lineNumber
	 */
	public void addParameterizedSelector(String name, Vector typev, int lineNumber)
	{
		// fixme: do something special with * here
		ParameterizedMessageSelectorAST temp = new ParameterizedMessageSelectorAST();
		s = temp;
		s.setDescriptionFileName(filename);
		s.setDescriptionLineNumber(lineNumber);
		s.setName(name);
		this.addToRepository(s);
		for (int j = 0; j < typev.size(); j++)
		{
			s.addParameterType(addConcernReference((Vector) typev.elementAt(j)));
		}

		if (workingOnMatching)
		{
			mp.setSelector(s);
			s.setParent(mp);
		}
		else
		{
			sp.setSelector(s);
			s.setParent(sp);
		}
	}

	/**
	 * Adds an OutputFilter to the repository
	 * 
	 * @param name name of the inputfilter
	 * @param type the type of the filter (e.g. Meta)
	 * @throws SemanticException when
	 *             internals/externals/inputfilters/outputfilters have duplicate
	 *             identifiers
	 * @param lineNumber
	 */
	public void addOutputFilter(String name, Vector type, int lineNumber) throws SemanticException
	{
		parsingInput = false;
		of = new FilterAST();
		of.setName(name);
		of.setDescriptionLineNumber(lineNumber);
		of.setDescriptionFileName(filename);
		of.setParent(fm);
		// of.setTypeImplementation(addConcernReference(type)); //turned of
		// because a filtertype is not a concern anymore
		addFilterType((String) type.lastElement(), lineNumber);
		if (fm.addOutputFilter(of))
		{
			this.addToRepository(of);
		}
		else
		{
			throw new SemanticException("Identifier not unique within filtermodule", of.getDescriptionFileName(),
					of.descriptionLineNumber, 0);
		}
	}

	// - SUPERIMPOSITION STUFF
	// -------------------------------------------------------------------------------------------

	/**
	 * Adds the SuperImposition to the current CpsConcern
	 */
	public void addSuperImposition()
	{
		si = new SuperImposition();
		si.setParent(cpsc);
		cpsc.setSuperImposition(si);
		this.addToRepository(si);

		// create a special 'self' reference
		// concernSelf = new SelectorDefinition();
		// concernSelf.setName("self");
		// concernSelf.setParent(si);
		//
		// concernSelfSelClass = new SelClass();
		// concernSelfSelClass.setClassName(cpsc.getName());
		// concernSelfSelClass.setParent(concernSelf);
		// concernSelf.addSelExpression(concernSelfSelClass);
		// don't add this to the repository, unless needed

		// FIXME:
		addSelectorDefinition("self", 0);
		// addSelectorExpression(1, cpsc.getName());
		Vector vtemp = new Vector();
		// vtemp.add(cpsc.getName());
		Iterator cpsit = StringConverter.stringToStringList(cpsc.getQualifiedName(), ".");
		while (cpsit.hasNext())
		{
			vtemp.add(cpsit.next());
		}
		addSelectorExpression(1, vtemp);
	}

	/**
	 * Creates a SelectorDefinition
	 * 
	 * @param name name of the selector
	 * @param lineNumber
	 */
	public void addSelectorDefinition(String name, int lineNumber)
	{
		sd = new SelectorDefinition();
		sd.setName(name);
		sd.setParent(si);
		sd.setDescriptionFileName(filename);
		sd.setDescriptionLineNumber(lineNumber);
		si.addSelectorDefinition(sd);
		this.addToRepository(sd);
	}

	/**
	 * Creates a SelectorExpression (i.e. { * = A }
	 * 
	 * @param type Whether to select the main class only or subclasses also
	 *            (0=include subclasses, 1=just main class)
	 * @param name Name of the class to select (can include package)
	 */
	public void addSelectorExpression(int type, Vector name)
	{
		if (type == 1)
		{ // just the main class
			SelClass sc = new SelClass();
			sc.setClass(addConcernReference(name));
			sc.setClassName((String) name.lastElement()); // fixme: should be
			// removed (double)
			// sc.setClassName(name);
			sc.setParent(sd);
			sd.addSelExpression(sc);
			this.addToRepository(sc);
		}
		else
		{ // main class and subclasses
			SelClassAndSubClasses scasc = new SelClassAndSubClasses();
			scasc.setClass(addConcernReference(name));
			scasc.setClassName((String) name.lastElement()); // fixme: should
			// be removed
			// removed
			// (double)
			// scasc.setClassName(name);
			scasc.setParent(sd);
			sd.addSelExpression(scasc);
			this.addToRepository(scasc);
		}
	}

	/**
	 * Creates a PredicateSelectorExpression (i.e. { Class | isClass(Class,
	 * 'SomeName') } )
	 * 
	 * @param variable - the variable to be used as 'output' of the predicate
	 * @param predicate - a prolog predicate resulting in a set of concerns
	 * @param line
	 */
	public void addPredicateSelectorExpression(String variable, String predicate, int line)
	{
		// predicate = predicate.substring(1,predicate.length()); //Cut of the |
		// and } at start and end
		PredicateSelector selector = new PredicateSelector(variable, predicate);
		selector.setDescriptionFileName(filename);
		selector.setDescriptionLineNumber(line);
		selector.setParent(sd);
		sd.addSelExpression(selector);
		// Main.all.add(selector); // I don't believe this is really necessary
	}

	/**
	 * Creates a new ConditionBinding (i.e. sel <- {a, b})
	 * 
	 * @param name the selector (can include package + concern)
	 * @param line
	 */
	public void addConditionBinding(Vector name, int line)
	{
		cb = new ConditionBinding();
		splitter.splitConcernElemReference(name, true);
		cb.setSelector(addSelectorRef(splitter.getPack(), splitter.getConcern(), splitter.getConcernelem(), line));
		cb.setParent(si);
		si.addConditionBinding(cb);
		this.addToRepository(cb);
	}

	/**
	 * Adds a condition to the current binding
	 * 
	 * @param name Name of the condition (may include package + concern + fm)
	 * @param starpresent Whether or not to select all conditions in a
	 *            filtermodule (1=yes)
	 */
	public void addConditionName(Vector name, boolean starpresent)
	{
		if (starpresent)
		{ // all conditions in a filtermodule
			StarCondition starc = new StarCondition();
			name.add("*"); // just add a star here so the splitting will be
			// performed correctly
			splitter.splitFmElemReference(name, true);
			starc.setPackage(splitter.getPack());
			starc.setConcern(splitter.getConcern());
			starc.setFilterModule(splitter.getFm());
			cb.addCondition(starc);
			this.addToRepository(starc);
		}
		else
		{ // only condition name, no star
			splitter.splitFmElemReference(name, true);
			cb.addCondition(addConditionReference(splitter.getPack(), splitter.getConcern(), splitter.getFm(), splitter
					.getFmelem()));
		}
	}

	/**
	 * Creates a new MethodBinding (i.e. sel <- {a, b})
	 * 
	 * @param name the selector (can include package + concern)
	 * @param line
	 */
	public void addMethodBinding(Vector name, int line)
	{
		mb = new MethodBinding();
		splitter.splitConcernElemReference(name, true);
		mb.setSelector(addSelectorRef(splitter.getPack(), splitter.getConcern(), splitter.getConcernelem(), line));
		mb.setParent(si);
		si.addMethodBinding(mb);
		this.addToRepository(mb);
	}

	/**
	 * Adds a method to the current binding
	 * 
	 * @param name Name of the method (can include pack + concern)
	 * @param starpresent Whether or not to select all methods in a filtermodule
	 *            (1=yes)
	 * @param typelist Types of the parameters in the method (i.e.
	 *            method(String, int)) (optional if no parameters)
	 */
	public void addMethodName(Vector name, boolean starpresent, Vector typelist)
	{
		if (starpresent)
		{ // all method in a filtermodule
			StarMethod starm = new StarMethod();
			name.add("*"); // just add a star here so the splitting will be
			// performed correctly
			splitter.splitFmElemReference(name, true);
			starm.setPackage(splitter.getPack());
			starm.setConcern(splitter.getConcern());
			starm.setFilterModule(splitter.getFm());
			mb.addMethod(starm);
			this.addToRepository(starm);
		}
		else
		{ // no star
			splitter.splitFmElemReference(name, true);
			mb.addMethod(addMethodReference(splitter.getPack(), splitter.getConcern(), splitter.getFm(), splitter
					.getFmelem(), typelist));
		}
	}

	/**
	 * Creates a new FilterModuleBinding
	 * 
	 * @param name the selector (can include package + concern)
	 * @param line
	 */
	public FilterModuleBinding addFilterModuleBinding(Vector name, int line)
	{
		fmb = new FilterModuleBinding();
		fmb.setDescriptionFileName(filename);
		fmb.setDescriptionLineNumber(line);

		splitter.splitConcernElemReference(name, true);

		fmb.setSelector(addSelectorRef(splitter.getPack(), splitter.getConcern(), splitter.getConcernelem(), line));
		fmb.setParent(si);
		si.addFilterModuleBinding(fmb);
		this.addToRepository(fmb);

		return fmb;
	}

	public void bindFilterModuleCondition(FilterModuleBinding binding, String conditionId) throws SemanticException
	{
		Condition condition = si.getFilterModuleCondition(conditionId);

		if (condition == null)
		{
			throw new SemanticException("Identifier " + conditionId + " cannot be bound to a filter module condition",
					binding.getDescriptionFileName(), binding.descriptionLineNumber, 0);
		}

		binding.setFilterModuleCondition(condition);
	}

	/**
	 * Adds a filtermodule to the current binding
	 * 
	 * @param name Name of the fm (may include package + concern)
	 * @param args
	 * @param line
	 */
	public void addFilterModuleName(Vector name, Vector args, int line)
	{
		FilterModuleReference fmref = new FilterModuleReference();
		fmref.setDescriptionFileName(filename);
		fmref.setDescriptionLineNumber(line);
		splitter.splitConcernElemReference(name, true);
		fmref.setPackage(splitter.getPack());
		fmref.setConcern(splitter.getConcern());
		fmref.setName(splitter.getConcernelem());
		fmb.addFilterModule(fmref);
		this.addToRepository(fmref);

		for (int i = 0; i < args.size(); i++)
		{
			FilterModuleParameter fmp = new FilterModuleParameter();
			fmp.setDescriptionFileName(filename);
			fmp.setDescriptionLineNumber(line);
			fmp.setValue(args.elementAt(i));
			fmref.addArg(fmp);
			this.addToRepository(fmp);
		}
	}

	public void addFMOrderingConstraint(String fm, String fm1)
	{
		SyntacticOrderingConstraint constraint = (SyntacticOrderingConstraint) this.orderingconstraints.get(fm);
		if (constraint == null) // First constraint
		{
			constraint = new SyntacticOrderingConstraint(fm);
			constraint.addRightFilterModule(fm1);
			this.orderingconstraints.put(fm, constraint);
		}
		else
		{
			constraint.addRightFilterModule(fm1);
		}
	}

	/**
	 * Creates a new AnnotationBinding
	 * 
	 * @param name the selector (can include package + concern)
	 * @param line
	 */
	public void addAnnotationBinding(Vector name, int line)
	{
		annotBinding = new AnnotationBinding();

		splitter.splitConcernElemReference(name, true);
		annotBinding.setSelector(addSelectorRef(splitter.getPack(), splitter.getConcern(), splitter.getConcernelem(),
				line));
		annotBinding.setParent(si);
		si.addAnnotationBinding(annotBinding);
		this.addToRepository(annotBinding);
	}

	/**
	 * Adds an annotation to the current binding
	 * 
	 * @param name Name of the annotation (may include package + concern)
	 */
	public void addAnnotationName(Vector name)
	{
		ConcernReference cref = new ConcernReference();
		splitter.splitConcernReference(name);
		cref.setPackage(splitter.getPack());
		cref.setName(splitter.getConcern());
		annotBinding.addAnnotation(cref);
		this.addToRepository(cref);
	}

	// - IMPLEMENTATION STUFF
	// -----------------------------------------------------------------------------------------

	/**
	 * Adds an assembly as implementationpart
	 * 
	 * @param className Name of the already compiled assembly (e.g.
	 *            a.b.c.String)
	 */
	public void addCompiledImplementation(Vector className)
	{
		CompiledImplementation compimp = new CompiledImplementation();
		// compimp.setName(className);
		// compimp.setClassName(cpsc.getName()); //default assembly class is
		// className = concern className
		compimp.setClassName(className);
		cpsc.setImplementation(compimp);
		// System.out.println("Compiled source: "+compimp.getClassName());
		// PrimitiveConcern concern = new PrimitiveConcern();
		this.addToRepository(compimp);
		// ds.addObject(compimp.getClassName(),concern);
	}

	/**
	 * Adds a sourcefile as implementationpart
	 * 
	 * @param lang Language of the implementation (i.e. to call the correct
	 *            compiler)
	 * @param filename Name of the file containing the source
	 */
	public void addSourceFile(String lang, String srcfilename)
	{
		SourceFile sf = new SourceFile();
		sf.setLanguage(lang);
		sf.setSourceFile(srcfilename);
		cpsc.setImplementation(sf);
		this.addToRepository(sf);
	}

	/**
	 * Adds embedded sourcecode as implementationpart
	 * 
	 * @param lang Language of the embedded source (i.e. to call the correct
	 *            compiler)
	 * @param filename Name of the file to which the source needs to be saved
	 * @param line
	 * @param className
	 */
	public void addEmbeddedSource(String lang, Vector className, String targetFilename, int line)
	{
		Source src = new Source();
		src.setLanguage(lang);
		src.setClassName(className);
		src.setSourceFile(targetFilename);
		src.setDescriptionFileName(filename);
		src.setDescriptionLineNumber(line);
		src.setSource(embeddedCode);
		cpsc.setImplementation(src);
		this.addToRepository(src);
	}

	// - GENERAL REFERENCE FUNCTIONS
	// -------------------------------------------------------------------------------

	/**
	 * Creates a reference to a Concern (or any of its subclasses); resolved in
	 * REXREF
	 * 
	 * @param pack Package / namespace of the concern
	 * @param name name Name of the Concern
	 * @return The created reference
	 */
	public ConcernReference addConcernReference(Vector pack, String name)
	{
		ConcernReference cref = new ConcernReference();
		cref.setPackage(pack);
		cref.setName(name);
		this.addToRepository(cref);
		return cref;
	}

	/**
	 * Creates a reference to a Concern (or any of its subclasses); resolved in
	 * REXREF
	 * 
	 * @param pack Package + concern name
	 * @return The created reference
	 */
	public ConcernReference addConcernReference(Vector pack)
	{
		ConcernReference cref = new ConcernReference();
		splitter.splitConcernReference(pack);
		cref.setPackage(splitter.getPack());
		cref.setName(splitter.getConcern());
		this.addToRepository(cref);
		return cref;
	}

	/**
	 * Creates a reference to a LabeledConcern
	 * 
	 * @param label label associated (e.g. the parameter name: argx:x.y.z.int)
	 * @param pack Package / namespace of the concern
	 * @param type the type of the concern
	 * @return The created reference
	 */
	public LabeledConcernReference addLabeledConcernReference(String label, Vector pack, String type)
	{
		LabeledConcernReference lcref = new LabeledConcernReference();
		lcref.setPackage(pack);
		lcref.setName(type); // fixme: change to setType??
		lcref.setLabel(label);
		this.addToRepository(lcref);
		return lcref;
	}

	/**
	 * Creates a reference to a LabeledConcern
	 * 
	 * @param pack Package + type
	 * @param label label associated (e.g. the parameter name: argx:x.y.z.int)
	 * @return The created reference
	 */
	public LabeledConcernReference addLabeledConcernReference(String label, Vector pack)
	{
		splitter.splitConcernReference(pack);
		return addLabeledConcernReference(label, splitter.getPack(), splitter.getConcern());
	}

	/**
	 * Creates a reference to a FilterModuleElementReference
	 * 
	 * @param pack Package + type ...
	 * @return The created reference
	 */
	// public FilterModuleElementReference
	// addFilterModuleElementReference(Vector pack, String concern, String
	// filterm, String fmelem) {
	// fmeref = new FilterModuleElementReference();
	// fmeref.setFilterModule(filterm);
	// fmeref.setPackage(pack);
	// fmeref.setConcern(concern);
	// fmeref.setName(fmelem);
	// Main.all.add(fmeref);
	// return(fmeref);
	// }
	/**
	 * Creates a new reference to a selector; will be resolved in REXREF
	 * 
	 * @param pack Name of the package containing the concern
	 * @param cname Name of the concern containing the selector
	 * @param sname Name of the selector
	 * @return the created reference
	 * @param line
	 */
	public SelectorReference addSelectorRef(Vector pack, String cname, String sname, int line)
	{
		if ("self".equals(sname))
		{ // fixme: should not happen here, but in the syntactic sugar part
			// if(selfAdded == false) {
			// //we use it, so add it to the repository
			// si.addSelectorDefinition(sd);
			// Main.all.add(concernSelf);
			// Main.all.add(concernSelfSelClass);
			// if(Main.debug) System.out.println("self used, added to all");
			// selfAdded = true;
			// }
		}
		SelectorReference sref = new SelectorReference();
		sref.setDescriptionFileName(filename);
		sref.setDescriptionLineNumber(line);
		sref.setPackage(pack);
		sref.setConcern(cname);
		sref.setName(sname);
		this.addToRepository(sref);
		return sref;
	}

	/**
	 * Creates a new reference to a condition; will be resolved in REXREF
	 * 
	 * @param pack Name of the package
	 * @param concern Name of the concern containing the condition
	 * @param filterm Name of the filtermodule containing the condition
	 * @param name Name of the condition
	 * @return the created reference
	 */
	public ConditionReference addConditionReference(Vector pack, String concern, String filterm, String name)
	{
		ConditionReference condref = new ConditionReference();
		condref.setPackage(pack);
		condref.setConcern(concern);
		condref.setFilterModule(filterm);
		condref.setName(name);
		this.addToRepository(condref);
		return condref;
	}

	/**
	 * Creates a new reference to a method; will be resolved in REXREF
	 * 
	 * @param pack Package name
	 * @param concern Name of the concern containing the method
	 * @param filterm Name of the filtermodule containing the method
	 * @param name Name of the method
	 * @param typelist Types of the parameters of the method (i.e. int, String
	 *            etc.)
	 * @return the created reference
	 */
	public MethodReference addMethodReference(Vector pack, String concern, String filterm, String name, Vector typelist)
	{
		int i;

		MethodReference mref = new MethodReference();
		mref.setPackage(pack);
		mref.setConcern(concern);
		mref.setFilterModule(filterm);
		mref.setName(name);
		if (typelist != null)
		{
			for (i = 0; i < typelist.size(); i++)
			{
				mref.addParameter(addConcernReference((Vector) typelist.elementAt(i)));
			}
		}
		this.addToRepository(mref);
		return mref;
	}

	/**
	 * Creates a new reference to an intenal; will be resolved in REXREF
	 * 
	 * @param pack Name of the package
	 * @param concern Name of the concern containing the condition
	 * @param filterm Name of the filtermodule containing the condition
	 * @param name Name of the condition
	 * @return the created reference
	 */
	public DeclaredObjectReference addDeclaredObjectReference(Vector pack, String concern, String filterm, String name)
	{
		DeclaredObjectReference doref = new DeclaredObjectReference();
		doref.setPackage(pack);
		doref.setConcern(concern);
		doref.setFilterModule(filterm);
		doref.setName(name);
		this.addToRepository(doref);
		return doref;
	}

	// - MISC. STUFF
	// -----------------------------------------------------------------------------------------

	/**
	 * Finishes up the builder (misc. cleanups)
	 */
	public void finish()
	{
		// complete all the rightoperator fields
		completeInputFilters();
		completeOutputFilters();
		completeFilterElements();

		this.ds.addObject(FILTH.FILTER_ORDERING_SPEC, this.orderingconstraints);
	}

	/**
	 * Makes sure the rightOperator fields in all inputfilters point to the
	 * correct objects
	 */
	private void completeInputFilters()
	{
		// int i;
		FilterModule temp;
		Filter current;
		Filter next;

		for (Iterator it = ds.getAllInstancesOf(FilterModule.class); it.hasNext();)
		{
			temp = (FilterModule) it.next();
			Iterator it1 = temp.getInputFilterIterator();
			Iterator it2 = temp.getInputFilterIterator();
			if (it2.hasNext())
			{
				it2.next(); // increase by 1, so it is one ahead of it
			}
			while (it1.hasNext())
			{
				current = (Filter) it1.next();
				if (it2.hasNext())
				{
					next = (Filter) it2.next(); // check for next filter
				}
				else
				{
					next = null;
				}

				if (next != null)
				{
					current.getRightOperator().setRightArgument(next);
				}
				else
				{
					// we're at the last element. replace the current
					// rightoperator
					// we couldn't do this before this point in the code
					VoidFilterCompOper vfco = new VoidFilterCompOper();
					vfco.setParent(current);
					vfco.setRightArgument(null); // nothing after this
					current.setRightOperator(vfco);
					vfco.setDescriptionFileName(current.getDescriptionFileName());
					ds.addObject(vfco);
				}
			}
		}
	}

	/**
	 * Makes sure the rightOperator fields in all outputfilters point to the
	 * correct objects
	 */
	private void completeOutputFilters()
	{
		// int i;
		FilterModule temp;
		Filter current;
		Filter next;

		for (Iterator it = ds.getAllInstancesOf(FilterModule.class); it.hasNext();)
		{
			temp = (FilterModule) it.next();
			Iterator it1 = temp.getOutputFilterIterator();
			Iterator it2 = temp.getOutputFilterIterator();
			if (it2.hasNext())
			{
				it2.next(); // increase by 1, so it is one ahead of it
			}
			while (it1.hasNext())
			{
				current = (Filter) it1.next();
				if (it2.hasNext())
				{
					next = (Filter) it2.next(); // check for next filter
				}
				else
				{
					next = null;
				}

				if (next != null)
				{
					current.getRightOperator().setRightArgument(next);
				}
				else
				{
					// we're at the last element. replace the current
					// rightoperator
					// we couldn't do this before
					VoidFilterCompOper vfco = new VoidFilterCompOper();
					vfco.setParent(current);
					vfco.setRightArgument(null); // nothing after this
					current.setRightOperator(vfco);
					vfco.setDescriptionFileName(current.getDescriptionFileName());
					ds.addObject(vfco);
				}
			}
		}
	}

	/**
	 * Makes sure the rightOperator fields in all FilterElements point to the
	 * correct objects
	 */
	private void completeFilterElements()
	{
		// int i;
		FilterAST temp;
		FilterElementAST current;
		FilterElementAST next;

		for (Iterator it = ds.getAllInstancesOf(FilterAST.class); it.hasNext();)
		{
			temp = (FilterAST) it.next();
			Iterator it1 = temp.getFilterElementIterator();
			Iterator it2 = temp.getFilterElementIterator();
			if (it2.hasNext())
			{
				it2.next(); // increase by 1, so it is one ahead of it
			}
			while (it1.hasNext())
			{
				current = (FilterElementAST) it1.next();
				if (it2.hasNext())
				{
					next = (FilterElementAST) it2.next(); // check for next
					// filter
				}
				else
				{
					next = null;
				}

				if (next != null)
				{
					current.getRightOperator().setRightArgument(next);
				}
				else
				{
					current.getRightOperator().setRightArgument(null);
					// nothing after this
				}
			}
		}
	}

	// used by splitter
	public String getFm()
	{
		return fm.getName();
	}

	// used by splitter
	public String getCpsc()
	{
		return cpsc.getName();
	}
}
