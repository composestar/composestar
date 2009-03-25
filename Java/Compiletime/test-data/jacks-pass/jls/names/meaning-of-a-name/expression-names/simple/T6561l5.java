
class T6561l5 {
    
	int i;
	void m() {
	    try {
		throw new Error();
	    } catch (Error i) {
		i = null; // refers to the parameter, not the field
	    }
	}
    
}
