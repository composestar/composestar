
class T852nsmu8 {
    
        final int i = 1;
        static class T852nsmu8_Test {
            void m(T852nsmu8 t) {
                int j = t.i; // i is qualified, doesn't count as usage
            }
        }
    
}
