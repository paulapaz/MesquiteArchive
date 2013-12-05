//package wpm;

import java.awt.*;
import java.applet.Applet;


public class BeanTree extends Applet
{
	static TreeCanvas TreeDisplay;
	static BeanTreeControls controls;


	public void init()
	{
		setLayout( new BorderLayout() );
		resize(300, 300);
		TreeDisplay = new TreeCanvas(10, 10, 400, 400);
		add( "Center", TreeDisplay );
		add("North", controls = new BeanTreeControls(TreeDisplay));
		TreeDisplay.Tree = new TreeClass();
		TreeDisplay.controls = controls;
		TreeDisplay.Tree.SubRoot = new Branch(null, false);
		TreeDisplay.Tree.Root = new Branch(TreeDisplay.Tree.SubRoot, false);
		TreeDisplay.edgewidth = 10;
		TreeDisplay.needsUpdating = false;
		TreeDisplay.highlightedBranch = null;
		TreeDisplay.Tree.exists = false;
		repaint();
	} 

    public void start() {
	controls.enable();
    }

    public void stop() {
	controls.disable();
    }
	public static void main(String args[])
	{
		com.metrowerks.AppletFrame.startApplet("BeanTree", "BeanTree", args);
	}

}
/** ======================================================================== */
class TreeCanvas extends Canvas  {
	TreeClass Tree;
	
	int x_max, y_max;
	int edgewidth;
	Branch highlightedBranch, branchFrom;
	int xFrom, yFrom, xTo, yTo;
	boolean needsUpdating;
	String treeDescription;
	BeanTreeControls controls;
	private int lastleft;
	private int taxspacing;
	private Branch foundBranch;
	
	
	public TreeCanvas( int x_max, int y_max, int x_size, int y_size ) 
	{
		this.x_max = x_max;
		this.y_max = y_max;
		
		resize( x_size, y_size );
		repaint();
	}
	
	
	/*_________________________________________________*/
  private void WriteTreeDescription(Branch N) {
      if (N.left!=null)
      	{
	treeDescription+='(';
      	WriteTreeDescription(N.left);
	treeDescription+=',';
	WriteTreeDescription(N.right);
	treeDescription+=')';
      	}
      else
      	{
	treeDescription+='*';
      	}
   }
    public String Describe() {
      treeDescription="";
      WriteTreeDescription(Tree.Root);
      treeDescription+=';';
      return treeDescription;
    }
	/*_________________________________________________*/
	private void CalcInternalLocs(Branch N)
	{
		if (N.left != null) //internal
			{
				CalcInternalLocs(N.left);
				CalcInternalLocs(N.right);
				
				N.y = (-N.left.x + N.right.x+N.left.y + N.right.y) / 2;
				N.x =(N.left.x + N.right.x - N.left.y + N.right.y) / 2;
			}
	}
		
	/*_________________________________________________*/
	private void CalcTerminalLocs(Branch N)
	{
		if (N.left == null) //terminal
			{
				lastleft+= taxspacing;
				N.y = 90;
				N.x = lastleft;
			}
		else
			{
				CalcTerminalLocs(N.left);
				CalcTerminalLocs(N.right);
			}
	}

	/*_________________________________________________*/
	private void CalcBranchPolys(Branch N)
	{
		if (N.left != null) //terminal
			{
			CalcBranchPolys(N.left);
			CalcBranchPolys(N.right);
			N.branchPoly.npoints=0;
			N.branchPoly.addPoint(N.x, N.y);
			N.branchPoly.addPoint(N.x+edgewidth/2, N.y-edgewidth/2);
			N.branchPoly.addPoint(N.x+edgewidth, N.y);
			N.branchPoly.addPoint(N.ancestor.x+edgewidth, N.ancestor.y);
			N.branchPoly.addPoint(N.ancestor.x, N.ancestor.y);
			N.branchPoly.npoints=5;
			}
		else
			{
			N.branchPoly.npoints=0;
			N.branchPoly.addPoint(N.x, N.y);
			N.branchPoly.addPoint(N.x+edgewidth, N.y);
			N.branchPoly.addPoint(N.ancestor.x+edgewidth, N.ancestor.y);
			N.branchPoly.addPoint(N.ancestor.x, N.ancestor.y);
			N.branchPoly.npoints=4;
			}
	}
	/*_________________________________________________*/
	private void calcBranches() {
		lastleft = 0;
		taxspacing = (bounds().width - 10) / Tree.numTaxa;
		CalcTerminalLocs(Tree.Root);
		CalcInternalLocs(Tree.Root);
		Tree.SubRoot.y = (Tree.Root.y)+20;
		Tree.SubRoot.x = (Tree.Root.x)-20;
		CalcBranchPolys(Tree.Root);
	}
	
