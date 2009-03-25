
class T6551n22 {
    
	class Inner extends T6551n22 {
	    // inherited Inner.Inner (etc.) is same as current Inner
	    Inner i;
	    Inner.Inner j = i;
	    Inner.Inner.Inner k = j;
	}
    
}
