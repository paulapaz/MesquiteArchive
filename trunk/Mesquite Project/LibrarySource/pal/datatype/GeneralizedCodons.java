// GeneralizedCodons.java
//
// (c) 1999-2001 PAL Development Core Team
//
// This package may be distributed under the
// terms of the Lesser GNU General Public License (LGPL)

package pal.datatype;

import pal.math.MathUtils;

// Note this class used to be called CodonTranslator

/** 
 * Used for mapping an exact codon to an exact state, and vice versa
 * Can also be used as a DataType (exactly as for Codons, but more versatile).
 * 
 * @author Matthew Goode
 * @version $Id: GeneralizedCodons.java,v 1.4 2001/07/13 14:39:13 korbinian Exp $
 */
public class GeneralizedCodons extends SimpleDataType implements java.io.Serializable {

	DataType characterType_ = null;
	int numberOfCharacterStates_ =	-1; // Caching of characterType_.getNumStates()
	int codonLength_;

	//==== For Datatype Interface ====
	int numberOfCodonStates_;

	/** The Biologists, what else do we need, constructor (nucleode character types, codon length 3) */
	public GeneralizedCodons () {
		this(new Nucleotides(),3);
	}

	/** The Computer Scientist's want of abstraction consturctor */
	public GeneralizedCodons (DataType characterType, int codonLength) {
		this.characterType_ = characterType;
		this.numberOfCharacterStates_ = characterType_.getNumStates();
		this.codonLength_ = codonLength;
		numberOfCodonStates_ = 1;
		for(int i = 0 ; i < codonLength_ ; i++) {
			numberOfCodonStates_*=numberOfCharacterStates_;
		}
	}

	/** Returns -1 if the codon has unknowns, or gaps in it, or is less than length length! */
	public final int getCodonIndexFromCodon(char[] codon) {
		if(codon.length<codonLength_) {
			return -1;
		}
		int index = 0;
		for(int i = 0 ; i < codonLength_ ; i++) {
			index*=numberOfCharacterStates_;
			int state = characterType_.getState(codon[i]);
			if(state>=numberOfCharacterStates_) {
				return -1;
			}
			index+=state;
		}
		return index;
	}

	/** Translates an index into a codon */
	public final char[] getCodonFromCodonIndex(int index) {
		char[] cs = new char[codonLength_];
		for(int i = codonLength_-1 ; i >=0 ; i--) {
			cs[i] = characterType_.getChar(index%numberOfCharacterStates_);
			index/=numberOfCharacterStates_;
		}
		return cs;
	}

	/**
	 * Returns a unique ascii character for any given codon index
	 */
	public final char getCodonCharFromCodonIndex(int index) {
		return (char)(index + 64);
	}

	/**
	 * Returns a unique ascii character for any given codon index
	 */
	public final int getCodonIndexFromCodonChar(char c) {
		return (int)(c - 64);
	}

	/**
	 * Returns a unique ascii character for any given codon
	 */
	public final char getCodonCharFromCodon(char[] codon) {
		return getCodonCharFromCodonIndex(getCodonIndexFromCodon(codon));
	}


	/**
	 * Translates a Nucleotide sequence into a Codon sequence
	 */
	public final String convertNucleotideToCodon(String nucleotideSequence, int startingPosition, int length, boolean reverse ) {
		return (convertNucleotideToCodon(nucleotideSequence.toCharArray(), startingPosition,length, reverse )).toString();
	}

	/**
	 * Translates a Nucleotide sequence into a Codon sequence
	 * @param nucleotideSequence - the base nucleotide sequence as a char array
	 * @param starting position - the starting position to begin reading from
	 * @param length - the length of the reading frame (in nucleotide units -
	 * should be a multiple of 3, if not remainder is truncated!)
	 * @param reverse - if true works backwards with codon at starting
	 * position being last in translation (codon read in reverse as well). 
	 * Else reads forwards.
	 * @param translator - the nucleotide translator to use for translation
	 * nucleotides into amino acids.
	 * @note can handle circular reading frames (ie startingPositon+length
	 * can be greater than seuqnce length)
	 */
	public final char[] convertNucleotideToCodon(char[] nucleotideSequence, int startingPosition, int length, boolean reverse) {

		char[] work = new char[codonLength_];
		//Normal direction
		int numberOfCodons = (length)/codonLength_;
		char[] aas = new char[numberOfCodons];
		if(reverse) {
			for(int i = 0 ; i < numberOfCodons ; i++) {
				int index = i*codonLength_+startingPosition;;
				for(int j = 0 ; j < codonLength_ ; j++) {
					work[j] = nucleotideSequence[(index+codonLength_-1-j)%nucleotideSequence.length];
				}
				aas[numberOfCodons-i-1] = getCodonCharFromCodon(work);
			}
		} else {
			for(int i = 0 ; i < numberOfCodons ; i++) {
				int index = i*codonLength_+startingPosition;
				for(int j = 0 ; j < codonLength_ ; j++) {
					work[j] = nucleotideSequence[(index+j)%nucleotideSequence.length];
				}
				aas[i] = getCodonCharFromCodon(work);
			}
		}
		return aas;
	}

	// ============================== Datatype Stuff	============================

	/**
	 * get number of unique states
	 *
	 * @return number of unique states
	 */
	public int getNumStates() { return numberOfCodonStates_;	}

	/**
	 * get state corresponding to a character
	 *
	 * @param c character
	 *
	 * @return state
	 */
	public int getState(char c)	{
		if(c==UNKNOWN_CHARACTER) {
		 	return numberOfCodonStates_;
		}
		return getCodonIndexFromCodonChar(c);
	}

	/**
	 * Get character corresponding to a given state
 	 */
	public char getChar(int state) {

		if(state>=numberOfCodonStates_) {
		 	return UNKNOWN_CHARACTER;
		}
		return getCodonCharFromCodonIndex(state);
	}

	/**
	 * String describing the data type
	 */
	public String getDescription() {
		return "Codon";
	}

	/**
	 * @return true if this state is an unknown state
	 */
	public boolean isUnknownState(int state) {
		return(state>=64);
	}	

	/**
	 * Get numerical code describing the data type
	 */
	public int getTypeID() 	{
		return DataType.GENERALIZEDCODONS;
	}

	//======

	public double[] getCodonFrequencies(double[] residueFrequencies) {
		residueFrequencies = MathUtils.getNormalized(residueFrequencies);
		double[] codonFreqs = new double[numberOfCodonStates_];

		for(int j = 0 ; j < numberOfCodonStates_ ; j++) {
			int index = j;
			double prob = 1;

			for(int i = codonLength_-1 ; i >=0 ; i--) {
				prob	*= residueFrequencies[index%numberOfCharacterStates_];
				index/=numberOfCharacterStates_;
			}

			codonFreqs[j] = prob;
		}
		return MathUtils.getNormalized(codonFreqs);
	}

	/** Not correctly implemented!! */
	public double[] getNucleotideFrequencies(double[] codonFrequencies) {
		return new double[] {0.25,0.25,0.25,0.25};
	}

}

