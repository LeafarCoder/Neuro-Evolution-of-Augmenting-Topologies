package rafa.Main;
import processing.core.PApplet;

public class GameManager {
	// Game board coordinates and dimension
	private int x_board=0;					// x upper left board corner
	private int y_board=0;					// y upper left board corner
	private int hor_cell=50;				// horizontal cell size
	private int ver_cell=50;				// vertical cell size
	
	private int boardSize = 3;
	private int[][] gameState = new int[boardSize][boardSize];
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
	
	int get_gameState(int x, int y){
		return gameState[x][y];
	}
	
	int[][] get_gameState(){
		return gameState;
	}
	
	int get_boardSize(){
		return boardSize;
	}
	
	public void update_gameState(int player, int x, int y){
		if(player>1 || player<-1 || x<0 || x>=boardSize || y<0 || y>=boardSize)
			System.err.println("Main::GameManager::update_gameState: Error updating game state (invalid parameters)");
		else
			gameState[x][y]=player;
	}
	
	void change_turn(){
		turn=!turn;
	}
	
	boolean valid_move(int x, int y){
		return gameState[x][y]==0;
	}
	
public int checkWin(){
		// return:
		// 1 if player 1 wins
		// -1 if plater -1 wins
		// 2 if board is full and no one won
	
		for(int player = -1; player<=1; player+=2){
			boolean win = false;
			for(int i = 0; i < boardSize; i++){
				boolean win_row = true;
				boolean win_col = true;
				for(int j = 0; j < boardSize; j++){
					win_row &= (gameState[i][j] == player);
					win_col &= (gameState[j][i] == player);
				}
				win |= (win_row || win_col);
				if(win)return player;
			}
			
			boolean win_diag1 = true;
			boolean win_diag2 = true;
			for(int i = 0; i < boardSize; i++){
				win_diag1 &= (gameState[i][i] == player);
				win_diag2 &= (gameState[i][boardSize - i - 1] == player);			
			}
			win |= (win_diag1 || win_diag2);
			if(win)return player;
		}
		
		boolean full = true;
		for(int i = 0; i < boardSize; i++){
			for(int j = 0; j < boardSize; j++){
				full &= gameState[i][j] != 0;
			}
		}
		// board is full and no one has win!
		if(full) return 2;
		
		return 0;
	}
	
	// Graphics
	void draw_board(){
		p.fill(255);
		
		// Print board
		for (int i=0; i<=3; i++)p.line(x_board, y_board+i*ver_cell, x_board+3*hor_cell, y_board+i*ver_cell);
		for (int i=0; i<=3; i++)p.line(x_board+i*hor_cell, y_board, x_board+i*hor_cell, y_board+3*ver_cell);
		
		// Print pieces
		for (int i=0; i<boardSize*boardSize; i++){
			if(gameState[i%boardSize][i/boardSize]!=0){
				if(gameState[i%boardSize][i/boardSize]==1){
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
