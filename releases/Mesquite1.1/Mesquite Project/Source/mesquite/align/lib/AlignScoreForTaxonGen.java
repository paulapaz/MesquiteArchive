package mesquite.align.lib;

import mesquite.align.lib.*;
import mesquite.categ.lib.*;
import mesquite.lib.*;
import mesquite.lib.characters.*;
import mesquite.lib.duties.*;

/* TODO: 
 *   allow user to alter pairwise alignment parameters
 */

public abstract class AlignScoreForTaxonGen extends NumberForTaxon {
	protected MatrixSourceCoord matrixSourceTask;
	protected Taxa currentTaxa = null;
	protected MCharactersDistribution observedStates =null;
	protected PairwiseAligner aligner;
	protected int alphabetLength;
	protected MesquiteInteger comparisonTaxon = new MesquiteInteger(0);
	//MesquiteTimer timer;
	/*.................................................................................................................*/
	public boolean startJob(String arguments, Object condition, CommandRecord commandRec, boolean hiredByName) {
 		matrixSourceTask = (MatrixSourceCoord)hireEmployee(commandRec, MatrixSourceCoord.class, "Source of character matrix (for number of stops)"); 
 		if (matrixSourceTask==null)
 			return sorry(commandRec, getName() + " couldn't start because no source of character matrices was obtained.");
 		//timer =  new MesquiteTimer();
		addMenuItem("Reference Taxon...", MesquiteModule.makeCommand("setReferenceTaxon", this));
/*
 		pairwiseTask = (TwoSequenceAligner)hireEmployee(commandRec, TwoSequenceAligner.class, "Pairwise Aligner");
		if (pairwiseTask == null)
			return sorry(commandRec, getName() + " couldn't start because no pairwise aligner obtained.");
*/		return true;
  	 }
  	 
	/*.................................................................................................................*/
	 public Snapshot getSnapshot(MesquiteFile file) {
	 	Snapshot temp = new Snapshot();
		if (comparisonTaxon.getValue()!=0)
			temp.addLine("setReferenceTaxon " + comparisonTaxon.getValue());
 	 	return temp;
	 }
	/*.................................................................................................................*/
	/** Generated by an employee who quit.  The MesquiteModule should act accordingly. */
 	public void employeeQuit(MesquiteModule employee) {
 		if (employee == matrixSourceTask)  // character source quit and none rehired automatically
 			iQuit();
	}
	/*.................................................................................................................*/
 	/** Override if one wishes to modify the alignment costs away from the default. */
 	 	public int[][] modifyAlignmentCosts(int[][] defaultSubs) {
   		return defaultSubs;
	}
	/*.................................................................................................................*/
	public void initAligner() {
  		MesquiteInteger gapOpen = new MesquiteInteger();
   		MesquiteInteger gapExtend = new MesquiteInteger();
 		AlignUtil.getDefaultGapCosts(gapOpen, gapExtend);  
  		int subs[][] = AlignUtil.getDefaultSubstitutionCosts(alphabetLength);  
  		subs = modifyAlignmentCosts(subs);
   		aligner = new PairwiseAligner(false,subs,gapOpen.getValue(), gapExtend.getValue(), alphabetLength);
   		aligner.setUseLowMem(true);
	}
	/*.................................................................................................................*/
	/** returns whether this module is requesting to appear as a primary choice */
   	public boolean requestPrimaryChoice(){
   		return false;  
   	}

   	/** Called to provoke any necessary initialization.  This helps prevent the module's intialization queries to the user from
   	happening at inopportune times (e.g., while a long chart calculation is in mid-progress)*/
   	public void initialize(Taxa taxa, CommandRecord commandRec){
   		currentTaxa = taxa;
   		matrixSourceTask.initialize(currentTaxa, commandRec);
   		
   	}
   	
