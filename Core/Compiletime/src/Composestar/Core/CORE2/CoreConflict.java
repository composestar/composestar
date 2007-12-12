package Composestar.Core.CORE2;

import Composestar.Core.RepositoryImplementation.RepositoryEntity;

/**
 * A conflict found by CORE
 * 
 * @author Arjan
 */
public class CoreConflict
{
	/*
	 * Types of conflicts
	 */

	public static final int CONDITION_EXPRESSION_FALSE = 1;

	public static final int MATCHING_PART_ALWAYS_MATCHES = 2;

	public static final int MATCHING_PART_NEVER_MATCHES = 3;

	public static final int MATCHING_PATTERN_ALWAYS_REJECTS = 4;

	public static final int MATCHING_PATTERN_ALWAYS_ACCEPTS = 5;

	public static final int MATCHING_PATTERN_REDUNDANT = 6;

	public static final int MATCHING_PATTERN_UNREACHABLE = 7;

	public static final int FILTER_ELEMENT_ALWAYS_REJECTS = 8;

	public static final int FILTER_ELEMENT_ALWAYS_ACCEPTS = 9;

	public static final int FILTER_ELEMENT_REDUNDANT = 10;

	public static final int FILTER_ELEMENT_UNREACHABLE = 11;

	public static final int FILTER_ALWAYS_REJECTS = 12;

	public static final int FILTER_ALWAYS_ACCEPTS = 13;

	public static final int FILTER_REDUNDANT = 14;

	public static final int FILTER_UNREACHABLE = 15;

	private static final String[] DESCRIPTIONS = { "", "Condition expression is the constant false",
			"Matching part always matches", "Matching part never matches", "Matching pattern always rejects",
			"Matching pattern always accepts", "Redundant matching pattern", "Unreachable matching pattern",
			"Filter element always rejects", "Filter element always accepts", "Redundant filter element",
			"Unreachable filter element", "Filter always rejects", "Filter always accepts", "Redundant filter",
			"Unreachable filter", };

	/*
	 * Fields
	 */

	/**
	 * Indicates the type of conflict
	 */
	private int type;

	/**
	 * The location of the conflict
	 */
	private RepositoryEntity location;

	/**
	 * The cause of the conflict
	 */
	private CoreConflict cause;

	public CoreConflict(int ctype, RepositoryEntity clocation)
	{
		type = ctype;
		location = clocation;
	}

	public CoreConflict(int ctype, RepositoryEntity clocation, CoreConflict ccause)
	{
		this(ctype, clocation);

		cause = ccause;
	}

	/**
	 * @return the cause
	 */
	public CoreConflict getCause()
	{
		return cause;
	}

	/**
	 * @return the location
	 */
	public RepositoryEntity getLocation()
	{
		return location;
	}

	/**
	 * @return the type
	 */
	public int getType()
	{
		return type;
	}

	public String getDescription()
	{
		return DESCRIPTIONS[type];
	}

}
