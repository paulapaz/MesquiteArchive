// CodonTable.java
//
// (c) 1999-2001 PAL Development Core Team
//
// This package may be distributed under the
// terms of the Lesser GNU General Public License (LGPL)


package pal.datatype;


/**
 * Describes a device for translating Nucleotide triplets
 * or codon indices into amino acid codes
 *
 * @author Matthew Goode
 * @author Alexei Drummond
 *
 * @version $Id: CodonTable.java,v 1.5 2001/07/13 14:39:13 korbinian Exp $
 */

public interface CodonTable extends java.io.Serializable {

	/** 
	 * Returns the char associated with AminoAcid represented by 'codon'
	 * @note char is as defined by AminoAcids.java
	 * @see AminoAcids
	 * @return state for '?' if codon unknown or wrong length
	 */
	char getAminoAcidChar(char[] codon);

	/** 
	 * Returns the state associated with AminoAcid represented by 'codon'
	 * @note state is as defined by AminoAcids.java
	 * @see AminoAcids
	 * @return '?' if codon unknown or wrong length
	 */
	int getAminoAcidState(char[] codon);

	/** Returns the amino acid char at the corresponding codonIndex */
	char getAminoAcidCharFromCodonIndex(int codonIndex);

	/** Returns the amino acid state at the corresponding codonIndex */
	int getAminoAcidStateFromCodonIndex(int codonIndex);

	/**
	 * Returns the codon indexes of terminator amino acids.
	 */
	int[] getTerminatorIndexes();

	/**
	 * Returns the number of terminator amino acids.
	 */
	int getNumberOfTerminatorIndexes();
}

