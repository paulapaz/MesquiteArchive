/* Mesquite source code.  Copyright 1997-2006 W. Maddison and D. Maddison.Version 1.12, September 2006.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html) */package mesquite.trees.BooleanTreeValue;import java.util.*;import java.awt.*;import mesquite.lib.*;import mesquite.lib.duties.*;/* ======================================================================== */public class BooleanTreeValue extends NumberForTree {	BooleanForTree booleanTask;	/*.................................................................................................................*/	public boolean startJob(String arguments, Object condition, CommandRecord commandRec, boolean hiredByName) {		if (arguments !=null) {			booleanTask = (BooleanForTree)hireNamedEmployee(commandRec, BooleanForTree.class, arguments);			if (booleanTask==null) {				return sorry(commandRec, "Boolean for tree (for list) can't start because the requested calculator module wasn't successfully hired");			}		}		else {			booleanTask = (BooleanForTree)hireEmployee(commandRec, BooleanForTree.class, "Value to calculate for trees (for tree list)");			if (booleanTask==null) {				return sorry(commandRec, "Boolean for tree (for list) can't start because no calculator module was successfully hired");			}		}		return true;	}	/*.................................................................................................................*/	public boolean isSubstantive(){		return true;	}	/*.................................................................................................................*/	/** returns the version number at which this module was first released.  If 0, then no version number is claimed.  If a POSITIVE integer	 * then the number refers to the Mesquite version.  This should be used only by modules part of the core release of Mesquite.	 * If a NEGATIVE integer, then the number refers to the local version of the package, e.g. a third party package*/	public int getVersionOfFirstRelease(){		return 110;  	}	/*.................................................................................................................*/	public boolean isPrerelease(){		return false;	}	/*.................................................................................................................*/	public Snapshot getSnapshot(MesquiteFile file) { 		Snapshot temp = new Snapshot();		temp.addLine("setValueTask ", booleanTask); 		return temp;	}	/*.................................................................................................................*/	public void employeeParametersChanged(MesquiteModule employee, MesquiteModule source, Notification notification, CommandRecord commandRec) {		outputInvalid(commandRec);		parametersChanged(null, commandRec);	}	/*.................................................................................................................*/	/** Generated by an employee who quit.  The MesquiteModule should act accordingly. */	public void employeeQuit(MesquiteModule employee) {		if (employee == booleanTask)  			iQuit();		super.employeeQuit(employee);	}	/*.................................................................................................................*/	public Object doCommand(String commandName, String arguments, CommandRecord commandRec, CommandChecker checker) {		if (checker.compare(this.getClass(), "Sets module that calculates a boolean for a tree", "[name of module]", commandName, "setValueTask")) {			BooleanForTree temp= (BooleanForTree)replaceEmployee(commandRec, BooleanForTree.class, arguments, "Boolean for a tree", booleanTask);			if (temp!=null) {				booleanTask = temp;				parametersChanged(null, commandRec);				return temp;			}		}		else			return super.doCommand(commandName, arguments, commandRec, checker);		return null;	}	/*.................................................................................................................*/	public String getParameters() {		if (booleanTask==null)			return null;		return "1 if " + booleanTask.getName();	}	/*.................................................................................................................*/	public String getName() {		return "Boolean Tree Value";	}	/*.................................................................................................................*/	public String getVeryShortName() {		if (booleanTask==null)			return getName();		return "1 if " + booleanTask.getName();	}	/*.................................................................................................................*/	/** returns an explanation of what the module does.*/	public String getExplanation() {		return "Gives a tree a value of 1 if it satisfies the criterion, 0 otherwise." ;	}	public void calculateNumber(Tree tree, MesquiteNumber result, MesquiteString resultString, CommandRecord commandRec) {		if (tree==null || booleanTask==null || result==null)			return;		MesquiteBoolean mb = new MesquiteBoolean();		booleanTask.calculateBoolean(tree,mb, resultString, commandRec);		if (mb.getValue()) {			result.setValue(1);		}		else if (!mb.getValue()) {			result.setValue(0);		}	}}