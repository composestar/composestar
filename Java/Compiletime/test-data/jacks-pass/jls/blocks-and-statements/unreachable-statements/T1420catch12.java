
class T1420catch12 {
    
        void foo() throws Exception {
            try {
                throw new Exception();
            } catch (ClassNotFoundException e) {
                // reachable, although the class instance creation cannot
                // create a subclass of Exception
            }
        }
    
}
