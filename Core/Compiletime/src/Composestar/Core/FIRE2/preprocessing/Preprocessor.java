/*
 * Created on 28-feb-2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package Composestar.Core.FIRE2.preprocessing;

import groove.graph.Graph;
import groove.io.GpsGrammar;
import groove.io.LayedOutXml;
import groove.io.XmlException;
import groove.lts.DefaultGraphState;
import groove.lts.ExploreStrategy;
import groove.lts.GTS;
import groove.lts.explore.BarbedStrategy;
import groove.lts.explore.FullStrategy;
import groove.lts.explore.LinearStrategy;
import groove.trans.view.RuleViewGrammar;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;

import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterModule;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.FIRE2.model.ExecutionModel;
import Composestar.Core.FIRE2.model.FIRE2Resources;
import Composestar.Core.FIRE2.model.FlowModel;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Core.Resources.CommonResources;
import Composestar.Utils.Logging.CPSLogger;

/**
 * @author Arjan de Roo
 */
public class Preprocessor implements CTCommonModule
{
	public final static String MODULE_NAME = "FIRE";

	protected static final CPSLogger logger = CPSLogger.getCPSLogger(MODULE_NAME);

	private LayedOutXml graphLoader;

	private RuleViewGrammar generateFlowGrammar;

	private RuleViewGrammar runtimeGrammar;

	private GrooveASTBuilder astBuilder;

	private ExecutionModelExtractor executionModelExtractor;

	private final static ExploreStrategy LINEAR_STRATEGY = new LinearStrategy();

	private final static ExploreStrategy BARBED_STRATEGY = new BarbedStrategy();

	private final static ExploreStrategy FULL_STRATEGY = new FullStrategy();

	private final static String GENERATE_FLOW_GRAMMAR_PATH = "groovegrammars/generateflow.gps";

	private final static String RUNTIME_GRAMMAR_PATH = "groovegrammars/runtime.gps";

	private final static boolean GROOVE_DEBUG = false;

	private final static File AST_OUT = new File("./ast.gst");

	private final static File FLOW_OUT = new File("./flow.gst");

	private final static File EXECUTION_OUT = new File("./execution.gst");

	// public final static String RESULT_ID = "FirePreprocessingResult";

	protected FIRE2Resources fire2Resources;

	public Preprocessor()
	{
		initialize();
	}

	private void initialize()
	{
		loadGrammars();

		astBuilder = new GrooveASTBuilder();
		executionModelExtractor = new ExecutionModelExtractor();
	}

	public void run(CommonResources resources) throws ModuleException
	{
		fire2Resources = resources.getResourceManager(FIRE2Resources.class, true);
		preprocess();
	}

	public void preprocess()
	{
		Iterator<FilterModule> moduleIter = DataStore.instance().getAllInstancesOf(FilterModule.class);

		logger.debug("Starting FIRE Preprocessing");

		while (moduleIter.hasNext())
		{
			FilterModule module = moduleIter.next();
			preprocessModule(module);
		}

		logger.debug("FIRE Preprocessing done");
	}

	private void preprocessModule(FilterModule module)
	{
		logger.debug("Preprocessing Filter Module: " + module.getQualifiedName());

		// build AST:
		Graph grooveAstIF = buildAst(module, true);
		Graph grooveAstOF = buildAst(module, false);

		// generate flow model:
		Graph grooveFlowModelIF = generateFlow(grooveAstIF);
		Graph grooveFlowModelOF = generateFlow(grooveAstOF);

		// extract flowmodel:
		FlowModel flowModelIF = extractFlowModel(grooveFlowModelIF);
		FlowModel flowModelOF = extractFlowModel(grooveFlowModelOF);

		// Simulate execution:
		GTS stateSpaceIF = execute(grooveFlowModelIF);
		GTS stateSpaceOF = execute(grooveFlowModelOF);

		// extract statespace:
		ExecutionModel executionModelIF = extractExecutionModel(stateSpaceIF, flowModelIF);
		ExecutionModel executionModelOF = extractExecutionModel(stateSpaceOF, flowModelOF);

		// store result:
		FirePreprocessingResult result = new FirePreprocessingResult(flowModelIF, executionModelIF, flowModelOF,
				executionModelOF);

		fire2Resources.addPreprocessingResult(module, result);
		// module.dynamicmap.put(RESULT_ID, result);
	}

