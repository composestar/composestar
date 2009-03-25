
class List {
    int value;
    List next;
    static List head = new List(0);
    List(int n) { value = n; next = head; head = this; }
}
class T1594rc2 {
    public static void main(String[] args) {
        int id = 0, oldid = 0;
        try {
            for (;;) {
                ++id;
                new List(oldid = id);
            }
        } catch (OutOfMemoryError e) {
            List.head = null; // try to reclaim memory
            System.out.print(oldid==id);
        }
    }
}
    