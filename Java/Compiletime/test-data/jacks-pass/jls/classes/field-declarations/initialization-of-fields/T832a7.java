
class T832a7 {
    
        void foo() throws ClassNotFoundException {
            new Object() {
                int m() throws ClassNotFoundException { return 1; }
                int i = m();
            };
        }
    
}
