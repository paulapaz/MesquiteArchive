/* Mesquite (package mesquite.ornamental).  Copyright 1997-2007 W. Maddison and D. Maddison. Version 2.0, September 2007.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html) */package mesquite.tol.SearchToLTaxon;/*~~  */import java.util.*;import java.awt.*;import java.awt.image.*;import mesquite.lib.*;import mesquite.lib.duties.*;/* ======================================================================== */public class SearchToLTaxon extends TreeDisplayAssistantI {	public Vector extras;	/*.................................................................................................................*/	public boolean startJob(String arguments, Object condition, boolean hiredByName){		extras = new Vector();		return true;	} 	/*.................................................................................................................*/	public   TreeDisplayExtra createTreeDisplayExtra(TreeDisplay treeDisplay) {		SearchToLTaxonToolExtra newPj = new SearchToLTaxonToolExtra(this, treeDisplay);		extras.addElement(newPj);		return newPj;	}	/*.................................................................................................................*/	public Object doCommand(String commandName, String arguments, CommandChecker checker) { 		if (checker.compare(this.getClass(), "Turns on tools", null, commandName, "enableTools")) {			for (int i=0; i<extras.size(); i++){				SearchToLTaxonToolExtra e = (SearchToLTaxonToolExtra)extras.elementAt(i);				e.enableTool();			}		}		else			return  super.doCommand(commandName, arguments, checker);		return null;	}	/*.................................................................................................................*/	public String getName() {		return "Get Taxon's tree from ToLWeb.org";	}	/*.................................................................................................................*/	/** returns an explanation of what the module does.*/	public String getExplanation() {		return "Supplies a tool for tree windows that gets tree for taxon touched from ToLweb.org." ;	}	public boolean isSubstantive(){		return false;	}   	 }/* ======================================================================== */class SearchToLTaxonToolExtra extends TreeDisplayExtra implements Commandable  {	TreeTool tolTool;	SearchToLTaxon taxonToLModule;	MesquiteCommand taxonCommand;	NameReference tolLeavesNameRef = NameReference.getNameReference("ToLLeaves");	NameReference tolHasChildrenNameRef = NameReference.getNameReference("ToLHasChildren");	public SearchToLTaxonToolExtra (SearchToLTaxon ownerModule, TreeDisplay treeDisplay) {		super(ownerModule, treeDisplay);		taxonToLModule = ownerModule;		taxonCommand = MesquiteModule.makeCommand("goToToLTaxon",  this);	}	public void enableTool(){		if (tolTool == null){			tolTool = new TreeTool(this, "goToToLTaxon", ownerModule.getPath(), "ToL.gif", 4,0,"Go to ToL", "This tool."); //; hold down shift to enter a URL			tolTool.setTouchedTaxonCommand(taxonCommand);			if (ownerModule.containerOfModule() instanceof MesquiteWindow) {				((MesquiteWindow)ownerModule.containerOfModule()).addTool(tolTool);			}		}	}	/*.................................................................................................................*/	public   void drawOnTree(Tree tree, int drawnRoot, Graphics g) {	}	/*.................................................................................................................*/	public   void printOnTree(Tree tree, int drawnRoot, Graphics g) {		drawOnTree(tree, drawnRoot, g);	}	/*.................................................................................................................*/	public   void setTree(Tree tree) {	}	MesquiteInteger pos = new MesquiteInteger();	/*.................................................................................................................*/	public boolean hasDescendants(Taxa taxa, int taxonNumber){		boolean children = true;		MesquiteBoolean n = (MesquiteBoolean)taxa.getAssociatedObject(tolHasChildrenNameRef, taxonNumber);		if (n !=null)			if (n.getValue())				children=false;		return children;   // test if it is a leaf etc. 	}	/*.................................................................................................................*/	public Object doCommand(String commandName, String arguments, CommandChecker checker) { 		if (checker.compare(this.getClass(), "Gets tree for that taxon from ToLweb.org", "[taxon number][modifiers]", commandName, "goToToLTaxon")) {			Tree tree = treeDisplay.getTree();			int M = MesquiteInteger.fromFirstToken(arguments, pos);			if (M<0 || !MesquiteInteger.isCombinable(M) || M>=tree.getTaxa().getNumTaxa())				return null;			if (!hasDescendants(tree.getTaxa(), M)) 				return null;			while (ownerModule.getProject().developing)				;			String openName = ParseUtil.tokenize(tree.getTaxa().getTaxonName(M));			String commands = "newThread; getProjectID; Integer.id *It; tell Mesquite; getWindowAutoShow; String.was *It; windowAutoShow off; closeProjectByID *Integer.id; openGeneral #GetToLTree " + openName;			commands +=  "; ifNotExists It;  debug; showAbout; endIf; windowAutoShow *String.was; endTell;";			Puppeteer p = new Puppeteer(ownerModule);			MesquiteInteger pos = new MesquiteInteger(0);			p.execute(ownerModule.getFileCoordinator(), commands, pos, "", false);			ownerModule.iQuit();		}		return null;	}	public void turnOff() {		taxonToLModule.extras.removeElement(this);		super.turnOff();	}}