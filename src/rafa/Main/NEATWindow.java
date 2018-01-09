package rafa.Main;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JTabbedPane;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import processing.core.PApplet;
import rafa.NEAT.Population;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import java.awt.Color;
import javax.swing.JProgressBar;
import javax.swing.JSpinner;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import java.security.cert.PKIXRevocationChecker.Option;
import java.awt.Choice;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.Font;
import javax.swing.JSeparator;
import java.awt.Toolkit;
import java.awt.Frame;
import java.awt.Dialog.ModalExclusionType;
import javax.swing.JScrollBar;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JRadioButton;
import javax.swing.JCheckBox;

public class NEATWindow{

	private Population population;
	
	private JFrame window;
	private final int COMPRESSED_WINDOW  = 250;
	private final int EXTENDED_WINDOW = 450;
	private final int HEIGHT_WINDOW = 385;
	
	private JPanel contentPane;
	
	private JLabel lblSizeInfo;
	private JLabel lblGenerationInfo;
	private JLabel lblNumberOfSpeciesInfo;
	private JLabel lblNumberOfInputsInfo;
	private JLabel lblNumberOfOutputsInfo;
	private JLabel lblMaxFitness;
	
	private JSpinner size_input;
	private JSpinner inputsNum_input;
	private JSpinner outputsNum_input;
	private JTextField textField;
	private JTextField textField_1;
	private JTextField textField_2;
	private JTextField textField_3;
	
	private JRadioButton rdbtnDefaultSettings;
	private JRadioButton rdbtnCostumSettings;
	
	private boolean extendedForMoreOptions = false;
	
	private JTabbedPane tabbedPane;
	private JTextField in_cofExcess;
	private JTextField in_cofDisjoint;
	private JTextField in_cofWeights;
	private JTextField in_threshold;
	
	public void updatePopulationInformation(){

		lblSizeInfo.setText("Population size: "+population.getPopSize());
		lblGenerationInfo.setText("Generation: "+population.getGeneration());
		lblNumberOfInputsInfo.setText("Number of inputs: "+population.getNumberOfInputs());
		lblNumberOfOutputsInfo.setText("Number of outputs: "+population.getNumberOfOutputs());
		lblNumberOfSpeciesInfo.setText("Number of species: "+population.getNumberOfSpecies());
		lblMaxFitness.setText("Maximum fitness: "+population.getMaxFitness());
		
		lblSizeInfo.setEnabled(true);
		lblGenerationInfo.setEnabled(true);
		lblNumberOfSpeciesInfo.setEnabled(true);
		lblNumberOfInputsInfo.setEnabled(true);
		lblNumberOfOutputsInfo.setEnabled(true);
		lblMaxFitness.setEnabled(true);
	}
	
	public void expandWindowForMoreOptions(){
		
		System.out.println(tabbedPane.getWidth());
		System.out.println(tabbedPane.getHeight());
		window.setSize(EXTENDED_WINDOW, HEIGHT_WINDOW);
		tabbedPane.setSize(400, 324);
		
		
	}
	
	public void compressWindowForMoreOptions(){
		
		window.setSize(COMPRESSED_WINDOW, HEIGHT_WINDOW);
		tabbedPane.setSize(222, 324);
		
	}
	
	public NEATWindow() {

		try{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch(Exception e){
			e.printStackTrace();
		}

		
		window = new JFrame("NEAT");
		window.setResizable(false);
		window.setSize(COMPRESSED_WINDOW, HEIGHT_WINDOW);
		window.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("NET.png")));
		window.setVisible(true);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			
		contentPane = new JPanel();
		//contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		window.setContentPane(contentPane);
		
