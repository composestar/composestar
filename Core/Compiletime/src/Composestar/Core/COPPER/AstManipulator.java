/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Core.COPPER;

import Composestar.Utils.*;
import antlr.*;
import antlr.collections.*;

import java.util.*;

public class AstManipulator implements CpsTokenTypes
{
	private CommonAST astroot;

	private PathHelper ph;

	private boolean navigateError; // error while navigating the tree

	private boolean adding;

	AstManipulator()
	{
		ph = new PathHelper();
	}

	// attach to an ast tree to modify
	void attach(CommonAST ast)
	{
		astroot = ast;
	}

	public boolean checkifExists(String path)
	{
		// 1. string splitten
		// 2. voor elk deeltje: ast = navigateTo(deeltje@1, AST)
		// 3. als dat ergens failt, dan bestaat die niet

		AST ast;
		int i;
		Piece piece;

		adding = false;
		ast = astroot;
		ph.splitPath(path);
		for (i = 0; i < ph.getNumberOfPieces(); i++)
		{
			piece = ph.getNextPiece();
			if (i == 0)
			{
				ast = checkRoot(piece, ast); // first item in path is always
												// root, no need to navigate,
												// just check
			}
			else
			{
				ast = navigateTo(piece, ast, path);
			}
			if (ast == null)
			{
				return false;
			}
		}

		return true;
	}

	public String getValue(String path)
	{
		// 1. string splitten
		// 2. voor elk deeltje: ast = navigateTo(deeltej@1, AST)
		// 3. als dat ergens failt, dan bestaat die niet
		// 4. als je er bent, curast.getText();
		// 5. returnen

		AST ast;
		int i;
		Piece piece;

		adding = false;
		ast = astroot;
		ph.splitPath(path);
		for (i = 0; i < ph.getNumberOfPieces(); i++)
		{
			piece = ph.getNextPiece();
			if (i == 0)
			{
				ast = checkRoot(piece, ast); // first item in path is always
												// root, no need to navigate,
												// just check
			}
			else
			{
				ast = navigateTo(piece, ast, path);
			}
			if (ast == null)
			{
				return null; // failed
			}
		}

		// gevonden
		return ast.getText();
	}

	public boolean add(String path)
	{
		// 1. string splitten
		// 2. voor elk deeltje:
		// a. kijken of geen @...new gebruikt is, dan altijd adden, naar stap c.
		// b. probeer: ast = navigateTo(deeltje@1, AST) <-- opletten of er niet
		// 2 zijn
		// c. lukt dat niet, dan bestaat die niet, dus aanmaken (gebruik tabel)
		// <-- opletten of je niet moet inserten

		AST ast, astback;
		int i;
		Piece piece;

		adding = true; // we're adding stuff, so don't display 'not found'
						// errors
		ast = astroot;
		ph.splitPath(path);
		for (i = 0; i < ph.getNumberOfPieces(); i++)
		{
			piece = ph.getNextPiece();
			if (piece.isCreateNew())
			{ // altijd toevoegen
				ast = addTo(piece, ast, path);
			}
			else
			{ // try to navigate, then add
				if (i == 0)
				{
					ast = checkRoot(piece, ast); // first item in path is
													// always root, no need to
													// navigate, just check
				}
				else
				{
					astback = ast;
					ast = navigateTo(piece, ast, path);
					if (navigateError)
					{
						return false; // ambiguous path
					}
					else if (ast == null)
					{ // didn't work, so try to add
						ast = addTo(piece, astback, path);
					}
				}
			}
		}

		// gevonden
		return true;
	}

	// ----- helper methods
	// -------------------------------------------------------------------

	private AST addTo(Piece piece, AST ast, String totalpath)
	{
		CommonAST astnew;

		if ((piece.getPosition() == null) || "last".equals(piece.getPosition()))
		{
			astnew = new CommonAST();
			astnew.setText(piece.getText());
			astnew = insertType(astnew, piece.getText());
			ast.addChild(astnew);
		}
		else
		{ // specific position specified
			astnew = new CommonAST();
			astnew.setText(piece.getText());
			astnew = insertType(astnew, piece.getText());
			insertChild(ast, Integer.parseInt(piece.getPosition()), astnew);
		}

		return astnew;
	}

