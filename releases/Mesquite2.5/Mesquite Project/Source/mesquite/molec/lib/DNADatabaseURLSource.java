package mesquite.molec.lib;

import java.util.Hashtable;

import mesquite.lib.*;

public abstract class DNADatabaseURLSource extends DatabaseURLSource {
	public static final int PRIMER_NAME = 1;
	public static final int SAMPLE_CODE = 2;
	public static final int EXTRACTION_CODE = 2;
	public static final int PCR_REACTION_CODE = 3;
	public static final int AUTHORIZATION_KEY = 4;
	public static final int GENE = 5;
	public static final int TAXON = 6;
	public static final int NAME = 7;
	public static final int EXTRACTION = 8;
	public static final int FASTA=9;
	public static final int COUNT=10;
	
	

	
	public static final int PRIMER_SERVICE = 1;
	public static final int SEQUENCE_NAME_SERVICE = 2;
	public static final int CONTRIBUTOR_SERVICE = 3;
	public static final int CHROMATOGRAM_SEARCH_SERVICE = 4;
	public static final int SEQUENCE_UPLOAD_SERVICE = 5;
	public static final int FASTA_UPLOAD_SERVICE = 6;
	public static final int CHROMATOGRAM_BATCH_CREATION_SERVICE = 7;
	public static final int CHROMATOGRAM_DOWNLOAD_SERVICE = 8;

	public static final int SEQUENCE_ELEMENT=1;
	
	public boolean includeSampleCodePrefixInSampleCode() {
		return false;
	}
	
	public String getChromatogramDownloadURL(Hashtable args) {
		return "";
	}

}
