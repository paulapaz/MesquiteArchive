/* Mesquite source code.  Copyright 1997-2011 W. Maddison and D. Maddison.
Version 2.75, September 2011.
Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. 
The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.
Perhaps with your help we can be more than a few, and make Mesquite better.

Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.
Mesquite's web site is http://mesquiteproject.org

This source code and its compiled class files are free and modifiable under the terms of 
GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)
*/
package mesquite.align.aAlignIntro;

import mesquite.lib.*;
import mesquite.lib.duties.*;

/* ======================================================================== */
public class aAlignIntro extends PackageIntro {
	/*.................................................................................................................*/
	public boolean startJob(String arguments, Object condition, boolean hiredByName) {
		return true;
	}
	public Class getDutyClass(){
		return aAlignIntro.class;
	}
	/*.................................................................................................................*/
	public String getExplanation() {
		return "Align is a Mesquite package providing tools for the alignment of sequence data.";
	}

	/*.................................................................................................................*/
	/** Returns whether package is built-in (comes with default install of Mesquite)*/
	public boolean isBuiltInPackage(){
		return true;
	}
	/*.................................................................................................................*/
	public String getName() {
		return "Align Package";
	}
	/*.................................................................................................................*/
	/** Returns the name of the package of modules (e.g., "Basic Mesquite Package", "Rhetenor")*/
	public String getPackageName(){
		return "Align Package";
	}
	/*.................................................................................................................*/
	/** Returns citation for a package of modules*/
	public String getPackageCitation(){
		return "Maddison, D.R., T.J. Wheeler, and W.P. Maddison. 2011.  Align: A Mesquite package for aligning sequence data. Version 1.53";
	}
	/*.................................................................................................................*/
	/** Returns whether there is a splash banner*/
	public boolean hasSplash(){
		return true; 
	}
	/*.................................................................................................................*/
	 public String getPackageVersion() {
		return "1.51";
 	 }
		/*.................................................................................................................*/
 	 public int getPackageVersionInt() {
		return 151;
  	 }

	/*.................................................................................................................*/
	public int getVersionOfFirstRelease(){
		return 110;  
	}
}

