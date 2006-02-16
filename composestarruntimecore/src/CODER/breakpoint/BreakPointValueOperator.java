package Composestar.RuntimeCore.CODER.BreakPoint;

import Composestar.RuntimeCore.CODER.BreakPointListener;
import Composestar.RuntimeCore.CODER.Halter;
import Composestar.RuntimeCore.CODER.StateHandler;
import Composestar.RuntimeCore.CODER.Model.DebuggableFilter;
import Composestar.RuntimeCore.CODER.Model.DebuggableMessage;
import Composestar.RuntimeCore.CODER.Value.Value;
import java.util.ArrayList;
import java.util.Dictionary;

/**
 * Summary description for BreakPointBiLTL.
 */
public class BreakPointValueOperator extends BreakPoint implements Value
{
	protected Value left;
	protected Value right;
	protected int operator;

	public static final byte TIMES = 12;
	public static final byte DIVREST = 11;
	public static final byte DIV = 10;
	public static final byte MINUS = 9;
	public static final byte PLUS = 8;

	public static final byte OR = 7;
	public static final byte AND = 6;
	public static final byte EQUIV = 14;
	public static final byte IMPLIES = 13;

	public static final byte BIGGERTHEN = 5;
	public static final byte LESSERTHEN = 4;
	public static final byte BIGGEREQ = 3;
	public static final byte LESSEREQ = 2;
	public static final byte NOTEQ = 1;
	public static final byte EQ = 0;

	private final static Boolean oTrue = new Boolean(true);
	private final static Boolean oFalse = new Boolean(false);


	public Object getValue() 
	{
		switch (operator) 
		{
			case EQ:
				return left.compareTo(right) == 0 ? oTrue : oFalse;
			case NOTEQ:
				return left.compareTo(right) == 0 ? oFalse : oTrue;
			case LESSEREQ:
				return left.compareTo(right) <= 0 ? oTrue : oTrue;
			case BIGGEREQ:
				return left.compareTo(right) >= 0 ? oTrue : oTrue;
			case LESSERTHEN:
				return left.compareTo(right) < 0 ? oTrue : oTrue;
			case BIGGERTHEN:
				return left.compareTo(right) > 0 ? oTrue : oTrue;
			default:
		}

		Object leftValue = left.getValue();
		Object rightValue = right.getValue();

		if(leftValue == null)	return rightValue;
		if(rightValue == null)  return leftValue;

		if(leftValue instanceof Number && rightValue instanceof Number)
		{
			switch(operator)
			{
				case TIMES:
					return new Double(((Number)leftValue).doubleValue() * ((Number)rightValue).doubleValue());
				case DIVREST:
					return new Double(((Number)leftValue).doubleValue() % ((Number)rightValue).doubleValue());
				case DIV:
					return new Double(((Number)leftValue).doubleValue() / ((Number)rightValue).doubleValue());
				case MINUS:
					return new Double(((Number)leftValue).doubleValue() - ((Number)rightValue).doubleValue());
				case PLUS:
					return new Double(((Number)leftValue).doubleValue() + ((Number)rightValue).doubleValue());
				default:
			}
			throw new RuntimeException("Cannot preform " + operatorToString());
		}
		if(leftValue instanceof Boolean && rightValue instanceof Boolean)
		{
			switch(operator)
			{
				case OR:
					return ((Boolean)leftValue).booleanValue() || ((Boolean)rightValue).booleanValue() ? oTrue: oFalse;
				case AND:
					return ((Boolean)leftValue).booleanValue() && ((Boolean)rightValue).booleanValue() ? oTrue: oFalse;
				case IMPLIES:
					return ((Boolean)leftValue).booleanValue() ? rightValue : oTrue;
				case EQUIV:
					return ((Boolean)leftValue).booleanValue() == ((Boolean)rightValue).booleanValue() ? oTrue : oFalse;
				default:
			}
		}
		throw new RuntimeException("Wrong operator Type:" + operator);
	}

	public BreakPointValueOperator(Halter halt, Value left, int operator, Value right)
	{
		super(halt);
		this.left = left;
		this.right = right;
		this.operator = operator;
	}

	public boolean threadSpecific() 
	{
		return false;
	}

	public boolean matchEvent(int eventType, DebuggableFilter currentFilter, Object source, DebuggableMessage message, Object target, ArrayList filters, Dictionary context)
	{
		//Just check the last one.
		return getValue() == oTrue;
	}

	public String toString() 
	{
		String result = "(";
		if (left != null ) 
		{
			result += left.toString();
		} 
		else
		{
			result += "null";
		}
		result += operatorToString();
		if (right != null ) 
		{
			result += right.toString();
		} 
		else
		{
			result += "null";
		}
		return result;
	}

	private String operatorToString() 
	{
		switch (operator) 
		{
			case EQ:
				return " == ";
			case NOTEQ:
				return " != ";
			case LESSEREQ:
				return " <= ";
			case BIGGEREQ:
				return " >= ";
			case LESSERTHEN:
				return " < ";
			case BIGGERTHEN:
				return " > ";
			case TIMES:
				return " * ";
			case DIVREST:
				return " % ";
			case DIV:
				return " / ";
			case MINUS:
				return " - ";
			case PLUS:
				return " + ";
			case OR:
				return " || ";
			case AND:
				return " && ";
			case IMPLIES:
				return " -> ";
			case EQUIV:
				return " <-> ";
			default:
				throw new RuntimeException("Wrong operator Type:" + operator);
		}
	}

	public int compareTo(Object o)
	{
		if(o instanceof Value)
		{
			int comp = ((Value)o).compareTo(getValue());
			//inverse
			if(comp == 0)
			{
				return 0;
			} 
			else if(comp > 0)
			{
				return -1;
			}
			else
			{
				return 1;
			}
		}
		else if(o instanceof Number) //Stupid .NET implements Comparable in the subclasses.
		{
			double value = ((Number)o).doubleValue();
			Object thisValue = getValue();
			return (!(thisValue instanceof Number)) ? 0 : value > ((Number)thisValue).doubleValue()? -1: value == ((Number)thisValue).doubleValue()? 0: 1;
		}
		return 0;
	}
}
