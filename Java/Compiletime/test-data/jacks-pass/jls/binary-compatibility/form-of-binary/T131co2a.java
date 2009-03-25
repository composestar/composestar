
class T131co2a {
    T131co2a[] m() { return null; } // change return type to a[] instead of b[]
}
// class T131co2b extends T131co2a {
//     T131co2b[] m() { return null; } // now covariant!
    // if this class were uncommented and compiled by gj, it would also have
    // /*synthetic*/ T131co2a[] m() { return /*T131co2b*/m(); }
// }
class T131co2c extends T131co2b {
    { m(); }
}
    