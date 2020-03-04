package rafa.GUI;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JTabbedPane;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import rafa.NEAT.Population.Parameters;
import rafa.Main.Simulations.Simulation;
import rafa.Main.Simulations.Created.FlappyBird.FlappyBird_Sim;
import rafa.Main.Simulations.Created.TicTacToe.TicTacToe_Sim;
import rafa.Main.Simulations.Created.XOR.XOR_Sim;
import rafa.NEAT.Network;
import rafa.NEAT.Population;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;

import java.awt.Color;
import javax.swing.JSpinner;
import javax.swing.DefaultListModel;

import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.Font;
import javax.swing.JSeparator;
import java.awt.Toolkit;
import java.awt.Dimension;

import javax.swing.JScrollPane;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URI;
import java.net.URISyntaxException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.swing.JTextPane;
import javax.swing.JTextArea;
import javax.swing.JList;
import javax.swing.ScrollPaneConstants;

public class NEATWindow{

	private NEATWindow NEATWindow_Obj;
	
	private Population population;
	private Simulation simulationObject;
	
	private ArrayList<Parameters> popParameters;

	/* 0: only Simulation Tab
	 * 1: + population Tab
	 * 2: +Visualize tab/ +properties tab 
	 */
	private int processState = 0;
	
	private JFrame window;
	private JPanel contentPane;

	private JLabel lblSizeInfo;	
	private JLabel lblGenerationInfo;
	private JLabel lblNumberOfSpeciesInfo;
	private JLabel lblNumberOfInputsInfo;
	private JLabel lblNumberOfOutputsInfo;
	private JLabel lblMaxFitness;

	private JSpinner size_input;
	private JTextArea browse_sim_txt;
	private JTextPane sim_status;
	private JTextPane pop_status;
	
	private JPanel tab_population;
	private JPanel tab_simulation;
	private JPanel tab_properties;
	private JPanel tab_evolution;

	private JTabbedPane tabbedPane;
	
	private JList<String> list_sims;

	private String popParametersLastFolderPath = "";
	private String simulationLastFolderPath = "";
	
	private String[] sim_obj_names;
	private String[] sim_folder_names;
	private final String[] simulation_names = new String[]{"Flappy Bird", "TicTacToe", "XOR"};
	
	private NetworkGraph net_graph;
	
	private static PopulationView popView;
	
	public void setProcessState(int state){
		processState = state;
		
		ArrayList<String> unlock_tabs = new ArrayList<String>();
		
		if(state >= 0)unlock_tabs.add("Simulation");
		if(state >= 1)unlock_tabs.add("Population");
		if(state >= 2){
			unlock_tabs.add("Properties");
			unlock_tabs.add("Visualize");
			unlock_tabs.add("Evolution");
		}
		
		for(int i = 0; i < tabbedPane.getTabCount(); i++){
			tabbedPane.setEnabledAt(i, false);
			if(unlock_tabs.contains(tabbedPane.getTitleAt(i))){
				tabbedPane.setEnabledAt(i, true);
			}
		}
		
	}
	
	public void setPopParameters(ArrayList<Parameters> popParam){
		popParameters = popParam;
	}
	
	public ArrayList<Parameters> getPopParameters(){
		return popParameters;
	}
	
	public void updatePopulationInformation(){

		lblSizeInfo.setText("Population size: "+population.getPopSize());
		lblGenerationInfo.setText("Generation: "+population.getGeneration());
		lblNumberOfSpeciesInfo.setText("Number of species: "+population.getNumberOfSpecies());
		lblMaxFitness.setText("Maximum fitness: "+population.getMaxFitness());

		lblNumberOfInputsInfo.setText("Number of inputs: "+simulationObject.getInputNodesNum());
		lblNumberOfOutputsInfo.setText("Number of outputs: "+simulationObject.getOutputNodesNum());
		
		lblSizeInfo.setEnabled(true);
		lblGenerationInfo.setEnabled(true);
		lblNumberOfSpeciesInfo.setEnabled(true);
		lblNumberOfInputsInfo.setEnabled(true);
		lblNumberOfOutputsInfo.setEnabled(true);
		lblMaxFitness.setEnabled(true);
	}

