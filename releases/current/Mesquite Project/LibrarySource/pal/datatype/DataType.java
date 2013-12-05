// DataType.java
//
// (c) 1999-2001 PAL Development Core Team
//
// This package may be distributed under the
// terms of the Lesser GNU General Public License (LGPL)


// Known bugs and limitations:
// - all states must have a non-negative value 0..getNumStates()-1
// - ? (unknown state) has value getNumStates()


package pal.datatype;

import java.io.Serializable;

/**
 * interface for sequence data types
 *
 * @version $Id: DataType.java,v 1.11 2001/07/13 14:39:13 korbinian Exp $
 *
 * @author Korbinian Strimmer
 * @author Alexei Drummond
 */
public interface DataType extends Serializable
{
	//
	// Public stuff
	//

	char UNKNOWN_CHARACTER = '?';
	
	int NUCLEOTIDES = 0;
	int AMINOACIDS = 1;
	int TWOSTATES = 2;
	int IUPACNUCLEOTIDES = 3;
	int CODONS = 4;
	int GENERALIZEDCODONS = 5;
	
	/**
	 * get number of unique states
	 *
	 * @return number of unique states
	 */
	int getNumStates();

	/**
	 * get number of unique non-ambiguous states
	 *
	 * @return number of unique states
	 */
	int getNumSimpleStates();

	/**
	 * get state corresponding to a character
	 *
	 * @param c character
	 *
	 * @return state
	 */
	int getState(char c);

	/**
	 * get character corresponding to a given state
	 *
	 * @param state state
	 *
	 * return corresponding character
	 */
	char getChar(int state);
	
	/**
	 * returns true if this state is an ambiguous state.
	 */
	boolean isAmbiguousState(int state);

	/**
	 * returns an array containing the non-ambiguous states that this state represents.
	 */
	int[] getSimpleStates(int state);
	
	/**
	 * description of data type
	 *
	 * @return string describing the data type
	 */
	String getDescription();
	
	/**
	 * get numerical code describing the data type
	 *
	 * @return numerical code
	 */
	int getTypeID();

	/**
	 * @return true if this state is an unknown state 
	 * (the same as check if a state is >= the number of states... but neater)
	 */
	boolean isUnknownState(int state);
}

