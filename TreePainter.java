package application;

import javafx.scene.canvas.GraphicsContext;

public interface TreePainter {
	static final int RECT_WIDTH = 70;
	static final int RECT_HEIGHT = 25;
	
	public double offset(int levelWidth);
	
	void draw(GraphicsContext gc, Tree<String> tree);
}
