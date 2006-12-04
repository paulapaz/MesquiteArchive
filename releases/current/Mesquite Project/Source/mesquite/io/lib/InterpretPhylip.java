/* Mesquite (package mesquite.io).  Copyright 2000-2006 D. Maddison and W. Maddison. Version 1.12, September 2006.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)Modifications:28 July 01 (WPM): checked for treeVector == null on export & used getCompatibleFileElements*/package mesquite.io.lib;/*~~  */import java.util.*;import java.awt.*;import mesquite.lib.*;import mesquite.lib.characters.*;import mesquite.lib.duties.*;import mesquite.categ.lib.*;/* ============  a file interpreter for Phylip files ============*/public abstract class InterpretPhylip extends FileInterpreterITree {	Class[] acceptedClasses;	Parser treeParser;	TreeVector treeVector = null;/*.................................................................................................................*/	public boolean startJob(String arguments, Object condition, CommandRecord commandRec, boolean hiredByName) {		acceptedClasses = new Class[] {ProteinState.class, DNAState.class, CategoricalState.class};		treeParser =  new Parser(); 		return true;  //make this depend on taxa reader being found?)  	 }  	 /*.................................................................................................................*/	public boolean canExportEver() {  		 return true;  //	}/*.................................................................................................................*/	public boolean canExportProject(MesquiteProject project) {  		 return project.getNumberCharMatrices(acceptedClasses) > 0;  //	}/*.................................................................................................................*/	public boolean canExportData(Class dataClass) {  		 for (int i = 0; i<acceptedClasses.length; i++)		 	if (dataClass==acceptedClasses[i])		 		return true;		 return false; 	}/*.................................................................................................................*/	public boolean canImport() {  		 return true;	}/*.................................................................................................................*/	public abstract CharacterData createData(CharactersManager charTask, Taxa taxa);/*.................................................................................................................*/	public boolean getInterleaved(){		return AlertDialog.query(module.containerOfModule(), "Interleaved or sequential?", "Is the matrix interleaved or sequential?", "Interleaved", "Sequential");	}	/*.................................................................................................................*/	public abstract void setPhylipState(CharacterData data, int ic, int it, char c);/*...............................................  read tree ....................................................*	/** Continues reading a tree description, starting at node "node" and the given location on the string*	private boolean readClade(MesquiteTree tree, int node) {					String c = treeParser.getNextToken();		c = c.trim();		if (",".equals(c)) {			c = treeParser.getNextToken();			c = c.trim();		}		if ("(".equals(c)){  //internal node			int sprouted = tree.sproutDaughter(node, false);			readClade(tree, sprouted);			boolean keepGoing = true;			while (keepGoing) {				int loc = treeParser.getPosition();				String next = treeParser.getNextToken();				if (",".equals(next)) 					next = treeParser.getNextToken();				if (")".equals(next))					keepGoing = false;				else {					treeParser.setPosition(loc);					sprouted = tree.sproutDaughter(node, false);					keepGoing = readClade(tree, sprouted);				}			}			return true;		}		else if (")".equals(c)) {			return false;		}	  	else {			int taxonNumber = tree.getTaxa().whichTaxonNumber(c);			if (taxonNumber >=0){ //taxon successfully found				if (tree.nodeOfTaxonNumber(taxonNumber)<=0){  // first time taxon encountered					tree.setTaxonNumber(node, taxonNumber, false);				 	return true;				 }			 }			return false;	  	}	  		}				/*.................................................................................................................*/	public void readPhylipTrees (MesquiteProject mf, MesquiteFile file, String line, ProgressIndicator progIndicator, Taxa taxa, CommandRecord commandRec) {		treeParser.setQuoteCharacter((char)0);		int numTrees = MesquiteInteger.infinite;		if (line != null){			treeParser.setString(line); 			String token = treeParser.getNextToken();  // numTaxa			numTrees = MesquiteInteger.fromString(token);		}		int iTree = 0;		TreeVector trees = null;		boolean abort = false;		line = file.readNextDarkLine();				while (!StringUtil.blank(line) && !abort && (iTree<numTrees)) {			treeParser.setString(line); //sets the string to be used by the parser to "line" and sets the pos to 0			if (trees == null) {				trees = new TreeVector(taxa);				trees.setName("Imported trees");			}			MesquiteTree t = new MesquiteTree(taxa);			//t.setTreeVector(treeVector);			t.readTree(line);			/*MesquiteInteger pos = new MesquiteInteger(0);			treeParser.setString(line);			readClade(t, t.getRoot());			t.setAsDefined(true);*/			t.setName("Imported tree " + iTree);			trees.addElement(t, false);			iTree++;			line = file.readNextDarkLine();					if (file.getFileAborted())				abort = true;		}		if (trees != null)			trees.addToFile(file,mf,(TreesManager)findElementManager(TreeVector.class));			}/*.................................................................................................................*/	public void readTreeFile(MesquiteProject mf, MesquiteFile file, String arguments, CommandRecord commandRec) {		Taxa taxa = getProject().chooseTaxa(containerOfModule(), "Of what taxa are these trees composed?",commandRec);		if (taxa== null) 			return;/*		treeVector = new TreeVector(taxa);		String name;		for (int it=0; it<taxa.getNumTaxa(); it++) {			name = taxa.getTaxonName(it);			if (name.length()>10)				treeVector.setTranslationLabel(name.substring(0,9), name, false);			else				treeVector.setTranslationLabel(name, name, false);		}*/		incrementMenuResetSuppression();		//file.linkProgressIndicator(progIndicator);		if (file.openReading()) {			String line = file.readNextDarkLine();					readPhylipTrees(mf, file, line, null, taxa, commandRec);			finishImport(null, file, false , commandRec);		}		decrementMenuResetSuppression();	}/*.................................................................................................................*/	public void readFile(MesquiteProject mf, MesquiteFile file, String arguments, CommandRecord commandRec) {		boolean interleaved = getInterleaved();				incrementMenuResetSuppression();		ProgressIndicator progIndicator = new ProgressIndicator(mf,"Importing File "+ file.getName(), file.existingLength());		progIndicator.start();		file.linkProgressIndicator(progIndicator);		if (file.openReading()) {			TaxaManager taxaTask = (TaxaManager)findElementManager(Taxa.class);			 CharactersManager charTask = (CharactersManager)findElementManager(CharacterData.class);						Taxa taxa = taxaTask.makeNewTaxa("Untitled Taxa", 0, false);			taxa.addToFile(file, getProject(), taxaTask);			CategoricalData data = (CategoricalData)createData(charTask,taxa);			data.addToFile(file, getProject(), null);						String token;			char c;			int numTaxa = 0;			int numChars = 0;			StringBuffer sb = new StringBuffer(1000);			file.readLine(sb);			String line = sb.toString();			parser.setString(line); 			token = parser.getNextToken();  // numTaxa			numTaxa = MesquiteInteger.fromString(token, "Mesquite does not currently accept PHYLIP files with options included at the top of the file", false,true);			if (numTaxa==MesquiteInteger.impossible) {				progIndicator.goAway();				file.close();				decrementMenuResetSuppression();				return;			}			token = parser.getNextToken();  // numchars			numChars = MesquiteInteger.fromString(token, "Mesquite does not currently accept PHYLIP files with options included at the top of the file", false,true);			if (numChars==MesquiteInteger.impossible) {				progIndicator.goAway();				file.close();				decrementMenuResetSuppression();				return;			}			boolean wassave = data.saveChangeHistory;			data.saveChangeHistory = false;			taxa.addTaxa(-1, numTaxa, true);			data.addCharacters(-1, numChars, false);			line = file.readNextDarkLine();   // reads first data line						boolean abort = false;			int block = 1;			int it=0;			int nextCharToRead = 0;	// first block			while (!StringUtil.blank(line) && !abort && (it<numTaxa)) {				parser.setString(line); //sets the string to be used by the parser to "line" and sets the pos to 0				token = "";				for (int taxonNameChar=0; taxonNameChar<100; taxonNameChar++) {  //this now assumes whitespace marks end of taxon name					c=parser.getNextChar();					if (c=='\0' || StringUtil.whitespace(c, null))						break;					token+=c;				}				Taxon t = taxa.getTaxon(it);				if (t!=null) {					if (interleaved)						progIndicator.setText("Reading block " + block + ", taxon "+it );					else						progIndicator.setText("Reading taxon "+it );					t.setName(StringUtil.deTokenize(token).trim());					for (int ic=0; ic<numChars; ic++) {						c=parser.nextDarkChar();						if (c=='\0') {							if ((!interleaved) && (ic<numChars)){								line = file.readNextDarkLine();									parser.setString(line);							}							else								break;						}						setPhylipState(data, ic, it, c);						if (it==0)							nextCharToRead++;					}				}				it++;				line = file.readNextDarkLine();						if (file.getFileAborted())					abort = true;			}// later blocks			if ((interleaved) && (!abort)) {				int startChar;				while (!StringUtil.blank(line) && !abort && (nextCharToRead<=numChars)) {					block++;					startChar=nextCharToRead;					it=0;					while (!StringUtil.blank(line) && !abort && (it<numTaxa)) {						parser.setString(line); //sets the string to be used by the parser to "line" and sets the pos to 0						Taxon t = taxa.getTaxon(it);						if (t!=null) {							progIndicator.setText("Reading block " + block + ", taxon "+it );							for (int ic=startChar; ic<numChars; ic++) {								c=parser.nextDarkChar();								if (c=='\0')									break;								setPhylipState(data,ic, it, c);								if (it==0)									nextCharToRead++;							}						}						it++;						line = file.readNextDarkLine();								if (file.getFileAborted())							abort = true;					}				}			}						if (!StringUtil.blank(line)) // then we have trees				readPhylipTrees(mf, file, line, progIndicator, taxa, commandRec);			data.saveChangeHistory = wassave;			data.resetChangedSinceSave();			finishImport(progIndicator, file, abort, commandRec);		}		decrementMenuResetSuppression();	}	/* ============================  exporting ============================*/	public boolean exportInterleaved=false;		/*.................................................................................................................*/	protected int taxonNameLength = 10;		public boolean getExportOptions(boolean dataSelected, boolean taxaSelected){		MesquiteInteger buttonPressed = new MesquiteInteger(1);		PhylipExporterDialog exportDialog = new PhylipExporterDialog(this,containerOfModule(), "Export Phylip Options", buttonPressed);						exportDialog.completeAndShowDialog(dataSelected, taxaSelected);					boolean ok = (exportDialog.query(dataSelected, taxaSelected)==0);		if (ok)			taxonNameLength = exportDialog.getTaxonNamesLength();		exportDialog.dispose();		return ok;	}		TreeVector previousVector;	/*.................................................................................................................*/	public abstract CharacterData findDataToExport(MesquiteFile file, String arguments, CommandRecord commandRec);	/*.................................................................................................................*/	public TreeVector findTreesToExport(MesquiteFile file, Taxa taxa, String arguments, boolean usePrevious, CommandRecord commandRec){		if (usePrevious)			return previousVector;		Listable[] treeVectors = getProject().getCompatibleFileElements(TreeVector.class, taxa);		TreeVector treeVector;		if (treeVectors.length==0)			return null;		if (treeVectors.length==1)			treeVector = (TreeVector)treeVectors[0];		else			treeVector = (TreeVector)ListDialog.queryList(containerOfModule(), "Include trees in file?", "Include trees in file?", MesquiteString.helpString, treeVectors, 0);		previousVector = treeVector;		return treeVector;	}	/*.................................................................................................................*/	public abstract void appendPhylipStateToBuffer(CharacterData data, int ic, int it, StringBuffer outputBuffer) ;	/*.................................................................................................................*/	protected void exportTrees(Taxa taxa, TreeVector treeVector, StringBuffer outputBuffer) { 		Tree tree;		if (treeVector !=null && treeVector.size()>0) {			outputBuffer.append(""+treeVector.size() + getLineEnding());			for (int iTree = 0; iTree < treeVector.size(); iTree++) {				tree = (Tree)treeVector.elementAt(iTree);				outputBuffer.append(tree.writeTree(Tree.BY_NUMBERS));  //or Tree.BY_NUMBERS  or Tree.BY_NAMES				// if do it BY_NAMES, make sure you truncate the taxon names to 10 characters!!				outputBuffer.append(getLineEnding());			}		}	}	/*.................................................................................................................*/	public void exportBlock(Taxa taxa, CharacterData data, StringBuffer outputBuffer, int startChar, int endChar) { 		int numTaxa = taxa.getNumTaxa();		int numChars = data.getNumChars();		int counter;		String pad = "          ";		while (pad.length() < taxonNameLength)			pad += "  ";		for (int it = 0; it<numTaxa; it++){			if (!writeOnlySelectedTaxa || (taxa.getSelected(it))){				if (startChar==0) {   // first block					String name = (taxa.getTaxonName(it)+ pad);					name = name.substring(0,taxonNameLength);					outputBuffer.append(name);				}				outputBuffer.append(" ");				counter = startChar;				for (int ic = startChar; ic<numChars; ic++) {					if (!writeOnlySelectedData || (data.getSelected(ic))){						int currentSize = outputBuffer.length();						appendPhylipStateToBuffer(data, ic, it, outputBuffer);						if (outputBuffer.length()-currentSize>1) {							alert("Sorry, this data matrix can't be exported to this format (some character states aren't represented by a single symbol [char. " + CharacterStates.toExternal(ic) + ", taxon " + Taxon.toExternal(it) + "])");							return;						}						counter++;						if (counter>endChar)							break;					}				}				outputBuffer.append(getLineEnding());			}		}	}	/*.................................................................................................................*/	public void exportFile(MesquiteFile file, String arguments, CommandRecord commandRec) { //if file is null, consider whole project open to export		Arguments args = new Arguments(new Parser(arguments), true);		boolean usePrevious = args.parameterExists("usePrevious");		CharacterData data = findDataToExport(file, arguments, commandRec);		Taxa t = null;		if (data != null)			t = data.getTaxa();		TreeVector trees = findTreesToExport(file, t, arguments, usePrevious, commandRec);		if (data ==null && trees == null) {			showLogWindow(true);			logln("WARNING: No suitable data or trees available for export to a file of format \"" + getName() + "\".  The file will not be written.\n");			return;		}		Taxa taxa = null;		if (data == null)			taxa = trees.getTaxa();		else			taxa = data.getTaxa();		boolean dataAnySelected = false;		if (data != null)			dataAnySelected =data.anySelected();		if (!commandRec.scripting() && !usePrevious)			if (!getExportOptions(dataAnySelected, taxa.anySelected()))				return;					int numTaxa = taxa.getNumTaxa();		int numTaxaWrite = taxa.numberSelected(this.writeOnlySelectedTaxa);		int numChars = 0;		StringBuffer outputBuffer = new StringBuffer(numTaxa*(20 + numChars));				if (data != null){			numChars = data.getNumChars();			int numCharWrite = data.numberSelected(this.writeOnlySelectedData); 			outputBuffer.append(Integer.toString(numTaxaWrite)+" ");			outputBuffer.append(Integer.toString(numCharWrite)+this.getLineEnding());			int blockSize=50;					if (exportInterleaved)				for (int ic=0; ic<numChars; ic= ic+blockSize) {					exportBlock(taxa, data, outputBuffer, ic, ic+blockSize-1);					outputBuffer.append(getLineEnding());				}			else				exportBlock(taxa, data, outputBuffer, 0, numChars);		}				exportTrees(taxa, trees, outputBuffer);				saveExportedFileWithExtension(outputBuffer, arguments, "phy", commandRec);	}	/*.................................................................................................................*/    	 public String getName() {		return "Phylip file";   	 }	/*.................................................................................................................*/   	  	/** returns an explanation of what the module does.*/ 	public String getExplanation() { 		return "Imports and exports Phylip files." ;   	 }	/*.................................................................................................................*/   	    	 }