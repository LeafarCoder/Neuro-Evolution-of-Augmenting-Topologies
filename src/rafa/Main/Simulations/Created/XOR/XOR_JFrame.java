package rafa.Main.Simulations.Created.XOR;

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
import javax.swing.SwingConstants;
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
import javax.swing.border.LineBorder;

public class XOR_JFrame extends JFrame implements Serializable{

	private NetworkGraph sketch_net;

	JPanel panel_net;
	
	JLabel lbl_A;
	JLabel lbl_B;
	JLabel lbl_Out;
	JPanel panel_A;
	JPanel panel_B;
	JPanel panel_Out;

	JTextPane fitness_text;

	XOR_Sim parent;

	private ButtonGroup bG;
	private JPanel panel_Real;
	private JLabel lbl_real;
	private JLabel lblExpected;
	private JLabel lblNetwork;
	private JButton button;
	private JButton button_1;
	private JButton button_2;
	private JButton button_3;
	private JPanel panel_Out_cut;
	private JLabel lbl_Out_cut;

	public void setInputs(int a, int b){
		lbl_A.setText(a + "");
		lbl_B.setText(b + "");

		if(a == 1){
			panel_A.setBackground(Color.BLACK);
		}else{
			panel_A.setBackground(Color.WHITE);
		}
		
		if(b == 1){
			panel_B.setBackground(Color.BLACK);
		}else{
			panel_B.setBackground(Color.WHITE);
		}
		
	}
	
	public void setOutputs(float net, float real){
		
		lbl_Out.setText(String.format("%.02f", net) + "");
		int gray = (int)((1 - net) * 255);
		panel_Out.setBackground(new Color(gray, gray, gray));

		if(net < 0.5){
			lbl_Out_cut.setText(0 + "");
			panel_Out_cut.setBackground(Color.WHITE);
		}else{
			lbl_Out_cut.setText(1 + "");
			panel_Out_cut.setBackground(Color.BLACK);
		}
		
		lbl_real.setText((int)real + "");
		if((int)real == 0){
			panel_Real.setBackground(Color.WHITE);
		}else{
			panel_Real.setBackground(Color.BLACK);
		}
	}

