//Source file: C:\\local\\staijen\\composestar\\src\\Composestar\\core\\SANE\\AbstractSIinfo.java

//Source file: D:\\Lodewijk\\software\\ComposeStar\\composestar\\src\\Composestar\\core\\SANE\\AbstractSIinfo.java

package Composestar.Core.SANE;

import Composestar.Core.CpsProgramRepository.CpsConcern.CpsConcern;
import java.util.Vector;
import Composestar.Utils.*;

public interface AbstractSIinfo {
    
    /**
     * @param concern
     * @roseuid 40598175012C
     */
    public void bind(CpsConcern concern);
    
    /**
     * get a Vector with all <things> that have been superimposed
     * @return java.util.Vector
     * @roseuid 405985FB0005
     */
    public Vector getAll();
    
    /**
     * @return Composestar.Utils.*;
     * @roseuid 4059861F03B4
     */
    public CPSIterator getIter();
}
