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
import groove.lts.explore.FullStrategy;
import groove.lts.explore.LinearStrategy;
import groove.trans.view.RuleViewGrammar;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;

import Composestar.Core.Annotations.ComposestarModule;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterModule;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.FIRE2.model.ExecutionModel;
import Composestar.Core.FIRE2.model.FIRE2Resources;
import Composestar.Core.FIRE2.model.FlowModel;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.ModuleNames;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Core.Resources.CommonResources;
import Composestar.Core.TASMAN.Manager;
import Composestar.Core.TASMAN.ParallelTask;
import Composestar.Core.TASMAN.Task;
import Composestar.Utils.Logging.CPSLogger;
import Composestar.Utils.Perf.CPSTimer;

/**
 * FIRE predicts the result of an incoming messages considering a filter set.
 * FIRE emulates each filter in the filter set and determines possible mappings
 * between the messages and actions. These combinations, with internal states,
 * are stored into the FIRE knowledge base. Providing a convenient interface,
 * FIRE allows other modules querying (and updating) the Reasoning Engine.
 * Modules that use FIRE are CORE, SECRET and SIGN.
 * 
 * @author Arjan de Roo
 */
@ComposestarModule(ID = ModuleNames.FIRE, dependsOn = { ModuleNames.COPPER })
public class Preprocessor implements CTCommonModule
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger(ModuleNames.FIRE);

	private LayedOutXml graphLoader;

	private RuleViewGrammar generateFlowGrammar;

	private RuleViewGrammar runtimeGrammar;

	private static final ExploreStrategy LINEAR_STRATEGY = new LinearStrategy();

	// private static final ExploreStrategy BARBED_STRATEGY = new
	// BarbedStrategy();

	private static final ExploreStrategy FULL_STRATEGY = new FullStrategy();

	private static final String GENERATE_FLOW_GRAMMAR_PATH = "groovegrammars/generateflow.gps";

	private static final String RUNTIME_GRAMMAR_PATH = "groovegrammars/runtime.gps";

	private static final boolean GROOVE_DEBUG = false;

	private static final File AST_OUT = new File("./ast.gst");

	private static final File FLOW_OUT = new File("./flow.gst");

	private static final File EXECUTION_OUT = new File("./execution.gst");

	// public static final String RESULT_ID = "FirePreprocessingResult";

	protected FIRE2Resources fire2Resources;

	public Preprocessor()
	{
		initialize();
	}

	private void initialize()
	{
		loadGrammars();
	}

	public ModuleReturnValue run(CommonResources resources) throws ModuleException
	{
		fire2Resources = resources.getResourceManager(FIRE2Resources.class, true);
		preprocess(resources.repository());
		// preprocessMP(resources);
		// TODO return error if something failed
		return ModuleReturnValue.Ok;
	}

	public void preprocess(DataStore ds)
	{
		Iterator<FilterModule> moduleIter = ds.getAllInstancesOf(FilterModule.class);

		logger.debug("Starting FIRE Preprocessing");

		CPSTimer timer = CPSTimer.getTimer(ModuleNames.FIRE);

		while (moduleIter.hasNext())
		{
			FilterModule module = moduleIter.next();
			timer.start(module.getOriginalQualifiedName());
			preprocessModule(module);
			timer.stop();
		}

		logger.debug("FIRE Preprocessing done");
	}

	// FIXME: fire is not complete MP safe yet
	public void preprocessMP(CommonResources resources) throws ModuleException
	{
		Iterator<FilterModule> moduleIter = resources.repository().getAllInstancesOf(FilterModule.class);

		logger.debug("Starting FIRE Preprocessing");

		ParallelTask ptask = new ParallelTask();
		ptask.setPerProcessor(1);
		while (moduleIter.hasNext())
		{
			FilterModule module = moduleIter.next();
			ptask.addTask(new FirePreprocessTask(module));
		}
		ptask.execute(null, resources);

		logger.debug("FIRE Preprocessing done");
	}

	private void preprocessModule(FilterModule module)
	{
		logger.debug("Preprocessing Filter Module: " + module.getQualifiedName());

		// build AST:
		GrooveASTBuilder astBuilder = new GrooveASTBuilder();
		Graph grooveAstIF = buildAst(astBuilder, module, true);
		Graph grooveAstOF = buildAst(astBuilder, module, false);

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
		ExecutionModelExtractor executionModelExtractor = new ExecutionModelExtractor();
		ExecutionModel executionModelIF = executionModelExtractor.extract(stateSpaceIF, flowModelIF);
		ExecutionModel executionModelOF = executionModelExtractor.extract(stateSpaceOF, flowModelOF);

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
			CPSTimer timer = CPSTimer.getTimer(ModuleNames.FIRE);
			URL genUrl = Preprocessor.class.getResource(GENERATE_FLOW_GRAMMAR_PATH);
			String fileName = genUrl.getFile().replaceAll("%20", " ");
			logger.debug("Loading grammar: " + fileName);
			if (fileName.indexOf('!') >= 0)
			{
				timer.start("Loading grammars from jar");
				// load from jar:
				JarGpsGrammar jarGpsLoader = new JarGpsGrammar();

				generateFlowGrammar = (RuleViewGrammar) jarGpsLoader.unmarshal(GENERATE_FLOW_GRAMMAR_PATH);
				runtimeGrammar = (RuleViewGrammar) jarGpsLoader.unmarshal(RUNTIME_GRAMMAR_PATH);
				timer.stop();
			}
			else
			{
				timer.start("Loading grammars");
				// load from directory:
				GpsGrammar gpsLoader = new GpsGrammar(new LayedOutXml());

				File f = new File(genUrl.getFile().replaceAll("%20", " "));
				RuleViewGrammar rvg = (RuleViewGrammar) gpsLoader.unmarshal(f);

				generateFlowGrammar = rvg;

				URL runUrl = Preprocessor.class.getResource(RUNTIME_GRAMMAR_PATH);
				runtimeGrammar = (RuleViewGrammar) gpsLoader
						.unmarshal(new File(runUrl.getFile().replaceAll("%20", " ")));

				timer.stop();
			}
		}
		catch (Exception exc)
		{
			exc.printStackTrace();
			// throw new RuntimeException( "Loading grammars failed", exc );
		}
	}

	private Graph buildAst(GrooveASTBuilder astBuilder, FilterModule module, boolean forInputFilters)
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
		Iterator<DefaultGraphState> finalStates = gts.getFinalStates().iterator();

		if (!finalStates.hasNext())
		{
			// should never happen
			throw new RuntimeException("FlowGraph could not be generated!");
		}
		else
		{
			DefaultGraphState state = finalStates.next();

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

	class FirePreprocessTask extends Task
	{
		protected FilterModule module;

		public FirePreprocessTask(FilterModule forModule)
		{
			module = forModule;
		}

		@Override
		public void execute(Manager manager, CommonResources resources) throws ModuleException
		{
			CPSTimer timer = CPSTimer.getTimer(ModuleNames.FIRE);
			timer.start(module.getOriginalQualifiedName());
			preprocessModule(module);
			timer.stop();
		}
	}
}
