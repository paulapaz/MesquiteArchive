package mesquite.examples.ExNumberForTreeUI;import mesquite.lib.*;import mesquite.lib.duties.*;//(6.) needed for the character-based calculationimport mesquite.categ.lib.*;/* This module calculates a statistic of probably little use, what it calls the Excess Polytomies Index. A polytomy with 5 descendants costs 3 (an excess of 3 over the dichotomous case); the cost of all polytomies is added and returned.    This module could be build step by step, following this sequence:  (1.) Calculates the simple Excess Polytomies index. (2.) Added is a silly menu item and a handler for it.  (3.) Added is a check menu item controlling whether the statistic is calculated as the square root of excess at each node. (4.)  Supported saving into data file whether square root is used (5.)  Supported saving preference so where to use square root (6.)  Added dependency on character states; counts polytomy excess only if there is heterogeneity among members in categorical character (7.)  Added control for what character used. */ public class ExNumberForTreeUI extends NumberForTree {	//(3.) The boolean wrapper for the flag as to whether the calculation uses square root	MesquiteBoolean useSquareRoot;	//(6.) The source of characters for the modified character-based calculation	CharSourceCoordObed characterSource;		int currentCharacter = 0; //(7.) Current character for the modified character-based calculation	Taxa recentTaxa = null; //(7.) Need to remember this for the command handling of Choose Character for the modified character-based calculation	/*.................................................................................................................*/	/** (1.) Required for all modules.  Called when module started.  Returns true if started successfully.*/	public boolean startJob(String arguments, Object condition, CommandRecord commandRec, boolean hiredByName) {		//(3.) the boolean wrapper to store whether to use square roots		useSquareRoot = new MesquiteBoolean(false);				//(5.) loading preferences.  This will cause processPreferencesFromFile to be called.  Note useSquareRoot must have been instantiated before this to receive value		loadPreferences();				//(2.) a silly menu item		addMenuItem("Hello", makeCommand("sayHello", this)); //(2.) requesting a menu item when the module is running, with command to be given when menu item chosen				//(3.) the menu item to toggle whether to use square roots		addCheckMenuItem(null, "Use Square Roots For Excess", makeCommand("toggleSquareRoot", this), useSquareRoot);				//(5.) Remembering current settings as defaults		addMenuItem("Store Defaults for Excess", makeCommand("storeDefaults", this)); 				//(6.) Hiring a source of characters for the 		characterSource = (CharSourceCoordObed)hireCompatibleEmployee(commandRec, CharSourceCoordObed.class, CategoricalState.class, "Source of characters for Excess Polytomies calculation"); 		if (characterSource == null) 			return sorry(commandRec, getName() + " couldn't start because no source of characters obtained."); 		 		//(7.) Adding a menu item to choose the character for the modified calculation		addMenuItem("Choose Character (for Excess Polytomies)", makeCommand("chooseCharacter", this)); 			 		return true;	}		/*.................................................................................................................*/	/** (5.) preference file has been read; now handle the information */	public void processPreferencesFromFile (String[] prefs) {		if (prefs!=null && prefs.length>0) {			useSquareRoot.setValue("useSqRt".equalsIgnoreCase(prefs[0]));		}	}	/*.................................................................................................................*/	/** (5.) Strings to write into preferences file */	public String[] preparePreferencesForFile () {		if (useSquareRoot.getValue())			return (new String[] {"useSqRt"});		else 			return (new String[] {"dontUseSqRt"});	}	/*.................................................................................................................*/	/**(4.) This method is called to build the snapshot of the state of the module, for instance to build the script in the Mesquite block..  Here the module	returns the commands it would need to return to its current settings */  	 public Snapshot getSnapshot(MesquiteFile file) {   	 	Snapshot temp = new Snapshot();   	 	//(4.) setting the useSquareRoot parameter  	 	temp.addLine("toggleSquareRoot " + useSquareRoot.toOffOnString());  	 	  	 	//(6.) This is needed so that the snapshot knows to place the character source's snapshot here (not strictly needed, but helpful)  	 	temp.addLine("getCharacterSource ", characterSource);  	 	  	 	//(7.) Helpful to remember current character when file re-read  	 	temp.addLine("setCharacter " + currentCharacter);  	 	return temp;  	 }	/*.................................................................................................................*/	/** (2.) Handling commands */ 	public Object doCommand(String commandName, String arguments, CommandRecord commandRec, CommandChecker checker){ 		//(2.) handling the command sayHello		if (checker.compare(this.getClass(), "Greets the user", null, commandName, "sayHello")) {     	 		alert("Hello!  The menu item belongs to " + getName() + ", and example module to show how to program Mesquite.");			return null;		} 		//(3.) handling the command toggleSquareRoot    	 	else if (checker.compare(this.getClass(), "Sets whether or not square roots are used in Excess Polytomies calculations", "[on = use square roots; off]", commandName, "toggleSquareRoot")) {    	 		useSquareRoot.toggleValue(parser.getFirstToken(arguments)); //toggles yes/know whether 			parametersChanged(null, commandRec); //notifies employers that parameters have been changed			return null;    	 	} 		//(5.) handling the command storeDefaults		else if (checker.compare(this.getClass(), "Store defaults", null, commandName, "storeDefaults")) {     	 		storePreferences(); //this causes preferences to be requested via preparePreferencesForFile and written into file			return null;		} 		//(6.) This is needed so that the snapshot knows to place the character source's snapshot here (not strictly needed, but helpful)    	 	else if (checker.compare(this.getClass(), "Returns module supplying characters", null, commandName, "getCharacterSource")) { 			return characterSource;    	 	} 		//(7.) Choosing the character    	 	else if (checker.compare(this.getClass(), "Queries the user about what character to use", null, commandName, "chooseCharacter")) {    	 		int ic=characterSource.queryUserChoose(recentTaxa, " to calculate Excess Polytomies ", commandRec);    	 		if (MesquiteInteger.isCombinable(ic)) {	   			currentCharacter = ic;	 			parametersChanged(null, commandRec);  			} 			return null;    	 	} 		//(7.) Setting the character (e.g., from the snapshot)    	 	else if (checker.compare(this.getClass(), "Sets what character to use", "[number, 0 based]", commandName, "setCharacter")) {    	 		int ic=MesquiteInteger.fromString(parser.getFirstToken(arguments));    	 		if (MesquiteInteger.isCombinable(ic)) {	   			currentCharacter = ic;	 			parametersChanged(null, commandRec);  			} 			return null;    	 	}    	 	else    	 		return super.doCommand(commandName, arguments, commandRec, checker); 	} 		/*.................................................................................................................*/ 	/**(6.) For the modified calculation, returns the accumulated states in the clade */ 	private long descendantStates(Tree tree, int node, CategoricalDistribution character){		if (tree.nodeIsTerminal(node)) { 			return character.getState(tree.taxonNumberOfNode(node));		}		else {			long states = 0L;			for (int daughter = tree.firstDaughterOfNode(node); tree.nodeExists(daughter); daughter = tree.nextSisterOfNode(daughter))	  			states |= descendantStates(tree, daughter, character);	  		return states;  		} 	}	/*.................................................................................................................*/	/** (1.) Calculates Excess Polytomies Index*/	private double excessPolytomies(Tree tree, int node, CategoricalDistribution character) {		double excess = 0;		if (tree.nodeIsInternal(node)) {  //recurse up the tree			for (int daughter = tree.firstDaughterOfNode(node); tree.nodeExists(daughter); daughter = tree.nextSisterOfNode(daughter))	  			excess += excessPolytomies(tree, daughter, character);  	 		if (tree.nodeIsPolytomous(node) && CategoricalState.cardinality(descendantStates(tree, node, character))>1) { //(6.)  	 			int numExcess = tree.numberOfDaughtersOfNode(node) -2;  	 			//(3.) Calculation varies depending on the flag useSquareRoot	  	 		if (useSquareRoot.getValue()){ //yes, use square roots	  	 			if (numExcess >=0) //just in case!	  	 				excess += Math.sqrt(numExcess); //how many beyond 2?	  	 		}	  	 		else	  	 			excess += numExcess; //how many beyond 2?	  	 	} 	  		}  		return excess;  	 }  	 	/*.................................................................................................................*/	/** (1.) Required by NumberForTree.  Called in case this module needs to initialize anything; this module doesn't */   	public void initialize(Tree tree, CommandRecord commandRec){   		if (tree != null)   			recentTaxa = tree.getTaxa(); //(7.) need to remember this for doCommand "chooseCharacter"   	}   		/*.................................................................................................................*/	/** (1.) This is the focal method of a NumberForTree.  A tree is passed; the tree should not be modified!!!!	The number is returned in result; a description of the result can be returned in resultString.	This particular module does a simple quantification of how resolved is the tree.*/	public void calculateNumber(Tree tree, MesquiteNumber result, MesquiteString resultString, CommandRecord commandRec) {    	 	if (result==null || tree == null)    	 		return;    	 	//(6.) getting the character.  This neededn't be done for every tree, i.e. could be obtained once then cached -- but that would require checking that the character hasn't changed    	 	CategoricalDistribution character = (CategoricalDistribution)characterSource.getCharacter(tree.getTaxa(), currentCharacter, commandRec);    	 	    	 	double excess = excessPolytomies(tree, tree.getRoot(), character);		result.setValue(excess);		if (resultString!=null) {			if (useSquareRoot.getValue()) //(3.)  indicate in string that square roots are used				resultString.setValue("Excess Polytomies Index (sqrt): "+ result.toString());			else				resultString.setValue("Excess Polytomies Index: "+ result.toString());		}   		recentTaxa = tree.getTaxa(); //(7.) remember this for "chooseCharacter" in doCommand	}		/*.................................................................................................................*/	/** (1.) Explains what the module does.*/    	 public String getExplanation() {		return "Calculates a resolution index for a tree.  This is an example module to show how to program for Mesquite.";   	 }   	 	/*.................................................................................................................*/	/** (1.) Name of module*/    	 public String getName() {		return "Excess Polytomies Index (example module)";   	 }}