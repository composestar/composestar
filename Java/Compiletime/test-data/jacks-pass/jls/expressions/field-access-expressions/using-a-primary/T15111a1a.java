
interface T15111a1a {
    int i = 1;
}
class T15111a1b {
    int i = 2;
}
class T15111a1c extends T15111a1b implements T15111a1a {
    static int j = new T15111a1c().i;
}
