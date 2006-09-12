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
import Composestar.Core.FIRE2.model.FlowModel;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Utils.Debug;

/**
 * 
 *
 * @author Arjan de Roo
 */
public class Preprocessor implements CTCommonModule{
    private final static String MODULE_NAME = "FIRE";
    
    private LayedOutXml graphLoader;
    
    private RuleViewGrammar generateFlowGrammar;
    private RuleViewGrammar runtimeGrammar;
    
    private GrooveASTBuilder astBuilder;
    private FlowModelExtractor flowModelExtractor;
    private ExecutionModelExtractor executionModelExtractor;
    
    private final static ExploreStrategy LINEAR_STRATEGY = new LinearStrategy();
    private final static ExploreStrategy BARBED_STRATEGY = new BarbedStrategy();
    private final static ExploreStrategy FULL_STRATEGY = new FullStrategy();
    
    
    private final static String GENERATE_FLOW_GRAMMAR_PATH = 
        "groovegrammars/generateflow.gps";
    private final static String RUNTIME_GRAMMAR_PATH = 
        "groovegrammars/runtime.gps";
    
    private final static File AST_OUT = new File( "./ast.gst" );
    private final static File FLOW_OUT = new File( "./flow.gst" );
    private final static File EXECUTION_OUT = new File( "./execution.gst" );
    
    public final static String RESULT_ID = "FirePreprocessingResult";
    
    
    public Preprocessor(){
        initialize();
    }
    
    private void initialize(){
        loadGrammars();
        
        astBuilder = new GrooveASTBuilder();
        flowModelExtractor = new FlowModelExtractor();
        executionModelExtractor = new ExecutionModelExtractor();
    }
    
    public void run(CommonResources resources) throws ModuleException
    {
        preprocess();
    }
    
    public void preprocess(){
        Iterator moduleIter = 
            DataStore.instance().getAllInstancesOf(FilterModule.class);

        Debug.out( Debug.MODE_DEBUG, MODULE_NAME, "Starting FIRE Preprocessing" );

        while( moduleIter.hasNext() ){
            FilterModule module = (FilterModule) moduleIter.next();
            preprocessModule( module );
        }


        Debug.out( Debug.MODE_DEBUG, MODULE_NAME, "FIRE Preprocessing done" );
    }
    
    private void preprocessModule( FilterModule module ){
        Debug.out( Debug.MODE_DEBUG, MODULE_NAME, 
                "Preprocessing Filter Module: " + module.getName() );
        
        //build AST:
        Graph grooveAst = buildAst( module );
        if ( Debug.getMode() == Debug.MODE_DEBUG ){
            //output ast:
            try {
                graphLoader.marshal( grooveAst, AST_OUT );
            } catch (XmlException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        
        
        //generate flow model:
        Graph grooveFlowModel = generateFlow( grooveAst );
        if ( Debug.getMode() == Debug.MODE_DEBUG ){
            //output flowgraph:
            try {
                graphLoader.marshal( grooveFlowModel, FLOW_OUT );
            } catch (XmlException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        
        
        //extract flowmodel:
        FlowModel flowModel = extractFlowModel( grooveFlowModel );

        
        //Simulate execution:
        GTS stateSpace = execute( grooveFlowModel );
        if ( Debug.getMode() == Debug.MODE_DEBUG ){
            //output execution-statespace:
            try {
                graphLoader.marshal( stateSpace, EXECUTION_OUT );
            } catch (XmlException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        
        
        //extract statespace:
        ExecutionModel executionModel = extractExecutionModel( 
                stateSpace, flowModel );
        
        
        //store result:
        FirePreprocessingResult result = 
            new FirePreprocessingResult( flowModel, executionModel );
        module.dynamicmap.put( RESULT_ID, result );
    }
    
    private void loadGrammars(){
        graphLoader = new LayedOutXml();
        
        try{
            URL genUrl = this.getClass().getResource( GENERATE_FLOW_GRAMMAR_PATH );
            String fileName = genUrl.getFile();
            Debug.out( Debug.MODE_DEBUG, MODULE_NAME, fileName );
            if ( fileName.indexOf('!' ) >= 0){
                //load from jar:
                JarGpsGrammar jarGpsLoader = new JarGpsGrammar();
                
                this.generateFlowGrammar = 
            	    (RuleViewGrammar) jarGpsLoader.unmarshal( GENERATE_FLOW_GRAMMAR_PATH );
                this.runtimeGrammar =
            	    (RuleViewGrammar) jarGpsLoader.unmarshal( RUNTIME_GRAMMAR_PATH );
            }
            else{
                //load from directory:
                GpsGrammar gpsLoader = new GpsGrammar(new LayedOutXml());
                
                File f = new File(genUrl.getFile());
                RuleViewGrammar rvg = (RuleViewGrammar)gpsLoader.unmarshal(f); 
                
                this.generateFlowGrammar = rvg;
                
                URL runUrl = this.getClass().getResource( RUNTIME_GRAMMAR_PATH );
            	this.runtimeGrammar =
            	    (RuleViewGrammar) gpsLoader.unmarshal( new File(runUrl.getFile()) );
            }
        }
        catch (Exception exc){
            exc.printStackTrace();
        	//throw new RuntimeException( "Loading grammars failed", exc );
        }
    }
    
    private Graph buildAst( FilterModule module ){
        return astBuilder.buildAST( module );
    }
    
    private Graph generateFlow( Graph ast ){
        generateFlowGrammar.setStartGraph( ast );
        GTS gts = generateFlowGrammar.gts();
        gts.setExploreStrategy( LINEAR_STRATEGY );
        try{
            gts.explore();
        }
        catch( InterruptedException exc ){
            //TODO nice exception handling
            exc.printStackTrace();
        }
        Iterator finalStates = gts.getFinalStates().iterator();
        
        if ( !finalStates.hasNext() ){
            //should never happen
            throw new RuntimeException( "FlowGraph could not be generated!" );
        }
        else{
            DefaultGraphState state = (DefaultGraphState) finalStates.next();
            return state.getGraph();
        }
    }
    
    private FlowModel extractFlowModel( Graph flowGraph ){
        return FlowModelExtractor.extract( flowGraph );
    }
    
    private GTS execute( Graph flowGraph ){
        runtimeGrammar.setStartGraph( flowGraph );
        GTS gts = runtimeGrammar.gts();
        gts.setExploreStrategy( FULL_STRATEGY );
        try{
            gts.explore();
        }
        catch( InterruptedException exc ){
            //TODO nice exception handling
            exc.printStackTrace();
        }
        
        return gts;
    }
    
    private ExecutionModel extractExecutionModel( GTS stateSpace, FlowModel flowModel ){
        return executionModelExtractor.extract( stateSpace, flowModel );
    }

}
