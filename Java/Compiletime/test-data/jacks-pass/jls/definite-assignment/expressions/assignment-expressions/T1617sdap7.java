
class T1617sdap7 {
    
        T1617sdap7 t;
        void foo() {
            T1617sdap7 x;
	    (true ? x = new T1617sdap7() : null).t = x;
	}
    
}
