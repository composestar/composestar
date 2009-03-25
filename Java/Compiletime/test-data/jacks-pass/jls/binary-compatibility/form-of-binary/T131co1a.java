
class T131co1a {
    T131co1a m() { return null; } // change return type to a instead of b
}
// class T131co1b extends T131co1a {
//     T131co1b m() { return null; } // now covariant!
    // if this class were uncommented and compiled by gj, it would also have
    // /*synthetic*/ T131co1a m() { return /*T131co1b*/m(); }
// }
class T131co1c extends T131co1b {
    { m(); }
}
    