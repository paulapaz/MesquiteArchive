/* Mesquite (package mesquite.io).  Copyright 2000-2006 D. Maddison and W. Maddison. Version 1.11, June 2006.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)*/package mesquite.mb.ExportForMrBayes;/*~~  */import java.util.*;import java.awt.*;import mesquite.lib.*;import mesquite.lib.characters.*;import mesquite.lib.duties.*;import mesquite.categ.lib.*;public class ExportForMrBayes extends FileInterpreterI {/*.................................................................................................................*/	public boolean startJob(String arguments, Object condition, CommandRecord commandRec, boolean hiredByName) { 		return true;  //make this depend on taxa reader being found?)  	 }  	 	public boolean isPrerelease(){		return false;	}	public boolean isSubstantive(){		return true;	}/*.................................................................................................................*/	public String preferredDataFileExtension() { 		return "nex";   	 }/*.................................................................................................................*/	public boolean canExportEver() {  		 return true;  //	}/*.................................................................................................................*/	public boolean canExportProject(MesquiteProject project) {  		 return (project.getNumberCharMatrices( CategoricalState.class) > 0) ;	}	/*.................................................................................................................*/	public boolean canExportData(Class dataClass) {  		return (dataClass==CategoricalState.class);	}/*.................................................................................................................*/	public boolean canImport() {  		 return false;	}/*.................................................................................................................*/	public void readFile(MesquiteProject mf, MesquiteFile file, String arguments, CommandRecord commandRec) {	}/* ============================  exporting ============================*/	/*.................................................................................................................*/	boolean convertAmbiguities = false;	boolean simplifyNames = true;	boolean useData = true;	String addendum = "";	String fileName = "untitled.nex";		public boolean getExportOptions(CharacterData data, boolean dataSelected, boolean taxaSelected){		MesquiteInteger buttonPressed = new MesquiteInteger(1);		ExporterDialog exportDialog = new ExporterDialog(this,containerOfModule(), "Export NEXUS For MrBayes", buttonPressed);		exportDialog.setSuppressLineEndQuery(true);		exportDialog.setDefaultButton(null);		String helpString = "The MrBayes block shown will be added to the bottom of the exported file; you may wish to edit it before using the file. \n\n";		helpString += "If you check 'simplify names', then Mesquite will remove all characters from the taxon names ";		helpString += "that MrBayes can not read, such as quotes, parentheses, etc.";		exportDialog.appendToHelpString(helpString);//		Checkbox convertToMissing = exportDialog.addCheckBox("convert partial ambiguities to missing", convertAmbiguities);		Checkbox simplifyNamesCheckBox = exportDialog.addCheckBox("simplify names as required for MrBayes", simplifyNames);		exportDialog.addLabel("MrBayes block: ");				addendum = getMrBayesBlock(data);				TextArea fsText =exportDialog.addTextAreaSmallFont(addendum,16);						exportDialog.completeAndShowDialog(dataSelected, taxaSelected);					boolean ok = (exportDialog.query(dataSelected, taxaSelected)==0);		//		convertAmbiguities = convertToMissing.getState();		simplifyNames = simplifyNamesCheckBox.getState();		addendum = fsText.getText();		exportDialog.dispose();		return ok;	}			private String basicBlock(){		String sT = "begin mrbayes;\n\tset autoclose=yes nowarn=yes;";		sT +="\n\tlset nst=6 rates=invgamma;\n\tunlink statefreq=(all) revmat=(all) shape=(all) pinvar=(all); \n\tprset applyto=(all) ratepr=variable;\n\tmcmcp ngen= 10000000 printfreq=1000  samplefreq=1000 nchains=4 savebrlens=yes;\n\tmcmc;\nend;";			 	return sT;	}	private String getMrBayesBlock(CharacterData data){		boolean writeCodPosPartition = false;		boolean writeStandardPartition = false;		CharactersGroup[] parts =null;		if (data instanceof DNAData)			writeCodPosPartition = ((DNAData)data).someCoding();		CharacterPartition characterPartition = (CharacterPartition)data.getCurrentSpecsSet(CharacterPartition.class);		if (characterPartition == null && !writeCodPosPartition)			return basicBlock();		if (characterPartition!=null) {			parts = characterPartition.getGroups();			writeStandardPartition = parts!=null;		}				if (!writeStandardPartition && !writeCodPosPartition) {			return basicBlock();		}		String sT = "begin mrbayes;\n\tset autoclose=yes nowarn=yes;  ";		String codPosPart = "";		boolean molecular = (data instanceof MolecularData);		boolean nucleotides = (data instanceof DNAData);		if (writeCodPosPartition) {			//codon positions if nucleotide			MesquiteInteger numberCharSets = new MesquiteInteger(0);			MesquiteString charSetList = new MesquiteString("");			String codPos = ((DNAData)data).getCodonsAsNexusCharSets(numberCharSets, charSetList);			if (!StringUtil.blank(codPos)) {				codPosPart += "\n\n [codon positions if you wish to use these]";				codPosPart +=codPos;				codPosPart += "\n\tpartition currentPartition = " + numberCharSets.getValue() + ": " + charSetList.toString() + ";";				codPosPart +="\n\tset partition = currentPartition;\n\tlset applyto=(";				for (int i = 1; i<=numberCharSets.getValue(); i++) {					codPosPart += i;					if (i<numberCharSets.getValue()) 						codPosPart += ", ";				}				codPosPart += ");\n";			}					}		String standardPart = "";		if (writeStandardPartition) {			int numCharSets = 0;			standardPart += "\n\n [currently specified groups if you wish to use these]";			for (int i=0; i<parts.length; i++) {				String q = ListableVector.getListOfMatches((Listable[])characterPartition.getProperties(), parts[i], CharacterStates.toExternal(0));				if (q != null) {					standardPart +=  "\n\tcharset " + StringUtil.simplifyIfNeededForOutput(parts[i].getName(),true) + " = " + q + ";";					numCharSets++;				}			}			if (numCharSets <=1)				standardPart = "";			else {				standardPart += "\n\tpartition currentPartition = " + numCharSets + ": ";				boolean firstTime = true;				String nums = "";				int num = 0;				for (int i=0; i<parts.length; i++) {					String q = ListableVector.getListOfMatches((Listable[])characterPartition.getProperties(), parts[i], CharacterStates.toExternal(0));					if (q != null) {						if (!firstTime){							standardPart += ", ";							nums += ", ";						}						firstTime = false;						standardPart += StringUtil.simplifyIfNeededForOutput(parts[i].getName(), true);						nums += Integer.toString(num+1);						num++;					}				}				standardPart +=";\n\tset partition = currentPartition;\n\tlset applyto=(" + nums + ")\n";			}		}		sT += codPosPart + standardPart;		if (molecular)			sT += "\n\tlset nst=6 " + "rates=invgamma;";		else			sT += "\n\tlset nst=1 " + "rates=gamma coding=variable;";		if (data instanceof ProteinData)			sT += "\n\tprset aamodelpr=mixed;";		sT += "\n\tunlink statefreq=(all) revmat=(all) shape=(all) pinvar=(all); \n\tprset applyto=(all) ratepr=variable;\n\tmcmcp ngen= 10000000 printfreq=1000  samplefreq=1000 nchains=4 savebrlens=yes;\n\tmcmc;\nend;";			 	return sT;}		/*.................................................................................................................*/	public void exportFile(MesquiteFile file, String arguments, CommandRecord commandRec) { //if file is null, consider whole project open to export		Arguments args = new Arguments(new Parser(arguments), true);		boolean usePrevious = args.parameterExists("usePrevious");		CategoricalData data = (CategoricalData)getProject().chooseData(containerOfModule(), file, null, CategoricalState.class, "Select data to export", commandRec);		if (data ==null) {			showLogWindow(true);			logln("WARNING: No suitable data available for export to a file of format \"" + getName() + "\".  The file will not be written.\n");			return;		}		MesquiteString dir = new MesquiteString();		MesquiteString fn = new MesquiteString();		String suggested = fileName;		if (file !=null)			suggested = file.getFileName();		MesquiteFile f;		if (!usePrevious){			if (!getExportOptions(data, data.anySelected(), data.getTaxa().anySelected()))				return;		}		String path = getPathForExport(arguments, suggested, dir, fn,  commandRec);		if (path != null) {			f = MesquiteFile.newFile(dir.getValue(), fn.getValue());			if (f !=null){				f.useSimplifiedNexus = true;				f.useDataBlocks = useData;				f.ambiguityToMissing = convertAmbiguities;				f.simplifyNames =simplifyNames;				f.openWriting(true);				f.writeLine("#NEXUS" + StringUtil.lineEnding());				if (!useData)					f.writeLine(((TaxaManager)findElementManager(Taxa.class)).getTaxaBlock(data.getTaxa(), null));				data.getMatrixManager().writeCharactersBlock(data, null, f, null);				if (addendum != null)					f.writeLine(addendum);				f.closeWriting();			}		}	}	/*.................................................................................................................*/ 	/** returns the version number at which this module was first released.  If 0, then no version number is claimed.  If a POSITIVE integer 	 * then the number refers to the Mesquite version.  This should be used only by modules part of the core release of Mesquite. 	 * If a NEGATIVE integer, then the number refers to the local version of the package, e.g. a third party package*/    	public int getVersionOfFirstRelease(){    		return 110;      	}	/*.................................................................................................................*/    	 public String getName() {		return "Export NEXUS for MrBayes";   	 }	/*.................................................................................................................*/   	  	/** returns an explanation of what the module does.*/ 	public String getExplanation() { 		return "Exports NEXUS files for use by MrBayes." ;   	 }	/*.................................................................................................................*/   	    	 }	