/* Mesquite source code.  Copyright 1997-2006 W. Maddison and D. Maddison. (for this source, not for PAL!). Version 1.1, May 2006.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)*/package mesquite.pal.aPALIntro;/*~~  */import java.util.*;import java.awt.*;import mesquite.lib.*;import mesquite.lib.duties.*;/* ======================================================================== */public class aPALIntro extends PackageIntro {	/*.................................................................................................................*/	public boolean startJob(String arguments, Object condition, CommandRecord commandRec, boolean hiredByName) { 		return true;  	 }  	 public Class getDutyClass(){  	 	return aPALIntro.class;  	 }	/*.................................................................................................................*/    	 public String getExplanation() {		return "Serves as an introduction to the use of the PAL library in Mesquite.";   	 }   	/*.................................................................................................................*/    	 public String getName() {		return "PAL Introduction";   	 }	/*.................................................................................................................*/	/** Returns the name of the package of modules (e.g., "Basic Mesquite Package", "Rhetenor")*/ 	public String getPackageName(){ 		return "PAL (Phylogenetic Analysis Library)"; 	}	/*.................................................................................................................*/	/** Returns information about a package of modules*/ 	public String getPackageCitation(){ 		return "Drummond, A. & K. Strimmer, 2001.  PAL: Phylogenetic Analysis Library, version 1.3.  http://www.cebl.auckland.ac.nz/pal-project/"; 	}	/** Returns whether there is a splash banner*/	public boolean hasSplash(){ 		return true; 	}}