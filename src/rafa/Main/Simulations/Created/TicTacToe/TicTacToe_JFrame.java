package rafa.Main.Simulations.Created.TicTacToe;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.Serializable;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import processing.core.PApplet;
import rafa.GUI.NetworkGraph;
import rafa.Main.Simulations.Created.TicTacToe.TicTacToe_Sim.RunSimulationOnBackground;
import rafa.NEAT.Network;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.JLabel;
import javax.swing.JTextPane;
import javax.swing.SwingWorker;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.border.TitledBorder;
import javax.swing.UIManager;
import java.awt.Color;
import javax.swing.JRadioButton;
import java.awt.Font;

public class TicTacToe_JFrame extends JFrame implements Serializable{

	private TicTacToe_GUI sketch_board;
	private NetworkGraph sketch_net;
	
	JPanel panel_board;
	JPanel panel_net;
	
	JTextPane fitness_text;
	
	TicTacToe_Sim parent;
	
	private ButtonGroup bG;
	private JRadioButton rdbtnPc;
	private JRadioButton rdbtnUser;
	private JLabel playerTurn_lbl;
	

	public TicTacToe_JFrame(TicTacToe_GUI board_gui, NetworkGraph net_graph, TicTacToe_Sim parent) {
		this.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){
            	sketch_board.stop();
            	sketch_net.stop();
            }
        });
		
		// Changes overall look (look and feel) of the window
		try{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch(Exception e){
			e.printStackTrace();
		}


		this.sketch_board = board_gui;
		this.sketch_net = net_graph;
		this.parent = parent;

		setTitle("TicTacToe");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setResizable(false);
		setVisible(true);
		setLocation(10, 10);
		setSize(1282, 607);
		getContentPane().setLayout(null);
		
		// board
		panel_board  = new JPanel(null);
		panel_board.setBounds(1066, 11, 200, 200);
		setSketchBoardSize(panel_board.getWidth(), panel_board.getHeight());
		panel_board.add(sketch_board);
		sketch_board.init();
		getContentPane().add(panel_board);
		
		
		// network
		panel_net  = new JPanel(null);
		panel_net.setBounds(10, 11, 1046, 550);
		getContentPane().add(panel_net);
		
		int w = panel_net.getWidth();
		int h = panel_net.getHeight();
		sketch_net.setAuxSize(w, h);
		sketch_net.resizeWindow(w, h);
		panel_net.add(sketch_net);
		sketch_net.init();

		
		JPanel panel = new JPanel(null);
		panel.setBounds(1066, 422, 200, 55);
		getContentPane().add(panel);
		panel.setLayout(new BorderLayout(0, 0));
		
		JLabel lblFitness = new JLabel("Fitness:");
		panel.add(lblFitness, BorderLayout.NORTH);
		
		fitness_text = new JTextPane();
		panel.add(fitness_text, BorderLayout.CENTER);
		fitness_text.setEditable(false);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Play Tic Tac Toe", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		panel_1.setBounds(1064, 288, 202, 123);
		getContentPane().add(panel_1);
		panel_1.setLayout(null);
		
		bG = new ButtonGroup();
		
		JButton btnPlay = new JButton("Play");
		btnPlay.setBounds(61, 81, 89, 31);
		panel_1.add(btnPlay);
		btnPlay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(rdbtnPc.isSelected()){
					parent.setOpponentMode(1);
				}else{
					parent.setOpponentMode(0);
				}
				parent.new RunSimulationOnBackground(parent, parent.getNetwork(), true);
			}
		});
		
		JPanel panel_2 = new JPanel();
		panel_2.setBorder(new TitledBorder(null, "Network's opponent", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_2.setBounds(10, 21, 182, 56);
		panel_1.add(panel_2);
		
		rdbtnPc = new JRadioButton("PC");
		panel_2.add(rdbtnPc);
		bG.add(rdbtnPc);
		
		rdbtnUser = new JRadioButton("User");
		panel_2.add(rdbtnUser);
		bG.add(rdbtnUser);
		
		JPanel panel_3 = new JPanel();
		panel_3.setBorder(new TitledBorder(null, "Player turn", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_3.setBounds(1066, 222, 200, 55);
		getContentPane().add(panel_3);
		
		playerTurn_lbl = new JLabel("");
		playerTurn_lbl.setFont(new Font("Tahoma", Font.BOLD, 12));
		panel_3.add(playerTurn_lbl);
		
		bG.setSelected(rdbtnPc.getModel(), true);
		
		lblFitness.setVisible(true);

	}
	
	public void setGameState(int game_state[][]){
		sketch_board.setGameState(game_state);
	}
	
	public void setPlayerTurnLabel(int player, int opponentMode){
		if(player == -1){	// network
			playerTurn_lbl.setText("Network [O]");
		}else{
			if(opponentMode == 0){
				playerTurn_lbl.setText("User [X]");
			}else{
				playerTurn_lbl.setText("PC [X]");
			}
		}
	}
	
	public void setFitnessText(double fitness){
		fitness_text.setText("" + fitness);
		repaint();
	}

	private void setSketchBoardSize(int width, int height){
		sketch_board.setBoardSizeInPixels(width, height);
	}
	
}
