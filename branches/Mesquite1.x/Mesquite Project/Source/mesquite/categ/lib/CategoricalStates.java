/* Mesquite source code.  Copyright 1997-2006 W. Maddison and D. Maddison.Version 1.12, September 2006.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)*/package mesquite.categ.lib;import java.awt.*;import java.util.*;import mesquite.lib.*;import mesquite.lib.characters.*;import mesquite.lib.duties.*;/* ======================================================================== *//** A class for an array of  categorical character states for one character, at each of the taxa  or nodes*/public abstract class CategoricalStates extends CharacterStates {	protected int maxState=CategoricalState.maxCategoricalState;	protected int minState=0;	protected long allStates=0;	protected boolean minFound=false;	protected boolean  maxFound=false;	private boolean  maxFoundInFreq=false;	protected double[][] frequencies;	protected int enforcedMaxState = 0;		public CategoricalStates (Taxa taxa) {		super(taxa);	}	/*..........................................  CategoricalStates  ..................................................*/	/** Indicates the type of character stored */ 	public Class getStateClass(){		return CategoricalState.class;	}	/**returns the corresponding CharacterData subclass*/	public Class getCharacterDataClass (){		return CategoricalData.class;	}	/** returns the name of the type of data stored */	public String getDataTypeName(){		return "Standard Categorical Data";	}	/*..........................................  CategoricalStates  ...................................................*/	/** return if frequencies exist for states at each node.*/	public boolean frequenciesExist(){		return (frequencies !=null);	}	/*..........................................  CategoricalStates  ...................................................*/	/** Copy frequency information from first to second CategoricalStates.*/	public static void copyFrequencies(CategoricalStates source, CategoricalStates sink) {		if (source!=null && sink!=null && source.frequenciesExist()) {			for (int i=0; i<source.getNumNodes() && i<sink.getNumNodes(); i++) {				sink.setFrequencies(i, source.getFrequencies(i));			}		}	}		int numFreqCategories = 0;	public void setNumFreqCategories(int numCategories){		this.numFreqCategories = numCategories;	}	private void prepareFrequencyStorage(int node, int numCategories){			if (frequencies == null) {				frequencies = new double[getNumNodes()][];				frequencies[node] = new double[numCategories];			}			else if (frequencies[node]== null || frequencies[node].length != numCategories) {				frequencies[node] = new double[numCategories];			}	}	/*..........................................  CategoricalStates  ...................................................*/	/** set freqency information*/	public void setFrequencies(int node, double[] freqs) {		if (checkIllegalNode(node, 0))			return;		if (freqs!=null) {			prepareFrequencyStorage(node, freqs.length);			for (int i=0; i<freqs.length; i++) {				frequencies[node][i] = freqs[i];			}		}	}	/*..........................................  CategoricalStates  ...................................................*/	/** set freqency information. */	public void setFrequency(int node, int category, double freq) {		if (checkIllegalNode(node, 0))			return;		prepareFrequencyStorage(node, numFreqCategories);		if (frequencies != null && category< frequencies[node].length)			frequencies[node][category] = freq;		}	/*..........................................  CategoricalStates  ...................................................*/	/** get freqency information at a particular node*/	public double[] getFrequencies(int node) {		if (checkIllegalNode(node, 1))			return null;		if (frequencies != null)			return frequencies[node];		return null;	}	/*..........................................  CategoricalStates  ...................................................*/	/** get freqency of particular category at a particular node*/	public double getFrequency(int node, int category) {		if (checkIllegalNode(node, 2))			return 0;		if (frequencies == null || frequencies[node]== null)			return MesquiteDouble.unassigned;		else			return frequencies[node][category];	}	/*..........................................  CategoricalStates  ...................................................*/	/** get state set represented by frequences at a particular node*/	public long getSetFromFrequencies(int node) {		if (checkIllegalNode(node, 3))			return 0L;		if (frequencies == null || frequencies[node]== null)			return 0L;		else {			long s = 0L;			for (int i=0; i<frequencies[node].length; i++)				if (frequencies[node][i]>0.0)					s = CategoricalState.addToSet(s, i);			return s;		}	}	/*..........................................  CategoricalStates  ...................................................*/	/** dispose frequency information (frees memory). */	public void disposeFrequencies() {		frequencies = null;	}	/*..........................................  CategoricalStates  ...................................................*/	/**Get the default CharacterModel for the given paradigm (e.g., "Parsimony") */	public CharacterModel getDefaultModel(MesquiteProject file, String paradigm){   		NameReference p = NameReference.getNameReference(paradigm);   		DefaultReference dR = CategoricalData.findDefaultReference(p);   		if (dR==null) {   			MesquiteMessage.println("Default model not found :" + paradigm);   			return null;   		}   		else {   			CharacterModel cm = file.getCharacterModel(dR.getDefault());   			if (cm==null)    				MesquiteMessage.println("Default model not found / " + dR.getDefault());   			return cm;   		}   	}	/*..........................................  CategoricalStates  ...................................................*/	/** get union of all statesets, including multiple-state uncertainties (but not missing, gap) */	public long getAllStates (){		long s = 0L;		for (int ic=0; ic<getNumNodes(); ic++) {			s |= getState(ic);		}		return s & CategoricalState.statesBitsMask;	}	/*..........................................  CategoricalStates  ...................................................*/	/** get union of all statesets, including multiple-state uncertainties (but not missing, gap), in the tree */	public long getAllStates (Tree tree, int node){		long s = 0L;		if (tree.nodeIsTerminal(node)){			s = getState(tree.taxonNumberOfNode(node));		}		else {			for (int d = tree.firstDaughterOfNode(node); tree.nodeExists(d); d = tree.nextSisterOfNode(d))				s |= getAllStates(tree, d);		}		return s & CategoricalState.statesBitsMask;	}	/*..........................................  CategoricalStates  ...................................................*/	/** get stateset at node N */	public abstract long getState (int N);	/*..........................................  CategoricalStates  ...................................................*/	/**returns CharacterState at node N */	public CharacterState getCharacterState (CharacterState cs, int N){		if (checkIllegalNode(N, 5)) {			if (cs!=null)				cs.setToUnassigned();			return cs;		}		CategoricalState c;		if (cs == null || !(cs instanceof CategoricalState)) {			c = new CategoricalState();		}		else			c = (CategoricalState)cs;		c.setValue(getState(N));		return c;	}	/*..........................................  CategoricalStates  ...................................................*/	/**returns blank CharacterState object */	public CharacterState getCharacterState (){		return new CategoricalState();	}	/*..........................................  CategoricalStates  ...................................................*/	/**returns whether state at node N is inapplicable */   	public  boolean isInapplicable(int N){   		return getState(N)== CategoricalState.inapplicable;   	}	/*..........................................  CategoricalStates  ...................................................*/	/**returns whether state at node N is unassigned (missing data)( */   	public  boolean isUnassigned(int N){   		return getState(N)== CategoricalState.unassigned;   	}	/*..........................................  CategoricalStates  ...................................................*/	/**returns whether state at node N is uncertain (partial missing or ambiguous data)( */   	public  boolean isUncertain(int N){   		return CategoricalState.isUncertain(getState(N));   	}	/*..........................................  CategoricalStates  ...................................................*/	/**Dump a listing of the states to the log. */	public void logStates(){		MesquiteModule.mesquiteTrunk.logln("States");		String statesString="";		for (int ic=0; ic<getNumNodes(); ic++) {			statesString+=CategoricalState.toString(getState(ic));		}		statesString+= '\n';		MesquiteModule.mesquiteTrunk.logln(statesString);	}	/*..........................................  CategoricalStates  ...................................................*/	/**returns string describing character states at node  */	public String toString (int node, String lineEnding) {		if (checkIllegalNode(node, 8))			return "";		CategoricalData data = null;		int ic = -1;		if (getParentData()!=null && getParentData() instanceof CategoricalData) {			data = (CategoricalData)getParentData();			ic = getParentCharacter();		}		if (data!=null){			boolean first = true;			if (frequencies != null && frequencies[node]!=null && frequencies[node].length>0) {				long stateSet = getState(node);				String s="";				boolean mismatch = false;				for (int i=0; i<frequencies[node].length && !mismatch; i++)					if (frequencies[node][i]!=0 && !CategoricalState.isElement(stateSet, i))						mismatch = true;				for (int i=0; i<frequencies[node].length; i++)  {					if (frequencies[node][i]!=0 || CategoricalState.isElement(stateSet, i)) {						if (!first)							s += lineEnding;						first = false;						s+= data.getSymbol(i);												if (frequencies[node][i]!=0)							s+= ":" + MesquiteDouble.toString(frequencies[node][i]);						if (mismatch && CategoricalState.isElement(stateSet, i))							s+= "*";					}				}				return s;			}			else if (ic>=0 && !(this instanceof CategoricalHistory)){  // treat as if terminal, not history				StringBuffer sb = new StringBuffer();				data.statesIntoStringBufferCore(ic, getState(node), sb, true);				return sb.toString();			}			else if (ic>=0 && data.hasStateNames(ic)){				String s = "";				first = true;				long ss = getState(node);				for (int e=0; e<=CategoricalState.maxCategoricalState; e++) {					if (CategoricalState.isElement(ss, e)) {						if (!first)							s+=",";						s+=data.getStateName(ic,  e);						first=false;					}				}				if (first)					s += '!'; //no state found!				return s;			}			else {				String s = "";				first = true;				long ss = getState(node);				for (int e=0; e<=CategoricalState.maxCategoricalState; e++) {					if (CategoricalState.isElement(ss, e)) {						if (!first)							s+=",";						s+=data.getSymbol( e);						first=false;					}				}				if (first)					s += '!'; //no state found!				return s;			}		}				//data is null		if (frequencies != null && frequencies[node]!=null && frequencies[node].length>0) {			String s="";			boolean first = true;			for (int i=0; i<frequencies[node].length; i++) 				if (frequencies[node][i]!=0) {					if (!first)						s += lineEnding;					first = false;					s+= i + ":" + MesquiteDouble.toString(frequencies[node][i]);				}			return s;		}		else {			return CategoricalState.toString(getState(node), null, 0, false, true);		}	}	/*..........................................  CategoricalStates  ...................................................*/	/**returns string describing states at nodes. */	public String toString () {		String s="";		for (int i=0; i<getNumNodes(); i++)			s += CategoricalState.toString(getState(i), false);		return s;	}	/*..........................................  CategoricalStates  ...................................................*/	/** returns true if statesets have single element each, and that at node N is greater than that at node M.  */	public boolean firstIsGreater (int N, int M){		if (CategoricalState.cardinality(getState(N)) == 1 && CategoricalState.cardinality(getState(M)) == 1) {			if (CategoricalState.minimum(getState(N)) > CategoricalState.minimum(getState(M)))				return true;		}		return false;			}	/*..........................................  CategoricalStates  ...................................................*/	/** returns true if states at nodes N and M are equal */	public boolean statesEqual(int N, int M) {		checkOutOfBounds(N, M);		return getState(N)==getState(M);	}	/*..........................................  CategoricalStates  ...................................................*/	private void checkOutOfBounds(int N, int M) {		if (N<0 || N> getNumNodes() || M<0 || M>getNumNodes())			MesquiteMessage.println("ERROR: out of bounds");	}}