		tabbedPane = new JTabbedPane();
		tabbedPane.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				if(tabbedPane.getSelectedIndex() != 0){
					window.setSize(COMPRESSED_WINDOW, HEIGHT_WINDOW);
				}else{
					if(rdbtnDefaultSettings.isSelected()){
						window.setSize(COMPRESSED_WINDOW, HEIGHT_WINDOW);
					}else{
						window.setSize(EXTENDED_WINDOW, HEIGHT_WINDOW);
					}
				}
				
			}
		});
		
				JPanel tab_population = new JPanel();
				tabbedPane.addTab("Population", null, tab_population, "Generate a new, load or save populations");
				
				JPanel panel_3 = new JPanel();
				panel_3.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Load / Save Population", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
				
				JPanel panel_4 = new JPanel();
				panel_4.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Generate new population", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
				GroupLayout gl_tab_population = new GroupLayout(tab_population);
				gl_tab_population.setHorizontalGroup(
					gl_tab_population.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_tab_population.createSequentialGroup()
							.addContainerGap()
							.addGroup(gl_tab_population.createParallelGroup(Alignment.LEADING, false)
								.addComponent(panel_3, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(panel_4, Alignment.TRAILING, 0, 0, Short.MAX_VALUE))
							.addContainerGap(249, Short.MAX_VALUE))
				);
				gl_tab_population.setVerticalGroup(
					gl_tab_population.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_tab_population.createSequentialGroup()
							.addContainerGap()
							.addComponent(panel_3, GroupLayout.PREFERRED_SIZE, 51, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(panel_4, GroupLayout.PREFERRED_SIZE, 227, GroupLayout.PREFERRED_SIZE)
							.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
				);
				
				JButton btn_generate = new JButton("Generate");
				btn_generate.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						
						int size = (int)size_input.getValue();
						int numIn = (int)inputsNum_input.getValue();
						int numOut = (int)outputsNum_input.getValue();
						float coefDisjoint = (float)Float.parseFloat(in_cofDisjoint.getText());
						float coefExcess = (float)Float.parseFloat(in_cofExcess.getText());
						float coefWeights = (float)Float.parseFloat(in_cofWeights.getText());
						float threshold = (float)Float.parseFloat(in_threshold.getText());
						
						population = new Population(size, numIn, numOut, 1);
						population.setSpeciationParameters(coefDisjoint, coefExcess, coefWeights, threshold);
						
						/*
						for(int i = 0; i < population.getPopSize(); i++){
							System.out.println(population.getNetworkByID(i).getGenomeString());
							
							for(int j = 0; j < population.getNetworkByID(i).getNumLayers(); j++){
								System.out.println("Nodes in layer "+j+": "+population.getNetworkByID(i).getNumNodesInLayer(j));
							}
						}
						*/						
						updatePopulationInformation();
						
						JOptionPane.showMessageDialog(null, "Population with specified requisites is created.");

					}
				});
				
				JLabel lblSize = new JLabel("Size:");
				
				JLabel label = new JLabel("Inputs:");
				
				JLabel label_1 = new JLabel("Ouputs:");
				
						size_input = new JSpinner();
						size_input.addChangeListener(new ChangeListener() {
							public void stateChanged(ChangeEvent arg0) {
								if((int)size_input.getValue() <= 0){
									btn_generate.setEnabled(false);
								}else{
									btn_generate.setEnabled(true);
								}
							}
						});
						size_input.setValue((int)1);
						size_input.setToolTipText("Inser the initial number of networks");
						
						inputsNum_input = new JSpinner();
						inputsNum_input.addChangeListener(new ChangeListener() {
							public void stateChanged(ChangeEvent e) {
								if((int)inputsNum_input.getValue() <= 0){
									btn_generate.setEnabled(false);
								}else{
									btn_generate.setEnabled(true);
								}
							}
						});
						inputsNum_input.setValue((int)1);
						inputsNum_input.setToolTipText("Insert the number of input nodes");
						
						outputsNum_input = new JSpinner();
						outputsNum_input.addChangeListener(new ChangeListener() {
							public void stateChanged(ChangeEvent e) {
								if((int)outputsNum_input.getValue() <= 0){
									btn_generate.setEnabled(false);
								}else{
									btn_generate.setEnabled(true);
								}
							}
						});
						outputsNum_input.setValue((int)1);
						outputsNum_input.setToolTipText("Insert the number of output nodes");
						
						ButtonGroup bg1 = new ButtonGroup();
						
						rdbtnCostumSettings = new JRadioButton("Costum settings");
						rdbtnCostumSettings.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								
								window.setSize(EXTENDED_WINDOW, HEIGHT_WINDOW);
								
							}
						});
						
						rdbtnDefaultSettings = new JRadioButton("Default settings");
						rdbtnDefaultSettings.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent arg0) {
								
								window.setSize(COMPRESSED_WINDOW, HEIGHT_WINDOW);
								
							}
						});
						rdbtnDefaultSettings.setSelected(true);

						bg1.add(rdbtnCostumSettings);
						bg1.add(rdbtnDefaultSettings);
						
						
						
						GroupLayout gl_panel_4 = new GroupLayout(panel_4);
						gl_panel_4.setHorizontalGroup(
							gl_panel_4.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_panel_4.createSequentialGroup()
									.addGroup(gl_panel_4.createParallelGroup(Alignment.LEADING)
										.addGroup(gl_panel_4.createSequentialGroup()
											.addContainerGap()
											.addGroup(gl_panel_4.createParallelGroup(Alignment.LEADING)
												.addComponent(lblSize)
												.addComponent(label, GroupLayout.PREFERRED_SIZE, 42, GroupLayout.PREFERRED_SIZE)
												.addComponent(label_1, GroupLayout.PREFERRED_SIZE, 42, GroupLayout.PREFERRED_SIZE))
											.addPreferredGap(ComponentPlacement.RELATED)
											.addGroup(gl_panel_4.createParallelGroup(Alignment.LEADING)
												.addComponent(outputsNum_input, GroupLayout.PREFERRED_SIZE, 67, GroupLayout.PREFERRED_SIZE)
												.addComponent(inputsNum_input, GroupLayout.PREFERRED_SIZE, 67, GroupLayout.PREFERRED_SIZE)
												.addComponent(size_input, GroupLayout.PREFERRED_SIZE, 67, GroupLayout.PREFERRED_SIZE)))
										.addGroup(gl_panel_4.createSequentialGroup()
											.addGap(42)
											.addGroup(gl_panel_4.createParallelGroup(Alignment.LEADING)
												.addComponent(rdbtnDefaultSettings)
												.addComponent(rdbtnCostumSettings))))
									.addContainerGap(260, Short.MAX_VALUE))
								.addGroup(Alignment.TRAILING, gl_panel_4.createSequentialGroup()
									.addContainerGap(53, Short.MAX_VALUE)
									.addComponent(btn_generate, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
									.addGap(46))
						);
						gl_panel_4.setVerticalGroup(
							gl_panel_4.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_panel_4.createSequentialGroup()
									.addContainerGap()
									.addGroup(gl_panel_4.createParallelGroup(Alignment.BASELINE)
										.addComponent(lblSize)
										.addComponent(size_input, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addGroup(gl_panel_4.createParallelGroup(Alignment.BASELINE)
										.addComponent(label)
										.addComponent(inputsNum_input, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addGroup(gl_panel_4.createParallelGroup(Alignment.BASELINE)
										.addComponent(label_1)
										.addComponent(outputsNum_input, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
									.addGap(17)
									.addComponent(rdbtnDefaultSettings)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(rdbtnCostumSettings)
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addComponent(btn_generate, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
									.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
						);
						panel_4.setLayout(gl_panel_4);
						
						JButton button_1 = new JButton("Load");
						button_1.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								

								JFileChooser fileChooser = new JFileChooser();
								fileChooser.setFileFilter(new FileNameExtensionFilter("JSON Files", "json"));
								String filePath = null;
								
								if(fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
									filePath = fileChooser.getSelectedFile().toString();
								}
								
								if(filePath != null){
									population = Population.loadPopulationFromFile(filePath);
									JOptionPane.showMessageDialog(null, "Population successefully loaded from "+filePath+".");
								}else{
									JOptionPane.showMessageDialog(null, "Population was not loaded.");
								}
								updatePopulationInformation();
									
							}
						});
						
						JButton button_2 = new JButton("Save");
						button_2.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								
								JFileChooser fileChooser = new JFileChooser();
								fileChooser.setFileFilter(new FileNameExtensionFilter("JSON Files", "json"));
								String pathName = null;
								
								if(fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
									pathName = fileChooser.getSelectedFile().toString();
									if(!pathName.endsWith(".json")){
										pathName += ".json";
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
						GroupLayout gl_panel_3 = new GroupLayout(panel_3);
						gl_panel_3.setHorizontalGroup(
							gl_panel_3.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_panel_3.createSequentialGroup()
									.addGap(24)
									.addComponent(button_1)
									.addGap(18)
									.addComponent(button_2)
									.addGap(25))
						);
						gl_panel_3.setVerticalGroup(
							gl_panel_3.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_panel_3.createSequentialGroup()
									.addGroup(gl_panel_3.createParallelGroup(Alignment.BASELINE)
										.addComponent(button_2)
										.addComponent(button_1))
									.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
						);
						panel_3.setLayout(gl_panel_3);
						tab_population.setLayout(gl_tab_population);
		
		JPanel tab_properties = new JPanel();
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
		
		JLabel lblPopulationProperties = new JLabel("Population properties:");
		lblPopulationProperties.setFont(new Font("Tahoma", Font.PLAIN, 12));
		
		JSeparator separator = new JSeparator();
		
		
		
		
		GroupLayout gl_tab_properties = new GroupLayout(tab_properties);
		gl_tab_properties.setHorizontalGroup(
			gl_tab_properties.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_tab_properties.createSequentialGroup()
					.addGap(20)
					.addGroup(gl_tab_properties.createParallelGroup(Alignment.LEADING)
						.addComponent(lblMaxFitness, GroupLayout.PREFERRED_SIZE, 133, GroupLayout.PREFERRED_SIZE)
						.addGroup(gl_tab_properties.createParallelGroup(Alignment.TRAILING, false)
							.addComponent(lblNumberOfOutputsInfo, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addComponent(lblNumberOfSpeciesInfo, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 133, GroupLayout.PREFERRED_SIZE))
						.addComponent(lblNumberOfInputsInfo, GroupLayout.PREFERRED_SIZE, 145, GroupLayout.PREFERRED_SIZE)
						.addGroup(gl_tab_properties.createParallelGroup(Alignment.TRAILING, false)
							.addComponent(lblSizeInfo, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addComponent(lblGenerationInfo, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 113, Short.MAX_VALUE))
						.addGroup(gl_tab_properties.createParallelGroup(Alignment.TRAILING)
							.addComponent(separator, GroupLayout.DEFAULT_SIZE, 194, Short.MAX_VALUE)
							.addComponent(lblPopulationProperties, Alignment.LEADING)))
					.addContainerGap(42, Short.MAX_VALUE))
		);
		gl_tab_properties.setVerticalGroup(
			gl_tab_properties.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_tab_properties.createSequentialGroup()
					.addGap(26)
					.addComponent(lblPopulationProperties)
					.addGap(10)
					.addComponent(separator, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblSizeInfo)
					.addGap(11)
					.addComponent(lblGenerationInfo)
					.addGap(11)
					.addComponent(lblNumberOfInputsInfo)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(lblNumberOfOutputsInfo)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(lblNumberOfSpeciesInfo)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(lblMaxFitness)
					.addContainerGap(86, Short.MAX_VALUE))
		);
		tab_properties.setLayout(gl_tab_properties);
		
		JPanel panel_2 = new JPanel();
		tabbedPane.addTab("Visualize", null, panel_2, "Visualize the networks");
		
		JButton btnVisualize = new JButton("Visualize");
		btnVisualize.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(population.getPopSize() < 4)return;

				NetworkGraph networkGraphWindow = new NetworkGraph();
				networkGraphWindow.setPopulation(population);
				PApplet.runSketch(new String[]{"netGraph"}, networkGraphWindow);
			}
		});
		GroupLayout gl_panel_2 = new GroupLayout(panel_2);
		gl_panel_2.setHorizontalGroup(
			gl_panel_2.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_2.createSequentialGroup()
					.addGap(57)
					.addComponent(btnVisualize)
					.addContainerGap(61, Short.MAX_VALUE))
		);
		gl_panel_2.setVerticalGroup(
			gl_panel_2.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_2.createSequentialGroup()
					.addGap(57)
					.addComponent(btnVisualize)
					.addContainerGap(204, Short.MAX_VALUE))
		);
		panel_2.setLayout(gl_panel_2);
		
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Costum settings", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		
		JLabel label_5 = new JLabel("Coefficient for excess genes:");
		
		in_cofExcess = new JTextField();
		in_cofExcess.setText("1.0");
		in_cofExcess.setColumns(10);
		
		in_cofDisjoint = new JTextField();
		in_cofDisjoint.setText("1.0");
		in_cofDisjoint.setColumns(10);
		
		JLabel label_3 = new JLabel("Coefficient for weights:");
		
		in_cofWeights = new JTextField();
		in_cofWeights.setText("1.0");
		in_cofWeights.setColumns(10);
		
		JLabel label_4 = new JLabel("Threshold for speciation:");
		
		in_threshold = new JTextField();
		in_threshold.setText("3.0");
		in_threshold.setColumns(10);
		
		
		JLabel label_2 = new JLabel("Coefficient for disjoint genes:");
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addComponent(label_5)
						.addComponent(in_cofExcess, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(label_2, GroupLayout.PREFERRED_SIZE, 155, GroupLayout.PREFERRED_SIZE)
						.addComponent(in_cofDisjoint, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(label_3, GroupLayout.PREFERRED_SIZE, 141, GroupLayout.PREFERRED_SIZE)
						.addComponent(in_cofWeights, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(label_4, GroupLayout.PREFERRED_SIZE, 141, GroupLayout.PREFERRED_SIZE)
						.addComponent(in_threshold, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.TRAILING)
				.addGroup(Alignment.LEADING, gl_panel.createSequentialGroup()
					.addContainerGap()
					.addComponent(label_5)
					.addGap(11)
					.addComponent(in_cofExcess, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addGap(6)
					.addComponent(label_2)
					.addGap(6)
					.addComponent(in_cofDisjoint, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addGap(6)
					.addComponent(label_3)
					.addGap(6)
					.addComponent(in_cofWeights, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addGap(6)
					.addComponent(label_4)
					.addGap(6)
					.addComponent(in_threshold, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(116, Short.MAX_VALUE))
		);
		panel.setLayout(gl_panel);
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addComponent(tabbedPane, GroupLayout.PREFERRED_SIZE, 223, GroupLayout.PREFERRED_SIZE)
					.addGap(18)
					.addComponent(panel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(89, Short.MAX_VALUE))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
						.addComponent(panel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 333, Short.MAX_VALUE)
						.addComponent(tabbedPane, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 333, Short.MAX_VALUE))
					.addContainerGap())
		);
		
		JPanel panel_1 = new JPanel();
		tabbedPane.addTab("Evolution", null, panel_1, "Used to control evolution of the NeuralNetwork");
		contentPane.setLayout(gl_contentPane);
		//contentPane.setLayout(gl_contentPane);
	}
}
