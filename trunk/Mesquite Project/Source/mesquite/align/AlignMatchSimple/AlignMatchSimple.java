/* Mesquite source code.  Copyright 1997-2005 W. Maddison and D. Maddison. Version 1.06, September 2005.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)*/package mesquite.align.AlignMatchSimple; import mesquite.align.lib.*;import mesquite.lib.*;import mesquite.lib.characters.*;import mesquite.lib.duties.*;import mesquite.categ.lib.*;/* ======================================================================== */public class AlignMatchSimple extends AlignMatch {   	/*.................................................................................................................*/	 public long[] 	getTransformedCandidateArray(long[] candidateArray) {		 return candidateArray;	 }   	/*.................................................................................................................*/    	 public String getName() {		return "Align Match";   	 }	/*.................................................................................................................*/  	 public String getExplanation() {		return "Returns the fraction by which sequences are exact matches.";   	 }		/*.................................................................................................................*/    	 public boolean isPrerelease() {		return true;   	 }}