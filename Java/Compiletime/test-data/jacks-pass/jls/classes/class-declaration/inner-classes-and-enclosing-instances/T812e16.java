
class T812e16 {
    
        int i = 1;
        Object o = new Object() {
            class Inner {
                int j = i;
            }
        };
    
}
