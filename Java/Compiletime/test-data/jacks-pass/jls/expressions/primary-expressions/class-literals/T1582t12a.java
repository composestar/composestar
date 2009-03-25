
class T1582t12a {
    private class Inner {}
}
class T1582t12b extends T1582t12a {
    Class c = T1582t12a.Inner.class;
}
    