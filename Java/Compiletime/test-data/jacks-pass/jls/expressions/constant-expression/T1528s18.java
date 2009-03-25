
class T1528s18 {
    
        static final String a = "a";
        static final String b = "b";
        void foo(int i) {
            final String s = a + b;
            switch (i) {
                case 0:
                case ((s == "ab") ? 1 : 0):
            }
        }
    
}
