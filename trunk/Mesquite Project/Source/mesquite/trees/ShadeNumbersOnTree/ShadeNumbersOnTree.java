/* Mesquite source code.  Copyright 1997-2009 W. Maddison and D. Maddison.Version 2.7, August 2009.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)*/package mesquite.trees.ShadeNumbersOnTree;import java.util.*;import java.awt.*;import mesquite.lib.*;import mesquite.lib.characters.*;import mesquite.lib.duties.*;import mesquite.cont.lib.*;/* ======================================================================== */public class ShadeNumbersOnTree extends DisplayNumbersAtNodes {	TreeDisplay treeDisplay;	MesquiteBoolean showLabels;	MesquiteBoolean shadeInColor;	MesquiteBoolean backRect;	MesquiteBoolean useLogScale;	MesquiteColorTable colorTable = new ContColorTable(); 	Vector labellers;	/*.................................................................................................................*/	public boolean startJob(String arguments, Object condition, boolean hiredByName) {		showLabels = new MesquiteBoolean(false);		backRect = new MesquiteBoolean(false);		shadeInColor = new MesquiteBoolean(true);		useLogScale = new MesquiteBoolean(false);		MesquiteSubmenuSpec mss = addSubmenu(null, "Display");		addCheckMenuItemToSubmenu(null, mss, "Label nodes", makeCommand("toggleLabels", this), showLabels);		addCheckMenuItemToSubmenu(null, mss, "Labels with background", makeCommand("toggleRectangle", this), backRect);		addCheckMenuItemToSubmenu(null, mss, "Color shading", makeCommand("toggleColor", this), shadeInColor);		addCheckMenuItemToSubmenu(null, mss, "Log Shades When Grey", makeCommand("toggleLog", this), useLogScale);   		labellers = new Vector(); 		return true;	}	/*.................................................................................................................*/  	 public Snapshot getSnapshot(MesquiteFile file) {   	 	Snapshot temp = new Snapshot();  	 	temp.addLine("toggleLabels " + showLabels.toOffOnString());  	 	temp.addLine("toggleColor " + shadeInColor.toOffOnString());  	 	temp.addLine("toggleRectangle " + backRect.toOffOnString());  	 	temp.addLine("toggleLog " + useLogScale.toOffOnString());  	 	return temp;  	 }	/*.................................................................................................................*/    	 public Object doCommand(String commandName, String arguments, CommandChecker checker) {    	 	if (checker.compare(this.getClass(), "Sets whether or not nodes are labeled with text", "[on = labeled; off]", commandName, "toggleLabels")) {    	 		showLabels.toggleValue(parser.getFirstToken(arguments));			parametersChanged();    	 	}    	 	else if (checker.compare(this.getClass(), "Sets whether shadings are shown in color or grayscale", "[on = color; off]", commandName, "toggleColor")) {    	 		shadeInColor.toggleValue(parser.getFirstToken(arguments));			parametersChanged();    	 	}    	 	else if (checker.compare(this.getClass(), "Sets whether labels are given a background rectangle", "[on = rectangle; off]", commandName, "toggleRectangle")) {    	 		backRect.toggleValue(parser.getFirstToken(arguments));			parametersChanged();    	 	}    	 	else if (checker.compare(this.getClass(), "Sets whether grayscale is shown with log or linear", "[on = color; off]", commandName, "toggleLog")) {    	 		useLogScale.toggleValue(parser.getFirstToken(arguments));			parametersChanged();    	 	}    	 	else    	 		return  super.doCommand(commandName, arguments, checker);		return null;   	 }	/*.................................................................................................................*/	/** returns whether this module is requesting to appear as a primary choice */   	public boolean requestPrimaryChoice(){   		return true;     	}	/*.................................................................................................................*/   	 public boolean isSubstantive(){   	 	return true;   	 }   	 public boolean isPrerelease(){   	 	return false;   	 }	/*.................................................................................................................*/	public   TreeDecorator createTreeDecorator(TreeDisplay treeDisplay, TreeDisplayExtra ownerExtra) {		ShadeNumbersDecorator newShadeler = new ShadeNumbersDecorator(this, treeDisplay, ownerExtra);		labellers.addElement(newShadeler);		return newShadeler;	} 		/*.................................................................................................................*/ 	public void endJob() {		Enumeration e = labellers.elements();		while (e.hasMoreElements()) {			Object obj = e.nextElement();			if (obj instanceof ShadeNumbersDecorator) {				ShadeNumbersDecorator tCO = (ShadeNumbersDecorator)obj;	 			tCO.turnOff();	 		}		} 		super.endJob();   	 }	/*.................................................................................................................*/    	 public String getName() {		return "Shade numbers";   	 }   	 	/*.................................................................................................................*/   	  	/** returns an explanation of what the module does.*/ 	public String getExplanation() { 		return "A module that displays numbers at tree nodes using labels.  This is a display-only module," 		+ " and would be hired by another module that organizes assigning numbers to the nodes." ;   	 }	/*.................................................................................................................*/   	 }/* ======================================================================== */class ShadeNumbersDecorator extends TreeDecorator { 	ShadeNumbersOnTree ownerModule;	ColorDistribution colors;	boolean shadeInternalNodes = true;	boolean shadeTerminalNodes = true;	public ShadeNumbersDecorator (ShadeNumbersOnTree ownerModule, TreeDisplay treeDisplay, TreeDisplayExtra ownerExtra) {		super(treeDisplay, ownerExtra);		this.ownerModule=ownerModule; 		colors = new ColorDistribution();	}	/*.................................................................................................................*/	private void writeAtNode(NumberArray numbers,Graphics g, FontMetrics fm, int N,  Tree tree) {		for (int d = tree.firstDaughterOfNode(N); tree.nodeExists(d); d = tree.nextSisterOfNode(d))				writeAtNode(numbers, g, fm, d, tree);		int nodeX = treeDisplay.getTreeDrawing().x[N];		int nodeY = treeDisplay.getTreeDrawing().y[N];		if (treeDisplay.getOrientation() == treeDisplay.UP) {			nodeY+=10;			//nodeX+=10;		}		else if (treeDisplay.getOrientation() == treeDisplay.DOWN) {			nodeY-=10;			//nodeX+=10;		}		else if (treeDisplay.getOrientation() == treeDisplay.RIGHT) {			//nodeY=20;			nodeX-=10;		}		else if (treeDisplay.getOrientation() == treeDisplay.LEFT) {			//nodeY+=20;			nodeX+=10;		}		if (!numbers.isUnassigned(N)) {			String s = numbers.toString(N);			if (ownerModule.backRect.getValue()){				int w = fm.stringWidth(s) + 4;				int h = fm.getMaxAscent()+fm.getMaxDescent(); //numbers wouldn't actually reach max descent				Color c = g.getColor();				g.setColor(Color.white);				g.fillRect(nodeX, nodeY, w, h);				g.setColor(Color.blue);				g.drawRect(nodeX, nodeY, w, h);				g.drawString(s, nodeX +2, nodeY + fm.getMaxAscent()+1);				if (c!=null) g.setColor(c);			}			else				StringUtil.highlightString(g, s, nodeX, nodeY, Color.blue, Color.white);		}	}	/*.................................................................................................................*/	private void shadeNode(int N, Tree tree, NumberArray numbers, MesquiteNumber min, MesquiteNumber max, Graphics g) {		for (int d = tree.firstDaughterOfNode(N); tree.nodeExists(d); d = tree.nextSisterOfNode(d))				shadeNode(d, tree, numbers, min, max, g);						if ((tree.nodeIsInternal(N) && shadeInternalNodes) || (tree.nodeIsTerminal(N) && shadeTerminalNodes)) {			Color c;			if (ownerModule.shadeInColor.getValue())				c = ownerModule.colorTable.getColor(numbers.getDouble(N), min.getDoubleValue(), max.getDoubleValue()); 			else 				c = MesquiteColorTable.getGrayScale(numbers.getDouble(N), min.getDoubleValue(), max.getDoubleValue(), ownerModule.useLogScale.getValue()); 			colors.initialize();			colors.setColor(0, c);			treeDisplay.getTreeDrawing().fillBranchWithColors(tree,  N, colors, g);			g.setColor(Color.black);		}	}	Color getColor(double d, MesquiteNumber min, MesquiteNumber max){		if (ownerModule.shadeInColor.getValue())			return ownerModule.colorTable.getColor(d, min.getDoubleValue(), max.getDoubleValue()); 		else			return MesquiteColorTable.getGrayScale(d, min.getDoubleValue(), max.getDoubleValue(), ownerModule.useLogScale.getValue()); 	}	public ColorRecord[] getLegendColorRecords(){		if (min.isCombinable() && max.isCombinable()) {			ColorRecord[] records = new ColorRecord[10];			records[0] = new ColorRecord(getColor(min.getDoubleValue(), min, max), MesquiteDouble.toString(min.getDoubleValue())); 			for (int i=1; i<9; i++){				double d = (max.getDoubleValue() - min.getDoubleValue())/10.0*i + min.getDoubleValue();				records[i] = new ColorRecord(getColor(d, min, max), MesquiteDouble.toString(d)); 			}			records[9] = new ColorRecord(getColor(max.getDoubleValue(), min, max), MesquiteDouble.toString(max.getDoubleValue())); 			return records;		}		else 			return new ColorRecord[] {new ColorRecord(MesquiteColorTable.getGrayScale(MesquiteDouble.unassigned, min.getDoubleValue(), max.getDoubleValue(), false), "Unassigned")};	}	/*.................................................................................................................*/	MesquiteNumber min = new MesquiteNumber();	MesquiteNumber max = new MesquiteNumber();	public   void drawOnTree(Tree tree, int drawnRoot, Object obj, Object obj2, Object obj3, Graphics g) {					if (obj instanceof NumberArray) {				if (treeDisplay!=null && tree!=null && obj!=null) {					NumberArray numbers = (NumberArray)obj;					numbers.placeMinimumValue(min);					numbers.placeMaximumValue(max);					if (treeDisplay!=null && tree!=null) {						shadeNode(drawnRoot, tree, numbers, min, max, g);						if (ownerModule.showLabels.getValue()) {							writeAtNode(numbers, g, g.getFontMetrics(), drawnRoot,tree);						}					}					else						MesquiteMessage.warnProgrammer("Shade states -- null tree display, tree");				}				else					System.out.println("shade on tree  values//null");			}			else				System.out.println("Error: Shade Numbers needs number array");			}		/*.................................................................................................................*/	public void turnOff() {	}}