	/*_________________________________________________*/
	private   void drawBranch(Graphics g, Branch N) {
	      g.setColor(N.color);
	      g.fillPolygon(N.branchPoly);
	      if (N.left != null)
	      	drawBranch(g, N.left);
	      if (N.right != null)
	      	drawBranch(g, N.right);
	   }
	/*_________________________________________________*/
	public   void drawTree(Graphics g) {
	        if (Tree.exists)
	        {
	        	calcBranches();
	       	 	drawBranch(g, Tree.Root);  
	       	 }
	   }
	/*_________________________________________________*/
	   public void paint(Graphics g) {
	   	drawTree(g);
	   	needsUpdating = false;
	   }
	/*_________________________________________________*/
	
	public   void InvertBranch(Graphics g, Branch N) {
	     // N.color=Color.white;
	      highlightedBranch=N;
	      g.setColor(Color.white);
	     g.fillPolygon(N.branchPoly);
	      // needsUpdating=true;
	   }
	   
	/*_________________________________________________*/
	public   void RevertBranch(Graphics g, Branch N) {
	   // N.color=Color.black;
	    highlightedBranch=null;
	      g.setColor(Color.black);
	      g.fillPolygon(N.branchPoly);
	      // needsUpdating=true;
	   }
	/*_________________________________________________*/
	private void ScanBranches(Branch N, int x, int y)
	{
		if (foundBranch==null) {
			if (N.branchPoly.inside(x, y))
				foundBranch = N;
			if (N.left != null) //terminal
				{
				ScanBranches(N.left, x, y);
				ScanBranches(N.right, x, y);
				}
		}
	}
	/*_________________________________________________*/
	public   Branch FindBranch(int x, int y) {
	        foundBranch=null;
	        ScanBranches(Tree.Root, x, y);
	        return foundBranch;
	}
	
	/*_________________________________________________*/
	public   void ScanTouch(Graphics g, int x, int y) {
	        Branch branchFound=FindBranch( x, y);
	        if (branchFound!=null) 
        		{
        		branchFrom=branchFound;
        		xFrom=x;
        		yFrom=y;
        		xTo=x;
        		yTo=y;
        		g.setXORMode(Color.gray);
	   		g.setColor(Color.black);
	   		g.drawLine(xFrom,yFrom,xTo,yTo);
        		}
 
	   }
	/*_________________________________________________*/
	public   void ScanFlash(Graphics g, int x, int y) {
	        Branch branchFound=FindBranch( x, y);
	        if (highlightedBranch != null)
	        	{if (branchFound==null) 
	        		RevertBranch(g, highlightedBranch);
	        	else if (branchFound!=highlightedBranch) 
	        		{
	        		RevertBranch(g, highlightedBranch); 
	        		InvertBranch(g, branchFound);
	        		}
	        	}
	        else if (branchFound!=null) 
	        	InvertBranch(g, branchFound);  
	   }
	/*_________________________________________________*/
	public   void ScanDrop(Graphics g, int x, int y) {
	        Branch branchTo=FindBranch( x, y);
	        if (branchTo != null)
	        	{if (branchFrom==branchTo) 
	        		{
	        		}
	        	else 
	        		{
	        		Tree.MoveBranch(branchFrom, branchTo);
	        		controls.RedescribeTree();
	        		}
	        	};  
      		 needsUpdating=true;
	        branchFrom = null;
	        
	   }
	/*_________________________________________________*/
	   public boolean mouseMove(Event e, int x, int y) {
	   	Graphics g = getGraphics();
	   	ScanFlash(g, x, y);
	      return true;
	   }
	/*_________________________________________________*/
	   public boolean mouseDown(Event e, int x, int y) {
	   	Graphics g = getGraphics();
	   	ScanTouch(g, x, y);
	   	if (needsUpdating)
	   		repaint();
	      return true;
	   }

	/*_________________________________________________*/
	   public boolean mouseDrag(Event e, int x, int y) {
	   	if (branchFrom!=null)
	   		{
		   	Graphics g = getGraphics();
	   		g.setColor(Color.black);
        		g.setXORMode(Color.gray);
	   		g.drawLine(xFrom,yFrom,xTo,yTo);
        		xTo=x;
        		yTo=y;
	   		g.drawLine(xFrom,yFrom,xTo,yTo);
	   		}
	      return true;
	   }
	/*_________________________________________________*/
	   public boolean mouseUp(Event e, int x, int y) {
	   	Graphics g = getGraphics();
	   	if (branchFrom!=null) {
	   		ScanDrop( g, x, y);
		   	repaint();
	   	}
	   	branchFrom=null;
        	g.setXORMode(g.getColor());
	      return true;
	   }
	}
	
	

/** ======================================================================== */
public class TreeClass {
   Branch Root, SubRoot;
  int  numTaxa;
  boolean exists;
   
