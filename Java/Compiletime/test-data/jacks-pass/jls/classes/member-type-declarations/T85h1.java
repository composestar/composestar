
class T85h1 {
    
	class One {
	    public class Pub {}
	    protected class Pro {}
	    class Pack {}
	    private class Priv {}
	}
	class Two extends One {
	    private class Pub {}
	    private class Pro {}
	    private class Pack {}
	    private class Priv {}
	}
	class Three extends One {
	    class Pub {}
	    class Pro {}
	    class Pack {}
	    class Priv {}
	}
	class Four extends One {
	    protected class Pub {}
	    protected class Pro {}
	    protected class Pack {}
	    protected class Priv {}
	}
	class Five extends One {
	    public class Pub {}
	    public class Pro {}
	    public class Pack {}
	    public class Priv {}
	}
    
}
