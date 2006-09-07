
package Composestar.C.wrapper;

public class Variable 
{
  public String name= "";      
  public boolean IsStatic= false;   
  private int pointerLevel= 0;
  private int arrayLevel=0;
  private boolean isGlobal= false;
  private boolean isExtern= false;
  private boolean isInline= false;
  public String FieldTypeString= "";
  public String filename="";
     
  public String FieldTypeString()
  {
    return this.FieldTypeString;     
  }
  
  public void setFieldTypeString(String FieldTypeString)
  {
     this.FieldTypeString = FieldTypeString;     
  }
  
  public boolean isStatic()
  {
    return this.IsStatic;     
  }
  
  public void setIsInline(boolean isInline)
  {
     this.isInline = isInline;     
  }
  public boolean isInline()
  {
    return this.isInline;     
  }
  
  public void setIsExtern(boolean isExtern)
  {
     this.isExtern = isExtern;     
  }
  public boolean isExtern()
  {
    return this.isExtern;     
  }
  
  public void setIsStatic(boolean isStatic)
  {
     this.IsStatic = isStatic;     
  }
  
  public boolean isPointer(){
	  return this.pointerLevel>0;
  }
  
  public int getPointerLevel(){
	  return this.pointerLevel;
  }
  
  public void setPointerLevel(int pointerLevel){
	  this.pointerLevel=pointerLevel;
  }
  
  public boolean isArray(){
	  return this.arrayLevel>0;
  }
  
  public void setArrayLevel(int arrayLevel){
	  this.arrayLevel=arrayLevel;
  }
  
  public int getArrayLevel(){
	  return this.arrayLevel;
  }
  
  public boolean isGlobal(){
	  return this.isGlobal;
  }
  
  public void setIsGlobal(boolean isGlobal){
	  this.isGlobal=isGlobal;
  }
  
  public String name()
  {
    return this.name;
  }
  
  public void setName(String name)
  {
    this.name = name;
  }
  public String fileName()
  {
    return this.filename;
  }
  
  public void setFileName(String name)
  {
    this.filename = name;
  }

}
