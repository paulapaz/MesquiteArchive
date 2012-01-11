/* Mesquite source code.  Copyright 1997-2011 W. Maddison and D. Maddison. Version 2.75, September 2011.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html) */package mesquite.molec.TaxaListHasData;/*~~  */import mesquite.categ.lib.CategoricalState;import mesquite.categ.lib.MolecularData;import mesquite.lists.lib.*;import java.awt.Color;import java.util.*;import mesquite.lib.*;import mesquite.lib.characters.*;import mesquite.lib.duties.*;import mesquite.lib.table.*;/* ======================================================================== */public class TaxaListHasData extends TaxonListAssistant  {	/*.................................................................................................................*/	public String getName() {		return "Taxon Has Data";	}	public String getExplanation() {		return "Indicates whether taxon has non-missing non-gap data in a given matrix." ;	}	Taxa taxa=null;	MesquiteTable table = null;	public void getEmployeeNeeds(){  //This gets called on startup to harvest information; override this and inside, call registerEmployeeNeed		EmployeeNeed e = registerEmployeeNeed(MatrixSourceCoord.class, getName() + "  needs a source of characters.",				"The source of characters is arranged initially");	}	MatrixSourceCoord matrixSourceTask;	//Taxa currentTaxa = null;	MCharactersDistribution observedStates =null;	Associable tInfo = null;	/*.................................................................................................................*/	public boolean startJob(String arguments, Object condition, boolean hiredByName) {		matrixSourceTask = (MatrixSourceCoord)hireCompatibleEmployee(MatrixSourceCoord.class, CategoricalState.class, "Source of character matrix (for " + getName() + ")"); 		if (matrixSourceTask==null)			return sorry(getName() + " couldn't start because no source of character matrices was obtained.");		addMenuItem("Delete Data For Selected Taxa", makeCommand("deleteData", this));		return true;	}	/*.................................................................................................................*/	public int getVersionOfFirstRelease(){		return 250;  	}	/** Returns whether or not it's appropriate for an employer to hire more than one instance of this module.   	If false then is hired only once; second attempt fails.*/	public boolean canHireMoreThanOnce(){		return true;	}	/*.................................................................................................................*/	/** Generated by an employee who quit.  The MesquiteModule should act accordingly. */	public void employeeQuit(MesquiteModule employee) {		if (employee == matrixSourceTask)  // character source quit and none rehired automatically			iQuit();	}	/*.................................................................................................................*/	public Snapshot getSnapshot(MesquiteFile file) { 		Snapshot temp = new Snapshot();		temp.addLine("getMatrixSource", matrixSourceTask);		return temp;	}	MesquiteInteger pos = new MesquiteInteger();	/*.................................................................................................................*/	public Object doCommand(String commandName, String arguments, CommandChecker checker) {		if (checker.compare(this.getClass(), "Returns the matrix source", null, commandName, "getMatrixSource")) {			return matrixSourceTask;		}		else if (checker.compare(this.getClass(), "Deletes the data for selected taxa", null, commandName, "deleteData")) {			if (observedStates == null)				return null;			CharacterData data = observedStates.getParentData();			if (data == null)				return null;			if (!AlertDialog.query(containerOfModule(), "Delete Data?", "Are you sure you want to delete the data for these taxa in the matrix \"" + data.getName() + "\"", "No", "Yes"))				zapData(data);			return null;		}		else return  super.doCommand(commandName, arguments, checker);	}	/*...............................................................................................................*/	/** returns whether or not a cell of table is editable.*/	public boolean isCellEditable(int row){		return true;	}	/*...............................................................................................................*/	/** for those permitting editing, indicates user has edited to incoming string.*/	public void setString(int row, String s){			if (StringUtil.blank(s))			setNote(row, null);		else if (s.equalsIgnoreCase("Yes") || s.equalsIgnoreCase("No Data"))			return;		else 			setNote(row, s);	}	void setNote(int row, String s){		if (tInfo == null)			return;		tInfo.setAssociatedObject(MolecularData.genBankNumberRef, row, s);	}	String getNote(int row){		if (tInfo == null)			return null;		Object obj = tInfo.getAssociatedObject(MolecularData.genBankNumberRef, row);		if (obj == null || !(obj instanceof String))			return null;		return (String)obj;	}	/*...............................................................................................................*/	public void setTableAndTaxa(MesquiteTable table, Taxa taxa){		//if (this.data !=null)		//	this.data.removeListener(this);		if (taxa != this.taxa)			observedStates =  null;		this.taxa = taxa;		matrixSourceTask.initialize(taxa);		this.table = table;		doCalcs();	}	/*...............................................................................................................*/	Bits bits;	public void doCalcs(){		if (bits != null)			bits.clearAllBits();		if (taxa == null)			return;		if (bits == null)			bits = new Bits(taxa.getNumTaxa());		else			bits.resetSize(taxa.getNumTaxa());		if (observedStates == null ) {			tInfo = null;			observedStates = matrixSourceTask.getCurrentMatrix(taxa);			if (observedStates != null) {				CharacterData data = observedStates.getParentData();				if (data != null)					tInfo = data.getTaxaInfo(true);			}		}		if (observedStates==null)			return;		for (int it = 0; it<taxa.getNumTaxa(); it++){			if (hasData(it))				bits.setBit(it);		}	}	/*...............................................................................................................*/	boolean hasData(int it){		CharacterState cs = null;		try {			for (int ic=0; ic<observedStates.getNumChars(); ic++) {				cs = observedStates.getCharacterState(cs, ic, it);				if (cs == null)					return false;				long s = ((CategoricalState)cs).getValue();				if (!CategoricalState.isInapplicable(s) && !CategoricalState.isUnassigned(s)) 					return true;			}		}		catch (NullPointerException e){		}		return false;	}	/*...............................................................................................................*/	public String getExplanationForRow(int ic){		if (observedStates != null && observedStates.getParentData() != null){			CharacterData data = observedStates.getParentData();			Associable tInfo = data.getTaxaInfo(false);			if (tInfo == null)				return null;			return "Notes: " + tInfo.toString(ic);		}		return null;	}	/*...............................................................................................................*/	void zapData(CharacterData data){		Taxa taxa = data.getTaxa();		Associable tInfo = data.getTaxaInfo(false);		for (int it = 0; it<taxa.getNumTaxa(); it++){			if (taxa.getSelected(it)){				if (tInfo != null)					tInfo.deassignAssociated(it);				for (int ic=0; ic<data.getNumChars(); ic++)					data.deassign(ic, it);			}		}		data.notifyListeners(this, new Notification(MesquiteListener.DATA_CHANGED));		outputInvalid();	}	/*.................................................................................................................*/	public void employeeParametersChanged(MesquiteModule employee, MesquiteModule source, Notification notification) {		observedStates = null;		super.employeeParametersChanged(employee, source, notification);	}	/** Gets background color for cell for row ic.  Override it if you want to change the color from the default. */	public Color getBackgroundColorOfCell(int it, boolean selected){		if (observedStates == null)			doCalcs();		if (bits ==null || it <0 || it > bits.getSize())			return null;		String note = getNote(it);		if (selected){			if (bits.isBitOn(it))				return ColorDistribution.darkGreen;			else				return ColorDistribution.darkRed;		}		else if (bits.isBitOn(it)){			if (StringUtil.blank(note))				return ColorDistribution.veryLightGreen;			if ( !(note.equalsIgnoreCase("x")))				return ColorDistribution.lightGreenYellowish;			return ColorDistribution.lightGreenYellow;		}		else {			if (StringUtil.blank(note))				return ColorDistribution.brown;			if ( !(note.equalsIgnoreCase("x"))) {				return Color.red;			}			return ColorDistribution.lightRed;		}	}	public String getStringForTaxon(int it){		String note = getNote(it);		if (note != null)			return note;		if (observedStates == null)			doCalcs();		if (bits ==null || it <0 || it > bits.getSize())			return "?";		if (bits.isBitOn(it))			return "Yes";		else			return "No Data";	}	public String getWidestString(){		return "88888888888";	}	/*.................................................................................................................*/	public String getTitle() {		if (observedStates == null)			doCalcs();		if (observedStates != null && getProject().getNumberCharMatricesVisible()>1){			CharacterData d = observedStates.getParentData();			if (d != null && d.getName()!= null) {				String n =  d.getName();				if (n.length()>12)					n = n.substring(0, 12); 				return "Has Data (" + n + ")";			}		}		return "Has Data in Matrix";	}	/*.................................................................................................................*/	/** returns whether this module is requesting to appear as a primary choice */	public boolean requestPrimaryChoice(){		return true;  	}	/*.................................................................................................................*/	public boolean isPrerelease(){		return false;  	}}