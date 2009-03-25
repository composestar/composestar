
class T15112s1a {
    int i;
}
class T15112s1b extends T15112s1a {
    int T15112s1b; // obscure the class name from normal expressions
    int j = T15112s1b.super.i;
}
    