// IdGroup.java
//
// (c) 1999-2001 PAL Development Core Team
//
// This package may be distributed under the
// terms of the Lesser GNU General Public License (LGPL)

package pal.misc;

/**
 * An indexed group of identifiers. For example of group of taxa
 * related by a phylogenetic tree. 
 * <BR><B>NOTE:</B> Was called Taxa but not general enough.
 *
 * @version $Id: IdGroup.java,v 1.8 2001/07/13 14:39:13 korbinian Exp $
 *
 * @author Alexei Drummond
 */
public interface IdGroup extends java.io.Serializable {

	/**
	 * Returns the number of identifiers in this group
	 */
	int getIdCount();

	/**
	 * Returns the ith identifier.
	 */
	Identifier getIdentifier(int i);

	/**
	 * Sets the ith identifier.
	 */
	void setIdentifier(int i, Identifier id);

	/**
	 * returns the index of the identifier with the given name.
	 */
	int whichIdNumber(String name);
}

