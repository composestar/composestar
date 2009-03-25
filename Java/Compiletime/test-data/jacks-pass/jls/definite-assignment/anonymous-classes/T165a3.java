
class T165a3 {
    
        T165a3(int j) {}
        void foo() {
            final int v;
            new T165a3(v = 1) {
                int i = v;
            };
        }
    
}
