import Composestar.Java.FLIRT.Annotations.FilterTypeDef;
import Composestar.Java.FLIRT.Filters.RTCustomFilterType;

/**
 * The "invalidate" filter type.
 */
@FilterTypeDef(name = "invalidate", acceptReturn = InvalidateCacheAction.class)
public class Invalidate extends RTCustomFilterType {
}
