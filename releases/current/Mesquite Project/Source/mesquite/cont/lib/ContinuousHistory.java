/* Mesquite source code.  Copyright 1997-2006 W. Maddison and D. Maddison.Version 1.11, June 2006.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)*/package mesquite.cont.lib;import java.awt.*;import java.util.*;import mesquite.lib.duties.*;import mesquite.lib.*;import mesquite.lib.characters.*;/* ======================================================================== *//** Contains an array of  continuous character states for one character, at each of the taxa or nodes.  See notes under <a href = "ContinuousData.html">ContinuousData</a> regarding items */public class ContinuousHistory extends ContinuousAdjustable  implements CharacterHistory {	private double minState = 0;	private double maxState = 100;	public ContinuousHistory (Taxa taxa, int numNodes, ContinuousData data) { 		super(taxa, numNodes);		setParentData(data);		if (data!=null) {			setItemsAs(data);		}	}	/*..........................................ContinuousHistory................*/	public CharacterHistory clone(CharacterHistory s) {		ContinuousHistory snew;		if ((s==null) || (s.getNumNodes()!=numNodes) || (!(s instanceof ContinuousHistory)))			snew = new ContinuousHistory(getTaxa(), numNodes, (ContinuousData)data); 		else {			snew = (ContinuousHistory)s;			snew.data = data;		}		snew.setItemsAs(this);		for (int i=0; i<numNodes; i++) {			for (int im = 0; im<getNumItems(); im++)				snew.setState(i, im, getState(i, im));		}		snew.characterNumber = characterNumber;		if (observedStates!=null)			snew.setObservedStates((CharacterDistribution)observedStates.getAdjustableClone());		return snew;	}	/*..........................................ContinuousHistory................*/	/** Returns a new object indicating the states at the tips (used whether or not History is reconstruction)*/	public CharacterDistribution getStatesAtTips(Tree tree){		if (observedStates !=null)			return (CharacterDistribution)observedStates.getAdjustableClone();		else {			ContinuousAdjustable d = new ContinuousAdjustable(tree.getTaxa(), tree.getTaxa().getNumTaxa());			d.setItemsAs(this);			fillDistribution(tree, tree.getRoot(), d);			return d;		}	}	/*..........................................ContinuousHistory................*/	private void fillDistribution(Tree tree, int node, ContinuousAdjustable dist) {  		if (tree.nodeIsTerminal(node)){			int t = tree.taxonNumberOfNode(node);			for (int i =0; i<getNumItems(); i++)				dist.setState(t, i, getState(node, i));		}		else for (int d = tree.firstDaughterOfNode(node); tree.nodeExists(d); d = tree.nextSisterOfNode(d))				fillDistribution(tree, d, dist);	}	/*..........................................ContinuousHistory................*/	CharacterDistribution observedStates;	/*..........................................ContinuousHistory................*/	/** Returns the states in the terminal taxa (used if History is reconstruction)*/	public CharacterDistribution getObservedStates(){		return observedStates;	}	/*..........................................ContinuousHistory................*/	/** Sets the states in the terminal taxa (used if History is reconstruction)*/	public void setObservedStates(CharacterDistribution observed){		this.observedStates = observed;	}	/*..........................................ContinuousHistory................*/	/** This readjust procedure can be called to readjust the size of storage of	states of a character for nodes. */	public CharacterHistory adjustSize(Tree tree) {		if (tree.getNumNodeSpaces() == this.getNumNodes())			return this;		else {			ContinuousHistory soc = new ContinuousHistory(tree.getTaxa(), tree.getNumNodeSpaces(), (ContinuousData)getParentData());			soc.setItemsAs(this);			soc.setParentData(getParentData());			soc.setParentCharacter(getParentCharacter());			return soc;		}	}	/*..........................................ContinuousHistory................*/	private void calcMinMaxStates(Tree tree, int node) {		for (int i=0; i<getNumItems(); i++) {			double s=getState(node, i); 			maxState = MesquiteDouble.maximum(maxState, s);			minState = MesquiteDouble.minimum(minState, s);		}		for (int d = tree.firstDaughterOfNode(node); tree.nodeExists(d); d = tree.nextSisterOfNode(d))				calcMinMaxStates(tree, d);	}		/*..........................................ContinuousHistory................*/	/** Must be called before a tree is shaded.  Goes through all nodes to find states present, to set	minima and maxima. */	public void prepareColors(Tree tree, int drawnRoot) {  		minState=MesquiteDouble.unassigned;		maxState=MesquiteDouble.unassigned;		calcMinMaxStates(tree, drawnRoot); 		if (getParentData()!=null && getParentCharacter()>=0){			int ic = getParentCharacter();			ContinuousData data  = ((ContinuousData)getParentData());			for (int it = 0; it<data.getNumTaxa(); it++) {				double s = data.getState(ic, it, 0);				maxState = MesquiteDouble.maximum(maxState, s);				minState = MesquiteDouble.minimum(minState, s);			}		}		else if (getObservedStates()!=null){			int ic = getParentCharacter();			ContinuousDistribution data  = (ContinuousDistribution)getObservedStates();			for (int it = 0; it<data.getNumNodes(); it++) {				double s = data.getState(it);				maxState = MesquiteDouble.maximum(maxState, s);				minState = MesquiteDouble.minimum(minState, s);			}		}	}	/*..........................................  ContinuousHistory  ..................................................*/	/** places into the already instantiated ColorDistribution the colors corresponding to the CharacterState, and returns the number of colors.  Uses default colors. 		Mode is MesquiteColorTable.GRAYSCALE, COLORS, COLORS_NO_BW, or DEFAULT (default depends on subclass)*/	public int getColorsOfState(CharacterState state, ColorDistribution colors, MesquiteColorTable colorTable) {		if (colors==null || state == null || ! (state instanceof ContinuousState))			return 0;		colors.initialize();		ContinuousState cState = (ContinuousState)state;		int colorCount=0;		if (cState.getNumItems() == 1) {			double s =cState.getValue(0); 			colors.setWeight(0, 1.0);			if (!MesquiteDouble.isCombinable(s))				colors.setColor(0, Color.white);			else {				int place = (int)(((s-minState)/(maxState-minState))*10); 				/*if (mode == MesquiteColorTable.DEFAULT || mode == MesquiteColorTable.COLORS_NO_BW)					colors.setColor(0, MesquiteColorTable.getDefaultColor(10,place, MesquiteColorTable.COLORS_NO_BW));				else 					colors.setColor(0, MesquiteColorTable.getDefaultColor(10,place, mode));				*/				colors.setColor(0, colorTable.getColor(s, minState, maxState));				/*	if (mode == MesquiteColorTable.DEFAULT || mode == MesquiteColorTable.COLORS_NO_BW)						colors.setColor(0, MesquiteColorTable.getColor(s, minState, maxState));					else if (mode == MesquiteColorTable.GRAYSCALE)						colors.setColor(0, MesquiteColorTable.getGrayScale(s, minState, maxState));					else 						colors.setColor(0, MesquiteColorTable.getColor(s, minState, maxState));*/			}		}		else {			for (int i=0; i<cState.getNumItems() ; i++) {				double s = cState.getValue(i); 				colors.setWeight(i, 1.0/cState.getNumItems() );				if (!MesquiteDouble.isCombinable(s))					colors.setColor(0, Color.white);				else {					int place = (int)(((s-minState)/(maxState-minState))*10); 					/*if (mode == MesquiteColorTable.DEFAULT || mode == MesquiteColorTable.COLORS_NO_BW)						colors.setColor(i, MesquiteColorTable.getDefaultColor(10,place, MesquiteColorTable.COLORS_NO_BW));					else 						colors.setColor(i, MesquiteColorTable.getDefaultColor(10,place, mode));					*/					colors.setColor(0, colorTable.getColor(s, minState, maxState));				/*	if (mode == MesquiteColorTable.DEFAULT || mode == MesquiteColorTable.COLORS_NO_BW)						colors.setColor(0, MesquiteColorTable.getColor(s, minState, maxState));					else if (mode == MesquiteColorTable.GRAYSCALE)						colors.setColor(0, MesquiteColorTable.getGrayScale(s, minState, maxState));					else 						colors.setColor(0, MesquiteColorTable.getColor(s, minState, maxState));*/				}			}		}		return cState.getNumItems() ;	}	/*..........................................ContinuousHistory................*/	public MesquiteColorTable getColorTable(MesquiteColorTable colorTable) {				if (colorTable == null || !(colorTable instanceof ContColorTable))			colorTable =  new ContColorTable();		colorTable.disableSetColor(true);		return colorTable;	}	/*..........................................ContinuousHistory................*/	/** Places into Color array the colors at the node, and returns the number of colors.  Uses passed	stateColors as color key.  If null is passed, uses color system of parent data (using maxState of this	StsOfCharcter) or, if there is not parent data object, the default colors. */	public int getColorsAtNode(int node, ColorDistribution colors, MesquiteColorTable stateColors, boolean showWeights) {		for (int i=0; i<10; i++) colors.initialize();		if (getNumItems() == 1) {			double s =firstItem.getValue(node); 			colors.setWeight(0, 1.0);			if (!MesquiteDouble.isCombinable(s))				colors.setColor(0, ColorDistribution.unassigned);			else {				int place = (int)(((s-minState)/(maxState-minState))*10); 								/*if (stateColors==null) {					if (mode == MesquiteColorTable.DEFAULT || mode == MesquiteColorTable.COLORS_NO_BW)						colors.setColor(0, MesquiteColorTable.getColor(s, minState, maxState));					else if (mode == MesquiteColorTable.GRAYSCALE)						colors.setColor(0, MesquiteColorTable.getGrayScale(s, minState, maxState));					else						colors.setColor(0, MesquiteColorTable.getColor(s, minState, maxState));				}				else*/					colors.setColor(0, stateColors.getColor(10,place));			}		}		else {			for (int i=0; i<getNumItems(); i++) {				double s = getState(node,i);				colors.setWeight(i, 1.0/getNumItems());				if (!MesquiteDouble.isCombinable(s))					colors.setColor(0, ColorDistribution.unassigned);				else {					int place = (int)(((s-minState)/(maxState-minState))*10); 					/*if (stateColors==null) {						if (mode == MesquiteColorTable.DEFAULT || mode == MesquiteColorTable.COLORS_NO_BW)							colors.setColor(i, MesquiteColorTable.getColor(s, minState, maxState));						else if (mode == MesquiteColorTable.GRAYSCALE)							colors.setColor(i, MesquiteColorTable.getGrayScale(s, minState, maxState));						else 							colors.setColor(i, MesquiteColorTable.getColor(s, minState, maxState));					}					else*/						colors.setColor(i, stateColors.getColor(10,place));				}			}		}		return getNumItems();	}	/*..........................................ContinuousHistory................*/	/** returns the vector of ColorEvents in order, e.g. for stochastic character mapping.  Return null if none.  Not yet supported for ContinuousHistory */	public ColorEventVector getColorSequenceAtNode(int node, MesquiteColorTable stateColors){		return null;	}	/*..........................................ContinuousHistory................*	public int getLegendStates(Color[] cs, String[] stateNames, MesquiteColorTable stateColors) {		return getLegendStates(cs, stateNames, stateColors, MesquiteColorTable.DEFAULT);	}	/*..........................................ContinuousHistory................*/	public int getLegendStates(Color[] cs, String[] stateNames, Point[] tableMappings, MesquiteColorTable stateColors) {		int colorCount=0;		double rangeUnit = (maxState-minState)/10.0; 		for (int e=0; e<=10; e++) {			/*if (stateColors==null) {				if (mode == MesquiteColorTable.DEFAULT || mode == MesquiteColorTable.COLORS_NO_BW)					cs[colorCount]= MesquiteColorTable.getDefaultColor(10,e, MesquiteColorTable.COLORS_NO_BW);				else 					cs[colorCount]= MesquiteColorTable.getDefaultColor(10,e, mode);			}			else*/				cs[colorCount]= stateColors.getColor(10,e);				if (tableMappings != null)					tableMappings[colorCount] = new Point(10, e);			stateNames[colorCount++]=MesquiteDouble.toString(minState + rangeUnit*e) + " to " + MesquiteDouble.toString(minState + rangeUnit*(e+1));		}		return colorCount;	}	/*..........................................ContinuousHistory................*	public int getLegendStates(Color[] cs, String[] stateNames, int mode) {		return getLegendStates(cs, stateNames, null, mode);	}	*/}