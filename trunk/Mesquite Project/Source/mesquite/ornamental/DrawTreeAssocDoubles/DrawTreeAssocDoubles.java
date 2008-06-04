/* Mesquite source code.  Copyright 1997-2007 W. Maddison and D. Maddison. Version 2.01, December 2007.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html) */package mesquite.ornamental.DrawTreeAssocDoubles;/*~~  */import java.util.*;import java.awt.*;import java.awt.image.*;import mesquite.lib.*;import mesquite.lib.duties.*;/* ======================================================================== */public class DrawTreeAssocDoubles extends TreeDisplayAssistantDI {	public Vector extras;	public boolean first = true;	MesquiteBoolean on, percentage, horizontal;	public ListableVector names;	static boolean asked= false;	int digits = 4;	int fontSize = 10;	int xOffset = 0;	int yOffset = 0;	/*.................................................................................................................*/	public boolean startJob(String arguments, Object condition, boolean hiredByName){		extras = new Vector();		names = new ListableVector();		if (!MesquiteThread.isScripting())			names.addElement(new MesquiteString("consensusFrequency", "consensusFrequency"), false);		on = new MesquiteBoolean(false);		percentage = new MesquiteBoolean(false);		horizontal = new MesquiteBoolean(true);		MesquiteSubmenuSpec mss = addSubmenu(null, "Node-Associated Values");		addCheckMenuItemToSubmenu(null, mss, "Show Values On Tree", makeCommand("setOn",  this), on);		addItemToSubmenu(null, mss, "Digits...", makeCommand("setDigits",  this));		addCheckMenuItemToSubmenu(null, mss, "Show As Percentage", makeCommand("writeAsPercentage",  this), percentage);		addCheckMenuItemToSubmenu(null, mss, "Horizontal", makeCommand("toggleHorizontal",  this), horizontal);		addItemToSubmenu(null, mss, "Font Size...", makeCommand("setFontSize",  this));		addItemToSubmenu(null, mss, "Locations...", makeCommand("setOffset",  this));		return true;	} 	/*.................................................................................................................*/	public   TreeDisplayExtra createTreeDisplayExtra(TreeDisplay treeDisplay) {		NodeAssocValuesExtra newPj = new NodeAssocValuesExtra(this, treeDisplay);		extras.addElement(newPj);		return newPj;	}	/*.................................................................................................................*/	public String getName() {		return "Node-Associated Values";	}	/*.................................................................................................................*/	public Snapshot getSnapshot(MesquiteFile file) {		Snapshot temp = new Snapshot();		temp.addLine("setOn " + on.toOffOnString());		for (int i=0; i< names.size(); i++)			temp.addLine("toggleShow " + StringUtil.tokenize(((Listable)names.elementAt(i)).getName()));		temp.addLine("setDigits " + digits); 		temp.addLine("writeAsPercentage " + percentage.toOffOnString());		temp.addLine("toggleHorizontal " + horizontal.toOffOnString());		temp.addLine("setFontSize " + fontSize); 		temp.addLine("setOffset " + xOffset + "  " + yOffset); 		return temp;	}	MesquiteInteger pos = new MesquiteInteger();	/*.................................................................................................................*/	public Object doCommand(String commandName, String arguments, CommandChecker checker) {		if (checker.compare(this.getClass(), "Sets whether to show the node associated values", "[on or off]", commandName, "setOn")) {			boolean wasOn = on.getValue();			if (StringUtil.blank(arguments))				on.setValue(!on.getValue());			else				on.toggleValue(parser.getFirstToken(arguments));			if (!asked && on.getValue() && !wasOn && !MesquiteThread.isScripting()){				alert("To choose what associated values to display, touch on field away from the tree branches and choose from the drop-down menu");				asked = true;			}			for (int i =0; i<extras.size(); i++){				NodeAssocValuesExtra e = (NodeAssocValuesExtra)extras.elementAt(i);				e.setOn(on.getValue());			}		}		else if (checker.compare(this.getClass(), "Sets whether to write the values as percentage", "[on or off]", commandName, "writeAsPercentage")) {			if (StringUtil.blank(arguments))				percentage.setValue(!percentage.getValue());			else				percentage.toggleValue(parser.getFirstToken(arguments));			if (!MesquiteThread.isScripting()) parametersChanged();		}		else if (checker.compare(this.getClass(), "Sets whether to write the values horizontally", "[on or off]", commandName, "toggleHorizontal")) {			if (StringUtil.blank(arguments))				horizontal.setValue(!horizontal.getValue());			else				horizontal.toggleValue(parser.getFirstToken(arguments));			if (!MesquiteThread.isScripting()) parametersChanged();		}		else if (checker.compare(this.getClass(), "Sets how many digits are shown", "[number of digits]", commandName, "setDigits")) {			int newWidth= MesquiteInteger.fromFirstToken(arguments, pos);			if (!MesquiteInteger.isCombinable(newWidth))				newWidth = MesquiteInteger.queryInteger(containerOfModule(), "Set number of digits", "Number of digits to display for values on tree:", digits, 1, 24);			if (newWidth>0 && newWidth<24 && newWidth!=digits) {				digits = newWidth;				if (!MesquiteThread.isScripting()) parametersChanged();			}		}		else if (checker.compare(this.getClass(), "Sets offset of label from nodes", "[offsetX] [offsetY]", commandName, "setOffset")) {			int newX= MesquiteInteger.fromFirstToken(arguments, pos);			int newY= MesquiteInteger.fromString(arguments, pos);			if (!MesquiteInteger.isCombinable(newX)){				MesquiteBoolean answer = new MesquiteBoolean(false);				MesquiteInteger nX = new MesquiteInteger(xOffset);				MesquiteInteger nY = new MesquiteInteger(yOffset);				MesquiteInteger.queryTwoIntegers(containerOfModule(), "Location of Values", "X offset from node", "Y offset from node", answer, nX, nY,-200,200,-200,200, null);				if (!answer.getValue())					return null;				newX = nX.getValue();				newY = nY.getValue();			}			if (newX>-200 && newX <200 && newY>-200 && newY <200 && (newX!=xOffset ||   newY!=yOffset)) {				xOffset = newX;				yOffset = newY;				if (!MesquiteThread.isScripting()) parametersChanged();			}		}		else if (checker.compare(this.getClass(), "Sets font size", "[font size]", commandName, "setFontSize")) {			int newWidth= MesquiteInteger.fromFirstToken(arguments, pos);			if (!MesquiteInteger.isCombinable(newWidth))				newWidth = MesquiteInteger.queryInteger(containerOfModule(), "Set font size", "Font Size:", fontSize, 2, 96);			if (newWidth>1 && newWidth<96 && newWidth!=fontSize) {				fontSize = newWidth;				for (int i =0; i<extras.size(); i++){					NodeAssocValuesExtra e = (NodeAssocValuesExtra)extras.elementAt(i);					e.resetFontSize();				}				if (!MesquiteThread.isScripting()) parametersChanged();			}		}		else if (checker.compare(this.getClass(), "Sets whether to show a node associated values", "[on or off]", commandName, "toggleShow")) {			String name = parser.getFirstToken(arguments);			if (isShowing(name)){				names.removeElementAt(names.indexOfByName(name), false);			}			else				names.addElement(new MesquiteString(name, name), false);			for (int i =0; i<extras.size(); i++){				NodeAssocValuesExtra e = (NodeAssocValuesExtra)extras.elementAt(i);				e.update();			}		}		else			return  super.doCommand(commandName, arguments, checker);		return null;	}	/*.................................................................................................................*/	/** returns an explanation of what the module does.*/	public String getExplanation() {		return "Supplies a tool for tree windows to attach and view footnotes for branches." ;	}	public boolean isSubstantive(){		return false;	}   	 	boolean isShowing(String name){		boolean s = names.indexOfByName(name)>=0;		return s;	}}/* ======================================================================== */class NodeAssocValuesExtra extends TreeDisplayExtra  {	DrawTreeAssocDoubles assocDoublesModule;	MesquiteCommand taxonCommand, branchCommand;	boolean on;	Tree lastTree = null;//	StringInABox	public NodeAssocValuesExtra (DrawTreeAssocDoubles ownerModule, TreeDisplay treeDisplay) {		super(ownerModule, treeDisplay);		assocDoublesModule = ownerModule;		on = assocDoublesModule.on.getValue();		resetFontSize();	}	StringInABox box = new StringInABox( "", treeDisplay.getFont(),1500);	public void resetFontSize(){		Font f = treeDisplay.getFont();		box.setFont(new Font(f.getName(),f.getStyle(), assocDoublesModule.fontSize)); 	}	/*.................................................................................................................*/	public   void myDraw(Tree tree, int node, Graphics g, DoubleArray[] arrays) {		for (int d = tree.firstDaughterOfNode(node); tree.nodeExists(d); d = tree.nextSisterOfNode(d))			myDraw(tree, d, g, arrays);		for (int i=0; i<arrays.length; i++){			double d = arrays[i].getValue(node);			if (MesquiteDouble.isCombinable(d)){				if (assocDoublesModule.percentage.getValue())					d *= 100;				box.setColors(Color.black, Color.white);				box.setString(MesquiteDouble.toStringDigitsSpecified(d, assocDoublesModule.digits));				int x = treeDisplay.getTreeDrawing().x[node] + assocDoublesModule.xOffset;				int y = treeDisplay.getTreeDrawing().y[node] + assocDoublesModule.yOffset + i*assocDoublesModule.fontSize*2;				if (assocDoublesModule.horizontal.getValue())					box.draw(g,  x, y);				else					box.draw(g,  x, y, 0, 1500, treeDisplay, false);			}		}	}	/*.................................................................................................................*/	public   void drawOnTree(Tree tree, int node, Graphics g) {		if (!on)			return;		int num = tree.getNumberAssociatedDoubles();		int total = 0;		for (int i = 0; i< num; i++){			DoubleArray da = tree.getAssociatedDoubles(i);			if (assocDoublesModule.isShowing(da.getName()))				total++;		}		DoubleArray[] arrays = new DoubleArray[total];		int count = 0;		for (int i = 0; i< num; i++){			DoubleArray da = tree.getAssociatedDoubles(i);			if (assocDoublesModule.isShowing(da.getName()))				arrays[count++] = da;		}		myDraw(tree, node, g, arrays);	}	void update(){		treeDisplay.pleaseUpdate(false);	}	void setOn(boolean a){		on = a;		treeDisplay.pleaseUpdate(false);	}	/**return a text version of information at node*/	public String textAtNode(Tree tree, int node){		if (!on)			return null;		double d = getValue(tree, node);		return MesquiteDouble.toString(d);	}	MesquitePopup popup=null;	/*.................................................................................................................*/	void redoMenu(Associable tree) {		if (popup==null)			popup = new MesquitePopup(treeDisplay);		popup.removeAll();		popup.add(new MesquiteMenuItem("Display Node-Associated Values", null, null));		popup.add(new MesquiteMenuItem("-", null, null));		int num = tree.getNumberAssociatedDoubles();		if (num == 0)			popup.add(new MesquiteMenuItem("This Tree has no values associated with nodes", null, null));		else 			for (int i = 0; i< num; i++){				DoubleArray da = tree.getAssociatedDoubles(i);				MesquiteCommand mc = new MesquiteCommand("toggleShow", assocDoublesModule);				String selName = " ";				if (assocDoublesModule.isShowing(da.getName()))					selName = da.getName();				popup.add(new MesquiteCheckMenuItem(da.getName(), assocDoublesModule, mc, StringUtil.tokenize(da.getName()), new MesquiteString(selName )));			}		treeDisplay.add(popup);	}	public void cursorTouchField(Tree tree, Graphics g, int x, int y, int modifiers){		if (!on)			return;		redoMenu((Associable)tree);		popup.show(treeDisplay, x, y);	}	/*.................................................................................................................*/	public   void printOnTree(Tree tree, int drawnRoot, Graphics g) {		if (!on)			return;		drawOnTree(tree, drawnRoot, g); //should draw numbered footnotes!	}	/*.................................................................................................................*/	public   void setTree(Tree tree) {		lastTree = tree;	}	NameReference assocValueRef = NameReference.getNameReference("consensusFrequency");	double getValue(Tree tree, int node){		return tree.getAssociatedDouble(assocValueRef, node);	}	public void turnOff() {		assocDoublesModule.extras.removeElement(this);		super.turnOff();	}}