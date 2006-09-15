package Composestar.Repository.Configuration;


import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class CommonConfigurationTest {
	CommonConfiguration config = null;
	
	@Before
	public void setUp() throws Exception {
		config = new CommonConfiguration();
	}

	@Test
	public void testCompiletimeDebugLevel_1() {
		config.set_CompiletimeDebugLevel(0);
		assertEquals(0, config.get_CompiletimeDebugLevel());
	}
	
	@Test
	public void testCompiletimeDebugLevel_2() {
		config.set_CompiletimeDebugLevel(4);
		assertEquals(4, config.get_CompiletimeDebugLevel());
	}
	
	@Test
	public void testCompiletimeDebugLevel_3() {
		config.set_CompiletimeDebugLevel(-1);
		assertEquals(3, config.get_CompiletimeDebugLevel());
	}
	
	@Test
	public void testCompiletimeDebugLevel_4() {
		config.set_CompiletimeDebugLevel(5);
		assertEquals(3, config.get_CompiletimeDebugLevel());
	}
}
