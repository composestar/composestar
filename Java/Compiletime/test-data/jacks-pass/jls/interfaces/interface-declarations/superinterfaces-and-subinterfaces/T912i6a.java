
class T912i6a {
    interface Super {}
}
class T912i6b extends T912i6a {
    interface C extends Super {}
}
interface T912i6c extends T912i6b.Super {}
    