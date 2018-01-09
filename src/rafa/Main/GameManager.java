package rafa.Main;
import processing.core.PApplet;

public class GameManager {
	// Game board coordinates and dimension
	int x_board=0;					// x upper left board corner
	int y_board=0;					// y upper left board corner
	int hor_cell=50;				// horizontal cell size
	int ver_cell=50;				// vertical cell size
	private int[] game_state=new int[9];	// game board state
	private boolean turn;					// player's turn
	PApplet p;						// PApplet
	
	GameManager(PApplet p_applet){
		p=p_applet;
	}
	
	GameManager(PApplet p_applet, int x_b, int y_b, int h_c, int v_c){
		p=p_applet;
		x_board=x_b;
		y_board=y_b;
		hor_cell=h_c;
		ver_cell=v_c;
	}
	
	boolean get_turn(){
		return turn;
	}
	int get_game_state(int pos){
		return game_state[pos];
	}
	int[] get_game_state(){
		return game_state;
	}
	
	void update_game_state(int player, int pos){
		if(player>2 || player<0 || pos<0 || pos >8)
			System.err.println("GameManager: Error updating game state (invalid parameters)");
		
		game_state[pos]=player;
		System.out.println("Has anyone won? "+check_win());
	}
	
	void change_turn(){
		turn=!turn;
	}
	
	boolean valid_move(int pos){
		return game_state[pos]==0;
	}
	
	int check_win(){
		// Lines
		for (int i=0; i<3; i++)
			if(game_state[3*i]==game_state[3*i+1] && game_state[3*i]==game_state[3*i+2] && game_state[3*i]!=0) return game_state[3*i];
		// Columns
		for (int i=0; i<3; i++)
			if(game_state[i]==game_state[i+3] && game_state[i]==game_state[i+6] && game_state[i]!=0) return game_state[i];	
		// Diagonals
		if(game_state[0]==game_state[4] && game_state[0]==game_state[8] && game_state[0]!=0) return game_state[0];
		if(game_state[2]==game_state[4] && game_state[2]==game_state[6] && game_state[2]!=0) return game_state[2];
		
		return 0;
	}
	
	void reset_game(){
		for (int i=0; i<9; i++)game_state[i]=0;
	}
	
	// Graphics
	void draw_board(){
		p.fill(255);
		
		// Print board
		for (int i=0; i<=3; i++)p.line(x_board, y_board+i*ver_cell, x_board+3*hor_cell, y_board+i*ver_cell);
		for (int i=0; i<=3; i++)p.line(x_board+i*hor_cell, y_board, x_board+i*hor_cell, y_board+3*ver_cell);
		
		// Print pieces
		for (int i=0; i<9; i++){
			if(game_state[i]!=0){
				if(game_state[i]==1){
					// Print circle
					p.ellipse((float)(x_board+(0.5+i%3)*hor_cell),(float) (y_board+(0.5+i/3)*ver_cell)
							, (float)(ver_cell*0.8), (float)(ver_cell*0.8));
				}else{
					// Print cross
					p.line((float)(x_board+(0.5+i%3)*hor_cell - 0.4*hor_cell),(float) (y_board+(0.5+i/3)*ver_cell - 0.4*ver_cell),
							(float)(x_board+(0.5+i%3)*hor_cell + 0.4*hor_cell),(float) (y_board+(0.5+i/3)*ver_cell + 0.4*ver_cell));
					
					p.line((float)(x_board+(0.5+i%3)*hor_cell - 0.4*hor_cell),(float) (y_board+(0.5+i/3)*ver_cell + 0.4*ver_cell),
							(float)(x_board+(0.5+i%3)*hor_cell + 0.4*hor_cell),(float) (y_board+(0.5+i/3)*ver_cell - 0.4*ver_cell));
				}
			}
		}
		
	}
	
}
