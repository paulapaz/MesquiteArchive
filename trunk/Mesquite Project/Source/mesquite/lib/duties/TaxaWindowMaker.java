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
package mesquite.lib.duties;

import java.awt.*;
import mesquite.lib.*;


/* ======================================================================== */
/**Presents a single taxa window.*/

public abstract class TaxaWindowMaker extends MesquiteModule   {

   	 public Class getDutyClass() {
   	 	return TaxaWindowMaker.class;
   	 }
  	public abstract void makeTaxaWindow(Taxa taxa);
  	 
  	public abstract TaxaDisplay getTaxaDisplay();
  	
 	public String getDutyName() {
 		return "Taxa Window Maker";
   	 }

   	public boolean isSubstantive(){
   		return false;  
   	}
}


