/*
 * Created on 30-mei-2006
 *
 */
package Composestar.Core.FIRE2.util.regex;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Vector;


/**
 * 
 *
 * @author Arjan de Roo
 */
public class Pattern {
    private RegularMachine machine;
    
    private Pattern( String pattern )
    throws PatternParseException
    {
        machine = Parser.parse( pattern );
    }
    
    public static Pattern compile( String pattern )
    throws PatternParseException
    {
        return new Pattern( pattern );
    }
    
    public RegularState getStartState(){
        return machine.getStartState();
    }
    
    public RegularState getEndState(){
        return machine.getEndState();
    }
    
    private static class Parser{
        public static RegularMachine parse( String pattern )
        throws PatternParseException
        {
            Lexer lexer = new Lexer( pattern );
            return parseRegularExpression( lexer );
        }
        
        private static RegularMachine parseRegularExpression( Lexer lexer )
        throws PatternParseException
        {
            return parseUnionExpression( lexer );
        }
        
        private static RegularMachine parseUnionExpression( Lexer lexer )
        throws PatternParseException
        {
            RegularMachine machine1 = parseConcatExpression( lexer );
            
            Token token = lexer.peekNextToken();
            if ( token.getType() == Token.UNION_OPERATOR ){
                lexer.nextToken();
                RegularMachine machine2 = parseUnionExpression( lexer );
                
                RegularMachine resultMachine = new RegularMachine();
                RegularState startState = new RegularState();
                resultMachine.setStartState( startState );
                RegularState endState = new RegularState();
                resultMachine.setEndState( endState );
                RegularTransition transition1 = new RegularTransition(
                        startState, machine1.getStartState() );
                RegularTransition transition2 = new RegularTransition(
                        startState, machine2.getStartState() );
                RegularTransition transition3 = new RegularTransition(
                        machine1.getEndState(), endState );
                RegularTransition transition4 = new RegularTransition(
                        machine2.getEndState(), endState );
                
                return resultMachine;
            }
            else{
                return machine1;
            }
        }
        
        private static RegularMachine parseConcatExpression( Lexer lexer )
        throws PatternParseException
        {
            RegularMachine machine1 = parseStarExpression( lexer );
            
            Token token = lexer.peekNextToken();
            switch( token.type ){
            case Token.LEFT_BRACKET:
            case Token.NEGATION_OPERATOR:
            case Token.RESOURCE_OPERATION:
                RegularMachine machine2 = parseConcatExpression( lexer );
            	RegularMachine resultMachine = new RegularMachine();
            	resultMachine.setStartState( machine1.getStartState() );
            	resultMachine.setEndState( machine2.getEndState() );
            	RegularTransition transition = new RegularTransition(
            	        machine1.getEndState(), machine2.getStartState() );
            	return resultMachine;
            case Token.STAR_OPERATOR:
                throw new PatternParseException( "Unexpected star-operator at" +
                		" position " + token.getPosition() + "." );
            default:
                return machine1;
            }
        }
        
        private static RegularMachine parseStarExpression( Lexer lexer )
        throws PatternParseException
        {
            RegularMachine machine1 = parseBasicExpression( lexer );
            
            Token token = lexer.peekNextToken();
            if ( token.type == Token.STAR_OPERATOR ){
                lexer.nextToken();
                
                RegularMachine result = new RegularMachine();
                
                RegularState startState = new RegularState();
                result.setStartState( startState );
                
                RegularState endState = new RegularState();
                result.setEndState( endState );
                
                RegularTransition transition1 = new RegularTransition(
                        startState, endState );
                RegularTransition transition2 = new RegularTransition(
                        startState, machine1.getStartState() );
                RegularTransition transition3 = new RegularTransition(
                        machine1.getEndState(), endState );
                RegularTransition transition4 = new RegularTransition(
                        machine1.getEndState(), machine1.getStartState() );
                
                return result;
            }
            else{
                return machine1;
            }
        }
        
