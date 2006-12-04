/* Mesquite source code.  Copyright 1997-2006 W. Maddison and D. Maddison.Version 1.12, September 2006.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)*/package mesquite.lib.duties;import java.awt.*;import mesquite.lib.*;/* ======================================================================== *//**Supplies a number for a taxon.*/public abstract class NumberForTaxon extends MesquiteModule implements NumberForItem  {   	 public Class getDutyClass() {   	 	return NumberForTaxon.class;   	 } 	public String getDutyName() { 		return "Number for Taxon";   	 }   	/** Called to provoke any necessary initialization.  This helps prevent the module's intialization queries to the user from   	happening at inopportune times (e.g., while a long chart calculation is in mid-progress)*/   	public abstract void initialize(Taxa taxa, CommandRecord commandRec);	public abstract void calculateNumber(Taxon taxon, MesquiteNumber result, MesquiteString resultString, CommandRecord commandRec); 		/*===== For NumberForItem interface ======*/   	public void initialize(Object object1, Object object2, CommandRecord commandRec){		if (object1 instanceof Taxa)    			initialize((Taxa)object1, commandRec);   	}	public  void calculateNumber(Object object1, Object object2, MesquiteNumber result, MesquiteString resultString, CommandRecord commandRec){		if (result==null)			return;		if (object1 instanceof Taxon) {			calculateNumber((Taxon)object1,result, resultString, commandRec);		}	}  	public  void calculateNumberInContext(Object object1, Object object2, ItemsSource source, int whichItem, MesquiteNumber result, MesquiteString resultString, CommandRecord commandRec){		calculateNumber(object1, object2, result, resultString, commandRec);	} 	public String getNameOfValueCalculated(){ 		return getNameAndParameters();   	}}