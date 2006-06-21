/* Mesquite source code.  Copyright 1997-2006 W. Maddison and D. Maddison.Version 1.11, June 2006.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)*/package mesquite.trees.BasicDrawTaxonNames;/*~~  */import java.util.*;import java.awt.*;import java.awt.image.*;import mesquite.lib.*;import mesquite.lib.duties.*;import java.awt.geom.*;/** Draws the taxon names in a tree drawing */public class BasicDrawTaxonNames extends DrawNamesTreeDisplay {	TreeDisplay treeDisplay;	TreeDrawing treeDrawing;	TextRotator textRotator;	TaxonPolygon[] namePolys;	Tree tree;	Graphics gL;	int separation = 10;	Font currentFont = null;	String myFont = null;	int myFontSize = -1;	FontMetrics fm;	int rise;	int descent;	int oldNumTaxa=0;	MesquiteString fontSizeName, fontName;	MesquiteBoolean colorPartition, shadePartition;	MesquiteBoolean showNodeLabels, showTaxonNames;	MesquiteString fontColorName;	Color fontColor=Color.black;	Color fontColorLight = Color.gray;	NumberForTaxon shader = null;	int longestString = 0;	MesquiteMenuItemSpec offShadeMI = null;	double[] shades = null;	double minValue, maxValue;	/*.................................................................................................................*/	public boolean startJob(String arguments, Object condition, CommandRecord commandRec, boolean hiredByName) {		currentFont = MesquiteWindow.defaultFont; 		fontName = new MesquiteString(MesquiteWindow.defaultFont.getName());		fontSizeName = new MesquiteString(Integer.toString(MesquiteWindow.defaultFont.getSize()));		MesquiteSubmenuSpec namesMenu = addSubmenu(null, "Names");		MesquiteSubmenuSpec msf = addSubmenu(null, "Font", makeCommand("setFont", this), MesquiteSubmenu.getFontList());		msf.setList(MesquiteSubmenu.getFontList());		msf.setDocumentItems(false);		msf.setSelected(fontName);		MesquiteSubmenuSpec mss = addSubmenu(null, "Font Size", makeCommand("setFontSize", this), MesquiteSubmenu.getFontSizeList());		mss.setList(MesquiteSubmenu.getFontSizeList());		mss.setDocumentItems(false);		mss.setSelected(fontSizeName);		fontColorName = new MesquiteString("Black");		MesquiteSubmenuSpec mmis = addSubmenu(null, "Font Color", makeCommand("setColor",  this));		mmis.setList(ColorDistribution.standardColorNames);		mmis.setSelected(fontColorName);		//MesquiteSubmenuSpec mssNames = addSubmenu(null, "Names");		colorPartition = new MesquiteBoolean(true);		addCheckMenuItemToSubmenu(null, namesMenu, "Color by Taxon Group", makeCommand("toggleColorPartition", this), colorPartition);		addItemToSubmenu(null, namesMenu, "Shade by Value...", makeCommand("shadeByNumber",  this));		offShadeMI = addItemToSubmenu(null, namesMenu, "Turn off Shading", makeCommand("offShading",  this));		offShadeMI.setEnabled(false);		shadePartition = new MesquiteBoolean(false);		addCheckMenuItemToSubmenu(null, namesMenu, "Background Color by Taxon Group", makeCommand("toggleShadePartition", this), shadePartition);		showNodeLabels = new MesquiteBoolean(true);		addCheckMenuItemToSubmenu(null, namesMenu, "Show Branch Names", makeCommand("toggleNodeLabels", this), showNodeLabels);		showTaxonNames = new MesquiteBoolean(true);		addCheckMenuItemToSubmenu(null, namesMenu, "Show Taxon Names", makeCommand("toggleShowNames", this), showTaxonNames);				return true;  	 }  	    	public void endJob(){		treeDisplay = null;		treeDrawing = null;		textRotator = null;		super.endJob();   	}	/*.................................................................................................................*/  	 public Snapshot getSnapshot(MesquiteFile file) {   	 	Snapshot temp = new Snapshot();		if (myFont!=null)			temp.addLine("setFont " + myFont);  //TODO: this causes problem since charts come before tree window		if (myFontSize>0)			temp.addLine("setFontSize " + myFontSize);  //TODO: this causes problem since charts come before tree window		temp.addLine("setColor " + ParseUtil.tokenize(fontColorName.toString()));  //TODO: this causes problem since charts come before tree window		temp.addLine("toggleColorPartition " + colorPartition.toOffOnString());		if (shader != null)			temp.addLine("shadeByNumber ", shader);		temp.addLine("toggleShadePartition " + shadePartition.toOffOnString());		temp.addLine("toggleNodeLabels " + showNodeLabels.toOffOnString());		temp.addLine("toggleShowNames " + showTaxonNames.toOffOnString());  	 	return temp;  	 }	/*.................................................................................................................*/    	 public Object doCommand(String commandName, String arguments, CommandRecord commandRec, CommandChecker checker) {     	 if (checker.compare(this.getClass(), "Sets module that calculates a number for a taxon by which to shade", "[name of module]", commandName, "shadeByNumber")) {    	 		NumberForTaxon temp= (NumberForTaxon)replaceEmployee(commandRec, NumberForTaxon.class, arguments, "Value by which to shade taxon names", shader);    	 		if (temp!=null) {    	 			shader = temp;    	 			offShadeMI.setEnabled(true);    	 			MesquiteTrunk.resetMenuItemEnabling();    	 			shades = null;    	 			calcShades(tree, commandRec);				parametersChanged(null, commandRec);    	 			return temp; 			}    	 	}     	 else if (checker.compare(this.getClass(), "Turns of shading by number", null, commandName, "offShading")) {    	 		if (shader != null)    	 			fireEmployee(shader);    	 		shader = null;    	 		shades = null;    	 		offShadeMI.setEnabled(false);    	 		MesquiteTrunk.resetMenuItemEnabling();    	 		parametersChanged(null, commandRec);    	 	}     	 else if (checker.compare(this.getClass(), "Toggles whether taxon names are colored according to their group in the current taxa partition", "[on or off]", commandName, "toggleColorPartition")) {    	 		boolean current = colorPartition.getValue();    	 		    	 		colorPartition.toggleValue(parser.getFirstToken(arguments));    	 		if (current!=colorPartition.getValue())	    	 		parametersChanged(null, commandRec);    	 	}    	 	else if (checker.compare(this.getClass(), "Toggles whether taxon names are given a background color according to their group in the current taxa partition", "[on or off]", commandName, "toggleShadePartition")) {    	 		boolean current = shadePartition.getValue();    	 		shadePartition.toggleValue(parser.getFirstToken(arguments));    	 		if (current!=shadePartition.getValue())	    	 		parametersChanged(null, commandRec);    	 	}    	 	else if (checker.compare(this.getClass(), "Toggles whether names of internal branches (clades) are shown", "[on or off]", commandName, "toggleNodeLabels")) {    	 		boolean current = showNodeLabels.getValue();    	 		showNodeLabels.toggleValue(parser.getFirstToken(arguments));    	 		if (current!=showNodeLabels.getValue())	    	 		parametersChanged(null, commandRec);    	 	}    	 	else if (checker.compare(this.getClass(), "Toggles whether names of terminal taxa are shown", "[on or off]", commandName, "toggleShowNames")) {    	 		boolean current = showTaxonNames.getValue();    	 		showTaxonNames.toggleValue(parser.getFirstToken(arguments));    	 		if (current!=showTaxonNames.getValue())	    	 		parametersChanged(null, commandRec);    	 	}    	 	else if (checker.compare(this.getClass(), "Sets the font used for the taxon names", "[name of font]", commandName, "setFont")) {    	 		String t = parser.getFirstToken(arguments);    	 		if (currentFont==null){    	 			myFont = t; 				fontName.setValue(t);   	 		}    	 		else {	    	 		Font fontToSet = new Font (t, currentFont.getStyle(), currentFont.getSize());	    	 		if (fontToSet!= null) {    	 				myFont = t;    	 				fontName.setValue(t);	    	 			currentFont = fontToSet;	    	 			parametersChanged(null, commandRec);	    	 		}    	 		}    	 	}    	 	else if (checker.compare(this.getClass(), "Sets the font size used for the taxon names", "[size of font]", commandName, "setFontSize")) {    	 		int fontSize = MesquiteInteger.fromString(arguments);    	 		if (currentFont==null){	    	 		if (!commandRec.scripting() && !MesquiteInteger.isPositive(fontSize))	    	 			fontSize = MesquiteInteger.queryInteger(containerOfModule(), "Font Size", "Font Size", 12);	    	 		if (MesquiteInteger.isPositive(fontSize)) {	    	 			myFontSize = fontSize;    	 				fontSizeName.setValue(Integer.toString(fontSize));    	 			}    	 		}    	 		else {	    	 		if (!commandRec.scripting() && !MesquiteInteger.isPositive(fontSize))	    	 			fontSize = MesquiteInteger.queryInteger(containerOfModule(), "Font Size", "Font Size", currentFont.getSize());	    	 		if (MesquiteInteger.isPositive(fontSize)) {	    	 			myFontSize = fontSize;		    	 		Font fontToSet = new Font (currentFont.getName(), currentFont.getStyle(), fontSize);		    	 		if (fontToSet!= null) {		    	 			currentFont = fontToSet;    	 					fontSizeName.setValue(Integer.toString(fontSize));			    	 		parametersChanged(null, commandRec);		    	 		}	    	 		}    	 		}    	 	}    	 	else if (checker.compare(this.getClass(), "Sets color of taxon names", "[name of color]", commandName, "setColor")) {    	 		String token = ParseUtil.getFirstToken(arguments, stringPos);    	 		Color bc = ColorDistribution.getStandardColor(token);			if (bc == null)				return null;			fontColor = bc;			fontColorLight = ColorDistribution.brighter(bc, ColorDistribution.dimmingConstant);			fontColorName.setValue(token);			parametersChanged(null, commandRec);		}    	 	else     	 		return super.doCommand(commandName, arguments, commandRec, checker);		return null;   	 }   	public Font getFont(){   		return currentFont;   	}   	public void invalidateNames(TreeDisplay treeDisplay){   		if (textRotator!=null)   			textRotator.invalidateAll();   	}   	public float getTaxonShade(double value){    		if (!MesquiteDouble.isCombinable(value) || !MesquiteDouble.isCombinable(minValue) || !MesquiteDouble.isCombinable(maxValue) || minValue == maxValue)   			return 1.0f;   		//return (float)((value-minValue)/(maxValue-minValue)*0.8 + 0.2);  //todo: need to distinguish polarity   		return (float)((maxValue - value)/(maxValue-minValue)*0.8 + 0.2);   	}	public void setTree(Tree tree, CommandRecord commandRec) {		if (shader != null ){			calcShades(tree, commandRec);		}	}	/*.................................................................................................................*/ 	public void employeeParametersChanged(MesquiteModule employee, MesquiteModule source, Notification notification, CommandRecord commandRec) {			calcShades(tree, commandRec);			parametersChanged(notification, commandRec);			}	private void calcShades(Tree tree, CommandRecord commandRec){			if (tree == null)				return;			minValue = MesquiteDouble.unassigned;			maxValue = MesquiteDouble.unassigned;			Taxa taxa = tree.getTaxa();			if (shades == null || shades.length < taxa.getNumTaxa())				shades = new double[taxa.getNumTaxa()];			for (int i = 0; i<shades.length; i++)				shades[i] = MesquiteDouble.unassigned;			MesquiteNumber n = new MesquiteNumber();			for (int i = 0; i< taxa.getNumTaxa(); i++){				shader.calculateNumber(taxa.getTaxon(i), n, null, commandRec);				if (n.isCombinable()) {					shades[i] = n.getDoubleValue();					if (minValue == MesquiteDouble.unassigned)						minValue = shades[i];					else if (minValue > shades[i])						minValue = shades[i];					if (maxValue == MesquiteDouble.unassigned)						maxValue = shades[i];					else if (maxValue < shades[i])						maxValue = shades[i];				}			}	}	/*.................................................................................................................*/	private void drawNamesOnTree(Tree tree, int N, TreeDisplay treeDisplay, TaxaPartition partitions) {		if  (tree.nodeIsTerminal(N)) {   //terminal			if (!showTaxonNames.getValue())				return;			Color bgColor = null;			int horiz=treeDrawing.x[N];			int vert=treeDrawing.y[N];			int lengthString;			Taxa taxa = tree.getTaxa();			int taxonNumber = tree.taxonNumberOfNode(N);			if (taxonNumber<0) {				//MesquiteMessage.warnProgrammer("error: negative taxon number found in DrawNamesTreeDisplay " + taxonNumber + "  tree: " + tree.writeTree());				return;			}			else if (taxonNumber>=taxa.getNumTaxa()) {				//MesquiteMessage.warnProgrammer("error: taxon number too high found in DrawNamesTreeDisplay " + taxonNumber + "  tree: " + tree.writeTree());				return;			}			if (taxonNumber>= namePolys.length) {				//MesquiteMessage.warnProgrammer("error: taxon number " + taxonNumber + " / name boxes " + nameBoxes.length);				return;			}			String s=taxa.getName(taxonNumber);			if (s== null)				return;			Taxon taxon = taxa.getTaxon(taxonNumber);			if (taxon== null)				return;			boolean selected = taxa.getSelected(taxonNumber);			//check all extras to see if they want to add anything			boolean underlined = false;			Color taxonColor;			if (!tree.anySelected() || tree.getSelected(N))				taxonColor = fontColor;			else				taxonColor = fontColorLight;							if (partitions!=null && (colorPartition.getValue() || shadePartition.getValue())){				TaxaGroup mi = (TaxaGroup)partitions.getProperty(taxonNumber);				if (mi!=null) {					if (colorPartition.getValue() && mi.getColor() != null)						taxonColor = mi.getColor();					if (shadePartition.getValue()){						bgColor =mi.getColor();						textRotator.assignBackground(bgColor);					}				}			}			ListableVector extras = treeDisplay.getExtras();			if (extras!=null){				Enumeration e = extras.elements();				while (e.hasMoreElements()) {					Object obj = e.nextElement();					TreeDisplayExtra ex = (TreeDisplayExtra)obj;		 			if (ex.getTaxonUnderlined(taxon))		 				underlined = true;		 			Color tc = ex.getTaxonColor(taxon);		 			if (tc!=null) {		 				taxonColor = tc;		 			}		 			String es = ex.getTaxonStringAddition(taxon);		 			if (!StringUtil.blank(es))		 				s+= es;			 	}		 	}		 				if(MesquiteWindow.Java2Davailable && shades != null && taxonNumber < shades.length)				ColorDistribution.setTransparentGraphics(gL,getTaxonShade(shades[taxonNumber]));					gL.setColor(taxonColor); 						lengthString = fm.stringWidth(s); //what to do if underlined?			int centeringOffset = 0;			if (treeDisplay.centerNames)				centeringOffset = (longestString-lengthString)/2;						if (treeDrawing.namesFollowLines && MesquiteWindow.Java2Davailable){				double slope = (treeDrawing.lineBaseY[N]*1.0-treeDrawing.lineTipY[N])/(treeDrawing.lineBaseX[N]-treeDrawing.lineTipX[N]);					//setBounds(namePolys[taxonNumber], horiz+separation, vert, lengthString, rise+descent);					boolean upper = treeDrawing.lineTipY[N]>treeDrawing.lineBaseY[N];					boolean right = treeDrawing.lineTipX[N]>treeDrawing.lineBaseX[N];					double radians = Math.atan(slope);					Font font = gL.getFont();					FontMetrics fontMet = gL.getFontMetrics(font);					double height = fontMet.getHeight(); //0.667					int length = fontMet.stringWidth(s)+ separation;					int textOffsetH = 0; //fontMet.getHeight()*2/3;					int textOffsetV = 0;					if (!right) {						textOffsetH = -(int)(Math.cos(radians)*length);												textOffsetV = -(int)(Math.sin(radians)*length);					}					else {						textOffsetH = (int)(Math.cos(radians + Math.atan(height*0.6/separation))*separation*1.4);  //1.0												textOffsetV = (int)(Math.sin(radians + Math.atan(height*0.6/separation))*separation*1.4);					}										int horizPosition = treeDrawing.lineTipX[N];					int vertPosition = treeDrawing.lineTipY[N];										textRotator.drawFreeRotatedText(s, gL, horizPosition, vertPosition, radians, new Point(textOffsetH, textOffsetV), false, namePolys[taxonNumber]);								}			else if ((treeDrawing.labelOrientation[N]==270) || treeDisplay.getOrientation()==TreeDisplay.UP) {				if (MesquiteWindow.Java2Davailable && MesquiteDouble.isCombinable(treeDrawing.namesAngle) && treeDrawing.labelOrientation[N]!=270){					textRotator.drawFreeRotatedText(s,  gL, horiz-rise/2, vert-separation, treeDrawing.namesAngle, null, true, namePolys[taxonNumber]);				}				else {					vert -= centeringOffset;					setBounds(namePolys[taxonNumber], horiz-rise/2, vert-separation-lengthString, rise+descent, lengthString);					textRotator.drawRotatedText(s, taxonNumber, gL, treeDisplay, horiz-rise/2, vert-separation);					if (underlined){						Rectangle b =namePolys[taxonNumber].getB();						gL.drawLine(b.x+b.width, b.y, b.x+b.width, b.y+b.height);						//gL.fillPolygon(namePolys[taxonNumber]);					}				}			}			else if ((treeDrawing.labelOrientation[N]==90) || treeDisplay.getOrientation()==TreeDisplay.DOWN) {				/*if (MesquiteWindow.Java2Davailable && (MesquiteDouble.isCombinable(treeDrawing.namesAngle) || treeDrawing.labelOrientation[N]!=90)){					textRotator.drawFreeRotatedText(s,  gL, horiz-rise*2, vert+separation, treeDrawing.namesAngle, null, false, namePolys[taxonNumber]); // /2				}				else */				{					vert += centeringOffset;					setBounds(namePolys[taxonNumber], horiz-rise/2, vert+separation, rise+descent, lengthString);					textRotator.drawRotatedText(s, taxonNumber, gL, treeDisplay, horiz-rise/2, vert+separation, false);					if (underlined){						Rectangle b =namePolys[taxonNumber].getB();						gL.drawLine(b.x+b.width, b.y, b.x+b.width, b.y+b.height);						//gL.fillPolygon(namePolys[taxonNumber]);					}				}			}			else if ((treeDrawing.labelOrientation[N]==0) || treeDisplay.getOrientation()==TreeDisplay.RIGHT) {				/*if (MesquiteWindow.Java2Davailable && (MesquiteDouble.isCombinable(treeDrawing.namesAngle) || treeDrawing.labelOrientation[N]!=0)){					textRotator.drawFreeRotatedText(s,  gL, horiz+separation, vert-rise*2, treeDrawing.namesAngle, null, false, namePolys[taxonNumber]); ///2				}				else */{					horiz += centeringOffset;					setBounds(namePolys[taxonNumber], horiz+separation, vert-rise/2, lengthString, rise+descent);					if (bgColor!=null) {						gL.setColor(bgColor);						gL.fillRect(horiz+separation, vert-rise/2, lengthString, rise+descent);						gL.setColor(taxonColor);					}					gL.drawString(s, horiz+separation, vert+rise/2);					if (underlined){						Rectangle b =namePolys[taxonNumber].getB();						gL.drawLine(b.x, b.y+b.height, b.x+b.width, b.y+b.height);						//gL.fillPolygon(namePolys[taxonNumber]);					}				}			}			else if ((treeDrawing.labelOrientation[N]==180) || treeDisplay.getOrientation()==TreeDisplay.LEFT) {				/*if (MesquiteWindow.Java2Davailable && (MesquiteDouble.isCombinable(treeDrawing.namesAngle) || treeDrawing.labelOrientation[N]!=0)){					textRotator.drawFreeRotatedText(s,  gL, horiz - separation, vert-rise*2, treeDrawing.namesAngle, null, true, namePolys[taxonNumber]);				}				else */{					horiz -= centeringOffset;					setBounds(namePolys[taxonNumber], horiz - separation - lengthString, vert-rise/2, lengthString, rise+descent);					if (bgColor!=null) {						gL.setColor(bgColor);						gL.fillRect(horiz - separation - lengthString, vert-rise/2, lengthString, rise+descent);						gL.setColor(taxonColor);					}					gL.drawString(s, horiz - separation - lengthString, vert+rise/2);					if (underlined){						Rectangle b =namePolys[taxonNumber].getB();						gL.drawLine(b.x, b.y+b.height, b.x+b.width, b.y+b.height);						//gL.fillPolygon(namePolys[taxonNumber]);					}				}			}			else if (treeDisplay.getOrientation()==TreeDisplay.FREEFORM) {				setBounds(namePolys[taxonNumber], horiz+separation, vert-rise/2, lengthString, rise+descent);				if (bgColor!=null) {					gL.setColor(bgColor);					gL.fillRect(horiz+separation, vert-rise/2, lengthString, rise+descent);					gL.setColor(taxonColor);				}				gL.drawString(s, horiz+separation, vert+rise/2);				if (underlined){					Rectangle b =namePolys[taxonNumber].getB();					gL.drawLine(b.x, b.y+b.height, b.x+b.width, b.y+b.height);						//gL.fillPolygon(namePolys[taxonNumber]);				}			}			else { 				double slope = (treeDrawing.lineBaseY[N]*1.0-treeDrawing.lineTipY[N])/(treeDrawing.lineBaseX[N]-treeDrawing.lineTipX[N]);				if (slope>=-1 && slope <= 1) {  //right or left side					if (treeDrawing.lineTipX[N]> treeDrawing.lineBaseX[N]) { // right						setBounds(namePolys[taxonNumber], horiz+separation, vert, lengthString, rise+descent);						if (bgColor!=null) {							gL.setColor(bgColor);							gL.fillRect(horiz+separation, vert, lengthString, rise+descent);							gL.setColor(taxonColor);						}						gL.drawString(s, horiz+separation, vert + rise);						if (underlined){							Rectangle b =namePolys[taxonNumber].getB();							gL.drawLine(b.x, b.y+b.height, b.x+b.width, b.y+b.height);							//gL.fillPolygon(namePolys[taxonNumber]);						}					}					else {						setBounds(namePolys[taxonNumber], horiz - separation - lengthString, vert, lengthString, rise+descent);						if (bgColor!=null) {							gL.setColor(bgColor);							gL.fillRect(horiz - separation - lengthString, vert, lengthString, rise+descent);							gL.setColor(taxonColor);						}						gL.drawString(s, horiz - separation - lengthString, vert + rise);						if (underlined){							Rectangle b =namePolys[taxonNumber].getB();							gL.drawLine(b.x, b.y+b.height, b.x+b.width, b.y+b.height);							//gL.fillPolygon(namePolys[taxonNumber]);						}					}				}				else {//top or bottom					if (treeDrawing.lineTipY[N]> treeDrawing.lineBaseY[N]) { // bottom						setBounds(namePolys[taxonNumber], horiz, vert+separation, rise+descent, lengthString);						textRotator.drawRotatedText(s, taxonNumber, gL, treeDisplay, horiz, vert+separation, false);						if (underlined){							Rectangle b =namePolys[taxonNumber].getB();							gL.drawLine(b.x+b.width, b.y, b.x+b.width, b.y+b.height);							//gL.fillPolygon(namePolys[taxonNumber]);						}					}					else { // top						setBounds(namePolys[taxonNumber], horiz, vert-separation-lengthString, rise+descent, lengthString);						textRotator.drawRotatedText(s, taxonNumber, gL, treeDisplay, horiz, vert-separation);						if (underlined){							Rectangle b =namePolys[taxonNumber].getB();							gL.drawLine(b.x+b.width, b.y, b.x+b.width, b.y+b.height);						//gL.fillPolygon(namePolys[taxonNumber]);						}					}				}			}			textRotator.assignBackground(null);			gL.setColor(Color.black);			if (MesquiteWindow.Java2Davailable)				ColorDistribution.setOpaqueGraphics(gL);					if (selected){				gL.setXORMode(Color.white);				gL.fillPolygon(namePolys[taxonNumber]);				gL.setPaintMode();			}		}		else {			for (int d = tree.firstDaughterOfNode(N); tree.nodeExists(d); d = tree.nextSisterOfNode(d))				drawNamesOnTree(tree, d, treeDisplay, partitions);							String label = null;			if (showNodeLabels.getValue())				label = tree.getNodeLabel(N);			if (label!=null && label.length() >0 && label.charAt(0)!='^') {			//check all extras to see if they want to add anything				boolean underlined = false;				Color taxonColor = Color.black;				if (!tree.anySelected()|| tree.getSelected(N))					taxonColor = fontColor;				else					taxonColor = fontColorLight;				String s = StringUtil.deTokenize(label);				ListableVector extras = treeDisplay.getExtras();				if (extras!=null){					Enumeration e = extras.elements();					while (e.hasMoreElements()) {						Object obj = e.nextElement();						TreeDisplayExtra ex = (TreeDisplayExtra)obj;			 			if (ex.getCladeLabelUnderlined(label, N))			 				underlined = true;			 			Color tc = ex.getCladeLabelColor(label, N);			 			if (tc!=null)			 				taxonColor = tc;			 			String es = ex.getCladeLabelAddition(label, N);			 			if (!StringUtil.blank(es))			 				s+= es;				 	}			 	}				StringUtil.highlightString(gL,s, treeDrawing.x[N], treeDrawing.y[N], taxonColor, Color.white);				gL.setColor(taxonColor);				if (underlined)					gL.drawLine(treeDrawing.x[N], treeDrawing.y[N]+1,treeDrawing.x[N] +  fm.stringWidth(s), treeDrawing.y[N]+1);			}		}	}	void setBounds(TaxonPolygon poly, int x, int y, int w, int h){			poly.getBounds();			poly.setB(x,y,w,h);			//int[] xs = poly.xpoints;			//int[] ys = poly.ypoints;			//if (true || xs == null || xs.length !=4 || ys == null || ys.length !=4){				poly.npoints=0;				poly.addPoint(x, y);				poly.addPoint(x+w, y);				poly.addPoint(x+w, y+h);				poly.addPoint(x, y+h);				poly.npoints=4;			/*}			else {				xs[0] = x;				xs[1] = x+w;				xs[2] = x+w;				xs[3] = x;				ys[0] = y;				ys[1] = y;				ys[2] = y+h;				ys[3] = y+h;			}*/	}	/*.................................................................................................................*/	public void drawNames(TreeDisplay treeDisplay,  Tree tree, int drawnRoot, Graphics g) {		if (treeDisplay==null)			return; // alert("tree display null in draw taxon names");		if (tree==null)			return; // alert("tree null in draw taxon names");		if (g==null)			return; // alert("graphics null in draw taxon names");		int totalNumTaxa = tree.getTaxa().getNumTaxa(); 		if (textRotator == null) 			textRotator = new TextRotator(totalNumTaxa);		if (namePolys==null) {			namePolys = new TaxonPolygon[totalNumTaxa];			oldNumTaxa=totalNumTaxa;			for (int i = 0; i<totalNumTaxa; i++) {				namePolys[i] = new TaxonPolygon();				namePolys[i].xpoints = new int[4];				namePolys[i].ypoints = new int[4];				namePolys[i].npoints=4;			}		}		else if (oldNumTaxa<totalNumTaxa) {			for (int i = 0; i<oldNumTaxa; i++)				namePolys[i]=null;			namePolys = new TaxonPolygon[totalNumTaxa];			for (int i = 0; i<totalNumTaxa; i++) {				namePolys[i] = new TaxonPolygon();				namePolys[i].xpoints = new int[4];				namePolys[i].ypoints = new int[4];				namePolys[i].npoints=4;			}			oldNumTaxa=totalNumTaxa;		}		this.treeDisplay =treeDisplay;		this.treeDrawing =treeDisplay.getTreeDrawing();		this.tree =tree;		this.gL =g;		if (treeDrawing==null)			alert("node displays null in draw taxon names");		try{		if (MesquiteTree.OK(tree)) {			if (currentFont ==null) {				currentFont = g.getFont();				if (myFont==null)					myFont = currentFont.getName();				if (myFontSize<=0)					myFontSize = currentFont.getSize();	    	 		Font fontToSet = new Font (myFont, currentFont.getStyle(), myFontSize);				if (fontToSet==null)					currentFont = g.getFont();				else					currentFont = fontToSet;			}			Font tempFont = g.getFont();			g.setFont(currentFont);			fm=g.getFontMetrics(currentFont);			rise= fm.getMaxAscent();			descent = fm.getMaxDescent();			separation = treeDisplay.getTaxonNameDistance();			TaxaPartition part = null;			if (colorPartition.getValue() || shadePartition.getValue())				part = (TaxaPartition)tree.getTaxa().getCurrentSpecsSet(TaxaPartition.class);			if (treeDisplay.centerNames) {				longestString = 0;				findLongestString(tree, drawnRoot);			}			drawNamesOnTree(tree, drawnRoot, treeDisplay, part);			g.setFont(tempFont);		}		}		catch (Exception e){			MesquiteMessage.warnProgrammer("Exception in draw taxon names");			e.printStackTrace();		}	}		/*.................................................................................................................*/	private void findLongestString(Tree tree,  int N) {		if  (tree.nodeIsTerminal(N)) {   //terminal						int taxonNumber = tree.taxonNumberOfNode(N);			if (taxonNumber<0 || taxonNumber>=tree.getTaxa().getNumTaxa()) 				return;						int lengthString = fm.stringWidth(tree.getTaxa().getTaxonName(taxonNumber)); 			if (lengthString>longestString)				longestString = lengthString;		}		else {			for (int d = tree.firstDaughterOfNode(N); tree.nodeExists(d); d = tree.nextSisterOfNode(d))					findLongestString(tree, d);		}	}	/*.................................................................................................................*/	int foundTaxon;	/*.................................................................................................................*/	private void findNameOnTree(Tree tree,  int N, int x, int y) {		if  (tree.nodeIsTerminal(N)) {   //terminal						int taxonNumber = tree.taxonNumberOfNode(N);			if (taxonNumber<0) {				//MesquiteMessage.warnProgrammer("error: negative taxon number found in findNameOnTree");				return;			}			if (taxonNumber>=tree.getTaxa().getNumTaxa()) {				MesquiteMessage.warnProgrammer("error:  taxon number too large found in findNameOnTree (" + taxonNumber + ") node: " + N); 				return;			}			if (taxonNumber>= namePolys.length) {				MesquiteMessage.warnProgrammer("error in draw taxon names: Name polys not big enough; taxon number " + taxonNumber + " / name boxes " + namePolys.length);				return;			}			if (namePolys[taxonNumber]!=null && namePolys[taxonNumber].contains(x,y)) {				foundTaxon=taxonNumber;			}		}		else {			for (int d = tree.firstDaughterOfNode(N); tree.nodeExists(d) && foundTaxon==-1; d = tree.nextSisterOfNode(d))					findNameOnTree(tree, d, x, y);		}	}	/*.................................................................................................................*/	public   int findTaxon(Tree tree, int drawnRoot, int x, int y) { 				foundTaxon=-1;		if (tree!=null && namePolys!=null) {			if (tree.getTaxa().isDoomed())				return -1;			findNameOnTree(tree, drawnRoot, x, y);		}		return foundTaxon; }		/*.................................................................................................................*/	public   void fillTaxon(Graphics g, int M) {		try {			if ((namePolys!=null) && (namePolys[M]!=null))				g.fillPolygon(namePolys[M]);		}		catch (ArrayIndexOutOfBoundsException e) {		alert("taxon flash out of getBounds");}	}	/*.................................................................................................................*/    	 public String getName() {		return "Basic Draw Names for Tree Display";   	 }	/*.................................................................................................................*/   	  	/** returns an explanation of what the module does.*/ 	public String getExplanation() { 		return "Draws taxon names on a tree.  Chooses orientation of names according to orientation of tree." ;   	 }   	 }class TaxonPolygon extends Polygon {	Rectangle b;		public void setB(int x, int y, int w, int h){		if (b == null)			b = new Rectangle(x, y, w, h);		else {			b.x = x;			b.y = y;			b.width = w;			b.height = h;		}	}		public Rectangle getB(){		return b;	}}