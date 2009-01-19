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
import groove.explore.strategy.BranchingStrategy;
import groove.explore.strategy.LinearStrategy;
import groove.graph.Graph;
import groove.io.AspectGxl;
import groove.io.LayedOutXml;
import groove.lts.GTS;
import groove.lts.GraphState;
import groove.trans.GraphGrammar;
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
import Composestar.Core.FIRE2.model.ExecutionModel;
import Composestar.Core.FIRE2.model.FIRE2Resources;
import Composestar.Core.FIRE2.model.FlowModel;
import Composestar.Core.FIRE2.model.FireModel.FilterDirection;
import Composestar.Core.FIRE2.preprocessing.io.StreamGpsLoader;
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
// @ComposestarModule(ID = ModuleNames.FIRE, dependsOn = { ModuleNames.COPPER })
public class Preprocessor implements CTCommonModule
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger(ModuleNames.FIRE);

	private DefaultGrammarView generateFlowGrammar;

	private DefaultGrammarView runtimeGrammar;

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

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.Master.CTCommonModule#getModuleName()
	 */
	public String getModuleName()
	{
		return ModuleNames.FIRE;
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.Master.CTCommonModule#getDependencies()
	 */
	public String[] getDependencies()
	{
		return new String[] { ModuleNames.COPPER };
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.Master.CTCommonModule#getImportance()
	 */
	public ModuleImportance getImportance()
	{
		return ModuleImportance.REQUIRED;
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
		return ModuleReturnValue.OK;
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
	}

	// FIXME: Groove is not MP safe
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

		GraphMetaData inputMeta = new GraphMetaData();
		GraphMetaData outputMeta = new GraphMetaData();

		timer.start("Create input AST %s", module.getFullyQualifiedName());
		Graph grooveAstIF = buildAst(module, FilterDirection.INPUT, inputMeta);
		timer.stop();
		timer.start("Create output AST %s", module.getFullyQualifiedName());
		Graph grooveAstOF = buildAst(module, FilterDirection.OUTPUT, outputMeta);
		timer.stop();

		if (GROOVE_DEBUG)
		{
			dumpGraphToFile(grooveAstIF, new File("./ast-" + FilterDirection.INPUT + "_"
					+ module.getFullyQualifiedName() + ".gst"));
			dumpGraphToFile(grooveAstOF, new File("./ast-" + FilterDirection.OUTPUT + "_"
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
			dumpGraphToFile(grooveFlowModelIF, new File("./flow-" + FilterDirection.INPUT + "_"
					+ module.getFullyQualifiedName() + ".gst"));
			dumpGraphToFile(grooveFlowModelOF, new File("./flow-" + FilterDirection.OUTPUT + "_"
					+ module.getFullyQualifiedName() + ".gst"));
		}

		// // // extract flowmodel:
		timer.start("Extract input flow model %s", module.getFullyQualifiedName());
		FlowModel flowModelIF = extractFlowModel(grooveFlowModelIF, inputMeta);
		timer.stop();
		timer.start("Extract output flow model %s", module.getFullyQualifiedName());
		FlowModel flowModelOF = extractFlowModel(grooveFlowModelOF, outputMeta);
		timer.stop();

		// Simulate execution:
		timer.start("Create input execution %s", module.getFullyQualifiedName());
		GTS stateSpaceIF = execute(grooveFlowModelIF);
		timer.stop();
		timer.start("Create output execution %s", module.getFullyQualifiedName());
		GTS stateSpaceOF = execute(grooveFlowModelOF);
		timer.stop();

		// extract statespace:
		ExecutionModelExtractor executionModelExtractor = new ExecutionModelExtractor();

		timer.start("Extract input execution model %s", module.getFullyQualifiedName());
		ExecutionModel executionModelIF = executionModelExtractor.extract(stateSpaceIF, flowModelIF, inputMeta);
		timer.stop();

		timer.start("Extract output execution model %s", module.getFullyQualifiedName());
		ExecutionModel executionModelOF = executionModelExtractor.extract(stateSpaceOF, flowModelOF, outputMeta);
		timer.stop();

		// store result:
		FirePreprocessingResult result =
				new FirePreprocessingResult(flowModelIF, executionModelIF, flowModelOF, executionModelOF);

		fire2Resources.addPreprocessingResult(module, result);
	}

	private void loadGrammars()
	{
		try
		{
			CPSTimer timer = CPSTimer.getTimer(ModuleNames.FIRE);
			URL genUrl = Preprocessor.class.getResource(GRAMMAR_FLOW);
			String fileName = genUrl.getFile().replaceAll("%20", " ");
			logger.debug("Loading grammar: " + fileName);

			StreamGpsLoader sgpsl = new StreamGpsLoader();
			timer.start("Loading grammars");
			generateFlowGrammar = sgpsl.unmarshal(Preprocessor.class, GRAMMAR_FLOW);
			runtimeGrammar = sgpsl.unmarshal(Preprocessor.class, GRAMMAR_EXEC);
			timer.stop();

			// // Groove's own limited loading mechanism
			// timer.start("Loading grammars");
			// // load from directory:
			// AspectualViewGps gpsLoader = new AspectualViewGps();
			//
			// File f = new File(genUrl.getFile().replaceAll("%20", " "));
			//
			// generateFlowGrammar = gpsLoader.unmarshal(f);
			//
			// URL runUrl = Preprocessor.class.getResource(GRAMMAR_EXEC);
			// runtimeGrammar = gpsLoader.unmarshal(new
			// File(runUrl.getFile().replaceAll("%20", " ")));
			//
			// timer.stop();
		}
		catch (Exception exc)
		{
			// TODO errors
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

	private Graph buildAst(FilterModule module, FilterDirection dir, GraphMetaData meta)
	{
		Graph grooveAst = GrooveASTBuilderCN.createAST(module, dir, meta);
		return grooveAst;
	}

	// /**
	// * Create a GraphGRammar for the given view and start graph
	// *
	// * @param view
	// * @param startGraph
	// * @return
	// */
	// private GraphGrammar getGrammar(DefaultGrammarView view, Graph
	// startGraph)
	// {
	// GraphGrammar result = new GraphGrammar(view.getName());
	// for (RuleView ruleView : view.getRuleMap().values())
	// {
	// try
	// {
	// // only add the enabled rules
	// if (ruleView.isEnabled())
	// {
	// SystemProperties properties = new SystemProperties();
	// AspectualRuleView rv = new AspectualRuleView(((AspectualRuleView)
	// ruleView).getAspectGraph(),
	// ruleView.getNameLabel(), properties);
	// result.add(rv.toRule());
	// }
	// }
	// catch (FormatException exc)
	// {
	// for (String error : exc.getErrors())
	// {
	// // TODO: better errors
	// logger.error(String.format("Format error in %s: %s",
	// ruleView.getNameLabel(), error));
	// }
	// return null;
	// }
	// }
	// result.setProperties(view.getProperties());
	// result.setStartGraph(startGraph);
	// try
	// {
	// result.setFixed();
	// }
	// catch (FormatException e)
	// {
	// // TODO: better errors
	// logger.error(e);
	// return null;
	// }
	// return result;
	// }

	private Graph generateFlow(Graph ast)
	{
		DefaultScenario<GraphState> scenario = new DefaultScenario<GraphState>();
		scenario.setStrategy(new LinearStrategy());
		Result<GraphState> result = new SizedResult<GraphState>(1);
		scenario.setResult(result);
		FinalStateAcceptor acceptor = new FinalStateAcceptor();
		scenario.setAcceptor(acceptor);

		GraphGrammar gram;
		synchronized (generateFlowGrammar)
		{
			generateFlowGrammar.setStartGraph(new AspectualGraphView(AspectGraph.getFactory().fromPlainGraph(ast)));
			try
			{
				gram = generateFlowGrammar.toGrammar();
			}
			catch (FormatException e)
			{
				logger.error(e);
				return null;
			}
		}

		GTS gts = new GTS(gram);

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

	private FlowModel extractFlowModel(Graph flowGraph, GraphMetaData meta)
	{
		return FlowModelExtractor.extract(flowGraph, meta);
	}

	private GTS execute(Graph flowGraph)
	{
		DefaultScenario<Object> scenario = new DefaultScenario<Object>();
		scenario.setStrategy(new BranchingStrategy());
		scenario.setResult(new EmptyResult<Object>());
		Acceptor<Object> acceptor = new EmptyAcceptor();
		scenario.setAcceptor(acceptor);

		GraphGrammar gram;
		synchronized (runtimeGrammar)
		{
			runtimeGrammar.setStartGraph(new AspectualGraphView(AspectGraph.getFactory().fromPlainGraph(flowGraph)));
			try
			{
				gram = runtimeGrammar.toGrammar();
			}
			catch (FormatException e)
			{
				logger.error(e);
				return null;
			}
		}
		GTS gts = new GTS(gram);

		scenario.setGTS(gts);
		scenario.setState(gts.startState());

		try
		{
			scenario.play();
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
