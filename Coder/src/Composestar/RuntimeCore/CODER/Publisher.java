package Composestar.RuntimeCore.CODER;

import Composestar.RuntimeCore.CODER.BreakPoint.*;
import Composestar.RuntimeCore.CODER.Visual.BreakPoint.Maker;
import Composestar.RuntimeCore.CODER.Visual.Representation.CodeDebugger;

/**
 * Created by IntelliJ IDEA.
 * User: reddog
 * Date: 18-feb-2007
 * Time: 21:58:47
 * To change this template use File | Settings | File Templates.
 */
public class Publisher {
    private DesignTime designTime;
    private CodeDebugger gui;

    public Publisher(){
    }
    
    public void setDesignTime(DesignTime designTime){
        this.designTime = designTime;
        this.gui = new CodeDebugger(this.designTime);
    }

    public BreakPoint getBreakPoint(){
        return Maker.getBreakPoint();
    }

    public void hitTheBreaks(ExecutionStackItem state){
        gui.go(state);
    }
}
