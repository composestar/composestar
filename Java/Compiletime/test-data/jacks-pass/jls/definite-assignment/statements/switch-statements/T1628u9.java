
public class T1628u9 {
    T1628u9 (){}
    public static void main(String[] args) {
        
	int i = 1;
	final int j;
	label: switch (i) {
	    case 0:
	    try {
		j = 1;
		break label; // doesn't exit switch
	    } finally {
		return;
	    }
	}
	j = 2;
    
    }
}
