/* Mesquite source code.  Copyright 1997-2010 W. Maddison and D. Maddison.Version 2.73, July 2010.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html) */package mesquite.trees.NumsForNodesBrLengths;import java.util.*;import java.awt.*;import mesquite.lib.*;import mesquite.lib.duties.*;/* ======================================================================== */public class NumsForNodesBrLengths extends BranchLengthsAltererMult {	public String getName() {		return "Set Branch Lengths to Numbers For Nodes";	}	public String getExplanation() {		return "Assigns a value of branch length for all of a tree's branches based upon the numbers calculated by a Numbers for Nodes." ;	}	public void getEmployeeNeeds(){  //This gets called on startup to harvest information; override this and inside, call registerEmployeeNeed		EmployeeNeed e = registerEmployeeNeed(NumbersForNodes.class, getName() + "  needs a method to calculate values for nodes by which to adjust branch lengths.",		"The method to calculate values can be chosen initially");	}	/*.................................................................................................................*/	NumbersForNodes numbersTask;	/*.................................................................................................................*/	public boolean startJob(String arguments, Object condition, boolean hiredByName) {		numbersTask = (NumbersForNodes)hireEmployee(NumbersForNodes.class,  "Source of matrix (for " + getName() + ")");		if (numbersTask == null)			return sorry(getName() + " couldn't start because no NumbersForNodes (for " + getName() + ") was obtained");		return true;	}	/*.................................................................................................................*/	public Snapshot getSnapshot(MesquiteFile file) { 		Snapshot temp = new Snapshot();		temp.addLine("getNumbersForNodes ", numbersTask); 		return temp;	}	MesquiteInteger pos = new MesquiteInteger();	/*.................................................................................................................*/	public Object doCommand(String commandName, String arguments, CommandChecker checker) {		if (checker.compare(this.getClass(), "Returns module supplying numbers for nodes", null, commandName, "getNumbersForNodes")) {			return numbersTask;		}		else			return  super.doCommand(commandName, arguments, checker);	}	/*.................................................................................................................*/	public boolean isSubstantive(){		return true;	}	/*.................................................................................................................*/	/** returns the version number at which this module was first released.  If 0, then no version number is claimed.  If a POSITIVE integer	 * then the number refers to the Mesquite version.  This should be used only by modules part of the core release of Mesquite.	 * If a NEGATIVE integer, then the number refers to the local version of the package, e.g. a third party package*/	public int getVersionOfFirstRelease(){		return 110;  	}	/*.................................................................................................................*/	public boolean isPrerelease(){		return false;	}	/*.................................................................................................................*/	/** returns whether this module is requesting to appear as a primary choice */	public boolean requestPrimaryChoice(){		return true;  	}	/*.................................................................................................................*/	/** Generated by an employee who quit.  The MesquiteModule should act accordingly. */	public void employeeQuit(MesquiteModule employee) {		if (employee == numbersTask)  			iQuit();		super.employeeQuit(employee);	}	/*.................................................................................................................*/	public  boolean transformTree(AdjustableTree tree, MesquiteString resultString, boolean notify){		if (tree instanceof MesquiteTree) {			int numNodes = tree.getNumNodeSpaces();			NumberArray result = new NumberArray(numNodes);			numbersTask.calculateNumbers(tree, result, null);			for (int i=0; i<numNodes; i++) 				((MesquiteTree)tree).setBranchLength(i,result.getDouble(i),false);			if (notify && tree instanceof Listened) ((Listened)tree).notifyListeners(this, new Notification(MesquiteListener.BRANCHLENGTHS_CHANGED));			return true;		}		return false;	}}