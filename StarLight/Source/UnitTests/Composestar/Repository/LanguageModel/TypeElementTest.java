package Composestar.Repository.LanguageModel;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TypeElementTest {
	TypeElement te;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		 te = new TypeElement();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testTypeElement() {
		assertNotNull(te);
	}

	@Test
	public void testGet_Id() {
		assertTrue( te.get_Id() > 0 );
	}

	@Test
	public void testGet_Name() {
		assertNotNull(te.get_Name());
	}
	
	@Test
	public void testSet_Name() {
		te.set_Name("TypeElement");
		assertEquals(te.get_Name(), "TypeElement");
	}
	
	@Test
	public void testGet_FullName() {
		assertNotNull(te.get_FullName());
	}

	@Test
	public void testSet_FullName() {
		te.set_FullName("Composestar.Repository.LanguageModel.TypeElement");
		assertEquals(te.get_FullName(), "Composestar.Repository.LanguageModel.TypeElement");
	}
	
	@Test
	public void testGet_BaseType() {
		assertNotNull(te.get_BaseType());
	}

	@Test
	public void testSet_BaseType() {
		te.set_BaseType("System.Object");
		assertEquals(te.get_BaseType(), "System.Object");
	}

	@Test
	public void testGet_AssemblyElement() {
		assertNotNull(te.get_AssemblyElement());
	}

	@Test
	public void testSet_AssemblyElement() {
		AssemblyElement ae = new AssemblyElement();
		ae.set_Name("AssemblyElement");
		ae.set_Version("1.0");
		te.set_AssemblyElement(ae);
		
		assertSame(ae, te.get_AssemblyElement());
	}
	
	@Test
	public void testGet_Namespace() {
		assertNotNull(te.get_FullName());
	}

	@Test
	public void testSet_Namespace() {
		te.set_Namespace("Composestar.Repository.LanguageModel");
		assertEquals(te.get_Namespace(), "Composestar.Repository.LanguageModel");
	}

	@Test
	public void testGet_IsAbstract() {
		assertNotNull(te.get_IsAbstract());
	}

	@Test
	public void testSet_IsAbstract() {
		te.set_IsAbstract(true);
		assertTrue(te.get_IsAbstract());
	}

	@Test
	public void testGet_IsInterface() {
		assertNotNull(te.get_IsInterface());
	}

	@Test
	public void testSet_IsInterface() {
		te.set_IsInterface(true);
		assertTrue(te.get_IsInterface());
	}

	@Test
	public void testGet_IsSealed() {
		assertNotNull(te.get_IsSealed());
	}

	@Test
	public void testSet_IsSealed() {
		te.set_IsSealed(true);
		assertTrue(te.get_IsSealed());
	}

	@Test
	public void testGet_IsValueType() {
		assertNotNull(te.get_IsValueType());
	}

	@Test
	public void testSet_IsValueType() {
		te.set_IsValueType(true);
		assertTrue(te.get_IsValueType());
	}

	@Test
	public void testGet_IsEnum() {
		assertNotNull(te.get_IsEnum());
	}

	@Test
	public void testSet_IsEnum() {
		te.set_IsEnum(true);
		assertTrue(te.get_IsEnum());
	}

	@Test
	public void testGet_IsClass() {
		assertNotNull(te.get_IsClass());
	}

	@Test
	public void testSet_IsClass() {
		te.set_IsClass(true);
		assertTrue(te.get_IsClass());
	}

	@Test
	public void testGet_IsNotPublic() {
		assertNotNull(te.get_IsPublic());
	}

	@Test
	public void testSet_IsNotPublic() {
		te.set_IsNotPublic(true);
		assertTrue(te.get_IsNotPublic());
	}

	@Test
	public void testGet_IsPrimitive() {
		assertNotNull(te.get_IsPrimitive());
	}

	@Test
	public void testSet_IsPrimitive() {
		te.set_IsPrimitive(true);
		assertTrue(te.get_IsPrimitive());
	}

	@Test
	public void testGet_IsPublic() {
		assertNotNull(te.get_IsPublic());
	}

	@Test
	public void testSet_IsPublic() {
		te.set_IsPublic(true);
		assertTrue(te.get_IsPublic());
	}

	@Test
	public void testGet_IsSerializable() {
		assertNotNull(te.get_IsSerializable());
	}

	@Test
	public void testSet_IsSerializable() {
		te.set_IsSerializable(true);
		assertTrue(te.get_IsSerializable());
	}

	@Test
	public void testGet_FromDLL() {
		assertNotNull(te.get_FromDLL());
	}

	@Test
	public void testSet_FromDLL() {
		te.set_FromDLL("LanguageModel.dll");
		assertEquals(te.get_FromDLL(), "LanguageModel.dll");
	}
}
