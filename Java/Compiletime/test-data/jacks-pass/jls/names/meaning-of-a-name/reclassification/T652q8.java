
class T652q8 {
    
        static class Super {
            static class t {
                static int i;
            }
        }
        class Sub extends Super {
            int j = Sub.t.i; // Sub.t is a type name since class t is inherited
        }
    
}
