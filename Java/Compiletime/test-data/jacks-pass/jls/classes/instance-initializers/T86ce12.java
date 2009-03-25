
class T86ce12 {
    
        void foo() throws ClassNotFoundException {
            new Object() {
                int m() throws ClassNotFoundException { return 1; }
                { m(); }
            };
        }
    
}
