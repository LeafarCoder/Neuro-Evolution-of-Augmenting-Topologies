package rafa.GUI;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.Component;
import java.awt.CardLayout;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.UIManager;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.border.TitledBorder;
import javax.swing.JTable;
import javax.swing.JList;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import rafa.NEAT.Population;
import rafa.Main.Simulations.Simulation;
import rafa.Main.Simulations.Created.TicTacToe.TicTacToe_Sim;
import rafa.Main.Simulations.Created.XOR.XOR_Sim;
import rafa.NEAT.Network;

import javax.swing.event.ListSelectionEvent;
import javax.swing.ListSelectionModel;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Enumeration;

public class PopulationView extends JFrame {
	private static final long serialVersionUID = 1L;
	
	public volatile static int counter=0;
	
	private Population population;
	private Simulation simulation;
	
	private JScrollPane all_net_table_scroll;
	
	private JPanel contentPane;
	private JTable all_net_table;
	private JPanel card_panel;
	private JPanel species_card;

	private JPanel all_networks;
	private JPanel species;
	private JList<String> specie_list;
	private JButton btnSeeRepresentative;
	private JButton btnSeeNetwork;
	private JButton button;
	
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		Population pop = new Population(100,9,3);
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					PopulationView frame = new PopulationView(pop, new XOR_Sim());
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}


	public void updatePopView(){
		prepareAllPanels();
	}

	public PopulationView(Population pop, Simulation sim){
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				counter--;
			}
		});

		// Changes overall look (look and feel) of the window
		try{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch(Exception e){
			e.printStackTrace();
		}

		// general variables
		counter++;
		this.population = pop;
		this.simulation = sim;
		
		// set general JFrame properties
		
		setResizable(false);
		setBackground(Color.WHITE);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(00, 00, 679, 503);
		setTitle("View Population");
		contentPane = new JPanel();
		contentPane.setBackground(Color.WHITE);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		// JPanel for holding all other panels (Card Layout)
		card_panel = new JPanel(new CardLayout(0, 0));
		card_panel.setBounds(10, 11, 504, 449);
		contentPane.add(card_panel);
		
		
		// ************************************************ ADDING OTHER PANELS TO card_panel ***********************************************************
		
		// *********************************** ALL_NETWORKS ******************************************
		all_networks = new JPanel();
		all_networks.setBorder(new TitledBorder(null, "All Networks", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		all_networks.setBackground(Color.WHITE);
		all_networks.setLayout(null);
		all_networks.setName("All Networks");
		card_panel.add(all_networks, all_networks.getName());
		all_net_table_scroll = new JScrollPane();
		all_net_table_scroll.setBounds(10, 21, 355, 417);
		all_networks.add(all_net_table_scroll);
		
		btnSeeNetwork = new JButton("See Network(s)");
		btnSeeNetwork.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				// if there is no selection skip
				if(!all_net_table.getSelectionModel().isSelectionEmpty()){
					Population auxPopulation = new Population();
	
					// for each network selected in table add it to auxPopulation
					int[] selectedNets = all_net_table.getSelectedRows();

					for(int row: selectedNets){
						int netID = (int) all_net_table.getValueAt(row, 0);
						auxPopulation.addNetwork(population.getNetworkByID(netID));
					}
					
					NetworkGraph sketch = new NetworkGraph();
			        sketch.setPopulation(auxPopulation);
			        
					new NetworksViewAuxJFrame(sketch);

				}
			}
		});
		btnSeeNetwork.setBounds(375, 19, 119, 32);
		all_networks.add(btnSeeNetwork);
		
		JButton btnPlayWithNetowkr = new JButton("Play with Network");
		btnPlayWithNetowkr.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				// if there is no selection skip
				if(!all_net_table.getSelectionModel().isSelectionEmpty()){
					// for each network selected in table add it to auxPopulation
					int selectedRow = all_net_table.getSelectedRow();
					int netID = (int) all_net_table.getValueAt(selectedRow, 0);

					
					try {
						simulation.getClass().getConstructor(Network.class, boolean.class, Population.class).newInstance(population.getNetworkByID(netID), true, population);
					} catch (NoSuchMethodException e) {
						e.printStackTrace();
					} catch (SecurityException e) {
						e.printStackTrace();
					} catch (InstantiationException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
					
					// TicTacToe_Sim simulation = new TicTacToe_Sim(population.getNetworkByID(netID));
					
				}
			
			}
		});
		btnPlayWithNetowkr.setBounds(375, 62, 119, 32);
		all_networks.add(btnPlayWithNetowkr);
		
		// *********************************** SPECIES ******************************************
		species = new JPanel(null);
		species.setBorder(new TitledBorder(null, "Species", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		species.setBackground(Color.WHITE);
		species.setName("Species");
		card_panel.add(species,species.getName());
		
		species_card = new JPanel(new CardLayout(0, 0));
		species_card.setBackground(Color.WHITE);
		species_card.setBounds(10, 18, 347, 417);
		species.add(species_card);
		
		DefaultListModel<String> specie_list_model = new DefaultListModel<String>();
		specie_list = new JList<String>(specie_list_model);
		specie_list.setBorder(new TitledBorder(null, "Specie ID", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		specie_list.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {
				CardLayout cl = (CardLayout)species_card.getLayout();
				cl.show(species_card, specie_list.getSelectedValue());
			}
		});
		JScrollPane specie_list_scroll = new JScrollPane(specie_list);
		specie_list_scroll.setBounds(367, 11, 127, 158);
		species.add(specie_list_scroll);
		
		btnSeeRepresentative = new JButton("See representative");
		btnSeeRepresentative.setBounds(367, 180, 127, 35);
		species.add(btnSeeRepresentative);
		
		// *********************************************************** OTHER COMPONENTS *************************************************************
		
		
		// SELECTION LIST

		DefaultListModel<String> model_card_list = new DefaultListModel<>();
		JList<String> cards_list = new JList<>(model_card_list);
		cards_list.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "View mode", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		cards_list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		cards_list.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {
				CardLayout cl = (CardLayout)card_panel.getLayout();
				cl.show(card_panel, cards_list.getSelectedValue());
			}

		});
		JScrollPane cards_list_scroll = new JScrollPane(cards_list);
		cards_list_scroll.setEnabled(false);
		cards_list_scroll.setBorder(null);
		cards_list_scroll.setBounds(524, 11, 141, 174);
		contentPane.add(cards_list_scroll);
		
		
		// ************************************************************ INITIALIZE VARIABLES *************************************************************
		
		for(Component c: card_panel.getComponents()){
			model_card_list.addElement(c.getName());
		}
		cards_list.setSelectedIndex(0);
		
		prepareAllPanels();
		
	}
	
	private void prepareAllPanels(){
		
		// ***************************************************** ALL_NETWORKS *****************************************************************************
		
		String[] col_headers = {"ID", "Species", "Fitness", "Hidden Layers"};
		
		int pop_size = population.getPopSize();
		Object[][] data = new Object[pop_size][col_headers.length];
		
		Enumeration<Integer> netIDs = population.getNetworkIDs();
		int counter = 0;
		while(netIDs.hasMoreElements()){
			int netID = netIDs.nextElement();
			Network net = population.getNetworkByID(netID);
			data[counter][0] = netID;
			data[counter][1] = net.getSpecie();
			data[counter][2] = net.getNetFitness();
			data[counter][3] = net.getNumLayers() - 2;	// #layers - inputLayer - outputLayer
			counter++;
		}
		
		DefaultTableModel tableModel = new DefaultTableModel(data, col_headers){
			private static final long serialVersionUID = 1L;
			public boolean isCellEditable(int row, int column){return false;}
		    
		    // IMPORTANT to define class so the sort works correctly! otherwise it will order as Strings
		    public Class<?> getColumnClass(int column) {
                if(column == 2){
                	return Double.class;
                }else{
                	return Integer.class;
                }
            }
		};
		all_net_table = new JTable(data, col_headers);
		all_net_table.setBackground(Color.WHITE);
		all_net_table.setModel(tableModel);
		all_net_table_scroll.setViewportView(all_net_table);
		
		TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(tableModel);
		all_net_table.setRowSorter(sorter);
		
		
		// ***************************************************** SPECIES *****************************************************************************
		
		species_card.removeAll();
		specie_list.removeAll();
		
		DefaultListModel<String> specie_list_model = (DefaultListModel<String>) specie_list.getModel();
		specie_list_model.clear();
		
		Enumeration<Integer> specie_ids = population.getSpecies().getSpeciesIDs();
		
		while(specie_ids.hasMoreElements()){
			int sp = specie_ids.nextElement();
			
			String[] col_headers_sp = {"ID","Fitness"};
			Object[][] data_sp = new Object[population.getSpecies().getSpecieSize(sp)][col_headers_sp.length];
			Network[] netsFromSpecie =  population.getSpecies().getNetworksFromSpecie(sp);
			for(int i = 0; i < netsFromSpecie.length; i++){
				data_sp[i][0] = netsFromSpecie[i].getNetID();
				data_sp[i][1] = netsFromSpecie[i].getNetFitness();
			}
					
			DefaultTableModel tableModel2 = new DefaultTableModel(data_sp, col_headers_sp){
				private static final long serialVersionUID = 1L;
				public boolean isCellEditable(int row, int column){return false;}
				
				public Class<?> getColumnClass(int column) {
	                if(column == 1){
	                	return Double.class;
	                }else{
	                	return Integer.class;
	                }
	            }
			};
			JTable table = new JTable(tableModel2);
			table.setBackground(Color.WHITE);
			
			JScrollPane table_scroll = new JScrollPane(table);
			table_scroll.setName("Specie " + sp);
			species_card.add(table_scroll, "Specie " + sp);

			if(!specie_list_model.contains("Specie " + sp)){
				specie_list_model.addElement("Specie " + sp);
			}
		}

		specie_list.setSelectedIndex(0);
		
	}
}
