import Composestar.Java.FLIRT.Annotations.FilterTypeDef;
import Composestar.Java.FLIRT.Filters.RTCustomFilterType;

/**
 * The log filter type
 */
@FilterTypeDef(name = "Log", acceptCall = LogCallAction.class, acceptReturn = LogReturnAction.class)
public class Log extends RTCustomFilterType {
}
