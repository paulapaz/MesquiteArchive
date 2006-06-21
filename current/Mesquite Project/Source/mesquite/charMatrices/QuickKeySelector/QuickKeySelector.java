/* Mesquite source code.  Copyright 1997-2006 W. Maddison and D. Maddison.Version 1.1, May 2006.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)*/package mesquite.charMatrices.QuickKeySelector; //DRM started April 02import java.util.*;import java.awt.*;import java.awt.event.*;import mesquite.lib.*;import mesquite.lib.characters.*;import mesquite.lib.duties.*;import mesquite.lib.table.*;import mesquite.categ.lib.*;/* ======================================================================== */public class QuickKeySelector extends DataWindowAssistantI implements ToolKeyListener {	TableTool quickKeySelectorTool;	CategoricalData data;	MesquiteTable table;	CommandRecord commandRec;	MesquiteBoolean autotabOff, autotabDown, autotabRight;	/*.................................................................................................................*/	public boolean startJob(String arguments, Object condition, CommandRecord commandRec, boolean hiredByName){		if (containerOfModule() instanceof MesquiteWindow) {			quickKeySelectorTool = new TableTool(this, "quickKeySelector", getPath(), "quickKeySelector.gif", 4,4,"Select and type", "This tool allows you to select cells; any keystrokes will then be entered into selected cells.", null, null, null);  //MesquiteModule.makeCommand("quickKeySelectorTouch",  this) 			quickKeySelectorTool.setToolKeyListener(this);			quickKeySelectorTool.setUseTableTouchRules(true);			//quickKeySelectorTool.setAllowAnnotate(true);			((MesquiteWindow)containerOfModule()).addTool(quickKeySelectorTool);			autotabOff = new MesquiteBoolean(true);			autotabDown = new MesquiteBoolean(false);			autotabRight = new MesquiteBoolean(false);			addCheckMenuItem(null, "Autotab Off", MesquiteModule.makeCommand("autotabOff", this), autotabOff);			addCheckMenuItem(null, "Autotab Down", MesquiteModule.makeCommand("autotabDown", this), autotabDown);			addCheckMenuItem(null, "Autotab Right", MesquiteModule.makeCommand("autotabRight", this), autotabRight);			quickKeySelectorTool.setPopUpOwner(this);			setUseMenubar(false); //menu available by touching oning button		}		else return false;		this.commandRec = commandRec;		return true;	}	  public Snapshot getSnapshot(MesquiteFile file) {   	 	Snapshot temp = new Snapshot();  	 	if (autotabDown.getValue())  	 		temp.addLine("autotabDown");  	 	else if (autotabRight.getValue())  	 		temp.addLine("autotabRight");  	 	else  	 		temp.addLine("autotabOff");  	 	return temp;  	 }    	 public Object doCommand(String commandName, String arguments, CommandRecord commandRec, CommandChecker checker) {    	 	if (checker.compare(this.getClass(), "turns off autotab", null, commandName, "autotabOff")) {   	 		autotabOff.setValue(true);   	 		autotabDown.setValue(false);   	 		autotabRight.setValue(false);   	 		quickKeySelectorTool.setImageFileName( "quickKeySelector.gif");			MesquiteWindow w = (MesquiteWindow)containerOfModule();			if (w != null && w.getCurrentTool() == quickKeySelectorTool)				w.resetCursor();    	 	}    	 	else if (checker.compare(this.getClass(), "turns on autotab down", null, commandName, "autotabDown")) {   	 		autotabOff.setValue(false);   	 		autotabDown.setValue(true);   	 		autotabRight.setValue(false);   	 		quickKeySelectorTool.setImageFileName( "quickKeyDown.gif");			MesquiteWindow w = (MesquiteWindow)containerOfModule();			if (w != null && w.getCurrentTool() == quickKeySelectorTool)				w.resetCursor();    	 	}    	 	else if (checker.compare(this.getClass(), "turns on autotab right", null, commandName, "autotabRight")) {   	 		autotabOff.setValue(false);   	 		autotabDown.setValue(false);   	 		autotabRight.setValue(true);   	 		quickKeySelectorTool.setImageFileName( "quickKeyRight.gif");			MesquiteWindow w = (MesquiteWindow)containerOfModule();			if (w != null && w.getCurrentTool() == quickKeySelectorTool)				w.resetCursor();    	 	}    	 	else    	 		return super.doCommand(commandName, arguments, commandRec, checker);	return null;   	 }	/*.................................................................................................................*/	/** Returns CompatibilityTest so other modules know if this is compatible with some object. */	public CompatibilityTest getCompatibilityTest(){		return new RequiresAnyCategoricalData();	}	/*.................................................................................................................*/   	 public boolean isSubstantive(){   	 	return true;   	 }	/*.................................................................................................................*/   	 public boolean isPrerelease(){   	 	return false;   	 }	/*.................................................................................................................*/	public void setTableAndData(MesquiteTable table, CharacterData data, CommandRecord commandRec){		this.table = table;		if (data instanceof CategoricalData)			this.data = (CategoricalData)data;		else			this.data = null;	}	/*.................................................................................................................*/    	 public String getName() {		return "Quick Key Entry";   	 }	/*.................................................................................................................*/  	 public String getExplanation() {		return "Provides a tool with which to quickly enter data.  If this tool is active, then typing a key will cause that value to be entered into all selected cells.";   	 }	/*.................................................................................................................*/	public void keyTyped(KeyEvent e, MesquiteTool tool){	}	/*.................................................................................................................*/	public void keyPressed(KeyEvent e, MesquiteTool tool){	}	/*.................................................................................................................*/	public void keyReleased(KeyEvent e, MesquiteTool tool){	   	if (data == null)	   		return;	 	if (data.getEditorInhibition()){	 		alert("This matrix is marked as locked against editing.");	 		return;	 	}		if (MesquiteEvent.commandOrControlKeyDown(MesquiteEvent.getModifiers(e)))			return;	   	if (!data.isAcceptableCharForState(e.getKeyChar()))	   		return;	   	boolean success = false;		if (table.anyCellSelected()) {			for (int i=0; i<table.getNumColumns(); i++)				for (int j=0; j<table.getNumRows(); j++)					if (table.isCellSelected(i,j)) {						data.setState(i,j, e.getKeyChar());					}			success = true;		}		if (table.anyRowSelected()) {			for (int j=0; j<table.getNumRows(); j++) {				if (table.isRowSelected(j))					for (int i=0; i<table.getNumColumns(); i++) 							data.setState(i,j, e.getKeyChar());			}			success = true;		}		if (table.anyColumnSelected()) {			for (int i=0; i<table.getNumColumns(); i++){				if (table.isColumnSelected(i))					for (int j=0; j<table.getNumRows(); j++) 						data.setState(i,j, e.getKeyChar());			}			success = true;		}		if (success){   			table.repaintAll();			data.notifyListeners(this, new Notification(MesquiteListener.DATA_CHANGED), commandRec);		}		if (autotabDown.getValue() || autotabRight.getValue()){			if (table.singleTableCellSelected()){				if (autotabDown.getValue())					table.downArrowPressed(null);				else if (autotabRight.getValue())					table.rightArrowPressed(null);			}		}	}}	