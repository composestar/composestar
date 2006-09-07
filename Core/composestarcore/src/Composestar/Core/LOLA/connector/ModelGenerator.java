/*
 * Generates a mapping of a Language Meta-model to prolog predicates
 * and writes these to an output stream.
 * 
 * Call prologGenerator on a language model to create the predicates for this model.
 * 
 * This class is in fact a helper class, it doesn't contain any information of its own.
 * Therefore it uses static calls.
 * 
 * Created on Oct 26, 2004 by havingaw
 */
package Composestar.Core.LOLA.connector;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Vector;

import Composestar.Core.LOLA.metamodel.CompositeLanguageUnitType;
import Composestar.Core.LOLA.metamodel.CompositeRelationPredicate;
import Composestar.Core.LOLA.metamodel.LanguageModel;
import Composestar.Core.LOLA.metamodel.LanguageUnitType;
import Composestar.Core.LOLA.metamodel.ModelException;
import Composestar.Core.LOLA.metamodel.RelationPredicate;


public class ModelGenerator
{
  public ModelGenerator()
  {
  }
  
  /**
   * Generates a list of prolog predicates implementing the connection between Java and prolog.
   * Static method because it doesn't need any fields.
   * 
   * @param model Language model to generate prolog predicates for
   * @param output Predicates will be written to this stream
   * @throws IOException when an error occurs while writing to the output stream
   * @throws ModelException when an inconsistency is detected in the language model
   */
  public static void prologGenerator(LanguageModel model, PrintStream output) throws IOException, ModelException
  {
    output.println("%% Definition of language unit types\n");
    Iterator types = model.getLanguageUnitTypes().iterator();
    while (types.hasNext())
    {
      LanguageUnitType type = (LanguageUnitType)types.next();
      if (type instanceof CompositeLanguageUnitType)
        output.println(genCompositeUnitTypePredicates((CompositeLanguageUnitType)type));
      else
        output.println(genUnitTypePredicates(type));
    }
    
    output.println("\n\n%% Definition of relations between language units\n");
    
    Iterator rels = model.getRelationPredicates().values().iterator();
    while (rels.hasNext())
    {
      RelationPredicate rel = (RelationPredicate)rels.next();
      if (rel instanceof CompositeRelationPredicate)
        output.println(genCompositeRelationPredicate((CompositeRelationPredicate)rel));
      else
        output.println(genRelationPredicate(rel));
    }
    
  }
  
  private static String genUnitTypePredicates(LanguageUnitType type)
  {
  	String typeName = type.getType(); // Should start with uppercase char.
  	String typeLower = typeName.toLowerCase();
  	
    StringBuffer res = new StringBuffer();
    // Definition of isSomething(Unit) predicate
      res.append("is").append(typeName).append("(Unit) :-\n");
      res.append("  check_or_gen1(isUnitTypeBuiltin(Unit, '").append(typeName).append("')).\n\n");
    
    // Definition of isSomethingWithName(Unit, Name) predicate
      res.append("is").append(typeName).append("WithName(Unit, Name) :-\n  ");
    if (!type.isNameUnique()) // Since the name is not unique, we need to apply the Source->answer generator
      res.append("check_or_gen1(");
      res.append("isUnitNameBuiltin(Unit, Name, '").append(typeName).append("')");
    if (!type.isNameUnique())
      res.append(')');
    res.append(".\n\n");
    
    // Definition of isSomethingWithAttribute predicate (many-to-many relation)
      res.append("is").append(typeName).append("WithAttribute(Unit, Attr) :-\n");
      res.append("  check_or_gen12(hasAttributeBuiltin(Unit, Attr, '").append(typeName).append("')).\n\n");

      res.append("is").append(typeName).append("WithNameInList(Unit, [Name|Ns]) :-\n");
      res.append("  or(is").append(typeName).append("WithName(Unit, Name), is").append(typeName).append("WithNameInList(Unit,Ns)).\n\n");

      res.append(typeLower).append("HasAnnotationWithName(Unit, AnnotName) :-\n");
      res.append("  isAnnotationWithName(A, AnnotName), ").append(typeLower).append("HasAnnotation(Unit, A).\n\n");
    
    return res.toString();
  }