        private static RegularMachine parseBasicExpression( Lexer lexer )
        throws PatternParseException
        {
            Token token = lexer.peekNextToken();
            if ( token.type == Token.RESOURCE_OPERATION ){
                lexer.nextToken();
                
                RegularMachine result = new RegularMachine();
                
                RegularState startState = new RegularState();
                result.setStartState( startState );
                
                RegularState endState = new RegularState();
                result.setEndState( endState );
                
                //FIXME add label to transition
                RegularTransition transition1 = new RegularTransition(
                        startState, endState );
                transition1.addResourceOperation( token.getValue() );
                
                return result;
            }
            else if ( token.type == Token.LEFT_BRACKET ){
                lexer.nextToken();
                RegularMachine result = parseRegularExpression( lexer );
                token = lexer.nextToken();
                if ( token.type != Token.RIGHT_BRACKET ){
                    throw new PatternParseException( "Unexpected token at" +
                    		" position " + token.getPosition() + 
                    		". Expected right-bracket." );
                }
                
                return result;
            }
            else if ( token.type == Token.NEGATION_OPERATOR ){
                lexer.nextToken();
                String[] resourceOperations = 
                    parseResourceOperationSequence( lexer );
                
                RegularMachine result = new RegularMachine();
                
                RegularState startState = new RegularState();
                result.setStartState( startState );
                
                RegularState endState = new RegularState();
                result.setEndState( endState );
                
                RegularTransition transition1 = new RegularTransition(
                        startState, endState );
                transition1.setNegation( true );
                for (int i=0; i<resourceOperations.length; i++){
                    transition1.addResourceOperation( resourceOperations[i] );
                }
                
                return result;
            }
            else{
                throw new PatternParseException( "Unexpected token at" +
                		" position " + token.getPosition() + 
                		". Expected resource operation, left-bracket or " +
                		"negation-operator." );
            }
        }
        
        private static String[] parseResourceOperationSequence( Lexer lexer )
        throws PatternParseException
        {
            Token token = lexer.peekNextToken();
            if ( token.type == Token.RESOURCE_OPERATION ){
                lexer.nextToken();
                
                String[] result = { token.getValue() };
                return result;
            }
            else if ( token.type == Token.LEFT_BRACKET ){
                lexer.nextToken();
                
                Vector result = new Vector();
                
                while( token.type != Token.RIGHT_BRACKET ){
                    token = lexer.nextToken();
                    if ( token.type != Token.RESOURCE_OPERATION ){
                        throw new PatternParseException( "Unexpected token at" +
                        		" position " + token.getPosition() + 
                        		". Expected resource operation." );
                    }
                    
                    result.addElement( token.getValue() );
                    
                    token = lexer.nextToken();
                    if ( token.type != Token.RIGHT_BRACKET  &&  
                            token.type != Token.UNION_OPERATOR)
                    {
                        throw new PatternParseException( "Unexpected token at" +
                        		" position " + token.getPosition() + 
                        		". Expected union-operator or right-bracket." );
                    }
                }
                
                return (String[]) result.toArray( new String[result.size()] );
            }
            else{
                throw new PatternParseException( "Unexpected token at" +
                		" position " + token.getPosition() + 
                		". Expected resource operation or left-bracket." );
            }
        }
    }
    
    
    
    private static class Lexer{
        private String pattern;
        private int pos;
        private Token bufferedToken;
        
        public Lexer( String pattern ){
            this.pattern = pattern.replaceAll( "\\s", "" );
            this.pos = 0;
        }
        
        public boolean hasMoreTokens(){
            return bufferedToken != null  ||  pos <= pattern.length();
        }
        
        public Token nextToken()
        throws PatternParseException
        {
            Token token;
            
            if ( bufferedToken != null ){
                token = bufferedToken;
                bufferedToken = null;
                return token;
            }
            else{
                token = getNextToken();
                pos += token.getLength();
                return token;
            }
        }
        
        public Token peekNextToken()
        throws PatternParseException
        {
            if ( bufferedToken == null ){
                bufferedToken = nextToken();
            }
            
            return bufferedToken;
        }
        
        private Token getNextToken()
        throws PatternParseException
        {
            if ( pos == pattern.length() )
                return new Token( Token.END_OF_PATTERN, "", pos, 0 );
            
            char c = pattern.charAt( pos );
            switch( c ){
            case '(':
                return new Token( Token.LEFT_BRACKET, "" + c, pos, 1 );
            case ')':
                return new Token( Token.RIGHT_BRACKET, "" + c, pos, 1 );
            case '|':
                return new Token( Token.UNION_OPERATOR, "" + c, pos, 1 );
            case '*':
                return new Token( Token.STAR_OPERATOR, "" + c, pos, 1 );
            case '!':
                return new Token( Token.NEGATION_OPERATOR, "" + c, pos, 1 );
            default:
                return getResourceOperation();
            }
        }
        
