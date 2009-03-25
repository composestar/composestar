
interface T6551n21a {
    final class T6551n21b {}
}
interface T6551n21b extends T6551n21a {
    // the inherited T6551n21a.T6551n21b shadows T6551n21b
    T6551n21b t = new T6551n21b() {};
}
    