// CodonTableFactory.java
//
// (c) 1999-2001 PAL Development Core Team
//
// This package may be distributed under the
// terms of the Lesser GNU General Public License (LGPL)


package pal.datatype;


/**
 * Generates CodonTables
 *
 * @author Matthew Goode
 * @version $Id: CodonTableFactory.java,v 1.5 2001/07/13 14:39:13 korbinian Exp $
 */


public class CodonTableFactory {

	/** Universal type */
	public static final int UNIVERSAL = 0; 

	/** Mitochondrial Vertebrate type */
	public static final int M_VERTEBRATE = 1; 

	public static final String[] TYPE_NAMES = {
		"Universal",
		"Mitochondrial Vertebrate"
	};

	/** 
	 * Creates a translator of a given types
	 * @param type - UNIVERSAL, M_VERTEBRATE
	 */
	public static CodonTable createTranslator(int type) {
		switch(type) {
			case UNIVERSAL : {
				return new UniversalTranslator();
			}
			case M_VERTEBRATE : {
				return new MVertebrateTranslator();
			}
			default : {
				return new UniversalTranslator();
			}
		}
	}

	public static final CodonTable createUniversalTranslator() {
		return createTranslator(UNIVERSAL);
	}
}

// ========================================================================


/** 
 * A concrete implementation of a NucleotideTranslator for
 * the Standard/Universal set of codes.
 */
class UniversalTranslator implements CodonTable {
	int[] translations_ = new int[64];
	int[] terminatorIndexes_ = null;
	DataType aminoAcids_ = new AminoAcids();
	DataType nucleotides_ = new Nucleotides();
GeneralizedCodons generalizedCodons_ = new GeneralizedCodons();


	private void addTerminalIndex(int index) {
	 	int[] ts = new int[terminatorIndexes_.length+1];

		for(int i = 0 ; i < terminatorIndexes_.length ; i++) {
			ts[i] = terminatorIndexes_[i];
		}
		ts[ts.length-1] = index;
		terminatorIndexes_ = ts;
	}

	/**
	 * state must already be in set!
	 */
	private void removeTerminalIndex(int index) {

	 	int[] ts = new int[terminatorIndexes_.length-1];
		int toAddIndex = 0;

		for(int i = 0 ; i < terminatorIndexes_.length ; i++) {
			if(index!=terminatorIndexes_[i]) {
				ts[toAddIndex] = terminatorIndexes_[i];
				toAddIndex++;
			}
		}
	}

	/** Add a codon/aminoAcid translation into table. 
	 * Overwrites previous translation for that codon. 
	 * If the codon is invalid will throw a runtime exception!
	 */
	protected void add(String codon, char aminoAcid) {
		int index = generalizedCodons_.getCodonIndexFromCodon(codon.toCharArray());
		if(index<0) {
			throw new RuntimeException("Assertion error: Adding invalid Codon:"+codon);
		}
		if(aminoAcid!= AminoAcids.TERMINATE_CHARACTER) {
			//Are we obscuring a terminate?
			if(translations_[index]==AminoAcids.TERMINATE_STATE) {
				removeTerminalIndex(index);
			}
		} else {
			addTerminalIndex(index);
		}
		translations_[index] = aminoAcids_.getState(aminoAcid);
	}

	/** 
	 * Returns the char associated with AminoAcid represented by 'codon'.
	 * @note char is as defined by AminoAcids.java
	 * @see AminoAcids
	 * @return state for '?' if codon unknown or wrong length
	 */
	public char getAminoAcidChar(char[] codon) {
		return aminoAcids_.getChar(getAminoAcidState(codon));
	}


	/** 
	 * Returns the state associated with AminoAcid represented by 'codon'.
	 * @note state is as defined by AminoAcids.java
	 * @return '?' if codon unknown or wrong length
	 * @see AminoAcids
	 */
	public int getAminoAcidState(char[] codon) {
		int index = generalizedCodons_.getCodonIndexFromCodon(codon);
		if(index<0) {
			return aminoAcids_.getState(DataType.UNKNOWN_CHARACTER);
		}
		return translations_[index];
	}