   private int stringloc;
   
    /** ________________  read tree ________________  */
  private void ParseTreeDescription(String TreeDescription, Branch Ancestor) {
      if (TreeDescription.charAt(stringloc) == '(')
      	{
      	stringloc++;
	Ancestor.sprout();
      	ParseTreeDescription(TreeDescription, Ancestor.left);
	stringloc++;  //skip comma
	ParseTreeDescription(TreeDescription, Ancestor.right);
      	stringloc++; //skip parensu
      	}
      else
      	{
      	stringloc++; //skip tt name
      	numTaxa++;
      	}
   }
   public void ReadTree(String TreeDescription) {
      stringloc=0;
      numTaxa=0;
      ParseTreeDescription(TreeDescription, Root);
      exists=true;
    }

    /** ______________________________________________  */
    
    private boolean DescendantOf(Branch branchD, Branch branchA) {
    	return false;
    }
    
    private boolean NodeSlantsRight(Branch N) {
    	if (N.ancestor.right==N) 
    		return true;
    	else
    		return false;
    }
   private Branch Sister(Branch N) {
    	if (N.ancestor.right==N) 
    		return N.ancestor.left;
    	else
    		return N.ancestor.right;
    }
   /** ______________________________________________  */
    public void MoveBranch(Branch branchFrom, Branch branchTo) {
	
		Branch sideN,C, G, F, H, NCAnc;
		boolean fromancslantRight, fromslantRight, toslantRight, NCslantRight, fromroot, toroot;

		if ((branchFrom.ancestor.ancestor == branchTo) & (branchTo == Root)) 
			{
				branchFrom = Sister(branchFrom);
				branchTo = Sister(branchFrom.ancestor);
			}
		if ((branchFrom.ancestor == Root) & (branchTo.ancestor.ancestor == Root))  
			{
				sideN = branchFrom;
				branchFrom = branchTo;
				branchTo = sideN;
			}

		fromroot = false;
		toroot = false;
		fromslantRight = NodeSlantsRight(branchFrom);
		fromancslantRight = NodeSlantsRight(branchFrom.ancestor);
		toslantRight = NodeSlantsRight(branchTo);
		G = Sister(branchFrom);
		H = branchFrom.ancestor.ancestor;
		C = branchTo.ancestor;
		F = branchFrom.ancestor;

		if (F == Root)  
			fromroot = true;
		else if (branchTo == Root )
			toroot = true;

		if (fromslantRight == toslantRight)  
			{
				sideN = F.left;
				F.left = F.right;
				F.right = sideN;
			}

		G.ancestor = H;
		F.ancestor = C;
		branchTo.ancestor = F;
		if (fromancslantRight)  
			H.right = G;
		else
			H.left = G;
		if (toslantRight)  
			{
				F.right = branchTo;
				C.right = F;
			}
		else
			{
				F.left = branchTo;
				C.left = F;
			}
		sideN = Sister(G);

		if (fromroot)  
			{
				sideN = Root;
				Root = G;
				G = sideN;
			}
		if (toroot)  
			{
				sideN = Root;
				Root = F;
				F = sideN;
			}
		if (!NodeSlantsRight(Root))  
			{
				SubRoot.right = Root;
			}
		
	}

}
/** ======================================================================== */
public class Branch {
   Branch left, right, ancestor;
   int x;
   int y;
   Polygon branchPoly;
   Color color;
   
   Branch(Branch Ancestor, boolean isFirstDescendant) {
   	this.ancestor = Ancestor;
   	this.color=Color.black;
	branchPoly= new Polygon();
	branchPoly.xpoints = new int[5];
	branchPoly.ypoints = new int[5];
	branchPoly.npoints=5;
   }
   
   public boolean sprout() {
      this.left = new Branch(this, true);
      this.right = new Branch(this, false);
      return true;
   }
}

class BeanTreeControls extends Panel {
    TextField tf;
    TreeCanvas TreeDisplay;

	public BeanTreeControls(TreeCanvas TreeDisplay) {
		this.TreeDisplay = TreeDisplay;
		add(tf = new TextField("(((*,*),(*,*)),(((*,*),*),*));", 40));
		add(new Button("Show"));
	}

	public boolean action(Event ev, Object arg) {
	if (ev.target instanceof Button) {
		/*  destroy current tree if present */
		/*Display.startApplet("BeanTree.html", "BeanTree.html", args);*/
		
		
		TreeDisplay.treeDescription=tf.getText();
		TreeDisplay.Tree.ReadTree(TreeDisplay.treeDescription);
		TreeDisplay.repaint();
		return true;
		}
	return false;
	}
	
	public void RedescribeTree() {
		TreeDisplay.treeDescription = TreeDisplay.Describe();
		tf.setText(TreeDisplay.treeDescription);
		repaint();
	}

}