        private Token getResourceOperation()
        throws PatternParseException
        {
            int startPos = pos;
            char c = pattern.charAt( startPos );
            
            if ( c == '\'' ){
                StringBuffer buffer = new StringBuffer();
                startPos++;
                while ( startPos < pattern.length() ){
                    c = pattern.charAt( startPos );
                    if ( c == '\'' )
                        break;
                    
                    if ( c == '_'  ||  Character.isLetterOrDigit(c) ){
                        buffer.append( c );
                    }
                    else{
                        throw new PatternParseException( "Unexpected character at" +
                        		" position " + startPos + 
                        		". Expected underscore, letter, digit or apostrophe." );
                    }
                    startPos++;
                }
                
                if ( c != '\'' ){
                    throw new PatternParseException( 
                            "Unexpected end of pattern. Expected underscore, " +
                            "letter, digit or apostrophe." );
                }
                
                return new Token( Token.RESOURCE_OPERATION, buffer.toString(), pos,
                        buffer.length() + 2 );
            }
            else if ( c == '_'  ||  Character.isLetterOrDigit(c) ){
                return new Token( Token.RESOURCE_OPERATION, "" + c, pos, 1 );
            }
            else{
                throw new PatternParseException( "Unexpected characters at" +
                		" position " + pos + 
                		". Expected underscore, letter, digit or apostrophe." );
            }
        }
    }
    
    
    
    private static class Token{
        private int type;
        private String value;
        private int position;
        private int length;
        
        public final static int LEFT_BRACKET = 1;
        public final static int RIGHT_BRACKET = 2;
        public final static int UNION_OPERATOR = 3;
        public final static int STAR_OPERATOR = 4;
        public final static int NEGATION_OPERATOR = 5;
        public final static int RESOURCE_OPERATION = 100;
        public final static int END_OF_PATTERN = 1000;
        
        private Token( int type, String value, int position, int length ){
            this.type = type;
            this.value = value;
            this.position = position;
            this.length = length;
        }
        
        
        
        public int getType() {
            return type;
        }
        public String getValue() {
            return value;
        }
        public int getPosition() {
            return position;
        }
        public int getLength() {
            return length;
        }
    }
    
    
    static class RegularMachine{
        private RegularState startState;
        private RegularState endState;
        
        
        public RegularMachine(){
        }
        
        
        public void setStartState( RegularState startState ){
            this.startState = startState;
        }
        
        public RegularState getStartState(){
            return startState;
        }
        
        public void setEndState( RegularState endState ){
            this.endState = endState;
        }
        
        public RegularState getEndState(){
            return endState;
        }
    }
    
    static class RegularState{
        private Vector outTransitions;
        
        public RegularState(){
            outTransitions = new Vector();
        }
        
        public void addOutTransition( RegularTransition transition ){
            outTransitions.addElement( transition );
        }
        
        public Enumeration getOutTransitions(){
            return outTransitions.elements();
        }
    }
    
    static class RegularTransition{
        private RegularState startState;
        private RegularState endState;
        
        private HashSet operations;
        private boolean negation;
        
        public RegularTransition( RegularState startState, RegularState endState ){
            this.startState = startState;
            this.endState = endState;
            
            startState.addOutTransition( this );
            
            operations = new HashSet();
        }
        
        public void setNegation( boolean negation ){
            this.negation = negation;
        }
        
        public boolean isNegation(){
            return negation;
        }
        
        public void addResourceOperation( String resourceOperation ){
            operations.add( resourceOperation );
        }
        
        public boolean match( String resourceOperation ){
            if ( operations.contains( "_" ) ){
                return !negation;
            }
            else if ( negation ){
                return !operations.contains( resourceOperation ); 
            }
            else{
                return operations.contains( resourceOperation );
            }
        }
        
        public boolean isEmpty(){
            return operations.isEmpty();
        }
        
        
        public RegularState getEndState() {
            return endState;
        }
        public RegularState getStartState() {
            return startState;
        }
    }
}