	public char getAminoAcidCharFromCodonIndex(int codonIndex) {
		return aminoAcids_.getChar(getAminoAcidStateFromCodonIndex(codonIndex));
	}

	public int getAminoAcidStateFromCodonIndex(int codonIndex) {
		return translations_[codonIndex];
	}

	/** 
	 * Reset all values of the translation tables to -1 to 
	 * signify they have not be initialised.
	 */
	private void clearTranslationTables() {
		for(int i = 0 ; i < translations_.length ; i++) {
		 	translations_[i] = -1;
		}
		terminatorIndexes_ = new int[0];
	}

	public UniversalTranslator() {
		clearTranslationTables();
		//Phe
		add("UUU", 'F'); add("UUC", 'F');

		//Leu
		add("UUA", 'L'); add("UUG", 'L'); add("CUU", 'L'); add("CUC", 'L'); add("CUA", 'L'); add("CUG", 'L');

		//Ile
		add("AUU", 'I'); add("AUC", 'I'); add("AUA", 'I');

		//Met
		add("AUG", 'M');

		//Val
		add("GUU", 'V'); add("GUC", 'V'); add("GUA", 'V'); add("GUG", 'V');

		//Ser
		add("UCU", 'S'); add("UCC", 'S'); add("UCA", 'S'); add("UCG", 'S');

		//Pro
		add("CCU", 'P'); add("CCC", 'P'); add("CCA", 'P'); add("CCG", 'P');

		//Thr
		add("ACU", 'T'); add("ACC", 'T'); add("ACA", 'T'); add("ACG", 'T');

		//Ala
		add("GCU", 'A'); add("GCC", 'A'); add("GCA", 'A'); add("GCG", 'A');

		//Tyr
		add("UAU", 'Y'); add("UAC", 'Y');

		//Ter
		add("UAA", AminoAcids.TERMINATE_CHARACTER); add("UAG", AminoAcids.TERMINATE_CHARACTER);

		//His
		add("CAU", 'H'); add("CAC", 'H');

		//Gln
		add("CAA", 'Q'); add("CAG", 'Q');

		//Asn
		add("AAU", 'N'); add("AAC", 'N');

		//Lys
		add("AAA", 'K'); add("AAG", 'K');

		//Asp
		add("GAU", 'D'); add("GAC", 'D');

		//Glu
		add("GAA", 'E'); add("GAG", 'E');

		//Cys
		add("UGU", 'C'); add("UGC", 'C');

		//Ter
		add("UGA", AminoAcids.TERMINATE_CHARACTER);

		//Trp
		add("UGG", 'W');

		//Arg
		add("CGU", 'R'); add("CGC" , 'R'); add("CGA", 'R'); add("CGG", 'R');

		//Ser
		add("AGU", 'S'); add("AGC" , 'S');

		//Arg
		add("AGA", 'R'); add("AGG", 'R');

		//Gly
		add("GGU", 'G'); add("GGC" , 'G'); add("GGA", 'G'); add("GGG", 'G');

		//check();
	}

	public void check() {
		System.out.println("Checking");
		for(int i = 0 ; i < translations_.length ; i++) {
			if(translations_[i] == -1) {
				System.out.println("No Translation for codon:"+i+"("+(new String(generalizedCodons_.getCodonFromCodonIndex(i)))+")");
			}
		}
	}

	public int[] getTerminatorIndexes() {
		return terminatorIndexes_;
	}
	public int getNumberOfTerminatorIndexes() {
	 	return terminatorIndexes_.length;
	}
}

class MVertebrateTranslator extends UniversalTranslator {
	public MVertebrateTranslator() {
		super();
		//Tro
		add("UGA", 'W');

		//Ter
		add("AGA", AminoAcids.TERMINATE_CHARACTER); add("AGG", AminoAcids.TERMINATE_CHARACTER);

		//Met
		add("AUA", 'M');
	}
}