  private static String genCompositeUnitTypePredicates(CompositeLanguageUnitType type)
  {
    StringBuffer res = new StringBuffer();
    Collection containedTypes = type.getContainedTypes();
    
    // Definition of isSomething(Unit) predicate
    Iterator iter = containedTypes.iterator();
    while (iter.hasNext())
    {
      LanguageUnitType containedType = (LanguageUnitType)iter.next();
        res.append("is").append(type.getType()).append("(Unit) :-\n");
        res.append("  is").append(containedType.getType()).append("(Unit).\n");
    }
    res.append('\n');

    // Definition of isSomethingWithName(Unit, Name) predicate
    iter = containedTypes.iterator();
    while (iter.hasNext())
    {
      LanguageUnitType containedType = (LanguageUnitType)iter.next();
        res.append("is").append(type.getType()).append("WithName(Unit, Name) :-\n");
        res.append("  is").append(containedType.getType()).append("WithName(Unit, Name).\n");
    }
    res.append('\n');
    
    return res.toString();
  }
  
  private static String commaSeparated(Collection argList)
  {
    String res = "";
    Iterator i = argList.iterator();
    while (i.hasNext())
    {
      res += i.next();
      if (i.hasNext()) // Only when there are more elements
        res += ", ";
    }
    return res;
  }
  
  private static Vector getVarList(RelationPredicate rel)
  { // Only binary relations are supported
    Vector res = new Vector();
    res.add(rel.getVarName(1));
    res.add(rel.getVarName(2));
    return res;
  }
  
  /**
   * Generates the string representing a relation between two Language Units.
   * 2 LanguageUnitTypes can 'point' to each other; this is called a relation.
   * Possible relations (from one unit to the other) are specified by a RelationType.
   * 2 relationTypes make up 1 RelationPredicate (one relationType for each 
   * directions - both directions always have to exist!)
   *     
   * @param rel a RelationPredicate specification to be generated
   * @return the generated relation predicate
   */
  private static String genRelationPredicate(RelationPredicate rel)
  {
    boolean var1unique, var2unique;
    Vector varList = getVarList(rel);
    // Yes, the 1's and 2's are exchanged on purpose, this is not a bug.
    // Explanation: if side1 says that the link to the other side is unique,
    // it means there is only one possible value for var2, and v.v.
    var2unique = rel.getRelationType(1).isUnique();
    var1unique = rel.getRelationType(2).isUnique();
    StringBuffer res = new StringBuffer();
    // Definition of relation predicate
      res.append(rel.getPredicateName()).append('(').append(commaSeparated(varList)).append(") :-\n  ");

    if (!var1unique && !var2unique) // It is a (*,*)-relation
    { /* Because both sides can have multiple answers, we have to put
       * an answer generator on both argument 1 and 2 */
      res.append("check_or_gen12("); 
    }
    else if (!var1unique && var2unique) // It is a (*,1)-relation
    { /* Most relations are one-to-many or many-to-one (e.g. because one side
       * is the 'Parent' and these are usually unique). Because unique items
       * can be handled way faster, a distinction is made for these cases. */      
      res.append("check_or_gen1("); 
    }
    else if (var1unique && !var2unique)
    {
      res.append("check_or_gen1("); // It is a (1,*)-relation
      Collections.reverse(varList); // Reverse the order of var1,var2 in the call to the builtin (!)
      /* The builtin binaryRelation will change the arguments back to the original order.
       * This reduces the number of cases we have to handle on the prolog-side of the connector (which is slow!) */   
    }
    // else: it is a (1,1)-relation so we don't need an answer generator (the answers are already unique)

      res.append(rel.getPredicateName()).append("Builtin");
      res.append('(').append(commaSeparated(varList)).append(')');
    if (!(var1unique && var2unique)) // Some generator was included, so we have to add the closing ')'
      res.append(')');
    res.append(".\n\n");
    
    return res.toString();
  }

  private static String genCompositeRelationPredicate(CompositeRelationPredicate rel)
  {
    Collection containedRels = rel.getContainedRelationPredicates();
    StringBuffer res = new StringBuffer();
    
    Iterator iter = containedRels.iterator();
    while (iter.hasNext())
    {
      RelationPredicate containedRel = (RelationPredicate)iter.next();
      Vector varList = getVarList(containedRel);
        res.append(rel.getPredicateName()).append('(').append(commaSeparated(varList)).append(") :-\n  ");
        res.append(containedRel.getPredicateName()).append('(').append(commaSeparated(varList)).append(").\n");
    }
    res.append('\n');
    
    return res.toString();
  }
  
  /* For testing */
  /*
  public static void main(String[] args)
  {
    LanguageModel m = LanguageModel.instance();
    try
    {
      m.createMetaModel();
      PrintStream out = new PrintStream(new FileOutputStream("c:/projects/composestar/builtin.pro"));
      ModelGenerator.prologGenerator(m, out);
      out.close();
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
    catch (ModelException e)
    {
      e.printStackTrace();
    }
  }*/
}
