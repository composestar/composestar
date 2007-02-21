package Composestar.RuntimeCore.CODER;

import java.lang.Thread;
/**
 * Halter
 * The halter class is responsible for suspening the execution of the target
 */
public class Halter {
    /**
     * The variable halted is set to true when the subjects execution needs to be halted.
     * The subject is started halted to allow the initialisation of the debugger.
     */
    private boolean halted = true;

    /**
     * checkHalt is called in the subjects execution.
     * The function waits when the subject needs to be halted.
     */
    public void checkHalt(){
       while(halted){
           try{
               Thread.sleep(1);
           }catch(InterruptedException e){
           }
       }
    }

    /**
     * The suspend function halts the subject
     */
    public void suspend(){
        halted = true;
    }

    /**
     * The resume function resumes the subject
     */
    public void resume(){
        halted = false;
    }
}
