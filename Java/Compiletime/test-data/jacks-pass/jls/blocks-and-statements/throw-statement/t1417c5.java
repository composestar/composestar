
class t1417c5 {
    
        void m() throws RuntimeException {
            try {
                throw new RuntimeException(); // all 3!
            } catch (RuntimeException e) {
            }
        }
    
}