	private void loadGrammars()
	{
		graphLoader = new LayedOutXml();

		try
		{
			URL genUrl = this.getClass().getResource(GENERATE_FLOW_GRAMMAR_PATH);
			String fileName = genUrl.getFile().replaceAll("%20", " ");
			logger.debug("Loading grammar: " + fileName);
			if (fileName.indexOf('!') >= 0)
			{
				// load from jar:
				JarGpsGrammar jarGpsLoader = new JarGpsGrammar();

				generateFlowGrammar = (RuleViewGrammar) jarGpsLoader.unmarshal(GENERATE_FLOW_GRAMMAR_PATH);
				runtimeGrammar = (RuleViewGrammar) jarGpsLoader.unmarshal(RUNTIME_GRAMMAR_PATH);
			}
			else
			{
				// load from directory:
				GpsGrammar gpsLoader = new GpsGrammar(new LayedOutXml());

				File f = new File(genUrl.getFile().replaceAll("%20", " "));
				RuleViewGrammar rvg = (RuleViewGrammar) gpsLoader.unmarshal(f);

				generateFlowGrammar = rvg;

				URL runUrl = this.getClass().getResource(RUNTIME_GRAMMAR_PATH);
				runtimeGrammar = (RuleViewGrammar) gpsLoader
						.unmarshal(new File(runUrl.getFile().replaceAll("%20", " ")));
			}
		}
		catch (Exception exc)
		{
			exc.printStackTrace();
			// throw new RuntimeException( "Loading grammars failed", exc );
		}
	}

	private Graph buildAst(FilterModule module, boolean forInputFilters)
	{
		Graph grooveAst = astBuilder.buildAST(module, forInputFilters);

		if (GROOVE_DEBUG)
		{
			// output ast:
			try
			{
				graphLoader.marshal(grooveAst, AST_OUT);
			}
			catch (XmlException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return grooveAst;
	}

	private Graph generateFlow(Graph ast)
	{
		generateFlowGrammar.setStartGraph(ast);
		GTS gts = generateFlowGrammar.gts();
		gts.setExploreStrategy(LINEAR_STRATEGY);
		try
		{
			gts.explore();
		}
		catch (InterruptedException exc)
		{
			// TODO nice exception handling
			exc.printStackTrace();
		}
		Iterator finalStates = gts.getFinalStates().iterator();

		if (!finalStates.hasNext())
		{
			// should never happen
			throw new RuntimeException("FlowGraph could not be generated!");
		}
		else
		{
			DefaultGraphState state = (DefaultGraphState) finalStates.next();

			Graph graph = state.getGraph();

			if (GROOVE_DEBUG)
			{
				// output flowgraph:
				try
				{
					graphLoader.marshal(graph, FLOW_OUT);
				}
				catch (XmlException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return graph;
		}
	}

	private FlowModel extractFlowModel(Graph flowGraph)
	{
		return FlowModelExtractor.extract(flowGraph);
	}

	private GTS execute(Graph flowGraph)
	{
		runtimeGrammar.setStartGraph(flowGraph);
		GTS gts = runtimeGrammar.gts();
		gts.setExploreStrategy(FULL_STRATEGY);
		try
		{
			gts.explore();
		}
		catch (InterruptedException exc)
		{
			// TODO nice exception handling
			exc.printStackTrace();
		}

		if (GROOVE_DEBUG)
		{
			// output execution-statespace:
			try
			{
				graphLoader.marshal(gts, EXECUTION_OUT);
			}
			catch (XmlException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return gts;
	}

	private ExecutionModel extractExecutionModel(GTS stateSpace, FlowModel flowModel)
	{
		return executionModelExtractor.extract(stateSpace, flowModel);
	}

}
