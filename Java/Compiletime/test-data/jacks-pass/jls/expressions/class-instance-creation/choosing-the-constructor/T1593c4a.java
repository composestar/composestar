
class T1593c4a {
    private T1593c4a(int i, byte b) {}
    T1593c4a(byte b, int i) {}
}
class T1593c4b {
    byte b;
    Object o = new T1593c4a(b, b) {};
}
    