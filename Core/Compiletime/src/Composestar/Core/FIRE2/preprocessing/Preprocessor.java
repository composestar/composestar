/*
 * Created on 28-feb-2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package Composestar.Core.FIRE2.preprocessing;

import groove.explore.strategy.AbstractStrategy;
import groove.explore.strategy.BranchingStrategy;
import groove.explore.strategy.LinearStrategy;
import groove.graph.Graph;
import groove.io.AspectGxl;
import groove.io.AspectualViewGps;
import groove.io.LayedOutXml;
import groove.lts.GTS;
import groove.view.DefaultGrammarView;
import groove.view.aspect.AspectGraph;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;

import Composestar.Core.Annotations.ComposestarModule;
import Composestar.Core.CpsRepository2.Repository;
import Composestar.Core.CpsRepository2.FilterModules.FilterModule;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.FIRE2.model.FIRE2Resources;
import Composestar.Core.FIRE2.model.FlowModel;
import Composestar.Core.FIRE2.model.FireModel.FilterDirection;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.ModuleNames;
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

	/**
	 * USed to save the generated graphs to a .gst file
	 */
	private AspectGxl graphSaver;

	private DefaultGrammarView generateFlowGrammar;

	private DefaultGrammarView runtimeGrammar;

	private static final AbstractStrategy LINEAR_STRATEGY = new LinearStrategy();

	// private static final ExploreStrategy BARBED_STRATEGY = new
	// BarbedStrategy();

	private static final AbstractStrategy FULL_STRATEGY = new BranchingStrategy();

	private static final String GENERATE_FLOW_GRAMMAR_PATH = "groovegrammars/generateflow.gps";

	private static final String RUNTIME_GRAMMAR_PATH = "groovegrammars/runtime.gps";

	private static final boolean GROOVE_DEBUG = Boolean.getBoolean("composestar.fire2.groovedebug");

	private File debugOutFlow;

	private File debugOutExec;

	// public static final String RESULT_ID = "FirePreprocessingResult";

	protected FIRE2Resources fire2Resources;

	public Preprocessor()
	{
		initialize();
	}

	private void initialize()
	{
		graphSaver = new AspectGxl(new LayedOutXml());
		// FIXME: uncomment me
		// loadGrammars();
	}

	public ModuleReturnValue run(CommonResources resources) throws ModuleException
	{
		fire2Resources = resources.getResourceManager(FIRE2Resources.class, true);
		preprocess(resources.repository());
		// preprocessMP(resources);
		// TODO return error if something failed
		return ModuleReturnValue.Ok;
	}

	public void preprocess(Repository repository)
	{
		logger.debug("Starting FIRE Preprocessing");

		CPSTimer timer = CPSTimer.getTimer(ModuleNames.FIRE);

		// FIXME: only use the superimposed filter modules
		for (FilterModule fm : repository.getAll(FilterModule.class))
		{
			timer.start(fm.getFullyQualifiedName());
			preprocessModule(fm);
			timer.stop();
		}

		logger.debug("FIRE Preprocessing done");

		// FIXME remove this
		throw new RuntimeException("Intentional stop!");
	}

	// FIXME: fire is not complete MP safe yet
	public void preprocessMP(CommonResources resources) throws ModuleException
	{
		logger.debug("Starting FIRE Preprocessing");

		ParallelTask ptask = new ParallelTask();
		ptask.setPerProcessor(1);
		for (FilterModule fm : resources.repository().getAll(FilterModule.class))
		{
			ptask.addTask(new FirePreprocessTask(fm));
		}
		ptask.execute(null, resources);

		logger.debug("FIRE Preprocessing done");
	}

	private void preprocessModule(FilterModule module)
	{
		logger.debug("Preprocessing Filter Module: " + module.getFullyQualifiedName());

		if (GROOVE_DEBUG)
		{

			debugOutFlow = new File("./flow_" + module.getFullyQualifiedName() + ".gst");
			debugOutExec = new File("./exec_" + module.getFullyQualifiedName() + ".gst");
		}

		// build AST:
		Graph grooveAstIF = buildAst(module, FilterDirection.Input);
		Graph grooveAstOF = buildAst(module, FilterDirection.Output);

		// TODO: implement

		// // generate flow model:
		// Graph grooveFlowModelIF = generateFlow(grooveAstIF);
		// Graph grooveFlowModelOF = generateFlow(grooveAstOF);
		//
		// // extract flowmodel:
		// FlowModel flowModelIF = extractFlowModel(grooveFlowModelIF);
		// FlowModel flowModelOF = extractFlowModel(grooveFlowModelOF);
		//
		// // Simulate execution:
		// GTS stateSpaceIF = execute(grooveFlowModelIF);
		// GTS stateSpaceOF = execute(grooveFlowModelOF);
		//
		// // extract statespace:
		// ExecutionModelExtractor executionModelExtractor = new
		// ExecutionModelExtractor();
		// ExecutionModel executionModelIF =
		// executionModelExtractor.extract(stateSpaceIF, flowModelIF);
		// ExecutionModel executionModelOF =
		// executionModelExtractor.extract(stateSpaceOF, flowModelOF);
		//
		// // store result:
		// FirePreprocessingResult result = new
		// FirePreprocessingResult(flowModelIF, executionModelIF, flowModelOF,
		// executionModelOF);
		//
		// fire2Resources.addPreprocessingResult(module, result);
	}

	private void loadGrammars()
	{
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

				generateFlowGrammar = (DefaultGrammarView) jarGpsLoader.unmarshal(GENERATE_FLOW_GRAMMAR_PATH);
				runtimeGrammar = (DefaultGrammarView) jarGpsLoader.unmarshal(RUNTIME_GRAMMAR_PATH);
				timer.stop();
			}
			else
			{
				timer.start("Loading grammars");
				// load from directory:
				AspectualViewGps gpsLoader = new AspectualViewGps();

				File f = new File(genUrl.getFile().replaceAll("%20", " "));

				generateFlowGrammar = gpsLoader.unmarshal(f);

				URL runUrl = Preprocessor.class.getResource(RUNTIME_GRAMMAR_PATH);
				runtimeGrammar = gpsLoader.unmarshal(new File(runUrl.getFile().replaceAll("%20", " ")));

				timer.stop();
			}
		}
		catch (Exception exc)
		{
			exc.printStackTrace();
			// throw new RuntimeException( "Loading grammars failed", exc );
		}
	}

	private Graph buildAst(FilterModule module, FilterDirection dir)
	{
		Graph grooveAst = GrooveASTBuilderCN.createAST(module, dir);

		if (GROOVE_DEBUG)
		{
			// output ast:
			try
			{
				AspectGraph saveGraph = AspectGraph.getFactory().fromPlainGraph(grooveAst);
				graphSaver.marshalGraph(saveGraph, new File("./ast-" + dir + "_" + module.getFullyQualifiedName()
						+ ".gst"));
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
					graphSaver.marshal(graph, debugOutFlow);
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
				graphSaver.marshal(gts, debugOutExec);
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
