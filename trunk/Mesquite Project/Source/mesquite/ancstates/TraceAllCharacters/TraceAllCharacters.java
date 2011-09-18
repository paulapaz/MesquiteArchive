/* Mesquite source code.  Copyright 1997-2010 W. Maddison and D. Maddison.Version 2.74, October 2010.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)*/package mesquite.ancstates.TraceAllCharacters;/*~~  */import java.applet.*;import java.util.*;import java.awt.*;import mesquite.lib.*;import mesquite.lib.characters.*;import mesquite.lib.duties.*;/*======================================================================== */public class TraceAllCharacters extends TreeDisplayAssistantA {	public void getEmployeeNeeds(){  //This gets called on startup to harvest information; override this and inside, call registerEmployeeNeed		EmployeeNeed e = registerEmployeeNeed(CharStatesForNodes.class, getName() + " needs a source of reconstructed ancestral states.",		"The reconstruction method is chosen initially or using the Reconstruction Method submenu");		EmployeeNeed e2 = registerEmployeeNeed(MatrixSourceCoord.class, getName() + " needs a source of matrices whose ancestral states will be reconstructed",		"The source of characters is chosen initially or in the Source of Character Matrices submenu");	}	CharsStatesForNodes allCharsTask;	//DisplayCharsStsAtNodes displayTask;	MatrixSourceCoord characterSourceTask;	Vector traces;	//MesquiteString displayTaskName;	MesquiteCommand dtC;	MesquiteBoolean byCharacters, selectedOnly, selectedCharactersOnly, showTerminals;	boolean suppressed = false;	/*.................................................................................................................*/	public boolean startJob(String arguments, Object condition, boolean hiredByName) {		traces = new Vector();		byCharacters = new MesquiteBoolean(true);		selectedOnly = new MesquiteBoolean(true);		selectedCharactersOnly = new MesquiteBoolean(false);		showTerminals = new MesquiteBoolean(false); 		makeMenu("Trace_All"); 		characterSourceTask = (MatrixSourceCoord)hireEmployee(MatrixSourceCoord.class,"Source of characters (for Trace All Characters)"); 		suppressed = MesquiteThread.isScripting(); 		if (characterSourceTask == null) { 			return sorry(getName() + " couldn't start because no source of characters was obtained."); 		} 		allCharsTask = (CharsStatesForNodes)hireEmployee(CharsStatesForNodes.class, "Reconstruction method"); 		if ( allCharsTask == null ) { 			return sorry(getName() + " couldn't start because no character reconstructor was obtained."); 		} 		/* 		displayTask = (DisplayCharsStsAtNodes)hireEmployee(DisplayCharsStsAtNodes.class, "Displayer of ancestral states"); 		if (displayTask == null) { 			return sorry(getName() + " couldn't start because now display module was obtained."); 		} 		dtC = makeCommand("setDisplayMode",  this); 		displayTask.setHiringCommand(dtC); 		displayTaskName = new MesquiteString(displayTask.getName());		if (numModulesAvailable(DisplayCharsStsAtNodes.class)>1){			MesquiteSubmenuSpec mss = addSubmenu(null, "Trace All display mode...", dtC, DisplayCharsStsAtNodes.class); 			mss.setSelected(displayTaskName);		} 		*/		addCheckMenuItem(null, "Show Selected Nodes Only", makeCommand("toggleSelectedOnly", this), selectedOnly);		addCheckMenuItem(null, "Show Selected Characters Only", makeCommand("toggleSelectedCharsOnly", this), selectedCharactersOnly);		addCheckMenuItem(null, "Show Terminal Nodes", makeCommand("toggleShowTerminals", this), showTerminals);		addCheckMenuItem(null, "Rows are Characters", makeCommand("toggleByCharacters", this), byCharacters);		addMenuItem( "Close Trace All", makeCommand("closeTrace",  this));		addMenuItem( "-", null); 		resetContainingMenuBar();		return true; 	}	/*.................................................................................................................*/	/** Generated by an employee who quit.  The MesquiteModule should act accordingly. */ 	public void employeeQuit(MesquiteModule employee) { 		if (employee == allCharsTask || employee == characterSourceTask)   			iQuit(); 		if (employee instanceof TextDisplayer){ 			for (int i = 0; i<traces.size(); i++){ 				TraceAllOperator tao = (TraceAllOperator)traces.elementAt(i); 				if (tao.displayer == employee){ 					tao.turnOff(); 					traces.removeElement(tao); 				} 			} 			if (traces.size() == 0) 				iQuit(); 		}	} 	MesquiteWindow window;	/*.................................................................................................................*/	public   TreeDisplayExtra createTreeDisplayExtra(TreeDisplay treeDisplay) {		TextDisplayer displayer = (TextDisplayer)hireEmployee(TextDisplayer.class, null);		if (displayer!=null) {			displayer.setWrap(false);			displayer.setPoppedOut(1);			displayer.showText("Trace All Characters", "Trace All Characters", true);			window = displayer.getModuleWindow();		}		TraceAllOperator newTrace = new TraceAllOperator(this, treeDisplay, displayer);		traces.addElement(newTrace);		return newTrace;	}  	    	/*.................................................................................................................*/  	 public Snapshot getSnapshot(MesquiteFile file) {   	 	Snapshot temp = new Snapshot();  	 	temp.addLine("toggleByCharacters " + byCharacters.toOffOnString());  	 	temp.addLine("toggleSelectedOnly " + selectedOnly.toOffOnString());  	 	temp.addLine("toggleSelectedCharsOnly " + selectedCharactersOnly.toOffOnString());  	 	temp.addLine("toggleShowTerminals " + showTerminals.toOffOnString());   	 	temp.addLine("getCharSource", characterSourceTask);  	 	temp.addLine("getReconstructor", allCharsTask); 	 	temp.addLine("desuppress");   	 	//temp.addLine("setDisplayMode ",displayTask); 	 	return temp;  	 }	/*.................................................................................................................*/    	 public Object doCommand(String commandName, String arguments, CommandChecker checker) {    	 	/*    	 	if (checker.compare(this.getClass(), "Sets module used to display reconstructed states", "[name of module]", commandName, "setDisplayMode")) {    	 		DisplayCharsStsAtNodes temp=  (DisplayCharsStsAtNodes)replaceEmployee(DisplayCharsStsAtNodes.class, arguments, "Display mode", displayTask); 			if (temp != null) { 				displayTask= temp;		 		displayTask.setHiringCommand(dtC); 				displayTaskName.setValue(displayTask.getName());				resetAllTraceOperators(); 				parametersChanged(); 				return displayTask; 			}    	 	}    	 	*/    	 	if (checker.compare(this.getClass(), "Undoes suppression of calculations when scripting", null, commandName, "desuppress")) {    	 		suppressed = false; 			recalcAllTraceOperators();			resetAllTraceOperators();    	 	}    	 	else if (checker.compare(this.getClass(), "Sets whether to show states only for selected nodes", "[on or off]", commandName, "toggleSelectedOnly")) {    	 		selectedOnly.toggleValue(parser.getFirstToken(arguments));			if (!MesquiteThread.isScripting())				resetAllTraceOperators();    	 	}    	 	else if (checker.compare(this.getClass(), "Sets whether to show states only for selected characters", "[on or off]", commandName, "toggleSelectedCharsOnly")) {    	 		selectedCharactersOnly.toggleValue(parser.getFirstToken(arguments));			if (!MesquiteThread.isScripting())				resetAllTraceOperators();    	 	}    	 	else if (checker.compare(this.getClass(), "Sets whether to show states for terminal nodes", "[on or off]", commandName, "toggleShowTerminals")) {    	 		showTerminals.toggleValue(parser.getFirstToken(arguments));			if (!MesquiteThread.isScripting())				resetAllTraceOperators();    	 	}    	 	else if (checker.compare(this.getClass(), "Sets whether to show states with rows = nodes and columns = characters, or the reverse", "[on or off]", commandName, "toggleByCharacters")) {    	 		byCharacters.toggleValue(parser.getFirstToken(arguments));			if (!MesquiteThread.isScripting())				resetAllTraceOperators();    	 	}    	 	else if (checker.compare(this.getClass(), "Returns source of chars", null, commandName, "getCharSource")) {   	 		return characterSourceTask;    	 	}    	 	else if (checker.compare(this.getClass(), "Returns reconstructor", null, commandName, "getReconstructor")) {   	 		return allCharsTask;    	 	}    	 	else if (checker.compare(this.getClass(), "Turns off Trace All Characters", null, commandName, "closeTrace")) {    	 		iQuit();			resetContainingMenuBar();    	 	}    	 	else    	 		return  super.doCommand(commandName, arguments, checker);		return null;   	 }	/*.................................................................................................................*/ 	public void employeeParametersChanged(MesquiteModule employee, MesquiteModule source, Notification notification) { 		if (!suppressed){ 			recalcAllTraceOperators();			parametersChanged();		}	}		void recalcAllTraceOperators(){		if (suppressed)			return;		Enumeration e = traces.elements();		while (e.hasMoreElements()) {			Object obj = e.nextElement();			if (obj instanceof TraceAllOperator) {				TraceAllOperator tCO = (TraceAllOperator)obj;	 			tCO.recalculate();	 		}		}	}	/*.................................................................................................................*/ 	public void resetAllTraceOperators() {		Enumeration e = traces.elements();		while (e.hasMoreElements()) {			Object obj = e.nextElement();			if (obj instanceof TraceAllOperator) {				TraceAllOperator tCO = (TraceAllOperator)obj;	 			tCO.refresh();	 		}		}	}	/*.................................................................................................................*/ 	public void closeAllTraceOperators() {		Enumeration e = traces.elements();		while (e.hasMoreElements()) {			Object obj = e.nextElement();			if (obj instanceof TraceAllOperator) {				TraceAllOperator tCO = (TraceAllOperator)obj;	 			tCO.turnOff();	 		}		}	}	/*.................................................................................................................*/ 	public void endJob() { 		if (window != null){ 			window.hide(); 		window.dispose(); 		}   	 	closeAllTraceOperators();		super.endJob(); 	}	/*.................................................................................................................*/    	 public String getName() {		return "Trace All Characters";   	 }	/*.................................................................................................................*/  	 public String getVersion() {		return null;   	 }   	 	/*.................................................................................................................*/  	 public boolean isPrerelease() {		return false;   	 }   	 	/*.................................................................................................................*/ 	/** returns current parameters, for logging etc..*/ 	public String getParameters() { 		if (characterSourceTask == null || allCharsTask== null) 			return "";		return "Ancestral States:\n" + allCharsTask.getNameAndParameters() + "\nusing:\n" + characterSourceTask.getNameAndParameters();   	 }/*.................................................................................................................*/  	 public String getExplanation() {		return "Summarizes for each node the reconstructions of the states at all characters of the tree.";   	 }}/*======================================================================== */class TraceAllOperator extends TreeDisplayDrawnExtra implements MesquiteListener {	Tree myTree;	TraceAllCharacters traceAllModule;	MCharactersHistory charsStates;	MCharactersDistribution matrix;	CharacterData oldData;	TextDisplayer displayer;	boolean turnedOff = false;	public TraceAllOperator (TraceAllCharacters ownerModule, TreeDisplay treeDisplay, TextDisplayer displayer) {		super(ownerModule, treeDisplay);		traceAllModule = ownerModule;		this.displayer = displayer;	}	/*.................................................................................................................*/	public   void recalculate(){				if (turnedOff)			return;		if (traceAllModule.allCharsTask!=null) {			//note: following doesn't pass MesquiteString for results since does character by character and would only get message from last 			charsStates = traceAllModule.allCharsTask.calculateStates(myTree, matrix = traceAllModule.characterSourceTask.getCurrentMatrix(myTree), charsStates, null);			int drawnRoot = treeDisplay.getTreeDrawing().getDrawnRoot();			if (!myTree.nodeExists(drawnRoot))				drawnRoot = myTree.getRoot();			refresh(); 		}	}	/*.................................................................................................................*/	public   void setTree(Tree tree){		myTree = tree;		if (turnedOff)			return; 		if (traceAllModule.allCharsTask!=null) {			charsStates = traceAllModule.allCharsTask.calculateStates(tree, matrix = traceAllModule.characterSourceTask.getCurrentMatrix(tree), charsStates, null);			refresh(); 		}	}		public void refresh(){		if (turnedOff)			return;		if (displayer!=null)			displayer.showText(composeText(myTree, charsStates), "Trace All Characters", true);	}		public String composeText(Tree tree, MCharactersHistory charStates){		if (traceAllModule == null || tree == null || charStates == null)			return null;		if (turnedOff)			return null;		StringBuffer sb = new StringBuffer(100);		StringBuffer sb2 = new StringBuffer(100);		MesquiteBoolean anyAdded = new MesquiteBoolean(false);		CharacterData data = null;		if (matrix !=null)			data = matrix.getParentData();		if (oldData !=null && data !=null && data !=oldData)			oldData.removeListener(this);		if (data !=null && data !=oldData)			data.addListener(this);		oldData = data;		CharInclusionSet incl = null;				if (data !=null)			incl = (CharInclusionSet)data.getCurrentSpecsSet(CharInclusionSet.class);				sb.append("Trace All Characters\n\n");		if (traceAllModule.allCharsTask!=null) {			sb.append(traceAllModule.getParameters());			sb.append("\n======================================\n");		}		sb.append("Ancestral states are listed by character and by node on the tree.  Node numbers are shown in red on the tree drawing.\n\n");		int drawnRoot = 0;		if (treeDisplay != null && treeDisplay.getTreeDrawing() != null)			drawnRoot = treeDisplay.getTreeDrawing().getDrawnRoot();		if (!tree.nodeExists(drawnRoot))			drawnRoot = tree.getRoot();		if (incl !=null && incl.numberSelected()==0){			sb.append("All characters are excluded.  You change the inclusion/exclusion of characters using the first column of the List of Characters window.");		}		/*else if (traceAllModule.selectedOnly.getValue() && !tree.anySelected()) {			sb.append("No nodes are selected.  To show the results of Trace All Characters, either turn off the menu item \"Show Selected Nodes Only\", or select some branches of the tree.");					}*/		else if (data !=null && traceAllModule.selectedCharactersOnly.getValue() && !data.anySelected()) {			sb.append("No characters are selected.  To show the results of Trace All Characters, either turn off the menu item \"Show Selected Characters Only\", or select some characters of the matrix.");					}		else if (traceAllModule.byCharacters.getValue()){			if (incl !=null && data !=null && (incl.numberSelected()< data.getNumChars()))				sb.append(Integer.toString(incl.numberSelected()) + " of " + data.getNumChars() + " characters included.\n\n");			sb.append("Char.\\Node");			boolean ignoreSelection = !traceAllModule.selectedOnly.getValue() || !tree.anySelected();			composeOneCharRec(tree, drawnRoot, null, sb, null, ignoreSelection);			sb.append('\n');			for (int ic=0; ic<charStates.getNumChars(); ic++) {				if ((!traceAllModule.selectedCharactersOnly.getValue() || data == null || data.getSelected(ic)) && (incl == null || incl.isBitOn(ic))){					CharacterHistory cH = charStates.getCharacterHistory(ic);					anyAdded.setValue(false);					sb2.setLength(0);					composeOneCharRec(tree, drawnRoot, cH, sb2, anyAdded, ignoreSelection);					if (anyAdded.getValue()){						sb.append("character ");						sb.append(Integer.toString(ic+1));						sb.append(sb2.toString());						sb.append('\n');					}				}			}		}		else {			if (incl !=null && data !=null && (incl.numberSelected()< data.getNumChars()))				sb.append(Integer.toString(incl.numberSelected()) + " of " + data.getNumChars() + " characters included.\n\n");			sb.append("Node\\Char.");			for (int ic=0; ic<charStates.getNumChars(); ic++) {				if ((!traceAllModule.selectedCharactersOnly.getValue() || data == null || data.getSelected(ic)) && (incl == null || incl.isBitOn(ic))){					sb.append('\t');					sb.append(Integer.toString(ic+1));				}			}			sb.append('\n');			boolean ignoreSelection = !traceAllModule.selectedOnly.getValue() || !tree.anySelected();			composeRecByNodes(tree, drawnRoot, charStates, data, incl, sb, ignoreSelection);		}				/*====experimental		sb.append("\nTaxa compared to immediate ancestor\n");		for (int it = 0; it < tree.getTaxa().getNumTaxa(); it++){			int taxon = tree.nodeOfTaxonNumber(it);			if (taxon > 0){				int anc = tree.motherOfNode(taxon);				if (anc >0){					int differences = 0;					CharacterState csTaxon = null;					CharacterState csAnc = null;					for (int ic = 0; ic < charStates.getNumChars(); ic++){						csTaxon = charStates.getCharacterState(csTaxon, ic, taxon);						csAnc = charStates.getCharacterState(csAnc, ic, anc);						if (!csAnc.equals(csTaxon))						//if (!csAnc.equals(csTaxon, true, true, true))							differences++;					}					if (differences == 0)						tree.getTaxa().setSelected(it, true);					sb.append(tree.getTaxa().getTaxonName(it) + " with " + differences + " differences compared to immediate ancestor\n");									}			}		}		*/		return sb.toString();			} 	private String nodeName(Tree tree, int node, boolean verbose){		if (tree.nodeIsInternal(node)) {			if (verbose)				return ("node " + node);			else				return Integer.toString(node);		}		return tree.getTaxa().getName(tree.taxonNumberOfNode(node));			}		/*.................................................................................................................*/	public   void composeOneCharRec(Tree tree, int node, CharacterHistory states, StringBuffer sb, MesquiteBoolean anyAdded, boolean ignoreSelection) {		for (int d = tree.firstDaughterOfNode(node); tree.nodeExists(d); d = tree.nextSisterOfNode(d))				composeOneCharRec(tree, d, states, sb, anyAdded, ignoreSelection);		if ((ignoreSelection || ((MesquiteTree)tree).getSelected(node)) && (traceAllModule.showTerminals.getValue() || tree.nodeIsInternal(node))){			sb.append('\t');			if (states != null){				String s = states.toString(node, "; ");				if (!StringUtil.blank(s)) {					sb.append(s);					if (anyAdded!=null)						anyAdded.setValue(true);				}			}			else				sb.append(nodeName(tree, node, false));		}	}	/*.................................................................................................................*/	public   void composeRecByNodes(Tree tree, int node, MCharactersHistory states, CharacterData data, CharInclusionSet incl, StringBuffer sb, boolean ignoreSelection) {		if (states == null)			return;		for (int d = tree.firstDaughterOfNode(node); tree.nodeExists(d); d = tree.nextSisterOfNode(d))				composeRecByNodes(tree, d, states, data, incl, sb, ignoreSelection);		if ((ignoreSelection || ((MesquiteTree)tree).getSelected(node)) && (traceAllModule.showTerminals.getValue() || tree.nodeIsInternal(node))){			sb.append(nodeName(tree, node, true) + ": ");			for (int ic=0; ic<states.getNumChars(); ic++) {				if ((!traceAllModule.selectedCharactersOnly.getValue() || data == null || data.getSelected(ic)) && (incl == null || incl.isBitOn(ic))){					sb.append('\t');					CharacterHistory cH = states.getCharacterHistory(ic);					sb.append(cH.toString(node, "; "));				}			}			sb.append('\n');		}	}	/*.................................................................................................................*/	private   void drawNumber(Tree tree, Graphics g, int N) {		if (tree.nodeExists(N)) {			if (tree.nodeIsInternal(N))				StringUtil.highlightString(g, Integer.toString(N), treeDisplay.getTreeDrawing().x[N]+8, treeDisplay.getTreeDrawing().y[N]+8, Color.red, Color.white);			for (int d = tree.firstDaughterOfNode(N); tree.nodeExists(d); d = tree.nextSisterOfNode(d))				drawNumber(tree, g, d);		}	}	/*.................................................................................................................*/	public   void drawOnTree(Tree tree, int drawnRoot, Graphics g) {		drawNumber(tree, g, drawnRoot);	}	/*.................................................................................................................*/	CharacterState cs = null;	/**return a text version of information at node*/	public String textAtNode(Tree tree, int node){		if (charsStates!=null) {			return "(" + node + ")";		}		return "";	}		/**return any additional explanatory text, e.g. if there is extensive information too verbose for a legend but which should be output to text view*/	public String additionalText(Tree tree, int node){		return "";	}	public   void printOnTree(Tree tree, int drawnRoot, Graphics g) {	}		/** passes which object changed, along with optional code number (type of change) and integers (e.g. which character)*/	public void changed(Object caller, Object obj, Notification notification){		int code = Notification.getCode(notification);		if (obj == oldData && (code == MesquiteListener.SELECTION_CHANGED || code == MesquiteListener.NAMES_CHANGED)){			refresh();		}	}	/** passes which object was disposed*/	public void disposing(Object obj){}	/** Asks whether it's ok to delete the object as far as the listener is concerned (e.g., is it in use?)*/	public boolean okToDispose(Object obj, int queryUser){return true;}		public void turnOff(){		turnedOff = true;		if (oldData !=null)			oldData.removeListener(this);		super.turnOff();	}}