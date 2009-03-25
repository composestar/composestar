
interface T6551n11a {}
class T6551n11b {
    class T6551n11a {}
    // T6551n11a refers to the member class, not the top-level interface
    class Sub extends T6551n11a {}
}
    