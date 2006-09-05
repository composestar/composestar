//Source file: H:\\composestar\\src\\Composestar\\core\\SECRET\\ConflictReporter.java

package Composestar.Core.SECRET;

import java.util.ArrayList;

public class ConflictReporter {
    private int exitcode;
    
    /**
     * @roseuid 40584F530100
     */
    public ConflictReporter() {
     
    }
    
    /**
     * @return int
     * @roseuid 4058392F0117
     */
    public int getExitCode() {
     	return this.exitcode;     
    }
    
    /**
     * @param code
     * @roseuid 4058393A007B
     */
    public void setExitCode(int code) {
    	this.exitcode = code;     
    }
    
    /**
     * @param actionseq
     * @param resource
     * @roseuid 405839460108
     */
    public void reportConflict(ArrayList actionseq, String resource) {
    	System.out.println("OOPS..."+resource);
    	for(int i=0; i<actionseq.size(); i++)
		{
			ActionDescription ad = (ActionDescription)actionseq.get(i);
			String tmp = ad.getActor();
			System.out.println('\t' +tmp);
		}     
    }
}
