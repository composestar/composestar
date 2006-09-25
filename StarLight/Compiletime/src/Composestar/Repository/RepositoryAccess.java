package Composestar.Repository;

import java.util.ArrayList;
import java.util.List;

import Composestar.Repository.Configuration.CommonConfiguration;
import Composestar.Repository.Configuration.ConcernInformation;
import Composestar.Repository.LanguageModel.CallElement;
import Composestar.Repository.LanguageModel.Condition;
import Composestar.Repository.LanguageModel.External;
import Composestar.Repository.LanguageModel.FieldElement;
import Composestar.Repository.LanguageModel.Internal;
import Composestar.Repository.LanguageModel.MethodBody;
import Composestar.Repository.LanguageModel.MethodElement;
import Composestar.Repository.LanguageModel.ParameterElement;
import Composestar.Repository.LanguageModel.TypeElement;

import com.db4o.ObjectSet;
import com.db4o.query.Predicate;

public class RepositoryAccess {
	private DataStoreContainer container;
	
	public RepositoryAccess() 
	{
        container = DataStoreContainer.getInstance();
	}
	
	
    
    private ArrayList ObjectSetToArrayList(ObjectSet os)
    {
        ArrayList result = new ArrayList();
        while (os.hasNext()) {
            result.add(os.next());
        
        }
        
        return result; 
    }
    
    public CommonConfiguration GetCommonConfiguration()
    {
        List result = container.getObjects( CommonConfiguration.class );
        if (result.isEmpty()){
            return null;
        }
        else{
            return (CommonConfiguration) result.get(0);
        }
    }
    
    public List GetConcernInformation()
    {
        return container.getObjects( ConcernInformation.class );
    }
    
    public void addTypeElement( TypeElement typeElement )
    {
        container.StoreObject( typeElement );
    }
    
    public TypeElement GetTypeElement(final String fullName)
    {
        List result = container.getObjectQuery( new Predicate(){
            public boolean match( TypeElement typeElement ){
                return typeElement.get_FullName().equals( fullName );
            }
        } 
        );
        
        if (!result.isEmpty())
            return (TypeElement) result.get(0);
        else 
            return null;
    }
    
    
    
    public List getTypeElements()
    {
        return container.getObjects( TypeElement.class );
    }
    
    public void storeTypeElement( TypeElement type ){
        container.StoreObject( type );
    }
    
    public List getFieldElements(final TypeElement type)
    {
        return container.getObjectQuery( new Predicate(){
            public boolean match( FieldElement field ){
                return (field.get_ParentTypeId() == type.get_Id());
            }
        });
    }   

    public List getMethodElements(final TypeElement type)
    {
        return container.getObjectQuery( new Predicate(){
            public boolean match( MethodElement methodElement ){
                return (methodElement.get_ParentTypeId() == type.get_Id());
            }
        });
    }
    
    public void storeMethodElement( MethodElement element ){
        container.StoreObject( element );
    }
    
    public MethodElement getMethodElement( ){
        //TODO
        return null;
    }
    
    public List getParameterElements(final MethodElement method)
    {
        return container.getObjectQuery( new Predicate(){
            public boolean match( ParameterElement parameterElement ){
                return (parameterElement.get_ParentMethodId() == method.get_Id());
            }
        });
    }
    
    public List getCallElements(final MethodBody methodBody)
    {
        return container.getObjectQuery( new Predicate(){
            public boolean match( CallElement callElement ){
                return (callElement.get_ParentMethodBodyId() == methodBody.get_Id());
            }
        });
    }
    
    public void storeCallElement( CallElement element ){
        container.StoreObject( element );
    }
    
    public void storeInternal( Internal internal ){
        container.StoreObject( internal );
    }
    
    public void storeExternal( External external ){
        container.StoreObject( external );
    }
    
    public void storeCondition( Condition condition ){
        container.StoreObject( condition );
    }
}
