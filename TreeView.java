package application;

import javafx.application.Application;

public abstract class TreeView extends Application {
	
	public static int getTreeHeight(Tree<String> tree) {
		return tree.getHeight(tree, 0, Integer.MIN_VALUE);
	}
	
	public static int getNumberOfTabs(char[] charArray) {
		int tabNum = 0;
		for(char c : charArray) {
			if(c == '\t')
				tabNum++;
			else
				break;
		}
		return tabNum;
	}
}
