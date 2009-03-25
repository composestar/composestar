
class T1431r2 {
    boolean x1, x2;
    T1431r2() {
        class Local {
            { x1 = true; }
        }
        new Local();
        new Object() {
            class Local {
                { x2 = true; }
            }
        }.new Local();
    }
    public static void main(String[] args) {
        T1431r2 t = new T1431r2();
        System.out.print(t.x1 + " " + t.x2);
    }
}
   