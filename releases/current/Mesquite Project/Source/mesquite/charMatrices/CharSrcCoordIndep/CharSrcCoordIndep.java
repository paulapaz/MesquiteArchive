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
package mesquite.charMatrices.CharSrcCoordIndep;
/*~~  */

import java.util.*;
import java.awt.*;
import mesquite.lib.*;
import mesquite.lib.characters.*;
import mesquite.lib.duties.*;
import mesquite.parsimony.lib.CharacterSteps;

public class CharSrcCoordIndep extends CharSourceCoord {

	public String getName() {
		return "Character Source ";  
	}
	public String getExplanation() {
		return "Coordinates the supply of characters from various sources of characters." ;
	}
	public void getEmployeeNeeds(){  //This gets called on startup to harvest information; override this and inside, call registerEmployeeNeed
		EmployeeNeed e = registerEmployeeNeed(CharSourceCoordObed.class, getName() + " needs a source of characters.",
				"You can request a source of characters when " + getName() + " starts, or later under the Source of Characters submenu.");
		e.setSuppressListing(true);
		//e.setAsEntryPoint(true);
	}
	public EmployeeNeed findEmployeeNeed(Class dutyClass) {
		return getEmployer().findEmployeeNeed(CharSourceCoord.class);
	}
	/*.................................................................................................................*/
	CharSourceCoordObed characterSourceTask;
	int currentChar = 0;
	Taxa taxa;
	/*.................................................................................................................*/
	/** condition passed to this module must be subclass of CharacterState */
	public boolean startJob(String arguments, Object condition, boolean hiredByName) { 
		String exp = "Source of Characters (for " + getEmployer().getName() + ")";
		if (!MesquiteDialog.useWizards){
			MesquiteModuleInfo mmi = MesquiteTrunk.mesquiteModulesInfoVector.findModule(mesquite.charMatrices.StoredCharacters.StoredCharacters.class);
			if (mmi != null && !mmi.isCompatible(condition, getProject(), this))
				exp += "\n\nNOTE: The choice Stored Characters does not appear because there are no appropriate matrices currently defined and stored in the data file or project.  ";
		}
		characterSourceTask = (CharSourceCoordObed)hireCompatibleEmployee(CharSourceCoordObed.class, condition, exp);
		if (characterSourceTask == null)
			return sorry(getName() + " couldn't start because no source of characters was obtained.");
		addMenuItem( "Choose Character", makeCommand("chooseCharacter",  this));
		return true;
	}
	/** Returns the purpose for which the employee was hired (e.g., "to reconstruct ancestral states" or "for X axis").*/
	public String purposeOfEmployee(MesquiteModule employee){
		return whatIsMyPurpose(); //transfers info to employer, as ithis is coordinator
	}
	/*.................................................................................................................*/
	/** Generated by an employee who quit.  The MesquiteModule should act accordingly. */
	public void employeeQuit(MesquiteModule employee) {
		if (employee == characterSourceTask)  // character source quit and none rehired automatically
			iQuit();
	}
	/** Called to provoke any necessary initialization.  This helps prevent the module's intialization queries to the user from
   	happening at inopportune times (e.g., while a long chart calculation is in mid-progress)*/
	public void initialize(Taxa taxa){
		this.taxa = taxa;
		characterSourceTask.initialize(taxa);
	}
	public Selectionable getSelectionable() {
		if (characterSourceTask!=null)
			return characterSourceTask.getSelectionable();
		else
			return null;
	}
	public void setEnableWeights(boolean enable){
		if (characterSourceTask!=null)
			characterSourceTask.setEnableWeights(enable);
	}
	public boolean itemsHaveWeights(Taxa taxa){
		if (characterSourceTask!=null)
			return characterSourceTask.itemsHaveWeights(taxa);
		return false;
	}
	public double getItemWeight(Taxa taxa, int ic){
		if (characterSourceTask!=null)
			return characterSourceTask.getItemWeight(taxa, ic);
		return MesquiteDouble.unassigned;
	}
	public void prepareItemColors(Taxa taxa){
		if (characterSourceTask!=null)
			characterSourceTask.prepareItemColors(taxa);
	}
	public Color getItemColor(Taxa taxa, int ic){
		if (characterSourceTask!=null)
			return characterSourceTask.getItemColor(taxa, ic);
		return null;
	}
	/*.................................................................................................................*/
	public Snapshot getSnapshot(MesquiteFile file) {
		Snapshot temp = new Snapshot();
		temp.addLine("getCharacterSource", characterSourceTask);
		temp.addLine("setCharacter " + CharacterStates.toExternal(currentChar));

		return temp;
	}
	/*.................................................................................................................*/
	public Object doCommand(String commandName, String arguments, CommandChecker checker) {
		if (checker.compare(this.getClass(), "returns module supplying characters", null, commandName, "getCharacterSource")) {
			return characterSourceTask;
		}
		else if (checker.compare(this.getClass(), "Queries the user about what character to use", null, commandName, "chooseCharacter")) {
			int ic=characterSourceTask.queryUserChoose(taxa, " ");
			if (MesquiteInteger.isCombinable(ic)) {
				currentChar = ic;
				//charStates = null;
				parametersChanged(); //?
			}
		}
		else if (checker.compare(this.getClass(), "Sets the character to use", "[character number]", commandName, "setCharacter")) {
			int icNum = MesquiteInteger.fromFirstToken(arguments, stringPos);
			if (!MesquiteInteger.isCombinable(icNum))
				return null;
			int ic = CharacterStates.toInternal(icNum);
			if ((ic>=0) && characterSourceTask.getNumberOfCharacters(taxa)==0) {
				currentChar = ic;
				//charStates = null;
			}
			else if ((ic>=0) && (ic<=characterSourceTask.getNumberOfCharacters(taxa)-1)) {
				currentChar = ic;
				//charStates = null;
				parametersChanged(); //?
			}
		}
		else
			return  super.doCommand(commandName, arguments, checker);
		return null;
	}
	/*.................................................................................................................*/
	public  int getNumberOfCharacters(Taxa taxa){
		this.taxa = taxa;
		if (null == characterSourceTask)
			return 0;
		return characterSourceTask.getNumberOfCharacters(taxa); 
	}
	/*.................................................................................................................*/
	public String getCurrentCharacterName(Taxa taxa) {
		this.taxa = taxa;
		if (null == characterSourceTask)
			return "";
		return characterSourceTask.getCharacterName(taxa, currentChar); 
	}
	/*.................................................................................................................*/
	/** gets the current matrix.*/
	public CharacterDistribution getCurrentCharacter(Taxa taxa){
		this.taxa = taxa;
		if (null == characterSourceTask)
			return null;
		return characterSourceTask.getCharacter(taxa, currentChar); 
	}
	/*.................................................................................................................*/
	/** returns whether this module is requesting to appear as a primary choice */
	public boolean requestPrimaryChoice(){
		return true;  
	}
	/*.................................................................................................................*/
	public boolean isPrerelease(){
		return false;
	}
	/*.................................................................................................................*/

	public String getParameters() {
		if (characterSourceTask==null)
			return null;
		return "Char. " + (currentChar+1) + " of " + characterSourceTask.getName() + " (" + characterSourceTask.getParameters() + ")";
	}
	public String getNameAndParameters() {
		return characterSourceTask.getNameAndParameters();
	}
}