	public void calculateNumber(Taxon taxon, MesquiteNumber result, MesquiteString resultString, CommandRecord commandRec){
		if (result==null)
			return;
		result.setToUnassigned();
		Taxa taxa = taxon.getTaxa();
		if (comparisonTaxon.getValue()<0 || comparisonTaxon.getValue()>=taxa.getNumTaxa())
			comparisonTaxon.setValue(0);
		int it = taxa.whichTaxonNumber(taxon);
		if (taxa != currentTaxa || observedStates == null ) {
			observedStates = matrixSourceTask.getCurrentMatrix(taxa, commandRec);
			currentTaxa = taxa;
		}
		if (observedStates==null)
			return;
		DNAData data = (DNAData)observedStates.getParentData();
		
		MesquiteNumber score = new MesquiteNumber();
		

		if (aligner==null) {
			alphabetLength = ((CategoricalState)data.makeCharacterState()).getMaxPossibleState()+1;	  
			initAligner();
		}

		getAlignmentScore(data, (MCategoricalDistribution)observedStates,comparisonTaxon.getValue(),it,score,commandRec);


		if (result !=null)
			result.setValue(score);
		if (resultString!=null)
			resultString.setValue(getScoreName() + " of sequence in matrix "+ observedStates.getName() + ": " + score.getIntValue());
	}
	/*.................................................................................................................*/
	protected abstract void getAlignmentScore(DNAData data, MCategoricalDistribution observedStates, int it1, int it2, MesquiteNumber score, CommandRecord commandRec) ;

	/*.................................................................................................................*/
	public boolean queryReferenceTaxon() {
		MesquiteInteger buttonPressed = new MesquiteInteger(1);
		ExtensibleDialog dialog = new ExtensibleDialog(containerOfModule(), "Reference Taxon",buttonPressed);  //MesquiteTrunk.mesquiteTrunk.containerOfModule()
		
		int maxNum = MesquiteInteger.unassigned;
		if (currentTaxa!=null)
			maxNum = currentTaxa.getNumTaxa();
		IntegerField refTaxonField = dialog.addIntegerField("Reference Taxon", comparisonTaxon.getValue()+1, 8, 1, maxNum);

		dialog.completeAndShowDialog(true);
		if (buttonPressed.getValue()==0)  {
			comparisonTaxon.setValue(refTaxonField.getValue()-1);
			storePreferences();
		}
		dialog.dispose();
		return (buttonPressed.getValue()==0);
	}
	/*.................................................................................................................*/
	public Object doCommand(String commandName, String arguments, CommandRecord commandRec, CommandChecker checker) {
		if (checker.compare(this.getClass(), "Allows one to specify which taxon is the reference taxon.", "[taxon number]", commandName, "setReferenceTaxon")) {
			MesquiteInteger io = new MesquiteInteger(0);
			int newRefTaxon = MesquiteInteger.fromString(arguments, io);
			if (newRefTaxon<0 ||  !MesquiteInteger.isCombinable(newRefTaxon)){
				queryReferenceTaxon();
			}
			else{
				comparisonTaxon.setValue(newRefTaxon);
			}
			parametersChanged(commandRec);
			
		}
		else
			return super.doCommand(commandName, arguments, commandRec, checker);
		return null;
	}
	/*.................................................................................................................*/
	/** Returns CompatibilityTest so other modules know if this is compatible with some object. */
	public CompatibilityTest getCompatibilityTest(){
		return new RequiresAnyMolecularData();
	}
	/*.................................................................................................................*/
   	 public void employeeParametersChanged(MesquiteModule employee, MesquiteModule source, Notification notification, CommandRecord commandRec) {
   	 	observedStates = null;
   	 	super.employeeParametersChanged(employee, source, notification, commandRec);
   	 }
	/*.................................................................................................................*/
    	 public abstract String getScoreName() ;
    /*.................................................................................................................*/
       	 public boolean isSubstantive(){
       	 	return true;
       	 }
    	 
 	public String getParameters() {
 		return getScoreName() + " of sequence in matrix from: " + matrixSourceTask.getParameters();
   	 }
	/*.................................................................................................................*/
   	 
 	/** returns an explanation of what the module does.*/
 	public String getExplanation() {
 		return "Reports the " + getScoreName() + " for a taxon in a data matrix." ;
   	 }
   	 
}



