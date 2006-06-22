/* Mesquite source code.  Copyright 1997-2006 W. Maddison and D. Maddison.Version 1.11, June 2006.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)*/package mesquite.correl.Pagel94Viewer;/*~~  */import java.awt.*;import java.util.*;import mesquite.lib.*;import mesquite.lib.characters.CharacterDistribution;import mesquite.lib.characters.CharacterStates;import mesquite.categ.lib.*;import mesquite.lib.duties.*;import mesquite.cont.lib.*;import mesquite.correl.lib.*;public class Pagel94Viewer extends TreeWindowAssistantA implements CLogger   {	int currentX = 0;	int currentY = 1;	Tree tree;	Pagel94Calculator numberTask;	CharSourceCoordObed characterSourceTask;	Taxa taxa;	MesquiteString numberTaskName;	MesquiteCommand ntC;//	MesquiteTextWindow window;	MesquiteWindow containingWindow;	P94Panel panel;	/*.................................................................................................................*/	public boolean startJob(String arguments, Object condition, CommandRecord commandRec, boolean hiredByName) { 		numberTask = (Pagel94Calculator)hireNamedEmployee(commandRec, NumberFor2CharAndTree.class, "#mesquite.correl.Pagel94.Pagel94");		if (numberTask == null)			return sorry(commandRec, getName() + " couldn't start because no calculator module obtained.");		ntC =makeCommand("setNumberTask",  this); 		numberTask.setHiringCommand(ntC); 		numberTaskName = new MesquiteString(); 		numberTaskName.setValue(numberTask.getName()); 				characterSourceTask = (CharSourceCoordObed)hireCompatibleEmployee(commandRec, CharSourceCoordObed.class, CategoricalState.class, "Source of  characters (for Pagel 94 calculations)"); 		if (characterSourceTask == null) 			return sorry(commandRec, getName() + " couldn't start because no source of characters was obtained."); 		Frame f = containerOfModule(); 		if (f instanceof MesquiteWindow){ 			containingWindow = (MesquiteWindow)f; 			containingWindow.addSidePanel(panel = new P94Panel(), 200);		}//		window = new MesquiteTextWindow(this, "Pagel 1994 Analysis", true);// 		setModuleWindow(window);// 		// 		if (!commandRec.scripting())// 			window.show(); 		numberTask.setLogger(this);		makeMenu("Pagel94");		addMenuItem( "Choose Character X...", makeCommand("chooseX",  this));		addMenuItem( "Choose Character Y...", makeCommand("chooseY",  this));		addMenuItem( "Close Pagel 94 Analysis", makeCommand("close",  this));		addMenuItem( "-", null); 		return true; 	}	public void cwriteln(String s){//		window.append(s + "\n");		panel.append(s + "\n");	} 	public void cwrite(String s){ //		window.append(s); 		panel.append(s); 	}		/*.................................................................................................................*/	public boolean isPrerelease(){		return false;	}	/*.................................................................................................................*/	/** returns whether this module is requesting to appear as a primary choice */   	public boolean requestPrimaryChoice(){   		return true;     	}	/*.................................................................................................................*/	/*.................................................................................................................*/ 	public void windowGoAway(MesquiteWindow whichWindow) {			whichWindow.hide();			whichWindow.dispose();			iQuit();	}	/*.................................................................................................................*/  	 public Snapshot getSnapshot(MesquiteFile file) {    	 	Snapshot temp = new Snapshot(); 	  	 	temp.addLine("getCharSource ", characterSourceTask); 	  	 	temp.addLine("getPagel94 ", numberTask); 	  	 	temp.addLine("setX " + CharacterStates.toInternal(currentX)); 	  	 	temp.addLine("setY " + CharacterStates.toInternal(currentY)); 	  	 	temp.addLine("doCounts");	/*  	 	Snapshot fromWindow = window.getSnapshot(file);			temp.addLine("getWindow");			temp.addLine("tell It");			temp.incorporate(fromWindow, true);			temp.addLine("showWindow");*/		 	 	return temp;  	 }  	 MesquiteInteger pos = new MesquiteInteger();	/*.................................................................................................................*/    	 public Object doCommand(String commandName, String arguments, CommandRecord commandRec, CommandChecker checker) {       	if (checker.compare(this.getClass(), "Returns employee", null, commandName, "getPagel94")) {    	 		return numberTask;    	 	}      	else   	if (checker.compare(this.getClass(), "Returns employee", null, commandName, "getCharSource")) {    	 		return characterSourceTask;    	 	}      	else if (checker.compare(this.getClass(), "Queries the user about what character to use", null, commandName, "chooseX")) {    	 		int ic=characterSourceTask.queryUserChoose(taxa, " (X, for Pagel 94 analysis) ", commandRec);    	 		if (MesquiteInteger.isCombinable(ic)) {	   			currentX = ic;	 			doCounts(commandRec); 			}    	 	}    	 	else if (checker.compare(this.getClass(), "Sets the character to use for X", "[character number]", commandName, "setX")) {    	 		int icNum = MesquiteInteger.fromFirstToken(arguments, stringPos);    	 		if (!MesquiteInteger.isCombinable(icNum))    	 			return null;    	 		int ic = CharacterStates.toInternal(icNum);    	 		if ((ic>=0) && characterSourceTask.getNumberOfCharacters(taxa, commandRec)==0) {    	 			currentX = ic;	 			  	 		}    	 		else if ((ic>=0) && (ic<=characterSourceTask.getNumberOfCharacters(taxa, commandRec)-1)) {    	 			currentX = ic;	 			 			}    	 	}    	 	else if (checker.compare(this.getClass(), "Queries the user about what character to use", null, commandName, "chooseY")) {    	 		int ic=characterSourceTask.queryUserChoose(taxa, " (Y, for Pagel 94 analysis) ", commandRec);    	 		if (MesquiteInteger.isCombinable(ic)) {	   			currentY = ic;	 			doCounts(commandRec); 			}    	 	}    	 	else if (checker.compare(this.getClass(), "Sets the character to use for X", "[character number]", commandName, "setY")) {    	 		int icNum = MesquiteInteger.fromFirstToken(arguments, stringPos);    	 		if (!MesquiteInteger.isCombinable(icNum))    	 			return null;    	 		int ic = CharacterStates.toInternal(icNum);    	 		if ((ic>=0) && characterSourceTask.getNumberOfCharacters(taxa, commandRec)==0) {    	 			currentY = ic;		 	}    	 		else if ((ic>=0) && (ic<=characterSourceTask.getNumberOfCharacters(taxa, commandRec)-1)) {    	 			currentY = ic;			}    	 	}    	 	else if (checker.compare(this.getClass(), "Provokes Calculation", null, commandName, "doCounts")) {    	 		doCounts(commandRec);    	 	}     	 else if (checker.compare(this.getClass(), "Quits", null, commandName, "close")) {	 		if (panel != null && containingWindow != null)				containingWindow.removeSidePanel(panel);   	 		iQuit();    	 	}	 	else    	 		return super.doCommand(commandName, arguments, commandRec, checker);    	 	return null;    	 }    	 long oldTreeID = -1;    	 long oldTreeVersion = 0;	/*.................................................................................................................*/	public   void setTree(Tree tree, CommandRecord commandRec) {		if (tree==null)			return;		this.tree=tree;		taxa = tree.getTaxa(); 	if ((tree.getID() != oldTreeID || tree.getVersionNumber() != oldTreeVersion) && !commandRec.scripting()) {			doCounts(commandRec);  //only do counts if tree has changed		}		oldTreeID = tree.getID();		oldTreeVersion = tree.getVersionNumber();	}	/*.................................................................................................................*/ 	public void employeeParametersChanged(MesquiteModule employee, MesquiteModule source, Notification notification, CommandRecord commandRec) {	   	 	if (numberTask!=null && !commandRec.scripting())				doCounts(commandRec);	}	/*.................................................................................................................*/    	 public void doCounts(CommandRecord commandRec) {    		 if (taxa == null)    			 return;		CharacterDistribution statesX = characterSourceTask.getCharacter(taxa, currentX, commandRec);		CharacterDistribution statesY = characterSourceTask.getCharacter(taxa, currentY, commandRec);		MesquiteNumber result = new MesquiteNumber();		MesquiteString rs = new MesquiteString();//		window.setText("");		panel.setStatus(true);		panel.repaint();		panel.setText("\nX: " + characterSourceTask.getCharacterName(taxa, currentX, commandRec) + "\n");		panel.append("Y: " + characterSourceTask.getCharacterName(taxa, currentY, commandRec) + "\n");		if (statesX == null || statesY == null)			rs.setValue("Sorry, one or both of the characters was not obtained.  The Pagel correlation analysis could not be completed.");		else if (!(statesX instanceof CategoricalDistribution) ||!(statesY instanceof CategoricalDistribution) ||  statesX.getStateClass() != CategoricalState.class || statesY.getStateClass() != CategoricalState.class)			rs.setValue("Sorry, one or both of the characters is not a standard non-molecular categorical character.  The Pagel correlation analysis could not be completed.");		else if (((CategoricalDistribution)statesX).getMaxState() >1 || ((CategoricalDistribution)statesY).getMaxState() >1)			rs.setValue("Sorry, both characters need to be binary (states 0 and 1 only).  The Pagel correlation analysis could not be completed.");		else if (((CategoricalDistribution)statesX).getMaxState()==-1 || ((CategoricalDistribution)statesY).getMaxState() ==-1)			rs.setValue("Sorry, one or both of the characters seems to have no states or corresponds to a non-existent column in a character matrix.  The Pagel correlation analysis could not be completed.");		else 			numberTask.calculateNumber(tree, statesX, statesY, result, rs, commandRec);		panel.setStatus(false);		panel.repaint();//		window.append("\n\n  " + rs);		panel.append("\n\n" + rs);	}	/*.................................................................................................................*/    	 public String getName() {		return "Pagel 94 Analysis";   	 }   	 	/*.................................................................................................................*/ 	/** returns an explanation of what the module does.*/ 	public String getExplanation() { 		return "Performs a Pagel 1994 Correlation analysis ." ;   	 }	public void endJob() {		if (panel != null && containingWindow != null)			containingWindow.removeSidePanel(panel);		super.endJob();	}}class P94Panel extends MousePanel{	TextArea text;		Font df = new Font("Dialog", Font.BOLD, 14);		boolean calculating = false;	public P94Panel(){		super();		text = new TextArea(" ", 50, 50, TextArea.SCROLLBARS_VERTICAL_ONLY);		setLayout(null);		add(text);		text.setLocation(0,26);		text.setVisible(true);		setBackground(Color.darkGray);		text.setBackground(Color.white);	}	public void setStatus(boolean calculating){		this.calculating = calculating;	}	public void setText(String t){		text.setText(t);	}	public void append(String t){		text.append(t);	}	public void setSize(int w, int h){		super.setSize(w, h);		text.setSize(w, h-26);	}	public void setBounds(int x, int y, int w, int h){		super.setBounds(x, y, w, h);		text.setSize(w, h-26);	}	public void paint(Graphics g){		g.setFont(df);				if (!calculating){			g.setColor(Color.white);			g.drawString("Pagel 94 Analysis", 8, 20);		}		else{			g.setColor(Color.black);			g.fillRect(0,0, getBounds().width, 50);			g.setColor(Color.red);			g.drawString("Pagel 94: Calculating", 8, 20);		}	}}