/* Mesquite source code.  Copyright 1997-2006 W. Maddison and D. Maddison. Version 1.12, September 2006.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)*/package mesquite.lists.CharacterList;/*~~  */import mesquite.lists.lib.*;import java.util.*;import java.awt.*;import mesquite.lib.*;import mesquite.lib.characters.*;import mesquite.lib.duties.*;import mesquite.lib.table.*;import mesquite.categ.lib.*;/* ======================================================================== */public class CharacterList extends ListModule {	public int currentDataSet = 0;	public CharacterData data = null;	CharacterListWindow window;	/*.................................................................................................................*/	public boolean startJob(String arguments, Object condition, CommandRecord commandRec, boolean hiredByName) {		addMenuItem("Rename Matrix...", MesquiteModule.makeCommand("renameMatrix",  this));		addMenuItem("Delete Matrix...", MesquiteModule.makeCommand("deleteMatrix",  this));		addMenuItem("Show Matrix", MesquiteModule.makeCommand("showMatrix",  this)); 		addMenuItem( "-", null);		return true;  	 }	/*.................................................................................................................*/  	 public boolean showing(Object obj){	 	if (obj instanceof String) {    	 		String arguments = (String)obj;    	 		int queryDataSet = MesquiteInteger.fromString(arguments);    	 		return (queryDataSet == currentDataSet && window!=null);	 	}	 	if (obj instanceof CharacterData) {    	 		CharacterData d = (CharacterData)obj;    	 		int queryDataSet = getProject().getMatrixNumber(d);    	 		return (queryDataSet == currentDataSet && window!=null);	 	}	 	return false;  	 }  	 	public Object getAddColumnCompatibility(){		if (data == null)			return null;		else 			return data.getStateClass();	}	/*.................................................................................................................*/  	 public void showListWindow(Object obj, CommandRecord commandRec){ ///TODO: change name to makeLIstWindow	 	if (obj instanceof CharacterData) {			data = (CharacterData)obj;    	 		currentDataSet = getProject().getMatrixNumber(data);	 	}	 	else 	 		data = getProject().getCharacterMatrix(currentDataSet); //todo:  		window = new CharacterListWindow(this, commandRec);		((CharacterListWindow)window).setObject(data, commandRec);		setModuleWindow(window);		makeMenu("List"); 		addMenuItem( "Save selected as set...", makeCommand("saveSelectedRows", this)); 		addMenuItem( "-", null);		if (!commandRec.scripting()){			CharListAssistant assistant= null;			if (!(data instanceof MolecularData)){ //added 1. 06		  		assistant = (CharListAssistant)hireNamedEmployee(commandRec, CharListAssistant.class, "#DefaultCharOrder");		 		if (assistant!= null){		 			((CharacterListWindow)window).addListAssistant(assistant, commandRec);					assistant.setUseMenubar(false);				}			}			assistant = (CharListAssistant)hireNamedEmployee(commandRec, CharListAssistant.class, "#CharListInclusion");	 		if (assistant!= null){	 			((CharacterListWindow)window).addListAssistant(assistant, commandRec);				assistant.setUseMenubar(false);			}	  		/**/	  		assistant = (CharListAssistant)hireNamedEmployee(commandRec, CharListAssistant.class, "States");	 		if (assistant!= null){	 			((CharacterListWindow)window).addListAssistant(assistant, commandRec);				assistant.setUseMenubar(false);			}			/* default columns*/			if (!(data instanceof MolecularData)){ //added 1. 06				assistant = (CharListAssistant)hireNamedEmployee(commandRec, CharListAssistant.class, StringUtil.tokenize("#CharListPartition"));		 		if (assistant!= null){		 			((CharacterListWindow)window).addListAssistant(assistant, commandRec);					assistant.setUseMenubar(false);				}			}			assistant = (CharListAssistant)hireNamedEmployee(commandRec, CharListAssistant.class, StringUtil.tokenize("Current Parsimony Models"));	 		if (assistant!= null){	 			((CharacterListWindow)window).addListAssistant(assistant, commandRec);				assistant.setUseMenubar(false);			}			assistant = (CharListAssistant)hireNamedEmployee(commandRec, CharListAssistant.class, StringUtil.tokenize("Current Probability Models"));	 		if (assistant!= null){	 			((CharacterListWindow)window).addListAssistant(assistant, commandRec);				assistant.setUseMenubar(false);			}						if (data instanceof DNAData) {				assistant = (CharListAssistant)hireNamedEmployee(commandRec, CharListAssistant.class, StringUtil.tokenize("Current Codon Positions"));		 		if (assistant!= null){		 			((CharacterListWindow)window).addListAssistant(assistant, commandRec);					assistant.setUseMenubar(false);				}			}			/**/		}		 		resetContainingMenuBar();		resetAllWindowsMenus();  	 }	/*.................................................................................................................*/	public boolean rowsMovable(){		return true;	}	/* following required by ListModule*/  	 public Object getMainObject(){  	 	return data;  	 }  	 public int getNumberOfRows(){  	 	if (data==null)  	 		return 0;  	 	else  	 		return data.getNumChars();  	 }	public Class getAssistantClass(){		return CharListAssistant.class;	}	public boolean rowsDeletable(){		return true;	}	public boolean deleteRow(int row){		if (data!=null) {			data.deleteCharacters(row, 1, false);			data.deleteInLinked(row, 1, true);			return true;		}		return false;	}	/** returns either a String (if not editable) or a MesquiteString (if to be editable) */	public String getAnnotation(int row){		if (data !=null) {			return data.getAnnotation(row);		}		return null;	}	/** sets the annotation for a row*/	public void setAnnotation(int row, String s, boolean notify){		if (data !=null) {			data.setAnnotation(row, s);		}	}	/** returns a String explaining the row */	public String getExplanation(int row){		return null;	}	public String getItemTypeName(){		return "Character";	}	public String getItemTypeNamePlural(){		return "Characters";	}	/*.................................................................................................................*/  	 public Snapshot getSnapshot(MesquiteFile file) {    	 	if (window==null || !window.isVisible())   	 		return null;   	 	Snapshot temp = new Snapshot();		  	 	temp.addLine("setData " + currentDataSet);   	 	      	 	if (window!=null)			window.incorporateSnapshot(temp, file);  	 	//if (window!=null && !window.isVisible())  	 	temp.addLine("showWindow");  	 	return temp;  	 }	/*.................................................................................................................*/    	 public Object doCommand(String commandName, String arguments, CommandRecord commandRec, CommandChecker checker) {    	 	if (checker.compare(this.getClass(), "Renames the matrix", "[name of matrix]", commandName, "renameMatrix")) {    	 		if (data == null)    	 			return null;    	 		String token = ParseUtil.getFirstToken(arguments, new MesquiteInteger(0));    	 		if (StringUtil.blank(token) && !commandRec.scripting()) {				token = MesquiteString.queryString(containerOfModule(), "Rename matrix", "New Name of Matrix:", data.getName(), 2);				if (StringUtil.blank(token))					return null;    	 		} 			data.setName(token);			MesquiteWindow.resetAllTitles();   	 			    	 	}    	 	else if (checker.compare(this.getClass(), "Deletes the matrix", null, commandName, "deleteMatrix")) {    	 		if (data == null)    	 			return null;    	 		if (!commandRec.scripting())    	 			if (!AlertDialog.query(containerOfModule(), "Delete matrix?", "Are you sure you want to delete the matrix?  You cannot undo this."))    	 				return null;    	 		data.deleteMe(false);   	 			    	 	}    	 	else if (checker.compare(this.getClass(), "Shows the matrix", null, commandName, "showMatrix")) {    	 		data.showMatrix(commandRec);    	 	}    	 	else if (checker.compare(this.getClass(), "Sets data set whose characters are to be displayed in this list window", "[number of data set]", commandName, "setData")) {    	 		currentDataSet = MesquiteInteger.fromString(arguments);    	 		if (window!=null && MesquiteInteger.isCombinable(currentDataSet) && currentDataSet<getProject().getNumberCharMatrices()) {				data = getProject().getCharacterMatrix(currentDataSet);//checker.getFile(),     	 			((CharacterListWindow)window).setObject(data, commandRec);    	 			((CharacterListWindow)window).repaintAll();    	 		}    	 	}    	 	else if (checker.compare(this.getClass(), "Returns data set whose characters are listed", null, commandName, "getData")) {    	 		return ((CharacterListWindow)window).getCurrentObject();    	 	}    	 	else if (checker.compare(this.getClass(), "Saves the selected rows as a character set", null, commandName, "saveSelectedRows")) {    	 		((CharacterListWindow)window).saveSelectedRows();    	 	}    	 	else    	 		return super.doCommand(commandName, arguments, commandRec, checker);		return null;   	 }	/*.................................................................................................................*/    	 public String getName() {		return "Character List";   	 }   	 public void endJob(){			if (data!=null) {				data.removeListener(this);				if (data.getTaxa()!=null)					data.getTaxa().removeListener(this);			}			super.endJob();   	 }	/*.................................................................................................................*/   	  	/** returns an explanation of what the module does.*/ 	public String getExplanation() { 		return "Makes windows listing characters and information about them." ;   	 }	/*.................................................................................................................*/ 	/** returns current parameters, for logging etc..*/ 	public String getParameters() {			if (data!=null) {				return "Character matrix: " + data.getName();			}			return "";   	 }   	 	/*.................................................................................................................*/	/** Requests a window to close.  In the process, subclasses of MesquiteWindow might close down their owning MesquiteModules etc.*/ 	public void windowGoAway(MesquiteWindow whichWindow) {			whichWindow.hide();	}   	    	 }/* ======================================================================== */class CharacterListWindow extends ListWindow implements MesquiteListener {	private int currentDataSet = 0;	CharacterData data = null;	CharSelectCoordinator selectionCoordinator;	CharacterList listModule;	public CharacterListWindow (CharacterList ownerModule, CommandRecord commandRec) {		super(ownerModule); //INFOBAR		this.listModule = ownerModule;		currentDataSet = ownerModule.currentDataSet;		data = ownerModule.getProject().getCharacterMatrix(currentDataSet);		getTable().setRowAssociable(data);		MesquiteButton matrixButton = new MesquiteButton(ownerModule, MesquiteModule.makeCommand("showMatrix",  ownerModule), null, true, MesquiteModule.getRootImageDirectoryPath() + "matrix.gif", 12, 16);		matrixButton.setShowBackground(false);		matrixButton.setButtonExplanation("Show Character Matrix Editor");		table.addControlButton(matrixButton);		ownerModule.hireAllCompatibleEmployees(commandRec, CharTableAssistantI.class, data.getStateClass());  		Enumeration enumeration=ownerModule.getEmployeeVector().elements();		while (enumeration.hasMoreElements()){			Object obj = enumeration.nextElement();			if (obj instanceof CharTableAssistantI) {				CharTableAssistantI init = (CharTableAssistantI)obj;				init.setTableAndData(getTable(), data, true, commandRec);			}		}		ownerModule.hireAllCompatibleEmployees(commandRec, CharListAssistantI.class, data.getStateClass());  		enumeration=ownerModule.getEmployeeVector().elements();		while (enumeration.hasMoreElements()){			Object obj = enumeration.nextElement();			if (obj instanceof CharListAssistantI) {				CharListAssistantI init = (CharListAssistantI)obj;				init.setTableAndData(getTable(), data, commandRec);			}		}		if (getTable() != null)			getTable().setDropDown(-1, -1, true);		selectionCoordinator = (CharSelectCoordinator)ownerModule.hireEmployee(commandRec, CharSelectCoordinator.class, null);		if (selectionCoordinator!=null)			selectionCoordinator.setTableAndObject(getTable(), data, true, commandRec);	}	/*	protected ToolPalette makeToolPalette(){		return new CharListPalette((CharacterList)ownerModule, this);	}	/*.................................................................................................................*/	/** When called the window will determine its own title.  MesquiteWindows need	to be self-titling so that when things change (names of files, tree blocks, etc.)	they can reset their titles properly*/	public void resetTitle(){		if (data!=null&& data.hasTitle())			setTitle("Characters \"" + data.getName() + "\""); 		else if (data==null)			setTitle("Characters (DATA NULL)" ); 		else			setTitle("Characters" ); 			}	public Object getCurrentObject(){		return data;	}	public void setCurrentObject(Object obj){		if (obj instanceof CharacterData) {			if (data!=null) {				data.removeListener(this);				if (data.getTaxa()!=null)					data.getTaxa().removeListener(this);			}			data = (CharacterData)obj;			getTable().setRowAssociable(data);			getTable().setDropDown(-1, -1, true);			data.addListener(this); //TODO: this needs to be done for taxon lists, etc.			data.getTaxa().addListener(this);			resetTitle();			if (selectionCoordinator!=null)				selectionCoordinator.setTableAndObject(getTable(), data, true, CommandRecord.getRecNSIfNull());	 		Enumeration enumeration=ownerModule.getEmployeeVector().elements();			while (enumeration.hasMoreElements()){				Object ct = enumeration.nextElement();				if (ct instanceof CharTableAssistantI) {					CharTableAssistantI init = (CharTableAssistantI)ct;					init.setTableAndData(getTable(), data, true, CommandRecord.getRecNSIfNull());				}			}	 		enumeration=ownerModule.getEmployeeVector().elements();			while (enumeration.hasMoreElements()){				Object ct = enumeration.nextElement();				if (ct instanceof CharListAssistantI) {					CharListAssistantI init = (CharListAssistantI)ct;					init.setTableAndData(getTable(), data, CommandRecord.getRecNSIfNull());				}			}			getTable().synchronizeRowSelection(data);		}	}   	public void focusInRow(int row){ 		Enumeration enumeration=ownerModule.getEmployeeVector().elements();		while (enumeration.hasMoreElements()){			Object obj = enumeration.nextElement();			if (obj instanceof CharListAssistantI) {				CharListAssistantI init = (CharListAssistantI)obj;				init.focusInRow(row, CommandRecord.nonscriptingRecord);			}		}   	}	public String getItemTypeName(){		return "Character";	}	public String getItemTypeNamePlural(){		return "Characters";	}	/*.................................................................................................................*/	public void showSelectionPopup(Container cont, int x, int y) {		if (selectionCoordinator!=null)			selectionCoordinator.showPopUp(cont, x+5, y+5);	}	/*.................................................................................................................*/	public void setRowName(int row, String name, CommandRecord commandRec){		if (data!=null) {			String warning = data.checkNameLegality(row, name);			if (warning == null)				data.setCharacterName(row, name);			else				ownerModule.discreetAlert(commandRec, warning);		}	}	public String getRowName(int row){		if (data!=null)			return data.getCharacterName(row);		else			return null;	}	/*...............................................................................................................*/	public void setRowNameColor(Graphics g, int row){//		g.setColor(Color.black);		if (data!=null)			if (!data.characterHasName(row))				g.setColor(Color.gray);	}	/*.................................................................................................................*/	/** passes which object is being disposed (from MesquiteListener interface)*/	public void disposing(Object obj){		if (data==null || (obj instanceof Taxa &&  (Taxa)obj ==data.getTaxa())||(obj instanceof CharacterData && (CharacterData)obj ==data)) {			if (ownerModule!=null) {				ownerModule.iQuit();			}		}	}	/*.................................................................................................................*/	/** passes which object is being disposed (from MesquiteListener interface)*/	public boolean okToDispose(Object obj, int queryUser){		return true;  //TODO: respond	}	/*.................................................................................................................*/	/** passes which object changed, along with optional integer (e.g. for character) (from MesquiteListener interface)*/	public void changed(Object caller, Object obj, Notification notification, CommandRecord commandRec){		int code = Notification.getCode(notification);		int[] parameters = Notification.getParameters(notification);		if (obj instanceof CharacterData && (CharacterData)obj ==data) {			if (code==MesquiteListener.NAMES_CHANGED) {				table.redrawRowNames();			}			else if (code==MesquiteListener.SELECTION_CHANGED) {				table.synchronizeRowSelection(data);				table.repaintAll();			}			else if (code==MesquiteListener.PARTS_CHANGED || code==MesquiteListener.PARTS_DELETED || code==MesquiteListener.PARTS_ADDED) {				table.setNumRows(data.getNumChars());				table.synchronizeRowSelection(data);				table.repaintAll();			}			else {				table.setNumRows(data.getNumChars());				table.synchronizeRowSelection(data);				table.repaintAll();			}		}		super.changed(caller, obj, notification, commandRec);	}		//have selection be done by changing selection of characters, and listening here for selection to change selection of table!!!!!	//likewise for data matrix 		public void saveSelectedRows() {		if (table.anyRowSelected()) {			String nameOfCharSet = MesquiteString.queryString(this, "Name of character set", "Name of character set:", "Stored char. set");	 		if (StringUtil.blank(nameOfCharSet))	 			return;	 		CharSelectionSet selectionSet= new CharSelectionSet(nameOfCharSet, data.getNumChars(), data);	 			 		data.storeSpecsSet(selectionSet, CharSelectionSet.class);	 			 		selectionSet.addToFile(data.getFile(), null, null);	 		selectionSet.setNexusBlockStored("SETS");			for (int ic= 0; ic< table.getNumRows(); ic++){				if (table.isRowSelected(ic)) { 					selectionSet.setSelected(ic, true);				}			}	 		data.notifyListeners(this, new Notification(AssociableWithSpecs.SPECSSET_CHANGED));		}	}		}/* ======================================================================== *class CharListPalette extends ToolPalette {	CharacterListWindow window;	Image matrix;	int iW = 24;	int iH = 20;	public CharListPalette(CharacterList ownerModule, CharacterListWindow containingWindow) {  //in future pass general MesquiteWindow		super( ownerModule,  containingWindow, 1);		window = containingWindow;		matrix = MesquiteImage.getImage(MesquiteModule.getRootImageDirectoryPath() + "matrix.gif");  		setEnabled(true);	 	setVisible(true);	}		public void paint(Graphics g) { 	   	if (MesquiteWindow.checkDoomed(this))	   		return;		super.paint(g); 		g.drawImage(matrix, 4, getBounds().height-iH -8, this);		MesquiteWindow.uncheckDoomed(this); 		 	}   	public void mouseUp(int modifiers, int x, int y, MesquiteTool tool) {   		if (x< iW + 4 && y > getBounds().height-iH -8)   			window.data.showMatrix(CommandRecord.nonscriptingRecord);   		else   			super.mouseUp(modifiers, x, y, tool);   	}}/**/