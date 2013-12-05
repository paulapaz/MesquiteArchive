// AminoAcidAlignment.java
//
// (c) 1999-2001 PAL Development Core Team
//
// This package may be distributed under the
// terms of the Lesser GNU General Public License (LGPL)


package pal.alignment;

import pal.datatype.*;


/**
 * generates an amino acid alignment from a nucleotide alignment. Is designed to
 * translate from nucleotide alignments that do not have gaps. Codons that include
 * one or more gaps will be translated as a '?'.
 *
 * @version $Id: AminoAcidAlignment.java,v 1.5 2001/07/13 14:39:12 korbinian Exp $
 *
 * @author Matthew Goode
 */
public class AminoAcidAlignment extends AbstractAlignment {



	//
	// Public stuff
	//

	/**
	 * Constructs an AminoAcidAlignment from a given alignment 
	 * (which should be a nucleotide alignment), from codon position 0.
	 *
	 * @param raw original alignment
	 * @param if a codon contains a gap or uses non nucleotide 
	 * characters resulting amino acid will be marked as a '?'
	 */
	public AminoAcidAlignment(Alignment raw) {
		this(raw,0);
	}

	/**
	 * Constructor
	 *
	 * @param raw original alignment
	 * @param startingCodonPosition - the starting codon position (0,1,2)
	 * @param if a codon contains a gap or uses non nucleotide characters 
	 * resulting amino acid will be marked as a '?'
	 */
	public AminoAcidAlignment(Alignment raw, int startingCodonPosition) {
		this(raw,startingCodonPosition,CodonTableFactory.createUniversalTranslator());
	}

	/**
	 * Constructor
	 *
	 * @param raw original alignment
	 * @param startingCodonPosition - the starting codon position (0,1,2)
	 * @param trans - the CodonTable to use to convert nucleotides to amino acids
	 * @param if a codon contains a gap or uses non nucleotide 
	 * characters resulting amino acid will be marked as a '?'
	 */
	public AminoAcidAlignment(Alignment raw, int startingCodonPosition,
		CodonTable trans) {
		numSeqs = raw.getSequenceCount();
		idGroup = raw;
		numSites = (raw.getSiteCount()-startingCodonPosition)/3;
		dataType = new AminoAcids();
		this.data_ = new char[numSeqs][numSites];
		constructData(raw,startingCodonPosition, trans);
	}

	private void constructData(Alignment raw, int startingCodonPosition,
		CodonTable trans) {
		for(int i = 0 ; i < numSeqs ; i++) {
			for(int j = 0 ; j < numSites ; j++) {
				char[] cs = new char[3];
				for(int k = 0 ; k < 3 ; k++) {
					cs[k]=raw.getData(i,j*3+startingCodonPosition+k);
				}
				this.data_[i][j] = trans.getAminoAcidChar(cs);
			}
		}
	}

	// Implementation of abstract Alignment method

	/** sequence alignment at (sequence, site) */
	public char getData(int seq, int site)
	{
		return data_[seq][site];
	}

	
	//
	// Private stuff
	//
	
	private char[][] data_;
}

