package rafa.NEAT;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import rafa.GUI.NetworkGraph;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;

public class Test extends JFrame {

	private JPanel contentPane;
	private JLabel similarity_lbl;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Test frame = new Test();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Test() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(10, 10, 833, 673);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JPanel panel = new JPanel(null);
		panel.setBounds(10, 11, 681, 612);
		contentPane.add(panel);
				
		// **********************************************************************
		
		Population pop = new Population(2, 1, 1);
		
		NetworkGraph net_graph = new NetworkGraph();
		net_graph.setPopulation(pop);
		net_graph.init();
		net_graph.resizeWindow(panel.getWidth(), panel.getHeight());
		panel.add(net_graph);

		JButton btnCrossover = new JButton("Crossover");
		btnCrossover.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				pop.addNetwork(Network.crossoverNetworks(5, pop.getNetworkByID(0), pop.getNetworkByID(1)));
				net_graph.setPopulation(pop);
				net_graph.resizeWindow(panel.getWidth(), panel.getHeight());

			}
		});
		btnCrossover.setBounds(701, 11, 106, 30);
		contentPane.add(btnCrossover);
		
		JButton btnSimilarity = new JButton("Similarity");
		btnSimilarity.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Species.sameSpecies(pop.getNetworkByID(0), pop.getNetworkByID(1));
			}
		});
		btnSimilarity.setBounds(701, 101, 106, 23);
		contentPane.add(btnSimilarity);
		
		similarity_lbl = new JLabel("New label");
		similarity_lbl.setBounds(701, 86, 106, 14);
		contentPane.add(similarity_lbl);
	}
}
