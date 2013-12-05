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

/**
A class whose declaration is exactly the same as a CharMatrixSource, since both can return character matrices one at a time.
The only distinction is to know purpose hired, so the CharMatrixOneSource can respond with parameters informing
about current matrix, while CharMatrixSource can respond in general terms about the matrices it produces.*/

public abstract class CharMatrixOneSource extends CharMatrixObedSource  {

   	 public Class getDutyClass() {
   	 	return CharMatrixOneSource.class;
   	 }
   	 public String[] getDefaultModule() {
   	 	return new String[] {"#StoredMatrices", "#SimulatedMatrix"};
   	 }
}

