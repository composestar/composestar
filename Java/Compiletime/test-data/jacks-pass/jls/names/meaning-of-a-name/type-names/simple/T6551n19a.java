
class T6551n19a {
    interface T6551n19b {}
}
final class T6551n19b extends T6551n19a {
    // the inherited T6551n19a.T6551n19b shadows T6551n19b
    { new T6551n19b() {}; }
}
    