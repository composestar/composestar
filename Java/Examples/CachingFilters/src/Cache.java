import Composestar.Java.FLIRT.Annotations.FilterTypeDef;
import Composestar.Java.FLIRT.Filters.RTCustomFilterType;

/**
 * The cache filter type
 */
@FilterTypeDef(name = "cache", acceptCall = CheckCacheAction.class, acceptReturn = CacheResultAction.class)
public class Cache extends RTCustomFilterType {
}
