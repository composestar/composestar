
class T652q11 {
    
        static class Super {
            Super sup;
        }
        static class Sub extends Super {
            void bar(Sub s) {
                // sup is an inherited field in type Sub, so s.sup is an
                s.sup.toString(); // expression name
            }
        }
    
}
