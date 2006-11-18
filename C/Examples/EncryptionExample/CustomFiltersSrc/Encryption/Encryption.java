
import Composestar.C.CONE.Semantic;

/**
 * @author johan
 *
 * Encryption filter
 */
public class Encryption extends Semantic{
		
		private String type="Encryption";
		private String advice="";
		private boolean afterAdvice=false;
		private boolean beforeAdvice=true;
		private boolean redirectMessage=false;
		private boolean needsHeaderFiles=false;
		
		/**Does not use parameter functions yet**/
		public String getBeforeAdvice(){
			String advice="";
			advice+= "char* x;";
			advice+= "x = "+ this.parameterName(0)+";";
			advice+= "printf(\"Encrypt message with filter: "+this.parameterName(0)+"\n\");";
			advice+= "while(*x != \'\\0\'){";
			advice+= "*x += 20;";
			advice+= "x++;"; 
			advice+= "}";
			return advice;
		}
		
		public String getAfterAdvice(){
			return advice;
		}
		
		public boolean afterAdvice(){
			return afterAdvice;
		}
		
		public boolean beforeAdvice(){
			return beforeAdvice;
		} 	
		
		public boolean redirectMessage(){
			return redirectMessage;
		}
		
		public String headerFile(){
			return "";
		}
		
		public boolean needsHeaderFiles(){
			return needsHeaderFiles;
		}
		
		public String getType(){
			return type;
		}
	

}
