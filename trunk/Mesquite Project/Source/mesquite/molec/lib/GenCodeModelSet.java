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
package mesquite.molec.lib;


import mesquite.lib.*;
import mesquite.lib.characters.*;

/*======================================================================== */
/** a ModelSet for genetic codes. */
public class GenCodeModelSet  extends ModelSet {
	
	public GenCodeModelSet (String name, int numChars, CharacterModel defaultModel, CharacterData data) {
		super(name, numChars, defaultModel, data);
	}
	public String getTypeName(){
		return "Genetic Code Model set";
	}
	public SpecsSet cloneSpecsSet(){
		GenCodeModelSet ms = new GenCodeModelSet(new String(name), getNumberOfParts(), (CharacterModel)getDefaultProperty(), data);
		for (int i=0; i<getNumberOfParts(); i++) {
			ms.setModel(getModel(i), i);
		}
		return ms;
	}
	public SpecsSet makeSpecsSet(AssociableWithSpecs parent, int numParts){
		if (!(parent instanceof CharacterData))
			return null;
		return new GenCodeModelSet("Genetic Code Model Set", numParts, (CharacterModel)getDefaultProperty(), (CharacterData)parent);
	}
	/*.................................................................................................................*/
 	/** Gets default model specified for ModelSet for character ic or new character added just after ic.  May take from current assignment*/
	public CharacterModel getDefaultModel(int ic) {
		return (CharacterModel)getDefaultProperty(ic);
	}
	 
	
	/*.................................................................................................................*/
 	/** Gets default model specified for ModelSet*/
	public Object getDefaultProperty(int ic) {
		if (getModel(ic) != null)
			return getModel(ic);
		int icLeft = ic;
		while (--icLeft>=0 && getModel(icLeft) != null)  //looking left
			return getModel(icLeft);
		int icRight = ic;
		while (++icRight< getNumChars() && getModel(icRight) != null)
			return getModel(icRight);
		return super.getDefaultModel(ic);
	}

}


