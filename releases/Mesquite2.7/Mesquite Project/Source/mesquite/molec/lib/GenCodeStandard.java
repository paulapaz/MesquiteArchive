/* Mesquite source code.  Copyright 1997-2009 W. Maddison and D. Maddison. 
Version 2.7, August 2009.
Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. 
The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.
Perhaps with your help we can be more than a few, and make Mesquite better.

Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.
Mesquite's web site is http://mesquiteproject.org

This source code and its compiled class files are free and modifiable under the terms of 
GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)
 */
package mesquite.molec.lib;


import mesquite.molec.lib.*;

public class GenCodeStandard extends GeneticCode {

	public void setCode() {
		setStandardCode();
	}

	public String getName() {
		return "The standard (\"universal\") genetic code";
	}

	public static String getShortName (){
		return "Standard";
	}

	public String getNEXUSName(){
		return "universal";
	}

	public int getNCBITranslationTableNumber(){
		return 1;
	}

}