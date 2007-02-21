package Composestar.RuntimeCore.CODER;

/**
 * Created by IntelliJ IDEA.
 * User: reddog
 * Date: 18-feb-2007
 * Time: 20:40:21
 * To change this template use File | Settings | File Templates.
 */
public class SymbolManager {
    public static SourceReference getSourceReferenceForFilter(Object filter){
        return new SourceReference();
    }

    public static SourceReference getSourceReferenceSender(Object sender){
        return new SourceReference();
    }

    public static SourceReference getSourceReferenceTarget(String selector, Object[] args, Object target){
        return new SourceReference();
    }
}
