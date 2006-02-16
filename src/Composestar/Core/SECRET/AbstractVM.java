//Source file: H:\\composestar\\src\\Composestar\\core\\SECRET\\AbstractVM.java

package Composestar.Core.SECRET;

import java.util.HashMap;
import java.util.ArrayList;

public class AbstractVM {
    private HashMap RDM = new HashMap();
    private HashMap RUM = new HashMap();
    private ConflictReporter cr;
    
    /**
     * @roseuid 40583AA303C7
     */
    public AbstractVM() {
     
    }
    
    /**
     * @param cr
     * @roseuid 404ECEF00294
     */
    public AbstractVM(ConflictReporter cr) {
    	this.cr = cr;     
    }
    
    /**
     * @param resource
     * @param rd
     * @roseuid 404DDBA300D1
     */
    public void addResourceDescription(String resource, ResourceDescription rd) {
    	this.RDM.put(resource, rd); 
    
    }
    
    /**
     * @param rsrc
     * @param ad
     * @roseuid 404ED6A800A6
     */
    public void addActionDescription(String rsrc, ActionDescription ad) {
    	ArrayList tmp = null;
    	if(this.RUM.containsKey(rsrc))
    	{
    		tmp = (ArrayList)this.RUM.get(rsrc);
    	}
    	else
    	{
    		tmp = new ArrayList();
    	}
    	tmp.add(ad);
		this.RUM.put(rsrc, tmp);
    
    }
    
    /**
     * @param rsrc
     * @return boolean
     * @roseuid 404ED6C2020E
     */
    public boolean checkResourceConflict(String rsrc) {
    	boolean result = false;
    	if(!this.RUM.containsKey(rsrc) || !this.RDM.containsKey(rsrc))
    	{
    		result = false;
    	}
    	else 
    	{
    		ArrayList list = (ArrayList)this.RUM.get(rsrc);
			this.analyzeResourceConflictSequence(rsrc,list);
    	}
    	return result;     
    }
    
    /**
     * @param rsrc
     * @param actionseq
     * @return boolean
     * @roseuid 4056B64A01E3
     */
    public boolean analyzeResourceConflictSequence(String rsrc, ArrayList actionseq) {
    	ResourceDescription rd = (ResourceDescription)this.RDM.get(rsrc);
		String okstring = rd.getOKRegex();
		ArrayList actors = new ArrayList();
		ArrayList new_actionseq = new ArrayList();
		String actionsequence = "";
		/* Get the actor information, we need to have the first part of the actor! */
		for(int i=0; i<actionseq.size(); i++)
		{
			ActionDescription ad = (ActionDescription)actionseq.get(i);
			actionsequence += ad.getAction();
			String tmp = ad.getActor();
			if(tmp.indexOf(".") > 0) actors.add(tmp.substring(0,tmp.indexOf(".")));
			else actors.add(tmp);
		}
		/* Check of there are any duplicate actors on one resource, if not check for conflicts! */
		if(!this.sequenceContainsDuplicates(actionseq))
		{
			/* Check against the regular expression of this resource */
			if(!java.util.regex.Pattern.matches(okstring,actionsequence))
			{
				//System.out.println("\tChecking pattern: "+okstring);
				//System.out.println("\tOn sequence: "+actionsequence);
				this.cr.setExitCode(-1);
				this.cr.reportConflict(actionseq,rsrc);
			}
		}
		else
		{
			/* If there are duplicate actors present we split the sequence in two
			 * and parse both sequences again until there are no duplicate actions anymore!
			 */
			for(int i=0; i<actors.size(); i++)
			{
				String item = (String)actors.get(i);
				if(this.isduplicate(item,actionseq))
				{
					
					Object obj1 = actionseq.remove(i);
					Object obj2 = actionseq.remove(i);
					ArrayList choice1 = (ArrayList)new_actionseq.clone();
					ArrayList choice2 = (ArrayList)new_actionseq.clone();
					choice1.add(obj1);
					choice2.add(obj2);
					choice1.addAll(actionseq);
					choice2.addAll(actionseq);
					/*System.out.println("\t\tObject1: "+obj1);
					System.out.println("\t\tChoice1: "+choice1);
					System.out.println("\t\tObject2: "+obj2);
					System.out.println("\t\tChoice2: "+choice2);
					*/
					this.analyzeResourceConflictSequence(rsrc,choice1);
					this.analyzeResourceConflictSequence(rsrc,choice2);
					break;
				}
				else
				{
					new_actionseq.add(actionseq.get(i));
					actionseq.remove(i);
					actors.remove(i);
					i--;
				}
			}
		}
		return (this.cr.getExitCode()!=-1);     
    }
    
    /**
     * @roseuid 404ED75201D0
     */
    public void init() {
    	this.RDM = new HashMap();
    	this.RUM = new HashMap();     
    }
    
    /**
     * @param seq
     * @return boolean
     * @roseuid 40582F690224
     */
    public boolean sequenceContainsDuplicates(ArrayList seq) {
    	ArrayList actors = new ArrayList();
    	for(int i=0; i<seq.size(); i++)
		{
			ActionDescription ad = (ActionDescription)seq.get(i);
			String tmp = ad.getActor();
			if(tmp.indexOf(".") > 0) actors.add(tmp.substring(0,tmp.indexOf(".")));
			else actors.add(tmp);
		}
    	//System.out.println("\tCHECK: "+actors);
		for(int i=0; i<actors.size(); i++)
		{
			String tmp = (String)actors.get(i);
			int index = actors.indexOf(tmp);
			actors.remove(tmp);
			//System.out.println("\tCHECK for["+tmp+"]: "+actors.indexOf(tmp));
			if(actors.indexOf(tmp) >= 0) { return true; } // Duplicate
			else { actors.add(index,tmp); }
		}
		//System.out.println("\tFalse!");
		return(false);     
    }
    
    /**
     * @param item
     * @param seq
     * @return boolean
     * @roseuid 4058356201DC
     */
    public boolean isduplicate(String item, ArrayList seq) {
    	ArrayList actors = new ArrayList();
    	for(int i=0; i<seq.size(); i++)
		{
			ActionDescription ad = (ActionDescription)seq.get(i);
			String tmp = ad.getActor();
			if(tmp.indexOf(".") > 0) actors.add(tmp.substring(0,tmp.indexOf(".")));
			else actors.add(tmp);
		}
    	
    	if(actors.contains(item))
    	{
    		actors.remove(item);
    		return(actors.contains(item));
    	}
		return(false);     
    }
}
