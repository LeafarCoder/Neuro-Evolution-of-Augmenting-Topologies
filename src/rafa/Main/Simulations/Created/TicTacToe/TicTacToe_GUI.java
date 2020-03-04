package rafa.Main.Simulations.Created.TicTacToe;

import java.io.Serializable;
import java.util.Arrays;

import processing.core.PApplet;

public class TicTacToe_GUI extends PApplet implements Serializable{

	private static final long serialVersionUID = 1L;

	// Game board coordinates and dimension
	private int x_board = 0;					// x upper left board corner
	private int y_board = 0;					// y upper left board corner
	private int hor_cell = 50;				// horizontal cell size
	private int ver_cell = 50;				// vertical cell size

	private int[] pressedCoord;
	int boardSize;
	int[][] gameState;
	boolean finished;
	boolean mouseClicked;

	public TicTacToe_GUI(int board_size){
		boardSize = board_size;
		gameState = new int[boardSize][boardSize];
		mouseClicked = false;
	}

	public void setup(){
		size(500,500);

		pressedCoord = new int[2];
		finished = false;
	}

	public void draw(){
		background(255);
		draw_board();
		
		if(finished){
			drawWinLine();
		}
	}
	
	public void resetBoard(){
		setGameState(new int[boardSize][boardSize]);	// reset gameState to matrix of 0's
	}
	
	public void setGameState(int game_state[][]){
		gameState = game_state;
	}
	
	public void setBoardSizeInPixels(int w, int h){
		hor_cell = Math.floorDiv(w,boardSize);
		ver_cell = Math.floorDiv(h,boardSize);
	}
	
	public int[] getBoardPressedCoord(){
		return pressedCoord;
	}
	
	public void mousePressed(){
		pressedCoord[0] = mouseY / hor_cell;
		pressedCoord[1] = mouseX / ver_cell;
		mouseClicked = true;

	}
	
	public void draw_board(){
		fill(255);
		
		// Print board
		for (int i = 0; i <= 3; i++){
			line(x_board, y_board+i*ver_cell, x_board+3*hor_cell, y_board+i*ver_cell);
			line(x_board+i*hor_cell, y_board, x_board+i*hor_cell, y_board+3*ver_cell);
		}

		// Print pieces
		for (int i = 0; i < boardSize; i++){
			for(int j = 0; j < boardSize; j++){


				if(gameState[i][j] != 0){	// if not empty
					if(gameState[i][j] == 1){	// if player 1 has a piece here
						// Print cross
						float x1 = (float)(x_board + (0.5+j)*hor_cell - 0.4*hor_cell);
						float x2 = (float)(x_board + (0.5+j)*hor_cell + 0.4*hor_cell);
						float y1 = (float)(y_board + (0.5+i)*ver_cell - 0.4*ver_cell);
						float y2 = (float)(y_board + (0.5+i)*ver_cell + 0.4*ver_cell);
						
						line(x1, y1, x2, y2);
						line(x1, y2, x2, y1);
					}else{
						// Print circle
						float x = (float)(x_board + (0.5+j)*hor_cell);
						float y = (float)(y_board + (0.5+i)*ver_cell);
						float radius_x = hor_cell*0.8f;
						float radius_y = ver_cell*0.8f;
						ellipse(x, y, radius_x, radius_y);
					}
				}
			}
		}

	}
	
	public void drawWinLine(){
		// determine the end points
		float endPts[] = getWinEndPoints();

		if(endPts[0] != - 1){
			// convert from index to pixel coordinates
			endPts[0] = (int)(x_board + (endPts[0] + 0.5) * hor_cell);
			endPts[1] = (int)(x_board + (endPts[1] + 0.5) * ver_cell);
			endPts[2] = (int)(y_board + (endPts[2] + 0.5) * hor_cell);
			endPts[3] = (int)(y_board + (endPts[3] + 0.5) * ver_cell);
			
			stroke(255, 0, 0);
			strokeWeight(5);

			line(endPts[0], endPts[1], endPts[2], endPts[3]);

			stroke(0);
			strokeWeight(1);
		}
	}

	private float[] getWinEndPoints(){
		float offset = 0.25f;
		
		float[] no_win = new float[]{-1, -1, -1, -1};	// if we return this same vector it means no one won
		float[] win_ans = new float[4];
		boolean win;

		for(int player = -1; player<=1; player+=2){
			for(int i = 0; i < boardSize; i++){
				win = true;
				win_ans = new float[]{0 - offset, i, 2 + offset, i};
				for(int j = 0; j < boardSize; j++){
					win &= (gameState[i][j] == player);
				}
				if(win)return win_ans;

				win = true;
				win_ans = new float[]{i, 0 - offset, i, 2 + offset};
				for(int j = 0; j < boardSize; j++){
					win &= (gameState[j][i] == player);
				}
				if(win)return win_ans;
			}

			win = true;
			win_ans = new float[]{0 - offset, 0 - offset, 2 + offset, 2 + offset};
			for(int i = 0; i < boardSize; i++){
				win &= (gameState[i][i] == player);
			}
			if(win)return win_ans;
			
			win = true;
			win_ans = new float[]{2 + offset, 0 - offset, 0 - offset, 2 + offset};
			for(int i = 0; i < boardSize; i++){
				win &= (gameState[i][boardSize - i - 1] == player);			
			}
			if(win)return win_ans;
		}

		return no_win;

	}

}
