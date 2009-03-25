
interface T6551n20a {
    class T6551n20b {}
}
interface T6551n20b extends T6551n20a {
    // the inherited T6551n20a.T6551n20b shadows T6551n20b
    T6551n20b t = new T6551n20b();
}
    