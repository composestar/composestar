// annotation super imposition
concern correct07_annotation_si
{
	superimposition
	{
		selectors
			s1 = { C | isClassWithName(C, 'C1') };
			s2 = { C | isClassWithName(C, 'C2') };
		annotations
			s1 <- Name.Space.Annotation;
			s2 <- AnnotationTwo, AnnotationThree;
			s1 <- {AnnotationThree, Space.Name.Annotations.Deprecated};
	}
}