package BasicTests;

import javax.xml.bind.annotation.XmlElement;
import javax.annotation.Generated;

@Generated("Just to test this annotation type")
public class DummyTester extends DummyTesterBase {

	public static final String CONST = "asdfasdf asdf asdfasdf";
	public static String[] CONST_SPLIT = CONST.split("\\w");

	@XmlElement(name = "bar", required = true)
	public int foo;

	public DummyTester(String name) {
		super(name, "alternative name: "+name);
	}

	@Override
	public String getString() {
		return null;
	}

	@Override
	public int sum(int a, int b) {
		return 0;
	}

}
