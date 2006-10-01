package Composestar.Repository;

import static org.junit.Assert.*;

import junit.framework.TestCase;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import Composestar.Repository.LanguageModel.*;

public class TypeElementStorage {
	private static TypeElement expected;
	private TypeElement result;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		String file = "test.yap";
		java.io.File f = new java.io.File(file);
		f.delete();
		//RepositoryAccess.setDatabaseFileName(file);

		expected = new TypeElement();
		expected.set_Name("TypeElement");
		expected.set_FullName("Composestar.Repository.LanguageModel.TypeElement");
		expected.set_BaseType("object");
		expected.set_IsAbstract(true);
		expected.set_IsEnum(false);
		expected.set_IsInterface(true);
		expected.set_IsSealed(false);
		expected.set_IsValueType(false);
		
		DataStoreContainer.getInstance().addTypeElement(expected);
	}

	@Before
	public void setUp() throws Exception {
		result = DataStoreContainer.getInstance().GetTypeElement(expected.get_FullName());
	}	
	
	@Test
	public void testTypeElementName() {
		assertTrue(result.get_Name().equals(expected.get_Name()));
	}

	@Test
	public void testTypeElementFullName() {
		assertTrue(result.get_FullName().equals(expected.get_FullName()));
	}
	
	@Test
	public void testTypeElementBaseType() {
		assertTrue(result.get_BaseType().equals(expected.get_BaseType()));
	}
	
	@Test
	public void testTypeElementIsAbstract() {
		assertTrue(result.get_IsAbstract() == expected.get_IsAbstract());
	}
	
	@Test
	public void testTypeElementIsEnum() {
		assertTrue(result.get_IsEnum() == expected.get_IsEnum());
	}
	
	@Test
	public void testTypeElementIsInterface() {
		assertTrue(result.get_IsInterface() == expected.get_IsInterface());
	}
	
	@Test
	public void testTypeElementIsSealed() {
		assertTrue(result.get_IsSealed() == expected.get_IsSealed());
	}	
	
	@Test
	public void testTypeElementIsValueType() {
		assertTrue(result.get_IsValueType() == expected.get_IsValueType());
	}	
	
	
	
	
	
}
