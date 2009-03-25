
class T86ce6 {
    
        void foo() throws ClassNotFoundException {
            new Object() {
                { if (true) throw new ClassNotFoundException(); }
            };
        }
    
}
