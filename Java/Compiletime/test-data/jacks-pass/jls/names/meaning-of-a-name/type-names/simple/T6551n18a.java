
class T6551n18a {
    interface T6551n18b {}
}
class T6551n18b extends T6551n18a {
    { new T6551n18b(); } // the inherited T6551n18a.T6551n18b shadows T6551n18b
}
    