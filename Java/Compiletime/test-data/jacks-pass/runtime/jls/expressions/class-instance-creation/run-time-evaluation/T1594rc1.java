
class T1594rc1 {
    class Inner {
        Inner(int i) {}
    }
    public static void main(String[] args) {
        int j = 1;
        T1594rc1 t = null;
        try {
            t.new Inner(j = 2);
        } catch (NullPointerException npe) {
            if (j == 2)
                System.out.println("NullPointer must precede argument evaluation");
            else
                System.out.print("OK");
            return;
        }
        System.out.println("Qualified instance creation must throw NullPointerException");
    }
}
    