	public XOR_JFrame(NetworkGraph net_graph, XOR_Sim parent) {
		this.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				// XOR_JFrame.this.dispose();
				sketch_net.stop();
			}
		});

		// Changes overall look (look and feel) of the window
		try{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch(Exception e){
			e.printStackTrace();
		}

		this.sketch_net = net_graph;
		this.parent = parent;

		setTitle("XOR");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setResizable(false);
		setVisible(true);
		setLocation(10, 10);
		setSize(1282, 607);
		getContentPane().setLayout(null);

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
		panel.setBounds(1066, 506, 200, 55);
		getContentPane().add(panel);
		panel.setLayout(new BorderLayout(0, 0));

		JLabel lblFitness = new JLabel("Fitness:");
		panel.add(lblFitness, BorderLayout.NORTH);

		fitness_text = new JTextPane();
		panel.add(fitness_text, BorderLayout.CENTER);
		fitness_text.setEditable(false);

		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Calculate XOR", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		panel_1.setBounds(1064, 334, 202, 161);
		getContentPane().add(panel_1);
		panel_1.setLayout(null);

		bG = new ButtonGroup();

		JButton btnPlay = new JButton("Random");
		btnPlay.setBounds(59, 22, 89, 44);
		panel_1.add(btnPlay);
		btnPlay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				parent.calculateRandom = true;
				parent.new RunXORSimulationOnBackground(parent, parent.getNetwork(), true, parent.getPopulation());
			}
		});
		
		button = new JButton("0  |  0");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				parent.calculateRandom = false;
				parent.setCalculateInputs(0, 0);
				parent.new RunXORSimulationOnBackground(parent, parent.getNetwork(), true, parent.getPopulation());
			}
		});
		button.setBounds(30, 77, 69, 31);
		panel_1.add(button);
		
		button_1 = new JButton("1  |  1");
		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				parent.calculateRandom = false;
				parent.setCalculateInputs(1, 1);
				parent.new RunXORSimulationOnBackground(parent, parent.getNetwork(), true, parent.getPopulation());
			}
		});
		button_1.setBounds(109, 77, 69, 31);
		panel_1.add(button_1);
		
		button_2 = new JButton("1  |  0");
		button_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				parent.calculateRandom = false;
				parent.setCalculateInputs(1, 0);
				parent.new RunXORSimulationOnBackground(parent, parent.getNetwork(), true, parent.getPopulation());
			}
		});
		button_2.setBounds(109, 119, 69, 31);
		panel_1.add(button_2);
		
		button_3 = new JButton("0  |  1");
		button_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				parent.calculateRandom = false;
				parent.setCalculateInputs(0, 1);
				parent.new RunXORSimulationOnBackground(parent, parent.getNetwork(), true, parent.getPopulation());
			}
		});
		button_3.setBounds(30, 119, 69, 31);
		panel_1.add(button_3);
		

		JPanel panel_2 = new JPanel();
		panel_2.setBorder(new TitledBorder(null, "Inputs", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_2.setBounds(1066, 11, 200, 92);
		getContentPane().add(panel_2);
		panel_2.setLayout(null);

		panel_A = new JPanel();
		panel_A.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel_A.setBounds(10, 38, 81, 43);
		panel_2.add(panel_A);

		panel_B = new JPanel();
		panel_B.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel_B.setBounds(109, 38, 81, 43);
		panel_2.add(panel_B);

		lbl_A = new JLabel("", SwingConstants.CENTER);
		lbl_A.setBounds(10, 21, 81, 14);
		panel_2.add(lbl_A);
		lbl_A.setFont(new Font("Tahoma", Font.BOLD, 13));

		lbl_B = new JLabel("", SwingConstants.CENTER);
		lbl_B.setBounds(109, 21, 81, 14);
		panel_2.add(lbl_B);
		lbl_B.setFont(new Font("Tahoma", Font.BOLD, 13));

		JPanel panel_3 = new JPanel();
		panel_3.setBorder(new TitledBorder(null, "Output", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_3.setBounds(1066, 114, 200, 209);
		getContentPane().add(panel_3);
		panel_3.setLayout(null);

		panel_Out = new JPanel();
		panel_Out.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel_Out.setBounds(10, 61, 92, 43);
		panel_3.add(panel_Out);
		
		lbl_Out = new JLabel("", SwingConstants.CENTER);
		lbl_Out.setFont(new Font("Tahoma", Font.BOLD, 13));
		lbl_Out.setBounds(10, 36, 92, 14);
		panel_3.add(lbl_Out);
		
		panel_Real = new JPanel();
		panel_Real.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel_Real.setBounds(10, 155, 180, 43);
		panel_3.add(panel_Real);
		
		lbl_real = new JLabel("", SwingConstants.CENTER);
		lbl_real.setFont(new Font("Tahoma", Font.BOLD, 13));
		lbl_real.setBounds(10, 130, 180, 14);
		panel_3.add(lbl_real);
		
		lblExpected = new JLabel("Expected:", SwingConstants.CENTER);
		lblExpected.setFont(new Font("Tahoma", Font.BOLD, 13));
		lblExpected.setBounds(10, 109, 180, 14);
		panel_3.add(lblExpected);
		
		lblNetwork = new JLabel("Network:", SwingConstants.CENTER);
		lblNetwork.setFont(new Font("Tahoma", Font.BOLD, 13));
		lblNetwork.setBounds(10, 13, 180, 14);
		panel_3.add(lblNetwork);
		
		panel_Out_cut = new JPanel();
		panel_Out_cut.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel_Out_cut.setBounds(100, 61, 90, 43);
		panel_3.add(panel_Out_cut);
		
		lbl_Out_cut = new JLabel("", SwingConstants.CENTER);
		lbl_Out_cut.setFont(new Font("Tahoma", Font.BOLD, 13));
		lbl_Out_cut.setBounds(98, 36, 92, 14);
		panel_3.add(lbl_Out_cut);

	}

	public void setFitnessText(double fitness){
		fitness_text.setText("" + fitness);
		repaint();
	}
}
