
public class T1628u8 {
    T1628u8 (){}
    public static void main(String[] args) {
        
	int i = 1;
	final int j;
	switch (i) {
	    case 0:
	    try {
		j = 1;
		break; // doesn't exit switch
	    } finally {
		return;
	    }
	}
	j = 2;
    
    }
}
