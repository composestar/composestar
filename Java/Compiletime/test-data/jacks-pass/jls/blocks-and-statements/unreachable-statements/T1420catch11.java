
class T1420catch11 {
    
        void choke() throws Exception {
            throw new ClassNotFoundException();
        }
        void foo() throws Exception {
            try {
                choke();
            } catch (ClassNotFoundException e) {
                // reachable, as choke() can throw any subclass
            }
        }
    
}
