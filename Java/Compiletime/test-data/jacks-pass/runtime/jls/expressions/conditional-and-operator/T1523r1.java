
class T1523r1 {
    public static void main(String[] args) {
        int i = 0;
        boolean b;
        if ((i++ < 10) && false);
        else System.out.print('a');
        System.out.print(i);
        if ((i++ < 10) && true);
        else System.out.print('b');
        System.out.print(i);
        if (false && (i++ < 10));
        else System.out.print('c');
        System.out.print(i);
        if (true && (i++ < 10));
        else System.out.print('d');
        System.out.print(i);
        b = (i++ < 10) && false;
        System.out.print((b ? "" : "e") + i);
        b = (i++ < 10) && true;
        System.out.print((b ? "" : "f") + i);
        b = false && (i++ < 10);
        System.out.print((b ? "" : "g") + i);
        b = true && (i++ < 10);
        System.out.print((b ? "" : "h") + i);
    }
}
    