package application;
	
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;

/**
 * Draws a tree with a horizontal orientation so that all nodes on the same level
 * have equal y-position values. The file is saved in the containing folder as "Tree.png".
 * If there is more than one top-most node, set a common pseudo-root as the parent for all of the top nodes before drawing the tree.
 * 
 * @author Alan Gan
 */
public class HorizontalOrientedTreeView extends TreeView
				  implements TreePainter {
	
	private static final double HORIZONTAL_WINDOW_PADDING = 25.0f;
	private static final double VERTICAL_WINDOW_PADDING = 20.0f;
	private static final double verticalPadding = 30.0;
	private static final double horizontalPadding = 10.0;
	private static double windowWidth;
	private static double windowHeight;
	private static Stage stage;
	private static File treeFile;
	
 @Override
 public void start(Stage primaryStage) {

	 stage = primaryStage;
	 
	 Tree<String> tree = loadTree();
	 if(tree == null)
		 tree = setupTree();
	 
	 int treeWidth = tree.maxWidth();
	 int treeHeight = getTreeHeight(tree);
	 windowWidth = treeWidth * (RECT_WIDTH + horizontalPadding) + HORIZONTAL_WINDOW_PADDING * 2;
	 windowHeight = treeHeight * (RECT_HEIGHT + verticalPadding) + VERTICAL_WINDOW_PADDING * 2;
	 
	 VBox vb = new VBox();
     ScrollPane root = new ScrollPane();
     
     vb.getChildren().add(root);
     VBox.setVgrow(vb, Priority.ALWAYS);
     
     root.setMaxHeight(600);
     root.setPrefSize(windowWidth, windowHeight);
     Scene scene = new Scene(vb, windowWidth, windowHeight);
     WritableImage wim = new WritableImage((int)windowWidth, (int)windowHeight);

     Canvas canvas = new Canvas(windowWidth, windowHeight);
     GraphicsContext gc = canvas.getGraphicsContext2D();
     
     draw(gc, tree);
     canvas.snapshot(null, wim);
     
     root.setContent(canvas);
     primaryStage.setTitle("Tree Structure");
     primaryStage.setScene(scene);
     primaryStage.setResizable(false);
     primaryStage.show();
     
     File file = new File("Tree.png");

     try {
         ImageIO.write(SwingFXUtils.fromFXImage(wim, null), "png", file);
     } catch (Exception s) {
    	 s.printStackTrace();
     }
     
 }
 
 public static int getTreeHeight(Tree<String> tree)
 {
	 return tree.getHeight(tree, 0, Integer.MIN_VALUE);
 }

 /**
  * Prompts the user for a text file containing a formatted tree.
  * Returns the tree as a <code>Tree{@literal <String>}</code>.
  * <br>
  * The root of the tree must be at the first line of the file with no indents.
  * Each level of the tree is marked by a TAB (\t) character only. Do not include any leading spaces
  * in the file. The file must be a readable text file.
  * 
  * <p>
  * For instance, the following input <br>
  * <img src="http://i61.tinypic.com/2j122ki.png"><p>
  * would output<br>
  * <img src="http://i57.tinypic.com/4kfzlv.png">
  *<p>
  * To implement your load method, you may extend this class and override this method. If you do so,
  * be sure to include {@code launch(args)} in the new {@code main} method.
  * 
  * 
  * @return      the tree parsed from the user-selected text file<br>
  * 		     {@code null} if the selected file does not exist or if the user cancels file selection
  */
 public static Tree<String> loadTree()
 {
	 if(loadTreeFile())
	 {
		 Scanner reader = null;
		 try {
			 reader = new Scanner(treeFile);
			 
			 String line, parent;
			 Tree<String> tree = new Tree<String>("");
			 LinkedList<String> stringList = new LinkedList<String>();
			 int level;
			 
			 while(reader.hasNextLine() && (line = reader.nextLine()) != null)
			 {
				 level = getNumberOfTabs(line.toCharArray());
				 parent = null;
				 if(level == 0)
				 {
					 line = line.replaceAll("\t", "");
					 tree.setData(line);
				 }
				 else 
				 {
					 int prevLevel = Integer.MAX_VALUE;
					 int listSize = stringList.size();
					 parent = stringList.getLast();
					 
					 while(prevLevel >= level)
					 {	
						 parent = stringList.get(listSize - 1);
						 prevLevel = getNumberOfTabs(parent.toCharArray());
						 listSize--;
					 }
					 
					 parent = parent.replaceAll("\t", "");
					 line = line.replaceAll("\t", "");
					 tree.addNode(parent, line);
				 }
				 
				 String tabs = "";
				 for(int i = 0; i < level; i++)
					 tabs += "\t";
				 stringList.add(tabs + line);
			 }
			 
			 return tree;
		 } catch (IOException e)  {
		 	 e.printStackTrace();
		 	 return null;
		 } finally {
			 reader.close();
		 }
	 }
	 return null;
 }
 
 private static boolean loadTreeFile()
 {
	 FileChooser fileChooser = new FileChooser();
	 fileChooser.setTitle("Open Tree Resource");
	 treeFile = fileChooser.showOpenDialog(stage);
	 if(treeFile != null && treeFile.exists())
		 return true;
	 return false;
 }
 
 public static Tree<String> setupTree()
 {
	 Tree<String> tree = new Tree<String>("Root");
	 tree.addNode("Hello").addNode("What's Up").addNode("Who").addNode("what").addNode("When").addNode("where").addNode("goodbye").addNode("for").addNode("now");
	 tree.addNode("Hey there").addNode("wee").addNode("woo").addNode("wa").addNode("HereIam");
	 tree.addNode("What's Up", "Why");
	 tree.addNode("Root", "Yo");
	 tree.addNode("Root", "IAMHERE");
	 tree.addNode("Root", "foo");
	 tree.addNode("Root", "bar");
	 tree.addNode("Root", "foobar");
	 return tree;
 }
 
 public void draw(GraphicsContext gc, Tree<String> tree) {
	 double initHeight = VERTICAL_WINDOW_PADDING;
	 
     gc.setFill(Color.BLACK);
     gc.setStroke(Color.BLACK);
     gc.setLineWidth(2);
     
     List<Tree<String>> currNodes = new ArrayList<Tree<String>>();
     List<Tree<String>> nextNodes = new ArrayList<Tree<String>>();
     currNodes.add(tree);
     
     List<Point2D> parentMidptCoords = new ArrayList<Point2D>();
     List<Point2D> childMidptCoords = new ArrayList<Point2D>();
     
     while(!currNodes.isEmpty())
     {
    	 double initOffsetValue = offset(currNodes.size());
    	 double offset = 0;
    	 for(Tree<String> t : currNodes)
    	 {
    		 String data = t.getData();	 
    		 
    		 gc.fillText(data, initOffsetValue + offset + RECT_WIDTH/3.4, initHeight +
    				 	 RECT_HEIGHT/1.6, RECT_WIDTH/2);
    		 gc.strokeRect(initOffsetValue + offset,
    				       initHeight, RECT_WIDTH, RECT_HEIGHT);
    		 
    		 if(t.hasChildren())
    		 {
    			 for(int i = 0; i < t.getChildNodes().size(); i++)
    				 parentMidptCoords.add(new Point2D(initOffsetValue + offset + RECT_WIDTH/2,
    					 						       initHeight + RECT_HEIGHT));
    		 }
    		 
    		 offset += RECT_WIDTH + horizontalPadding;
    		 nextNodes.addAll(t.getChildNodes());
    	 }
    	 
    	 double nextInitialOffset = offset(nextNodes.size());
    	 double nextOffset = RECT_WIDTH/2;
    	 
    	 for(int i = 0; i < nextNodes.size(); i++)
    	 {
    		 childMidptCoords.add(new Point2D(nextInitialOffset + nextOffset,
    				 						  initHeight + RECT_HEIGHT + verticalPadding));
    		 nextOffset += RECT_WIDTH + horizontalPadding;
    	 }
    	 
    	 for(int j = 0; j < parentMidptCoords.size(); j++)
    	 {
    		 Point2D pt = parentMidptCoords.get(j);
    		 Point2D pt2 = childMidptCoords.get(j);
    		 gc.strokeLine(pt.getX(), pt.getY(), pt2.getX(), pt2.getY());
    	 }
    	 
    	 initHeight += RECT_HEIGHT + verticalPadding;
    	 currNodes.clear();
    	 currNodes.addAll(nextNodes);
    	 nextNodes.clear();
    	 parentMidptCoords.clear();
    	 childMidptCoords.clear();
     }
 }
 
 public double offset(int levelWidth)
 {
	 boolean isEven = levelWidth % 2 == 0;
	 double evenOddOffset = isEven ? RECT_WIDTH/2 : 0;
	 double offset = windowWidth/2 - levelWidth/2 * (RECT_WIDTH + horizontalPadding)
			 		 - HORIZONTAL_WINDOW_PADDING + evenOddOffset;
	 return offset;
 }
}