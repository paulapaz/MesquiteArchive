/* Mesquite source code.  Copyright 1997-2006 W. Maddison and D. Maddison.Version 1.11, June 2006.Disclaimer:  The Mesquite source code is lengthy and we are few.  There are no doubt inefficiencies and goofs in this code. The commenting leaves much to be desired. Please approach this source code with the spirit of helping out.Perhaps with your help we can be more than a few, and make Mesquite better.Mesquite is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.Mesquite's web site is http://mesquiteproject.orgThis source code and its compiled class files are free and modifiable under the terms of GNU Lesser General Public License.  (http://www.gnu.org/copyleft/lesser.html)*/package mesquite.lib.duties;import java.awt.*;import mesquite.lib.*;/* ======================================================================== *//**.*/public abstract class TreeTransformer extends MesquiteModule  {   	 public Class getDutyClass() {   	 	return TreeTransformer.class;   	 } 	public String getDutyName() { 		return "Tree Transformer";   	 }	//Returns true if calling code is responsible for notifying listeners of tree.  This is the old version and is present here for compatibility; it should be considered deprecated.  Old classes will override this	public boolean transformTree(AdjustableTree tree, MesquiteString resultString, CommandRecord commandRec){		return false;	}	/**/		//Should be overridden; when previous version is deleted in future, this will be abstract.  returns whether successfully transformed.	public boolean transformTree(AdjustableTree tree, MesquiteString resultString, boolean notify, CommandRecord commandRec){				if (transformTree(tree, resultString, commandRec)){			if (notify && tree instanceof Listened)				((Listened)tree).notifyListeners(this, new Notification(MesquiteListener.UNKNOWN), commandRec);		}		return true;			}}