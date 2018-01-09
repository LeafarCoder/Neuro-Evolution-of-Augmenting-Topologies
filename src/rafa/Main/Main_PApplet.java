package rafa.Main;
import processing.core.PApplet;
import rafa.NEAT.*;

public class Main_PApplet extends PApplet{
	
	// Variables declaration
	GameManager game_manager = new GameManager(this,10,10,80,80);

	// Prevents close of PApplet to finish all processes
	/*
	static final void removeExitEvent(final PSurface surf) {
		final java.awt.Window win= ((processing.awt.PSurfaceAWT.SmoothCanvas) surf.getNative()).getFrame();
		for (final java.awt.event.WindowListener evt : win.getWindowListeners())win.removeWindowListener(evt);
	}
	*/
	
	public void settings(){
		size(500,500);
		
		
		//frame.setTitle("3 em linha");
	}
	public void setup(){
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
			button=2;
			break;
		case CENTER:
			button=0;
			break;
		}
		int index = (x_m-10)/80 + 3*((y_m-10)/80);
		
		if(index>=0 && index<=8){
			System.out.println((x_m-10)/80+" "+(y_m-10)/80+" "+index);
			game_manager.update_game_state(button, index);
		}
	}
	
	
	
	/*
	@ Override
	public void exit() {
	  }
	*/
	
}