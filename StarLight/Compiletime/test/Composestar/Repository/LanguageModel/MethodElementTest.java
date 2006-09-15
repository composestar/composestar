package Composestar.Repository.LanguageModel;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class MethodElementTest {
	MethodElement me;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		me = new MethodElement();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testMethodElement() {
		assertNotNull(me);
	}

	@Test
	public void testGet_Id() {
		assertTrue( me.get_Id() > 0 );
	}

	@Test
	public void testSet_ParentTypeId() {
		me.set_ParentTypeId(12345);
		assertEquals(me.get_ParentTypeId(), 12345);
	}

	@Test
	public void testGet_ParentTypeId() {
		assertNotNull(me.get_ParentTypeId());
	}

	@Test
	public void testGet_Name() {
		assertNotNull(me.get_Name());
	}

	@Test
	public void testSet_Name() {
		me.set_Name("MethodElement");
		assertEquals(me.get_Name(), "MethodElement");
	}

	@Test
	public void testGet_ReturnType() {
		assertNotNull(me.get_ReturnType());
	}

	@Test
	public void testSet_ReturnType() {
		me.set_ReturnType("void");
		assertEquals(me.get_ReturnType(), "void");
	}

	@Test
	public void testGet_IsAbstract() {
		assertNotNull(me.get_IsAbstract());
	}

	@Test
	public void testSet_IsAbstract() {
		me.set_IsAbstract(true);
		assertTrue(me.get_IsAbstract());
	}

	@Test
	public void testGet_IsConstructor() {
		assertNotNull(me.get_IsConstructor());
	}

	@Test
	public void testSet_IsConstructor() {
		me.set_IsConstructor(true);
		assertTrue(me.get_IsConstructor());
	}

	@Test
	public void testGet_IsPrivate() {
		assertNotNull(me.get_IsPrivate());
	}

	@Test
	public void testSet_IsPrivate() {
		me.set_IsPrivate(true);
		assertTrue(me.get_IsPrivate());
	}

	@Test
	public void testGet_IsPublic() {
		assertNotNull(me.get_IsPublic());
	}

	@Test
	public void testSet_IsPublic() {
		me.set_IsPublic(true);
		assertTrue(me.get_IsPublic());
	}

	@Test
	public void testGet_IsStatic() {
		assertNotNull(me.get_IsStatic());
	}

	@Test
	public void testSet_IsStatic() {
		me.set_IsStatic(true);
		assertTrue(me.get_IsStatic());
	}

	@Test
	public void testGet_IsVirtual() {
		assertNotNull(me.get_IsVirtual());
	}

	@Test
	public void testSet_IsVirtual() {
		me.set_IsVirtual(true);
		assertTrue(me.get_IsVirtual());
	}

	@Test
	public void testGet_MethodBody() {
		assertNotNull(me.get_MethodBody());
	}

	@Test
	public void testSet_MethodBody() {
		MethodBody mb = new MethodBody();
		me.set_MethodBody(mb);
		
		assertSame(mb, me.get_MethodBody());
	}

}
