
class T814s11a {
    interface Super {}
}
class T814s11b extends T814s11a {
    class C implements Super {}
}
class T814s11c implements T814s11b.Super {}
    