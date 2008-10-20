/*
 * Created on 28-feb-2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package Composestar.Core.FIRE2.preprocessing;

import groove.explore.DefaultScenario;
import groove.explore.result.Acceptor;
import groove.explore.result.EmptyAcceptor;
import groove.explore.result.EmptyResult;
import groove.explore.result.FinalStateAcceptor;
import groove.explore.result.Result;
import groove.explore.result.SizedResult;
import groove.explore.strategy.AbstractStrategy;
import groove.explore.strategy.BranchingStrategy;
import groove.explore.strategy.LinearStrategy;
import groove.graph.Graph;
import groove.io.AspectGxl;
import groove.io.AspectualViewGps;
import groove.io.LayedOutXml;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.view.AspectualGraphView;
import groove.view.DefaultGrammarView;
import groove.view.FormatException;
import groove.view.aspect.AspectGraph;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import Composestar.Core.Annotations.ComposestarModule;
import Composestar.Core.CpsRepository2.FilterModules.FilterModule;
import Composestar.Core.CpsRepository2.SIInfo.ImposedFilterModule;
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

	private DefaultGrammarView generateFlowGrammar;

	private DefaultGrammarView runtimeGrammar;

	private static final AbstractStrategy LINEAR_STRATEGY = new LinearStrategy();

	private static final AbstractStrategy FULL_STRATEGY = new BranchingStrategy();

	private static final String GRAMMAR_FLOW = "groovegrammars2/flowmodel.gps";

	private static final String GRAMMAR_EXEC = "groovegrammars2/execmodel.gps";

	private static final boolean GROOVE_DEBUG = Boolean.getBoolean("composestar.fire2.groovedebug");

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
		// only process superimposed filter module instances, ignore the rest
		Set<FilterModule> filterModules = new HashSet<FilterModule>();
		for (ImposedFilterModule ifm : resources.repository().getAll(ImposedFilterModule.class))
		{
			filterModules.add(ifm.getFilterModule());
		}
		preprocess(filterModules);
		// preprocessMP(resources, filterModules);
		// TODO return error if something failed
		return ModuleReturnValue.Ok;
	}

	public void preprocess(Set<FilterModule> filterModules)
	{
		logger.debug("Starting FIRE Preprocessing");

		CPSTimer timer = CPSTimer.getTimer(ModuleNames.FIRE);

		for (FilterModule fm : filterModules)
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
	public void preprocessMP(CommonResources resources, Set<FilterModule> filterModules) throws ModuleException
	{
		logger.debug("Starting FIRE Preprocessing");

		ParallelTask ptask = new ParallelTask();
		ptask.setPerProcessor(1);
		for (FilterModule fm : filterModules)
		{
			ptask.addTask(new FirePreprocessTask(fm));
		}
		ptask.execute(null, resources);

		logger.debug("FIRE Preprocessing done");
	}

	private void preprocessModule(FilterModule module)
	{
		logger.debug("Preprocessing Filter Module: " + module.getFullyQualifiedName());

		// build AST:
		CPSTimer timer = CPSTimer.getTimer(ModuleNames.FIRE);

		timer.start("Create input AST %s", module.getFullyQualifiedName());
		Graph grooveAstIF = buildAst(module, FilterDirection.Input);
		timer.stop();
		timer.start("Create output AST %s", module.getFullyQualifiedName());
		Graph grooveAstOF = buildAst(module, FilterDirection.Output);
		timer.stop();

		if (GROOVE_DEBUG)
		{
			dumpGraphToFile(grooveAstIF, new File("./ast-" + FilterDirection.Input + "_"
					+ module.getFullyQualifiedName() + ".gst"));
			dumpGraphToFile(grooveAstOF, new File("./ast-" + FilterDirection.Output + "_"
					+ module.getFullyQualifiedName() + ".gst"));
		}

		// generate flow model:
		timer.start("Create input flow %s", module.getFullyQualifiedName());
		Graph grooveFlowModelIF = generateFlow(grooveAstIF);
		timer.stop();
		timer.start("Create output flow %s", module.getFullyQualifiedName());
		Graph grooveFlowModelOF = generateFlow(grooveAstOF);
		timer.stop();

		if (GROOVE_DEBUG)
		{
			dumpGraphToFile(grooveFlowModelIF, new File("./flow-" + FilterDirection.Input + "_"
					+ module.getFullyQualifiedName() + ".gst"));
			dumpGraphToFile(grooveFlowModelOF, new File("./flow-" + FilterDirection.Output + "_"
					+ module.getFullyQualifiedName() + ".gst"));
		}

		// // extract flowmodel:
		timer.start("Extract input flow model %s", module.getFullyQualifiedName());
		FlowModel flowModelIF = extractFlowModel(grooveFlowModelIF);
		timer.stop();
		timer.start("Extract output flow model %s", module.getFullyQualifiedName());
		FlowModel flowModelOF = extractFlowModel(grooveFlowModelOF);
		timer.stop();

		// Simulate execution:
		timer.start("Create input execution %s", module.getFullyQualifiedName());
		GTS stateSpaceIF = execute(grooveFlowModelIF);
		timer.stop();
		timer.start("Create output execution %s", module.getFullyQualifiedName());
		GTS stateSpaceOF = execute(grooveFlowModelOF);
		timer.stop();

		// extract statespace:
		// ExecutionModelExtractor executionModelExtractor = new
		// ExecutionModelExtractor();
		//
		// timer.start("Extract input execution model %s",
		// module.getFullyQualifiedName());
		// ExecutionModel executionModelIF =
		// executionModelExtractor.extract(stateSpaceIF, flowModelIF);
		// timer.stop();
		//
		// timer.start("Extract output execution model %s",
		// module.getFullyQualifiedName());
		// ExecutionModel executionModelOF =
		// executionModelExtractor.extract(stateSpaceOF, flowModelOF);
		// timer.stop();
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
			URL genUrl = Preprocessor.class.getResource(GRAMMAR_FLOW);
			String fileName = genUrl.getFile().replaceAll("%20", " ");
			logger.debug("Loading grammar: " + fileName);
			if (fileName.indexOf('!') >= 0)
			{
				// timer.start("Loading grammars from jar");
				// // load from jar:
				// JarGpsGrammar jarGpsLoader = new JarGpsGrammar();
				//
				// generateFlowGrammar = (DefaultGrammarView)
				// jarGpsLoader.unmarshal(GRAMMAR_FLOW);
				// runtimeGrammar = (DefaultGrammarView)
				// jarGpsLoader.unmarshal(GRAMMAR_EXEC);
				// timer.stop();
			}
			else
			{
				timer.start("Loading grammars");
				// load from directory:
				AspectualViewGps gpsLoader = new AspectualViewGps();

				File f = new File(genUrl.getFile().replaceAll("%20", " "));

				generateFlowGrammar = gpsLoader.unmarshal(f);

				URL runUrl = Preprocessor.class.getResource(GRAMMAR_EXEC);
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

	private void dumpGraphToFile(Graph graph, File toFile)
	{
		try
		{
			AspectGxl graphSaver = new AspectGxl(new LayedOutXml());
			AspectGraph saveGraph = AspectGraph.getFactory().fromPlainGraph(graph);
			graphSaver.marshalGraph(saveGraph, toFile);
		}
		catch (IOException e)
		{
			logger.error(e);
		}
	}

	private Graph buildAst(FilterModule module, FilterDirection dir)
	{
		Graph grooveAst = GrooveASTBuilderCN.createAST(module, dir);
		return grooveAst;
	}

	private Graph generateFlow(Graph ast)
	{
		DefaultScenario<GraphState> scenario = new DefaultScenario<GraphState>();
		scenario.setStrategy(LINEAR_STRATEGY);
		Result<GraphState> result = new SizedResult<GraphState>(1);
		scenario.setResult(result);
		FinalStateAcceptor acceptor = new FinalStateAcceptor();
		scenario.setAcceptor(acceptor);

		generateFlowGrammar.setStartGraph(new AspectualGraphView(AspectGraph.getFactory().fromPlainGraph(ast)));
		GTS gts;
		try
		{
			gts = new GTS(generateFlowGrammar.toGrammar());
		}
		catch (FormatException e)
		{
			logger.error(e);
			return null;
		}
		scenario.setGTS(gts);
		scenario.setState(gts.startState());

		try
		{
			result = scenario.play();
		}
		catch (InterruptedException e)
		{
			logger.error(e);
			return null;
		}

		if (result.getResult().isEmpty())
		{
			// TODO nice error
			throw new RuntimeException("FlowGraph could not be generated!");
		}
		else
		{
			GraphState state = result.getResult().iterator().next();
			return state.getGraph();
		}
	}

	private FlowModel extractFlowModel(Graph flowGraph)
	{
		return FlowModelExtractor.extract(flowGraph);
	}

	private GTS execute(Graph flowGraph)
	{
		DefaultScenario<Object> scenario = new DefaultScenario<Object>();
		scenario.setStrategy(FULL_STRATEGY);
		Result<Object> result = new EmptyResult<Object>();
		scenario.setResult(result);
		Acceptor<Object> acceptor = new EmptyAcceptor();
		scenario.setAcceptor(acceptor);

		runtimeGrammar.setStartGraph(new AspectualGraphView(AspectGraph.getFactory().fromPlainGraph(flowGraph)));
		GTS gts;
		try
		{
			gts = new GTS(runtimeGrammar.toGrammar());
		}
		catch (FormatException e)
		{
			logger.error(e);
			return null;
		}
		scenario.setGTS(gts);
		scenario.setState(gts.startState());

		try
		{
			result = scenario.play();
		}
		catch (InterruptedException e)
		{
			logger.error(e);
			return null;
		}
		return gts;
	}

	/**
	 * Task for FIRE2 in multi-threading mode
	 * 
	 * @author Michiel Hendriks
	 */
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
			timer.start(module.getFullyQualifiedName());
			preprocessModule(module);
			timer.stop();
		}
	}
}
