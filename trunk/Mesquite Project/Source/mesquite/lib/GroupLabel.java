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
package mesquite.lib;

import java.awt.*;
import mesquite.lib.duties.*;

	
/* ======================================================================== */
/** DOCUMENT.*/
public class GroupLabel extends FileElement {
	Color color = null;
	MesquiteSymbol symbol = null;
	int id;
	boolean colorWasSet = false;
	boolean symbolWasSet = false;
	static int numLabels;
	static {
		numLabels = 0;
	}
	public GroupLabel() {
		//this.taxa = taxa; //TODO: should listen to changes in the taxa!!!
		id = numLabels++;
		/*int tS;
		if (id /2 * 2 == id)
			tS = 25 - id;
		else
			tS = id;
		if (tS<0)
			tS = -tS;
		tS = tS % 25;
		color = new Color(Color.HSBtoRGB((float)((25-tS) * 0.8 /25),(float)1.0,(float)1.0));
		*/
		color = null;
	}
	public static boolean supportsSymbols() {
		return false;
	}
	public String getTypeName(){
		return "Group Label";
	}
	/*.................................................................................................................*/
	public void setColor(Color color){
		this.color = color;
		colorWasSet = true;
	}
	/*.................................................................................................................*/
	public Color getColor(){
		return color;
	}
	/*.................................................................................................................*/
	public boolean colorSet(){
		return colorWasSet;
	}
	/*.................................................................................................................*/
	public void setSymbol(MesquiteSymbol symbol){
		this.symbol = symbol;
		symbolWasSet = true;
	}
	/*.................................................................................................................*/
	public MesquiteSymbol getSymbol(){
		return symbol;
	}
	/*.................................................................................................................*/
	public boolean symbolSet(){
		return symbolWasSet;
	}
	
	/*.................................................................................................................*/
}