	private void loadSimFilesIntoJList(String path){
		/*
		browse_sim_txt.setText(path);
		
		sim_folder_names = new File(simulationLastFolderPath).list(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				if (dir.isDirectory()) {return true;} else {return false;}
			}
		});
		sim_obj_names = new String[sim_folder_names.length];
		
		
		((DefaultListModel<String>)list_sims.getModel()).clear();
		for(int i = 0; i < sim_folder_names.length; i++){
			File obj = new File(simulationLastFolderPath + "\\" + sim_folder_names[i]);
			File[] objs = obj.listFiles(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					if (name.endsWith(".sim")) {return true;} else {return false;}
				}
			});
			if(objs.length > 0){
				String name = objs[0].getName();
				sim_obj_names[i] = name;
				((DefaultListModel<String>)list_sims.getModel()).addElement(name);
			}
		}
		 */

		
		for(int i = 0; i < simulation_names.length; i++){
			((DefaultListModel<String>)list_sims.getModel()).addElement(simulation_names[i]);
		}

		if(((DefaultListModel<String>)list_sims.getModel()).getSize() > 0){
			list_sims.setSelectedIndex(0);
		}
	}

	public static void updatePopView(){
		popView.updatePopView();
	}
	
	public NEATWindow() {

		// Changes overall look (look and feel) of the window
		try{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch(Exception e){
			e.printStackTrace();
		}

		// ********************************************* INITIALIZE VARIABLES *******************************************************
		
		population = new Population();
		
		popView = new PopulationView(population, simulationObject);
		
		NEATWindow_Obj = this;
		
		// initialize PopParameters:
		popParameters = new ArrayList<Parameters>();
		
		URI uri = null;
		try {
			uri = new URI(NEATWindow.class.getResource("/resources/parameters/parameters2.json").toString());
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}

		JSONParser parser = new JSONParser();
		try{
			Object obj = parser.parse(new FileReader(uri.getPath()));
			JSONObject jsonObject = (JSONObject)obj;

			JSONArray param = (JSONArray)jsonObject.get("Parameters");
			JSONArray val = (JSONArray)jsonObject.get("Values");
			JSONArray descr = (JSONArray)jsonObject.get("Description");
			
			Population aux_pop = new Population();
			for(int i = 0; i < param.size(); i++){
				
				if(val.get(i) instanceof Long){
					popParameters.add(aux_pop.new Parameters((String)param.get(i), Long.valueOf(val.get(i)+"").doubleValue(), (String)descr.get(i)));
				}else{
					popParameters.add(aux_pop.new Parameters((String)param.get(i),(double)val.get(i), (String)descr.get(i)));
				}
			}
			
		} catch (FileNotFoundException er) {
			er.printStackTrace();
		} catch (IOException er) {
			er.printStackTrace();
		} catch (org.json.simple.parser.ParseException er) {
			er.printStackTrace();
		}
		
		popParametersLastFolderPath = "src/resources/parameters";

		// ************************************************ INITIALIZE VARIABLES (end) *************************************************************************

		window = new JFrame("NEAT");
		window.setBackground(Color.WHITE);
		window.setResizable(false);
		window.setSize(254,426);
		window.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/resources/images/NET.png")));
		window.setVisible(true);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// set initial position to the center of the screen
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		window.setLocation(dim.width/2-window.getSize().width/2, dim.height/2-window.getSize().height/2);

		contentPane = new JPanel();
		contentPane.setBackground(Color.WHITE);
		window.setContentPane(contentPane);

		// Multiple tabs
		tabbedPane = new JTabbedPane();
		tabbedPane.setBounds(0, 0, 100, 100);
		tabbedPane.setBackground(Color.WHITE);
		
		// ******************************** SIMULATION TAB ***********************************
		tab_simulation = new JPanel();
		tab_simulation.setBackground(Color.WHITE);
		tabbedPane.addTab("Simulation", null, tab_simulation, null);
		tab_simulation.setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBounds(10, 63, 204, 257);
		//panel.setBounds(0,0, 300,400);
		panel.setBackground(Color.WHITE);
		panel.setBorder(new TitledBorder(null, "Load Simulation", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		tab_simulation.add(panel);
		panel.setLayout(null);
		
		list_sims = new JList<String>(new DefaultListModel<String>());
		list_sims.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane scroll_list = new JScrollPane();
		scroll_list.setLocation(10, 95);
		scroll_list.setViewportView(list_sims);
		scroll_list.setSize(184, 65);
		panel.add(scroll_list);
		
		JButton btnLoadSim = new JButton("Browse folder");
		btnLoadSim.setBounds(54, 18, 107, 29);
		btnLoadSim.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				JFileChooser fc = new JFileChooser();
				if(simulationLastFolderPath != ""){
					fc.setCurrentDirectory(new File(simulationLastFolderPath));
				}

				// select only .sim files
				fc.setFileFilter(new FileNameExtensionFilter("Simulation Object", "sim"));
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

				if(fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
					simulationLastFolderPath = fc.getSelectedFile().getAbsolutePath();
					browse_sim_txt.setText(simulationLastFolderPath);
					loadSimFilesIntoJList(simulationLastFolderPath);
				}

			}
		});
		panel.add(btnLoadSim);

		JButton btnSaveSim = new JButton("Load Simulation");
		btnSaveSim.setBounds(51, 213, 110, 35);
		btnSaveSim.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				if(list_sims.isSelectionEmpty()){
					JOptionPane.showMessageDialog(null, "No Simulation is selected. Search for Simulations on your computer.");
					return;
				}
				
				int index = list_sims.getSelectedIndex();
				String simulation = list_sims.getSelectedValue();
				
				if(simulation.equals(simulation_names[0])){
					simulationObject = new FlappyBird_Sim();
					
				}else if(simulation.equals(simulation_names[1])){
					simulationObject = new TicTacToe_Sim();
					
				}else if(simulation.equals(simulation_names[2])){
					simulationObject = new XOR_Sim();
				}
				
				// change status
				sim_status.setBackground(new Color(50,250,50));
				sim_status.setText(simulation + " loaded");
				
				JOptionPane.showMessageDialog(null, simulation + "  was loaded successfully!\nNow create a population for it!", "Simulation", JOptionPane.INFORMATION_MESSAGE);
				
				setProcessState(1);
				
				tabbedPane.setSelectedComponent(tab_population);
				
				/*
				String filePath = simulationLastFolderPath + "\\" +  sim_folder_names[index] + "\\" + simulation;
				Object sim = null;
				try {
					FileInputStream fileIn = new FileInputStream(filePath);
					ObjectInputStream in = new ObjectInputStream(fileIn);
					sim = in.readObject();
					in.close();
					fileIn.close();
					
					if(sim instanceof Simulation){
						simulationObject = (Simulation) sim;
						setProcessState(1);
						String sim_name = simulation.substring(0, simulation.length() - 4);
						JOptionPane.showMessageDialog(null, sim_name + "  was loaded successfully!\nNow create a population for it!", "Simulation", JOptionPane.INFORMATION_MESSAGE);
						tabbedPane.setSelectedComponent(tab_population);
						
						// change status
						sim_status.setBackground(new Color(50,250,50));
						sim_status.setText(sim_name + " loaded");
					}else{
						JOptionPane.showMessageDialog(null, simulation + " does not implement the Simulation Interface!", "Simulation not supported", JOptionPane.ERROR_MESSAGE);
					}
				} catch (IOException i) {
					i.printStackTrace();
				} catch (ClassNotFoundException c) {
					c.printStackTrace();
				}
				*/
				
				
			}
		});
		panel.add(btnSaveSim);
		
		browse_sim_txt = new JTextArea();
		browse_sim_txt.setFont(new Font("Tahoma", Font.PLAIN, 11));
		browse_sim_txt.setEditable(false);
		panel.add(browse_sim_txt);
		JScrollPane scroll_browse_sim = new JScrollPane(browse_sim_txt);
		scroll_browse_sim.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		scroll_browse_sim.setBounds(10, 51, 184, 35);
		panel.add(scroll_browse_sim);
		
		JButton btnCreateNew = new JButton("<html><center>Create new<br>Simulation</center></html>\r\n");
		btnCreateNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				File javaFilesDir = new File("src/rafa/Main/Simulations/Created");
				File objDir = new File(simulationLastFolderPath);
				
				GenerateSimulationObjects generateSim = new GenerateSimulationObjects(javaFilesDir.getAbsolutePath(),objDir.getAbsolutePath());
				generateSim.setVisible(true);
				
				loadSimFilesIntoJList(simulationLastFolderPath);

			}
		});
		btnCreateNew.setBounds(51, 171, 110, 35);
		panel.add(btnCreateNew);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(null, "Status", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_1.setBackground(Color.WHITE);
		panel_1.setBounds(10, 11, 204, 48);
		tab_simulation.add(panel_1);
		panel_1.setLayout(null);
		
		sim_status = new JTextPane();
		sim_status.setFont(new Font("Tahoma", Font.PLAIN, 14));
		sim_status.setEditable(false);
		sim_status.setBounds(10, 13, 184, 26);
		panel_1.add(sim_status);
		
		
		// *********************************************** POPULATION TAB ********************************************************
		tab_population = new JPanel();
		tab_population.setBackground(Color.WHITE);
		tab_population.setLayout(null);
		tabbedPane.addTab("Population", null, tab_population, "Generate a new, load or save populations");

		JPanel panel_3 = new JPanel();
		panel_3.setBackground(Color.WHITE);
		panel_3.setSize(204, 63);
		panel_3.setLocation(10, 65);
		tab_population.add(panel_3);
		panel_3.setLayout(null);
		panel_3.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Load / Save Population", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));

		JPanel panel_4 = new JPanel();
		panel_4.setBackground(Color.WHITE);
		panel_4.setBounds(10, 135, 204, 108);
		tab_population.add(panel_4);
		panel_4.setLayout(null);
		panel_4.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Generate new population", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		

		JButton btn_generate = new JButton("Generate");
		btn_generate.setBounds(40, 60, 130, 33);
		panel_4.add(btn_generate);
		btn_generate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				int size = (int)size_input.getValue();
				
				double coefDisjoint = (double)popParameters.get(0).getValue();
				double coefExcess = (double)popParameters.get(1).getValue();
				double coefWeights = (double)popParameters.get(2).getValue();
				double threshold = (double)popParameters.get(3).getValue();

				int inNodesNum = simulationObject.getInputNodesNum();
				int outNodesNum = simulationObject.getOutputNodesNum();
				population = new Population(size, inNodesNum, outNodesNum, popParameters);
				
				updatePopulationInformation();

				// population is generated
				setProcessState(2);
				
				// change status
				pop_status.setBackground(new Color(50,250,50));
				pop_status.setText("Population generated");
				
				// popView.updatePopView();
				
				JOptionPane.showMessageDialog(null, "Population with specified requirements is created.");
			}
		});

		JLabel lblSize = new JLabel("Size:");
		lblSize.setBounds(10, 21, 31, 28);
		panel_4.add(lblSize);

		size_input = new JSpinner();
		size_input.setBounds(40, 25, 50, 20);
		panel_4.add(size_input);
		size_input.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				if((int)size_input.getValue() <= 0){
					btn_generate.setEnabled(false);
				}else{
					btn_generate.setEnabled(true);
				}
			}
		});
		size_input.setValue((int)20);
		size_input.setToolTipText("Insert the initial number of networks");
		
		// Button opens another window to Load/Save Population Parameters
		JButton btnParameters = new JButton("Parameters...");
		btnParameters.setBounds(95, 21, 99, 28);
		panel_4.add(btnParameters);
		btnParameters.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				new ParametersJDialog(NEATWindow_Obj, popParametersLastFolderPath);

			}
		});

		JButton button_1 = new JButton("Load");
		button_1.setBounds(29, 21, 68, 30);
		panel_3.add(button_1);
		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {


				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileFilter(new FileNameExtensionFilter("Population", "pop"));
				String filePath = null;

				if(fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
					filePath = fileChooser.getSelectedFile().toString();
				}

				if(filePath != null){
					population = Population.loadPopulationFromFile(filePath);
					JOptionPane.showMessageDialog(null, "Population successefully loaded from "+filePath+".");
					updatePopulationInformation();
					setProcessState(2);
					
					// change status
					pop_status.setBackground(new Color(50,250,50));
					pop_status.setText("Population loaded");
				}else{
					JOptionPane.showMessageDialog(null, "Population was not loaded.");
				}
			}
		});

		JButton button_2 = new JButton("Save");
		button_2.setBounds(107, 21, 68, 30);
		panel_3.add(button_2);
		
		JPanel panel_2 = new JPanel();
		panel_2.setLayout(null);
		panel_2.setBorder(new TitledBorder(null, "Status", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_2.setBackground(Color.WHITE);
		panel_2.setBounds(10, 11, 204, 48);
		tab_population.add(panel_2);
		
		pop_status = new JTextPane();
		pop_status.setFont(new Font("Tahoma", Font.PLAIN, 14));
		pop_status.setEditable(false);
		pop_status.setBackground(new Color(250, 50, 50));
		pop_status.setBounds(10, 13, 184, 26);
		panel_2.add(pop_status);
		button_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileFilter(new FileNameExtensionFilter("Population", "pop"));
				String pathName = null;

				if(fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
					pathName = fileChooser.getSelectedFile().toString();
					if(!pathName.endsWith(".pop")){
						pathName += ".pop";
					}
				}

				if(pathName != null){
					population.savePopulationToFile(pathName);
					JOptionPane.showMessageDialog(null, "Population successefully saved in "+pathName+".");
				}else{
					JOptionPane.showMessageDialog(null, "Population was not saved.");
				}

			}
		});
		
		// ******************************************** PROPERTIES TAB *******************************************

		tab_properties = new JPanel();
		tab_properties.setBackground(Color.WHITE);
		
		tabbedPane.addTab("Properties", null, tab_properties, "See the population properties");

		lblSizeInfo = new JLabel("Size: 0");
		lblGenerationInfo = new JLabel("Generation: 0");
		lblNumberOfSpeciesInfo = new JLabel("Number of species: 0");
		lblNumberOfInputsInfo = new JLabel("Number of inputs: 0");
		lblNumberOfOutputsInfo = new JLabel("Number of outputs: 0");
		lblMaxFitness = new JLabel("Maximum fitness: 0");

		lblSizeInfo.setEnabled(false);
		lblGenerationInfo.setEnabled(false);
		lblNumberOfSpeciesInfo.setEnabled(false);
		lblNumberOfInputsInfo.setEnabled(false);
		lblNumberOfOutputsInfo.setEnabled(false);
		lblMaxFitness.setEnabled(false);

		JLabel lblPopulationProperties = new JLabel("General population properties:");
		lblPopulationProperties.setFont(new Font("Tahoma", Font.PLAIN, 12));

		JSeparator separator = new JSeparator();
		
		JLabel lblTopology = new JLabel("Topology:");
		lblTopology.setFont(new Font("Tahoma", Font.PLAIN, 12));
		
		JSeparator separator_1 = new JSeparator();




		GroupLayout gl_tab_properties = new GroupLayout(tab_properties);
		gl_tab_properties.setHorizontalGroup(
			gl_tab_properties.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_tab_properties.createSequentialGroup()
					.addGap(20)
					.addGroup(gl_tab_properties.createParallelGroup(Alignment.LEADING, false)
						.addComponent(lblNumberOfOutputsInfo)
						.addComponent(lblNumberOfInputsInfo, GroupLayout.PREFERRED_SIZE, 145, GroupLayout.PREFERRED_SIZE)
						.addComponent(separator_1, GroupLayout.PREFERRED_SIZE, 176, GroupLayout.PREFERRED_SIZE)
						.addComponent(separator, GroupLayout.PREFERRED_SIZE, 176, GroupLayout.PREFERRED_SIZE)
						.addGroup(gl_tab_properties.createSequentialGroup()
							.addComponent(lblNumberOfSpeciesInfo, GroupLayout.PREFERRED_SIZE, 133, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED, 353, GroupLayout.PREFERRED_SIZE))
						.addComponent(lblSizeInfo)
						.addComponent(lblPopulationProperties)
						.addComponent(lblMaxFitness, GroupLayout.PREFERRED_SIZE, 133, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblTopology, GroupLayout.PREFERRED_SIZE, 120, GroupLayout.PREFERRED_SIZE)
						.addGroup(Alignment.TRAILING, gl_tab_properties.createSequentialGroup()
							.addComponent(lblGenerationInfo, GroupLayout.PREFERRED_SIZE, 86, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED, 400, Short.MAX_VALUE)))
					.addGap(0))
		);
		gl_tab_properties.setVerticalGroup(
			gl_tab_properties.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_tab_properties.createSequentialGroup()
					.addGap(26)
					.addComponent(lblPopulationProperties)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(separator, GroupLayout.PREFERRED_SIZE, 2, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblSizeInfo)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblGenerationInfo)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblNumberOfSpeciesInfo)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(lblMaxFitness)
					.addPreferredGap(ComponentPlacement.RELATED, 82, Short.MAX_VALUE)
					.addComponent(lblTopology, GroupLayout.PREFERRED_SIZE, 15, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(separator_1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblNumberOfInputsInfo)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblNumberOfOutputsInfo)
					.addGap(68))
		);
		tab_properties.setLayout(gl_tab_properties);

		// ************************************ EVOLUTION TAB ******************************************

		tab_evolution = new JPanel();
		tab_evolution.setBackground(Color.WHITE);
		tabbedPane.addTab("Evolution", null, tab_evolution, "Used to control evolution of the Neural Network");
		tab_evolution.setLayout(null);
		
		JButton btnNewButton = new JButton("+1 Generation");
		btnNewButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				
				population.nextGeneration(simulationObject);
				
			}
		});
		btnNewButton.setBounds(58, 29, 113, 45);
		tab_evolution.add(btnNewButton);
		
		JButton btnGenerations = new JButton("+10 Generations");
		btnGenerations.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				for(int i = 0; i < 10; i++){
					population.nextGeneration(simulationObject);
				}
				
			}
		});
		btnGenerations.setBounds(58, 99, 113, 45);
		tab_evolution.add(btnGenerations);
		
		
		
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
				gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
						.addContainerGap()
						.addComponent(tabbedPane, GroupLayout.PREFERRED_SIZE, 229, GroupLayout.PREFERRED_SIZE)
						.addContainerGap(155, Short.MAX_VALUE))
				);
		gl_contentPane.setVerticalGroup(
				gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
						.addContainerGap()
						.addComponent(tabbedPane, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addContainerGap(133, Short.MAX_VALUE))
				);
		contentPane.setLayout(gl_contentPane);




		// *************************************************** AFTER CREATION / PRE-START ***********************************

		// load default set of Simulations:
		File simPathFile = new File("src/resources/simulations/");
		simulationLastFolderPath = simPathFile.getAbsolutePath(); 
		loadSimFilesIntoJList(simulationLastFolderPath);

		// set simulation status:
		StyledDocument doc = sim_status.getStyledDocument();
		SimpleAttributeSet center = new SimpleAttributeSet();
		StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
		doc.setParagraphAttributes(0, doc.getLength(), center, false);
		sim_status.setBackground(new Color(250,50,50));
		sim_status.setText("No simulation loaded");

		// set population status:
		StyledDocument doc2 = pop_status.getStyledDocument();
		SimpleAttributeSet center2 = new SimpleAttributeSet();
		StyleConstants.setAlignment(center2, StyleConstants.ALIGN_CENTER);
		doc2.setParagraphAttributes(0, doc2.getLength(), center2, false);
		pop_status.setBackground(new Color(250,50,50));
		pop_status.setText("No population generated");
		
		JPanel panel_5 = new JPanel();
		panel_5.setBorder(new TitledBorder(null, "View Population", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_5.setBackground(Color.WHITE);
		panel_5.setBounds(10, 254, 204, 66);
		tab_population.add(panel_5);
		panel_5.setLayout(null);
		
		JButton btnShowBestNetwork = new JButton("View Population");
		btnShowBestNetwork.setBounds(40, 20, 130, 33);
		panel_5.add(btnShowBestNetwork);
		btnShowBestNetwork.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// if population is set && only one population viewer exists:
				if(processState >= 2){
					popView = new PopulationView(population, simulationObject);
					popView.setVisible(true);
				}else{
					JOptionPane.showMessageDialog(null, "There is no population yet! Generate or load one first!", "Population missing",JOptionPane.ERROR_MESSAGE,null);
				}
			}
		});

		// enable only the Simulation Tab:
		setProcessState(0);

	}
}
