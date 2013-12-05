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
import java.util.*;
import java.awt.event.*;
import mesquite.lib.duties.*;

/*====================================================*/
/**The vector of menuItems used by a mesquite module.*/
public class MenuItemsSpecsVector extends ListableVector {
	public MenuItemsSpecsVector  () {
		super();
		notifyOfChanges = false;
	}
	public void dispose(){
		removeAllElements(false);
		if (listeners!=null) {
			Enumeration e = listeners.elements();
			while (e.hasMoreElements()) {
				Object obj = e.nextElement();
				MesquiteListener listener = (MesquiteListener)obj;
	 				listener.disposing(this);
				}
		}
		disposed = true;
		super.dispose();
	}
}


