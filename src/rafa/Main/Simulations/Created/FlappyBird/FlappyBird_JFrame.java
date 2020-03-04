package rafa.Main.Simulations.Created.FlappyBird;

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
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.basic.BasicBorders.RadioButtonBorder;
import javax.swing.UIManager;
import java.awt.Color;
import javax.swing.JRadioButton;
import java.awt.Font;

public class FlappyBird_JFrame extends JFrame implements Serializable{

	private static final long serialVersionUID = 1L;
	
	FlappyBird_GUI sketch_game;
	NetworkGraph sketch_net;
	
	JPanel panel_board;
	JPanel panel_net;
	
	JTextPane fitness_text;
	
	FlappyBird_Sim parent;
	
	private ButtonGroup bG;
	

	public FlappyBird_JFrame(FlappyBird_GUI game_gui, NetworkGraph net_graph, FlappyBird_Sim parent) {
		this.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){
            	sketch_game.stop();
            	sketch_net.stop();
            }
        });
		
		// Changes overall look (look and feel) of the window
		try{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch(Exception e){
			e.printStackTrace();
		}


		this.sketch_game = game_gui;
		this.sketch_net = net_graph;
		this.parent = parent;

		setTitle("Flappy Bird");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setResizable(false);
		setVisible(true);
		setLocation(10, 10);
		setSize(1282, 607);
		getContentPane().setLayout(null);
		
		// game
		panel_board  = new JPanel(null);
		panel_board.setBounds(702, 11, 564, 556);
		panel_board.add(sketch_game);
		sketch_game.init();
		getContentPane().add(panel_board);
		
		
		// network
		panel_net  = new JPanel(null);
		panel_net.setBounds(10, 96, 666, 471);
		getContentPane().add(panel_net);
		
		int w = panel_net.getWidth();
		int h = panel_net.getHeight();
		sketch_net.setAuxSize(w, h);
		sketch_net.resizeWindow(w, h);
		panel_net.add(sketch_net);
		sketch_net.init();

		
		JPanel panel = new JPanel(null);
		panel.setBounds(222, 11, 200, 74);
		getContentPane().add(panel);
		panel.setLayout(new BorderLayout(0, 0));
		
		JLabel lblFitness = new JLabel("Fitness:");
		panel.add(lblFitness, BorderLayout.NORTH);
		
		fitness_text = new JTextPane();
		panel.add(fitness_text, BorderLayout.CENTER);
		fitness_text.setEditable(false);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Play Flappy Bird", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		panel_1.setBounds(10, 11, 202, 74);
		getContentPane().add(panel_1);
		panel_1.setLayout(null);
		
		bG = new ButtonGroup();
		
		JButton btnPlay = new JButton("Play");
		btnPlay.setBounds(56, 26, 89, 31);
		panel_1.add(btnPlay);
		
		JPanel panel_2 = new JPanel();
		panel_2.setBorder(new TitledBorder(null, "Sound effects", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_2.setBounds(432, 11, 93, 74);
		getContentPane().add(panel_2);
		panel_2.setLayout(null);
		
		JRadioButton rdbtnOn = new JRadioButton("On");
		rdbtnOn.setSelected(true);
		rdbtnOn.setBounds(25, 15, 39, 23);
		panel_2.add(rdbtnOn);
		
		JRadioButton rdbtnMute = new JRadioButton("Mute");
		rdbtnMute.setBounds(22, 40, 49, 23);
		panel_2.add(rdbtnMute);
		
		ButtonGroup bg = new ButtonGroup();
		bg.add(rdbtnMute);
		bg.add(rdbtnOn);

		ActionListener buttonClick = new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				AbstractButton aButton = (AbstractButton) actionEvent.getSource();
				if(aButton.getText() == "On"){
					sketch_game.enableSound(true);
				}else{
					sketch_game.enableSound(false);
				}
			}
		};
		
		rdbtnMute.addActionListener(buttonClick);
		rdbtnOn.addActionListener(buttonClick);

		btnPlay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				parent.new RunFlappyBirdSimulationOnBackground(parent, parent.getNetwork(), true);

			}
		});
		
		lblFitness.setVisible(true);

	}

	public void setFitnessText(double fitness){
		fitness_text.setText("" + fitness);
		repaint();
	}
}
