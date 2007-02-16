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

	public final static int CONDITION_EXPRESSION_FALSE = 1;

	public final static int MATCHING_PART_ALWAYS_MATCHES = 2;

	public final static int MATCHING_PART_NEVER_MATCHES = 3;

	public final static int MATCHING_PATTERN_ALWAYS_REJECTS = 4;

	public final static int MATCHING_PATTERN_ALWAYS_ACCEPTS = 5;

	public final static int MATCHING_PATTERN_REDUNDANT = 6;

	public final static int MATCHING_PATTERN_UNREACHABLE = 7;

	public final static int FILTER_ELEMENT_ALWAYS_REJECTS = 8;

	public final static int FILTER_ELEMENT_ALWAYS_ACCEPTS = 9;

	public final static int FILTER_ELEMENT_REDUNDANT = 10;

	public final static int FILTER_ELEMENT_UNREACHABLE = 11;

	public final static int FILTER_ALWAYS_REJECTS = 12;

	public final static int FILTER_ALWAYS_ACCEPTS = 13;

	public final static int FILTER_REDUNDANT = 14;

	public final static int FILTER_UNREACHABLE = 15;

	private final static String[] DESCRIPTIONS = { "", "Condition expression is the constant false",
			"Matching part always matches", "Matching part never matches", "Matching pattern always rejects",
			"Matching pattern always accepts", "Redundant matching pattern", "Unreachable matching pattern",
			"Filter element always rejects", "Filter element always accepts", "Redundant filter element",
			"Unreachable filter element", "Filter always rejects", "Filter always accepts", "Redundant filter",
			"Unreachable filter" };

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

	public CoreConflict(int type, RepositoryEntity location)
	{
		this.type = type;
		this.location = location;
	}

	public CoreConflict(int type, RepositoryEntity location, CoreConflict cause)
	{
		this(type, location);

		this.cause = cause;
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
