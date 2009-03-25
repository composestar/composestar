
class T1585v2 {
    
        static int i1 = 1, j1 = (i1)--;
        int i2 = 1, j2 = (i2)--;
        private static int i3 = 1;
        private int i4 = 1;
        class Inner {
            Inner() {
                int j3 = (i3)--;
                int j4 = (i4)--;
            }
        }
        void foo() {
            int i5 = 1, j5 = (i5)--;
            int[] i6 = {1};
            int j6 = (i6[0])--;
        }
    
}
