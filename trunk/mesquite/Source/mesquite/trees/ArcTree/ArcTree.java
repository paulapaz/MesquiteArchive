/* Mesquite source code.  Copyright 1997-2005 W. Maddison and D. Maddison. Version 1.06, August 2005.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite is distributed under the terms of the GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)*/package mesquite.trees.ArcTree;import java.util.*;import java.awt.*;import mesquite.lib.*;import mesquite.lib.duties.*;import java.awt.geom.*;/* ======================================================================== */public class ArcTree extends DrawTree {	NodeLocsVH nodeLocsTask;	MesquiteCommand edgeWidthCommand;	MesquiteString orientationName;	Vector drawings;	int oldEdgeWidth = 8;	int ornt;	MesquiteString nodeLocsName;	/*.................................................................................................................*/	public boolean startJob(String arguments, Object condition, CommandRecord commandRec, boolean hiredByName) {		nodeLocsTask= (NodeLocsVH)hireNamedEmployee(commandRec, NodeLocsVH.class, "#NodeLocsStandard");		if (nodeLocsTask == null)			return sorry(commandRec, getName() + " couldn't start because no node locator module was obtained");		nodeLocsName = new MesquiteString(nodeLocsTask.getName());		if (numModulesAvailable(NodeLocsVH.class)>1){			MesquiteSubmenuSpec mss = addSubmenu(null, "Node Locations Calculator", makeCommand("setNodeLocs", this), NodeLocsVH.class);			mss.setSelected(nodeLocsName);		}		drawings = new Vector();		orientationName = new MesquiteString("Up");		ornt = TreeDisplay.UP; 		MesquiteSubmenuSpec orientationSubmenu = addSubmenu(null, "Orientation"); 		orientationSubmenu.setSelected(orientationName); 		addItemToSubmenu(null, orientationSubmenu, "Up", makeCommand("orientUp",  this)); 		addItemToSubmenu(null, orientationSubmenu, "Right", makeCommand("orientRight",  this)); 		addItemToSubmenu(null, orientationSubmenu, "Down", makeCommand("orientDown",  this)); 		addItemToSubmenu(null, orientationSubmenu, "Left", makeCommand("orientLeft",  this)); 		addMenuItem( "Line Width...", makeCommand("setEdgeWidth",  this)); 		return true;		 	 }  	  	 public void employeeQuit(MesquiteModule m){  	 	iQuit();  	 }	public   TreeDrawing createTreeDrawing(TreeDisplay treeDisplay, int numTaxa) {		ArcTreeDrawing treeDrawing =  new ArcTreeDrawing (treeDisplay, numTaxa, this);		if (legalOrientation(treeDisplay.getOrientation())){			orientationName.setValue(orient(treeDisplay.getOrientation()));			ornt = treeDisplay.getOrientation();		}		else			treeDisplay.setOrientation(ornt);		drawings.addElement(treeDrawing);		return treeDrawing;	}	public boolean legalOrientation (int orientation){		return (orientation == TreeDisplay.UP || orientation == TreeDisplay.DOWN || orientation == TreeDisplay.RIGHT || orientation == TreeDisplay.LEFT);	}		public String orient (int orientation){		if (orientation == TreeDisplay.UP)			return "Up";		else if (orientation == TreeDisplay.DOWN)			return "Down";		else if (orientation == TreeDisplay.RIGHT)			return "Right";		else if (orientation == TreeDisplay.LEFT)			return "Left";		else return "other";	}	/*.................................................................................................................*/	/*.................................................................................................................*/  	 public Snapshot getSnapshot(MesquiteFile file) {    	 	Snapshot temp = new Snapshot();  	 	temp.addLine("setNodeLocs", nodeLocsTask); 	 	temp.addLine("setEdgeWidth " + oldEdgeWidth);   	 	if (ornt== TreeDisplay.UP)  	 		temp.addLine("orientUp");   	 	else if (ornt== TreeDisplay.DOWN)  	 		temp.addLine("orientDown");   	 	else if (ornt== TreeDisplay.LEFT)  	 		temp.addLine("orientLeft");   	 	else if (ornt== TreeDisplay.RIGHT)  	 		temp.addLine("orientRight");   	 	return temp;  	 }	MesquiteInteger pos = new MesquiteInteger();	/*.................................................................................................................*/    	 public Object doCommand(String commandName, String arguments, CommandRecord commandRec, CommandChecker checker) {		if (checker.compare(this.getClass(), "Sets the node locations calculator", "[name of module]", commandName, "setNodeLocs")) {			NodeLocsVH temp = (NodeLocsVH)replaceEmployee(commandRec, NodeLocsVH.class, arguments, "Node Locations Calculator", nodeLocsTask);			if (temp != null) {				nodeLocsTask = temp;				nodeLocsName.setValue(nodeLocsTask.getName());				parametersChanged(null, commandRec);			}			return nodeLocsTask;		}		else 	if (checker.compare(this.getClass(), "Sets the width of lines for drawing the tree", "[width in pixels]", commandName, "setEdgeWidth")) {	    	 						int newWidth= MesquiteInteger.fromFirstToken(arguments, pos);				if (!MesquiteInteger.isCombinable(newWidth))					newWidth = MesquiteInteger.queryInteger(containerOfModule(), "Set edge width", "Edge Width:", oldEdgeWidth, 1, 99);	    	 		if (newWidth>0 && newWidth<100 && newWidth!=oldEdgeWidth) {	    	 			oldEdgeWidth=newWidth;					Enumeration e = drawings.elements();					while (e.hasMoreElements()) {						Object obj = e.nextElement();						ArcTreeDrawing treeDrawing = (ArcTreeDrawing)obj;	    	 				treeDrawing.setEdgeWidth(newWidth);	    					treeDrawing.treeDisplay.setMinimumTaxonNameDistance(newWidth, 6); 	    				}					if (commandRec != null && !commandRec.scripting()) parametersChanged(null, commandRec);	    	 		}	    	 			    	 	}	    	 	else if (checker.compare(this.getClass(), "Returns the employee module that assigns node locations", null, commandName, "getNodeLocsEmployee")) {	    	 		return nodeLocsTask;	    	 	}    	 	else if (checker.compare(this.getClass(), "Orients the tree drawing so that the terminal taxa are on top", null, commandName, "orientUp")) {			Enumeration e = drawings.elements();			ornt = 0;			while (e.hasMoreElements()) {				Object obj = e.nextElement();				ArcTreeDrawing treeDrawing = (ArcTreeDrawing)obj;		    	 	treeDrawing.reorient(TreeDisplay.UP);			    	 ornt = treeDrawing.treeDisplay.getOrientation();		    	 }			orientationName.setValue(orient(ornt));			parametersChanged(null, commandRec);    	 	}    	 	else if (checker.compare(this.getClass(), "Orients the tree drawing so that the terminal taxa are at the bottom", null, commandName, "orientDown")) {			Enumeration e = drawings.elements();			ornt = 0;			while (e.hasMoreElements()) {				Object obj = e.nextElement();				ArcTreeDrawing treeDrawing = (ArcTreeDrawing)obj;		    	 	treeDrawing.reorient(TreeDisplay.DOWN);			    	 ornt = treeDrawing.treeDisplay.getOrientation();		    	 }			orientationName.setValue(orient(ornt));			parametersChanged(null, commandRec);    	 	}    	 	else if (checker.compare(this.getClass(), "Orients the tree drawing so that the terminal taxa are at right", null, commandName, "orientRight")) {			Enumeration e = drawings.elements();			ornt = 0;			while (e.hasMoreElements()) {				Object obj = e.nextElement();				ArcTreeDrawing treeDrawing = (ArcTreeDrawing)obj;		    	 	treeDrawing.reorient(TreeDisplay.RIGHT);			    	 ornt = treeDrawing.treeDisplay.getOrientation();		    	 }			orientationName.setValue(orient(ornt));			parametersChanged(null, commandRec);    	 	}    	 	else if (checker.compare(this.getClass(), "Orients the tree drawing so that the terminal taxa are at left", null, commandName, "orientLeft")) {			Enumeration e = drawings.elements();			ornt = 0;			while (e.hasMoreElements()) {				Object obj = e.nextElement();				ArcTreeDrawing treeDrawing = (ArcTreeDrawing)obj;		    	 	treeDrawing.reorient(TreeDisplay.LEFT);			    	 ornt = treeDrawing.treeDisplay.getOrientation();		    	 }			orientationName.setValue(orient(ornt));			parametersChanged(null, commandRec);    	 	}    	 	else { 			return super.doCommand(commandName, arguments, commandRec, checker); 		}		return null;   	 }	/*.................................................................................................................*/    	 public String getName() {		return "Curvogram";   	 }	/*.................................................................................................................*/	/** returns whether this module is requesting to appear as a primary choice */   	public boolean requestPrimaryChoice(){   		return true;     	}   	 	/*.................................................................................................................*/   	  	/** returns an explanation of what the module does.*/ 	public String getExplanation() { 		return "Draws trees with curved branches (as PHYLIP's 'Curvogram')." ;   	 }	/*.................................................................................................................*/}/* ======================================================================== */class ArcTreeDrawing extends TreeDrawing  {	private int lastleft;	private int taxspacing;	public int highlightedBranch, branchFrom;	public int xFrom, yFrom, xTo, yTo;	public ArcTree ownerModule;	public int edgewidth = 8;	public int preferredEdgeWidth = 8;	int oldNumTaxa = 0; 	public static final int inset=2;	private boolean ready=false;	BasicStroke defaultStroke;	private int foundBranch;		public ArcTreeDrawing (TreeDisplay treeDisplay, int numTaxa, ArcTree ownerModule) {		super(treeDisplay, MesquiteTree.standardNumNodeSpaces(numTaxa));	    	treeDisplay.setMinimumTaxonNameDistance(edgewidth, 6); //better if only did this if tracing on		this.ownerModule = ownerModule;		this.treeDisplay = treeDisplay;		oldNumTaxa = numTaxa;		try{			defaultStroke = new BasicStroke();		}		catch (Throwable t){		}		ready = true;	}	/*_________________________________________________*/	private void calculateLines(Tree tree, int node) {		for (int d = tree.firstDaughterOfNode(node); tree.nodeExists(d); d = tree.nextSisterOfNode(d))			calculateLines( tree, d);		lineTipY[node]=y[node];		lineTipX[node]=x[node];		lineBaseY[node]=y[tree.motherOfNode(node)];		lineBaseX[node]=x[tree.motherOfNode(node)];	}	/*_________________________________________________*/	private void calcBranchStuff(Tree tree, int drawnRoot, CommandRecord commandRec) {		if (ownerModule==null) {MesquiteTrunk.mesquiteTrunk.logln("ownerModule null"); return;}		if (ownerModule.nodeLocsTask==null) {ownerModule.logln("nodelocs task null"); return;}		if (treeDisplay==null) {ownerModule.logln("treeDisplay null"); return;}		if (tree==null) { ownerModule.logln("tree null"); return;}				ownerModule.nodeLocsTask.calculateNodeLocs(treeDisplay,  tree, drawnRoot,  treeDisplay.getField(), commandRec); //Graphics g removed as parameter May 02		calculateLines(tree, drawnRoot);		edgewidth = preferredEdgeWidth;		if (treeDisplay.getTaxonSpacing()<edgewidth+2) {			edgewidth= treeDisplay.getTaxonSpacing()-2;			if (edgewidth<2)				edgewidth=2;		}	}		/*_________________________________________________*/	/** Draw highlight for branch node with current color of graphics context */	public void drawHighlight(Tree tree, int node, Graphics g, boolean flip){		Color tC = g.getColor();		if (flip)			g.setColor(Color.yellow);		else			g.setColor(Color.blue);					if (treeDisplay.getOrientation()==TreeDisplay.DOWN || treeDisplay.getOrientation()==TreeDisplay.UP){			g.fillOval(x[node]-4, y[node], 8, 8);		}		else {			g.fillOval(x[node], y[node]-4, 8,8);		}		g.setColor(tC);	}	/*_________________________________________________*/	private   void drawOneBranch(Tree tree, Graphics g, int node, int start, int width, int adj) {		if (tree.nodeExists(node)) {			int nM = tree.motherOfNode(node);			int xN=x[node];			int xnM = x[nM];			int yN =y[node];			int ynM = y[nM];			boolean done = false;			try{				if (MesquiteWindow.Java2Davailable && g instanceof Graphics2D) {					if (treeDisplay.getOrientation()==TreeDisplay.UP) {						if (xnM>xN){ //leans left							xN += width/2+start;							xnM += width/2;							ynM += edgewidth - width/2 -start;							yN += width/2;						}						else {							if (start>1)								start++;							xN += width/2+start;							xnM += width/2;							ynM += width/2 +start;							yN += width/2;						}											}					else if (treeDisplay.getOrientation()==TreeDisplay.DOWN){ //����						if (xnM>xN){ //leans left							xN += width/2+start;							xnM += width/2;							ynM -= edgewidth - width/2 -start;							yN -= width/2;						}						else {							if (start>1)								start++;							xN += width/2+start;							xnM += width/2;							ynM -= width/2 +start;							yN -= width/2;						}					}					else  if (treeDisplay.getOrientation()==TreeDisplay.RIGHT) {						if (ynM>yN){ //leans left							yN += width/2+start;							ynM += width/2;							xnM -= edgewidth - width/2 -start;							xN -= width/2;						}						else {							if (start>1)								start++;							yN += width/2+start;							ynM += width/2;							xnM -= width/2 +start;							xN -= width/2;						}											}					else  if (treeDisplay.getOrientation()==TreeDisplay.LEFT){  //����						if (ynM>yN){ //leans right							yN += width/2+start;							ynM += width/2;							xnM += edgewidth - width/2 -start;							xN += width/2;						}						else {							if (start>1)								start++;							yN += width/2+start;							ynM += width/2;							xnM += width/2 +start;							xN += width/2;						}					}					Arc2D.Double arc = null;					if (treeDisplay.getOrientation()==TreeDisplay.UP) {						if (xnM>xN) {  //leans left							//g.setColor(Color.blue);							arc = new Arc2D.Double(xN, yN-(ynM-yN), (xnM-xN)*2,  (ynM - yN)*2, 180, 90, Arc2D.OPEN); // left							//g.drawRect(xN, yN-(ynM-yN), (xnM-xN)*2,  (ynM - yN)*2);						}						else {							//g.setColor(Color.green);							arc = new Arc2D.Double(xnM-(xN-xnM), yN - (ynM - yN), (xN-xnM)*2,  (ynM - yN)*2, 0, -90, Arc2D.OPEN); //right							//g.drawRect(xnM-(xN-xnM), yN - (ynM - yN), (xN-xnM)*2,  (ynM - yN)*2);						}					}					else if (treeDisplay.getOrientation()==TreeDisplay.DOWN){//����						if (xnM>xN) {  //leans right							//g.setColor(Color.blue);							arc = new Arc2D.Double(xN, ynM, (xnM-xN)*2,  -(ynM - yN)*2, 90, 90, Arc2D.OPEN); // left							//g.drawRect(xN, yN-(ynM-yN), (xnM-xN)*2,  (ynM - yN)*2);						}						else {							//g.setColor(Color.green);							arc = new Arc2D.Double(xnM-(xN-xnM), ynM, (xN-xnM)*2,  -(ynM - yN)*2, 0, 90, Arc2D.OPEN); //right							//g.drawRect(xnM-(xN-xnM), yN - (ynM - yN), (xN-xnM)*2,  (ynM - yN)*2);						}					}					else  if (treeDisplay.getOrientation()==TreeDisplay.RIGHT) {						if (ynM>yN) { //leans left							//g.setColor(Color.blue);							arc = new Arc2D.Double(xnM, yN, (xN-xnM)*2,  (ynM - yN)*2, 90, 90, Arc2D.OPEN); // left							//g.drawRect(xN, yN-(ynM-yN), (xnM-xN)*2,  (ynM - yN)*2);						}						else {							//g.setColor(Color.green);							arc = new Arc2D.Double(xnM, ynM + (ynM - yN), (xN-xnM)*2,  -(ynM - yN)*2, 180,90, Arc2D.OPEN); //right							//g.drawRect(xnM-(xN-xnM), yN - (ynM - yN), (xN-xnM)*2,  (ynM - yN)*2);						}					}					else  if (treeDisplay.getOrientation()==TreeDisplay.LEFT){ //����						if (ynM>yN) { //leans right							//g.setColor(Color.blue);							arc = new Arc2D.Double(xN - (xnM-xN), yN, -(xN-xnM)*2,  (ynM - yN)*2, 0, 90, Arc2D.OPEN); 							//g.drawRect(xN, yN-(ynM-yN), (xnM-xN)*2,  (ynM - yN)*2);						}						else {							//g.setColor(Color.green);							arc = new Arc2D.Double(xN - (xnM-xN), ynM + (ynM - yN), -(xN-xnM)*2, - (ynM - yN)*2, 0,-90, Arc2D.OPEN); 							//g.drawRect(xnM-(xN-xnM), yN - (ynM - yN), (xN-xnM)*2,  (ynM - yN)*2);						}					}					if (arc!=null) {						BasicStroke wideStroke = new BasicStroke(width);						Graphics2D g2 = (Graphics2D)g;						g2.setStroke(wideStroke);						g2.draw(arc);						done  = true;						g2.setStroke(defaultStroke);					}				}								}			catch (Throwable t){			}			if (!done){				if (treeDisplay.getOrientation()==TreeDisplay.UP) {					if (xnM > xN)  ynM += edgewidth-1-start;					else ynM+=start;				}				else if (treeDisplay.getOrientation()==TreeDisplay.DOWN){ //����					if (xnM > xN)  ynM -= edgewidth-1-start;					else ynM-=start;					xnM +=adj; //why this adj is needed, I don't know.  But it seems to work.					xN += adj;				}				else  if (treeDisplay.getOrientation()==TreeDisplay.RIGHT) {					if (ynM > yN)  xnM -= edgewidth-1-start;					else xnM-=start;				}				else  if (treeDisplay.getOrientation()==TreeDisplay.LEFT){  //����					if (ynM > yN) xnM += edgewidth-1-start;					else xnM+=start;					ynM +=adj;//why this adj is needed, I don't know.  But it seems to work.					yN += adj;				}				else					System.out.println("Error: wrong tree orientation in Arc Tree");				for (int i=0; i<width; i++) {					if (treeDisplay.getOrientation()==TreeDisplay.UP) {						if (xnM>xN) {							g.drawArc(xN + start, yN - (ynM - yN), (xnM-xN)*2,  (ynM - yN)*2, 180, 90); // left							ynM--;						}						else {							g.drawArc(xnM-(xN-xnM) + start, yN - (ynM - yN), (xN-xnM)*2,  (ynM - yN)*2, 0, -90); //right							ynM++; //** start off -						}						xN++;					}					else if (treeDisplay.getOrientation()==TreeDisplay.DOWN){//����						if (xnM>xN) {							g.drawArc(xN - start,ynM, (xnM-xN)*2,  (yN -ynM)*2, 90, 90); //right							ynM++;						}						else {							g.drawArc(xnM-(xN-xnM) - start,ynM, (xN-xnM)*2,   (yN -ynM)*2, 0, 90); //left 							ynM--;  //**start off + edgewidth						}						xN++;					}					else  if (treeDisplay.getOrientation()==TreeDisplay.RIGHT) {						if (ynM>yN) {							g.drawArc(xnM, yN + start, (xN-xnM)*2,  (ynM - yN)*2, 90, 90);  //left							xnM++;						}						else {							g.drawArc(xnM,ynM - (yN -ynM) + start, (xN-xnM)*2,  (yN -ynM)*2, 180, 90);  //right 							xnM--;  //start off + edgewidth						}						yN++;					}					else  if (treeDisplay.getOrientation()==TreeDisplay.LEFT){ //����						if (ynM>yN) {							g.drawArc(xN - (xnM-xN), yN - start, (xnM-xN)*2,  (ynM - yN)*2, 0, 90);  //right							xnM--;						}						else {							g.drawArc(xN - (xnM-xN),ynM - (yN -ynM) - start, (xnM-xN)*2,  (yN -ynM)*2, 0, -90);  //left 							xnM++;  //start off - edgewidth						}						yN++;					}									}			}		}	}	/*_________________________________________________*/	private boolean inBranch(Tree tree, int node, int h, int v) {		if (tree.nodeExists(node)) {			int nM = tree.motherOfNode(node);			int xN=x[node];			int xnM = x[nM];			int yN =y[node];			int ynM = y[nM];			double centerX, centerY,axisX, axisY;			centerX =  centerY =  axisX =   axisY =0;			if (treeDisplay.getOrientation()==TreeDisplay.UP) {				if (xnM>xN) {					if (h< xN || h>xnM)						return false;					centerX = xnM;					centerY = yN;					axisX =  xnM-xN;					axisY =ynM + edgewidth - yN;				}				else {					if (h< xnM || h>xN+ edgewidth)						return false;					centerX = xnM;					centerY = yN;					axisX =  xN-xnM+ edgewidth;					axisY =ynM + edgewidth - yN;				}				if (v < yN || v> ynM + edgewidth)					return false;			}			else if (treeDisplay.getOrientation()==TreeDisplay.DOWN){				if (xnM>xN) {					if (h< xN || h>xnM)						return false;					centerX = xnM;					centerY = yN;					axisX =  xnM-xN;					axisY =yN - ynM + edgewidth;				}				else {					if (h< xnM || h>xN+ edgewidth)						return false;					centerX = xnM;					centerY = yN;					axisX =  xN-xnM+ edgewidth;					axisY =yN - ynM + edgewidth;				}				if (v < ynM || v> yN)					return false;			}			else  if (treeDisplay.getOrientation()==TreeDisplay.RIGHT) {				if (ynM>yN) {					if (v< yN || v>ynM)						return false;					centerX = xN;					centerY = ynM;					axisX =  xN-xnM+ edgewidth;					axisY =ynM - yN;				}				else {					if (v< ynM || v>yN+ edgewidth)						return false;					centerX = xN;					centerY = ynM;					axisX =  xN-xnM+ edgewidth;					axisY =yN - ynM+ edgewidth;				}				if (h < xnM-edgewidth || h> xN)					return false;			}			else  if (treeDisplay.getOrientation()==TreeDisplay.LEFT){ 				if (ynM>yN) {					if (v< yN- edgewidth || v>ynM)						return false;					centerX = xN;					centerY = ynM;					axisX =  xnM-xN+ edgewidth;					axisY =ynM - yN+ edgewidth;				}				else {					if (v< ynM || v>yN)						return false;					centerX = xN;					centerY = ynM;					axisX =  xnM-xN+ edgewidth;					axisY =yN - ynM;				}				if (h < xN || h> xnM+ edgewidth)					return false;			}						if ((h-centerX)*(h-centerX)/(axisX*axisX) + (v-centerY)*(v-centerY)/(axisY*axisY) <= 1.0)  //inside outer edge				if ((h-centerX)*(h-centerX)/((axisX-edgewidth)*(axisX-edgewidth)) + (v-centerY)*(v-centerY)/((axisY-edgewidth)*(axisY-edgewidth)) > 1.0) //outside inner edge					return true;		}		return false;	}	/*_________________________________________________*/	private   void drawClade(Tree tree, Graphics g, int node) {		if (tree.nodeExists(node)) {			g.setColor(treeDisplay.getBranchColor(node));			if (tree.getRooted() || tree.getRoot()!=node)				drawOneBranch(tree, g, node, 0, edgewidth,0);			int thisSister = tree.firstDaughterOfNode(node);			while (tree.nodeExists(thisSister)) {				drawClade( tree, g, thisSister);				thisSister = tree.nextSisterOfNode(thisSister);			}		}	}	/*_________________________________________________*/	public   void drawTree(Tree tree, int drawnRoot, Graphics g) {	        if (MesquiteTree.OK(tree)) {	        	if (tree.getNumNodeSpaces()!=numNodes)	        		resetNumNodes(tree.getNumNodeSpaces());	        	g.setColor(treeDisplay.branchColor);	       	 	drawClade(tree, g, drawnRoot);  	       	 }	   }	/*_________________________________________________*/	public   void recalculatePositions(Tree tree, CommandRecord commandRec) {	        if (MesquiteTree.OK(tree)) {	        	if (tree.getNumNodeSpaces()!=numNodes)	        		resetNumNodes(tree.getNumNodeSpaces());	        	if (!tree.nodeExists(getDrawnRoot()))	        		setDrawnRoot(tree.getRoot());	        	calcBranchStuff(tree, getDrawnRoot(), commandRec);		}	}		/*_________________________________________________*/	public  void fillTerminalBox(Tree tree, int node, Graphics g) {		Rectangle box;		if (treeDisplay.getOrientation()==TreeDisplay.UP) {			box = new Rectangle(x[node], y[node]-edgewidth/2-2, edgewidth, edgewidth);			g.fillArc(box.x, box.y, box.width, box.height, 0, 180);	        	g.setColor(treeDisplay.getBranchColor(node));			g.drawArc(box.x, box.y, box.width, box.height, 0, 180);			g.drawLine(box.x, box.y+ edgewidth/2, box.x+edgewidth,  box.y+ edgewidth/2);		}		else if (treeDisplay.getOrientation()==TreeDisplay.DOWN) {			box = new Rectangle(x[node], y[node] + 2, edgewidth, edgewidth);			g.fillArc(box.x, box.y -  box.height/2, box.width, box.height, 180, 180);	        	g.setColor(treeDisplay.getBranchColor(node));			g.drawArc(box.x, box.y -  box.height/2, box.width, box.height, 180, 180);			g.drawLine(box.x, box.y , box.x+edgewidth,  box.y);		}		else  if (treeDisplay.getOrientation()==TreeDisplay.RIGHT) {			box = new Rectangle(x[node] + 2, y[node], edgewidth, edgewidth);			g.fillArc(box.x- box.width/2, box.y, box.width, box.height, 270, 180);	        	g.setColor(treeDisplay.getBranchColor(node));			g.drawArc(box.x- box.width/2, box.y, box.width, box.height, 270, 180);			g.drawLine(box.x, box.y, box.x ,  box.y+edgewidth);		}		else  if (treeDisplay.getOrientation()==TreeDisplay.LEFT) {			box = new Rectangle(x[node]-edgewidth/2-2, y[node], edgewidth, edgewidth);			g.fillArc(box.x, box.y, box.width, box.height, 90, 180);	        	g.setColor(treeDisplay.getBranchColor(node));			g.drawArc(box.x, box.y, box.width, box.height, 90, 180);			g.drawLine(box.x+edgewidth/2, box.y, box.x+edgewidth/2,  box.y+edgewidth);		}		else {			box = new Rectangle(x[node], y[node], edgewidth, edgewidth);			g.fillArc(box.x, box.y, box.width, box.height, 0, 360);	        	g.setColor(treeDisplay.getBranchColor(node));			g.drawArc(box.x, box.y, box.width, box.height, 0, 360);		}	}	/*_________________________________________________*/	public  void fillTerminalBoxWithColors(Tree tree, int node, ColorDistribution colors, Graphics g){		Rectangle box;		int numColors = colors.getNumColors();		if (treeDisplay.getOrientation()==TreeDisplay.UP) {			box = new Rectangle(x[node], y[node]-edgewidth/2-2, edgewidth, edgewidth);			for (int i=0; i<numColors; i++) {				g.setColor(colors.getColor(i, !tree.anySelected()|| tree.getSelected(node)));				g.fillArc(box.x, box.y, box.width, box.height, 0+ (i*180/numColors), 180- (i*180/numColors));			}	        	g.setColor(treeDisplay.getBranchColor(node));			g.drawArc(box.x, box.y, box.width, box.height, 0, 180);			g.drawLine(box.x, box.y+ edgewidth/2, box.x+edgewidth,  box.y+ edgewidth/2);		}		else if (treeDisplay.getOrientation()==TreeDisplay.DOWN) {			box = new Rectangle(x[node], y[node] + 2, edgewidth, edgewidth);			for (int i=0; i<numColors; i++) {				g.setColor(colors.getColor(i, !tree.anySelected()|| tree.getSelected(node)));				g.fillArc(box.x, box.y -  box.height/2, box.width, box.height, 180+ (i*180/numColors), 180- (i*180/numColors));			}	        	g.setColor(treeDisplay.getBranchColor(node));			g.drawArc(box.x, box.y -  box.height/2, box.width, box.height, 180, 180);			g.drawLine(box.x, box.y , box.x+edgewidth,  box.y);		}		else  if (treeDisplay.getOrientation()==TreeDisplay.RIGHT) {			box = new Rectangle(x[node] + 2, y[node], edgewidth, edgewidth);			for (int i=0; i<numColors; i++) {				g.setColor(colors.getColor(i, !tree.anySelected()|| tree.getSelected(node)));				g.fillArc(box.x- box.width/2, box.y, box.width, box.height, 270+ (i*180/numColors), 180- (i*180/numColors));			}			g.setColor(treeDisplay.getBranchColor(node));			g.drawArc(box.x- box.width/2, box.y, box.width, box.height, 270, 180);			g.drawLine(box.x, box.y, box.x ,  box.y+edgewidth);		}		else  if (treeDisplay.getOrientation()==TreeDisplay.LEFT) {			box = new Rectangle(x[node]-edgewidth/2-2, y[node], edgewidth, edgewidth);			for (int i=0; i<numColors; i++) {				g.setColor(colors.getColor(i, !tree.anySelected()|| tree.getSelected(node)));				g.fillArc(box.x, box.y, box.width, box.height, 90+ (i*180/numColors), 180- (i*180/numColors));			}			g.setColor(treeDisplay.getBranchColor(node));			g.drawArc(box.x, box.y, box.width, box.height, 90, 180);			g.drawLine(box.x+edgewidth/2, box.y, box.x+edgewidth/2,  box.y+edgewidth);		}		else {			box = new Rectangle(x[node], y[node], edgewidth, edgewidth);			for (int i=0; i<numColors; i++) {				g.setColor(colors.getColor(i, !tree.anySelected()|| tree.getSelected(node)));				g.fillArc(box.x, box.y, box.width, box.height, 0, 360);			}			g.setColor(treeDisplay.getBranchColor(node));			g.drawArc(box.x, box.y, box.width, box.height, 0, 360);		}	}	/*_________________________________________________*/	public  int findTerminalBox(Tree tree, int drawnRoot, int x, int y){		return -1;	}	/*_________________________________________________*/	public void fillBranchWithColors(Tree tree, int node, ColorDistribution colors, Graphics g) {		if (node>0 && (tree.getRooted() || tree.getRoot()!=node)) {			Color c = g.getColor();			int fillWidth = edgewidth-2*inset;			int numColors = colors.getNumColors();			for (int i=0; i<numColors; i++) {				Color color;				if ((color = colors.getColor(i, !tree.anySelected()|| tree.getSelected(node)))!=null)					g.setColor(color);				drawOneBranch(treeDisplay.getTree(), g, node, inset + i*fillWidth/numColors,  (i+1)*fillWidth/numColors -i*fillWidth/numColors, 4) ;			}			if (c!=null) g.setColor(c);		}	}	/*_________________________________________________*/	public   void fillBranch(Tree tree, int node, Graphics g) {		if (node>0 && (tree.getRooted() || tree.getRoot()!=node)) {			drawOneBranch(tree, g, node, inset, edgewidth-inset*2, 4);		}	}	   	/*_________________________________________________*/	private void ScanBranches(Tree tree, int node, int x, int y)	{		if (foundBranch==0) {			if (inBranch(tree, node, x,y) )				foundBranch = node;			int thisSister = tree.firstDaughterOfNode(node);			while (tree.nodeExists(thisSister)) {				ScanBranches(tree, thisSister, x, y);				thisSister = tree.nextSisterOfNode(thisSister);			}		}	}	/*_________________________________________________*/	public   int findBranch(Tree tree, int drawnRoot, int x, int y) {	        if (MesquiteTree.OK(tree) && ready) {	        	foundBranch=0;	       		 ScanBranches(tree, drawnRoot, x, y);	       		 if (foundBranch == tree.getRoot() && !tree.getRooted())	       		 	return 0;	       		 else	       		 return foundBranch;	       	}	       	return 0;	}		/*_________________________________________________*/	public void reorient(int orientation) {		treeDisplay.setOrientation(orientation);		treeDisplay.pleaseUpdate(true, null);	}	/*_________________________________________________*/	public void setEdgeWidth(int edw) {		edgewidth = edw;		preferredEdgeWidth = edw;	}}	