	private CommonAST insertType(CommonAST astnew, String type)
	{
		if (type.equals("concern"))
		{
			astnew.setType(LITERAL_concern);
		}
		else if (type.equals("filtermodule"))
		{
			astnew.setType(LITERAL_filtermodule);
		}
		else if (type.equals("inputfilters"))
		{
			astnew.setType(LITERAL_inputfilters);
		}
		else if (type.equals("inputfilter"))
		{
			astnew.setType(IFILTER_);
		}
		else if (type.equals("type"))
		{
			astnew.setType(TYPE_);
		}
		else if (type.equals("filterelements"))
		{
			astnew.setType(FILTERSET_);
		}
		else if (type.equals("filterelement"))
		{
			astnew.setType(FILTERELEM_);
		}
		else if (type.equals("messagePatternSet"))
		{
			astnew.setType(MPSET_);
		}
		else if (type.equals("messagePattern"))
		{
			astnew.setType(MP_);
		}
		else if (type.equals("["))
		{
			astnew.setType(LSQUARE);
		}
		else if (type.equals("<"))
		{
			astnew.setType(LANGLE);
		}
		else if (type.equals("'"))
		{
			astnew.setType(SINGLEQUOTE);
		}
		else if (type.equals("selector"))
		{
			astnew.setType(SELEC_);
		}
		else if (type.equals("target"))
		{
			astnew.setType(TARGET_);
		}
		else if (type.equals("*"))
		{
			astnew.setType(STAR);
		}
		else if (type.equals(";"))
		{
			astnew.setType(SEMICOLON);
		}
		else if (type.equals("superimposition"))
		{
			astnew.setType(LITERAL_superimposition);
		}
		else if (type.equals("filtermodule set"))
		{
			astnew.setType(STAR);
		}
		else
		{
			astnew.setType(NAME); // default
		}
		return astnew;
	}

	private boolean insertChild(AST ast, int position, AST astnew)
	{
		Vector children;
		Vector newChildren = new Vector();
		int i;

		children = getAllChildren(ast);
		// deleten current children //fixme

		// inserten op nieuwe positie
		for (i = 0; i < position; i++)
		{
			newChildren.add(children.elementAt(i));
		}
		newChildren.add(astnew);
		for (i = position; i < children.size(); i++)
		{
			newChildren.add(children.elementAt(i));
		}

		return false;
	}

	private AST checkRoot(Piece piece, AST ast)
	{
		String a = piece.getText();
		String b = ast.getText();

		if (a.equals(b))
		{
			return ast;
		}
		else
		{
			return null;
		}
	}

	private AST navigateTo(Piece piece, AST ast, String totalpath)
	{
		Vector children;
		AST correctast;

		children = getAllChildren(ast);
		if (children == null)
		{
			return null; // fixme: give error message?
		}
		correctast = searchChildren(children, piece.getText(), piece.getPosition(), totalpath);
		return correctast;
	}

	private AST searchChildren(Vector children, String text, String position, String totalpath)
	{
		int i, counter = -1;
		int[] positions = new int[children.size()];

		navigateError = false;

		if (text != null)
		{ // because we can also have @num and then the text doesn't matter
			// collect all positions
			for (i = 0; i < children.size(); i++)
			{
				if (((AST) children.elementAt(i)).getText().equals(text))
				{
					counter++;
					positions[counter] = i;
				}
			}
			// checking
			if ((counter > 1) && (position == null))
			{ // meer dan 1 match en geen positie
				Debug.out(Debug.MODE_WARNING, "COPPER", "Ambiguous node " + text + " in path " + totalpath
						+ ". Add position.");
				navigateError = true;
				return null;
			}
			if ((position != null) && (!"last".equals(position)) && (Integer.parseInt(position) > counter))
			{ // positie die niet bestaat opgegeven
				if (!adding)
				{
					Debug.out(Debug.MODE_WARNING, "COPPER", "Invalid position specified in node " + text + " in path "
							+ totalpath + '.'); // fixme: doens't matter for add
				}
				return null;
			}

			if (counter > -1)
			{ // ok alles goed
				if (position == null)
				{ // geen positie en 1 element (> 1 geeft boven error)
					return (AST) children.elementAt(positions[0]);
				}
				else if (position.equals("last"))
				{ // last
					return (AST) children.elementAt(positions[counter]);
				}
				else
				{ // @num
					return (AST) children.elementAt(positions[Integer.parseInt(position)]);
				}
			}
			else
			{
				if (!adding)
				{
					Debug.out(Debug.MODE_WARNING, "COPPER", "Node " + text + " not found in path " + totalpath + '.'); // fixme:
																														// doesn't
																														// matter
																														// for
																														// add
				}
				return null;
			}
		}
		else
		{ // only position specified
			if (position == null)
			{
				Debug.out(Debug.MODE_WARNING, "COPPER", "No node specified in in path " + totalpath + '.');
				return null;
			}
			else if ("last".equals(position))
			{ // last
				return (AST) children.lastElement();
			}
			else
			{ // @num
				return (AST) children.elementAt(Integer.parseInt(position));
			}
		}
	}

	private Vector getAllChildren(AST ast)
	{
		int count, i;
		AST curast = null;
		Vector children;

		children = new Vector();
		count = ast.getNumberOfChildren();
		if (count == 0)
		{
			return null; // no children
		}
		try
		{
			for (i = 0; i < count; i++)
			{
				if (i == 0)
				{
					curast = ast.getFirstChild(); // alleen de 1e keer
													// getchild gebruiken
				}
				else
				{
					curast = curast.getNextSibling();
				}

				children.add(curast);
			}
		}
		catch (NullPointerException e)
		{
			Debug.out(Debug.MODE_WARNING, "COPPER", e.getMessage());
		}
		return children;
	}
}
