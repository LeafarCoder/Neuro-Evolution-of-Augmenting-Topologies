package rafa.Main;
import processing.core.*;

public class Main_PApplet extends PApplet{
	
	// Variables declaration
	private int x_pos = 10;
	private int y_pos = 10;
	private int height = 80;
	private int width = 80;
	
	GameManager game_manager = new GameManager(this, x_pos, y_pos, height, width);
	int boardSize = game_manager.get_boardSize();
	
	// Prevents close of PApplet to finish all processes
	
/*	static final void removeExitEvent(final PSurface surf) {
		final java.awt.Window win= ((processing.awt.PSurfaceAWT.SmoothCanvas) surf.getNative()).getFrame();
		for (final java.awt.event.WindowListener evt : win.getWindowListeners())win.removeWindowListener(evt);
	}*/
	
	

	public void setup(){
		
		size(500,500);
		//removeExitEvent(getSurface());

		/*
		game_manager.update_game_state(1,0);
		game_manager.update_game_state(2,8);
		game_manager.update_game_state(1,3);
		game_manager.update_game_state(1,7);
		*/
	}
	public void draw(){
		background(255);
		
		game_manager.draw_board();
	}
	
	public void mousePressed(){
		int x_m = mouseX;
		int y_m = mouseY;
		int button = 0;
		switch (mouseButton) {
		case LEFT:
			button=1;
			break;
		case RIGHT:
			button=-1;
			break;
		case CENTER:
			button=0;
			break;
		}
		int x = (x_m-x_pos)/width;
		int y = (y_m-y_pos)/height;
		
		if((x>=0 && x<boardSize) || (y>=0 && y<boardSize)){
			game_manager.update_gameState(button, x, y);
		}
	}
	
	
	
	/*
	@ Override
	public void exit() {
	  }
	*/
	
}