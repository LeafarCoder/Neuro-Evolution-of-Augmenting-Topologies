package rafa.GUI;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Writer;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import rafa.Main.Simulations.JavaFileToObj;

import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.UIManager;

import java.awt.Font;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class GenerateSimulationObjects extends JDialog {

	private static String path_sims_folders;
	private static String path_obj;
	
	private boolean main_java_file_found;
	private JavaFileToObj main_FileToObj;
	
	private JPanel contentPane;
	
	public static JList<String> list_sims_folders;
	public static JList<String> list_obj;
	
	JTextArea txtArea_paths;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		
		File javaFilesDir = new File("src/rafa/Main/Simulations/Created");
		File objDir = new File("src/resources/simulations");
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GenerateSimulationObjects frame = new GenerateSimulationObjects(javaFilesDir.getAbsolutePath(),objDir.getAbsolutePath());
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	
	public GenerateSimulationObjects(String java_p, String obj_p) {
		setResizable(false);

		setModal(true);
		
		// Changes overall look (look and feel) of the window
		try{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch(Exception e){
			e.printStackTrace();
		}

		// initialize path variables:
		// Get local path for default Simulations (relative to Project path)
		path_sims_folders = java_p;
		path_obj = obj_p;

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setTitle("Simulation objects generator");
		setBounds(100, 100, 552, 296);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		list_sims_folders = new JList<String>(new DefaultListModel<String>());
		list_sims_folders.setBounds(10, 34, 170, 167);
		contentPane.add(list_sims_folders);
		
		JButton btnCreateObject = new JButton("<html><center>Create Object<br>>>></center></html>");
		btnCreateObject.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				if(list_sims_folders.getSelectedIndices().length == 0)return;
				
				main_java_file_found = false;
				
				String simFolderName = list_sims_folders.getSelectedValue();
				String fullPathOfSim = path_sims_folders + "\\" + simFolderName;
				File simDirectory = new File(fullPathOfSim);

				File[] java_files = simDirectory.listFiles(new FilenameFilter() {
					public boolean accept(File dir, String name) {
						if (name.endsWith(".java")) {return true;} else {return false;}
					}
				});

				new File(path_obj + "\\" +  simFolderName).mkdirs();
				
				for(File java_file: java_files){

					try{
						String java_file_name = java_file.getName();
						java_file_name = java_file_name.substring(0, java_file_name.length() - 5);
						
						JavaFileToObj jto = new JavaFileToObj(path_obj + "\\" + simFolderName, java_file_name, java_file);

						JavaFileObject file = jto.getJavaFileObject();
						Iterable<? extends JavaFileObject> files = Arrays.asList(file);
						jto.compile(files);
						
						
						if(java_file_name.equals(simFolderName + "_Sim")){
							main_java_file_found = true;
							main_FileToObj = jto;
						}

					} catch (SecurityException e1) {
						e1.printStackTrace();
					}
				}
				
				if(main_java_file_found){
					Object newObj = main_FileToObj.instantiateObject();

					// ask name of file
					String obj_Name = (String) JOptionPane.showInputDialog(null, "Insert simulation object name:", "Simulation object", JOptionPane.DEFAULT_OPTION , null, null, simFolderName);
					if(obj_Name == null)obj_Name = simFolderName;

					// create object
					FileOutputStream fos;
					try {
						// new File(path_obj + "\\" +  simFolderName).mkdirs();
						fos = new FileOutputStream(new File(path_obj + "\\" +  simFolderName + "\\" + obj_Name + ".sim"));
						ObjectOutputStream oos;
						oos = new ObjectOutputStream(fos);
						oos.writeObject(newObj);
						oos.close();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					

					updateObjList(path_obj);
				}

			}
		});
		btnCreateObject.setBounds(215, 81, 111, 39);
		contentPane.add(btnCreateObject);
		
		list_obj = new JList<String>(new DefaultListModel<String>());
		list_obj.setBounds(356, 34, 170, 167);
		contentPane.add(list_obj);
		
		JLabel lbl_class = new JLabel("Java files");
		lbl_class.setFont(new Font("Tahoma", Font.BOLD, 18));
		lbl_class.setBounds(48, 9, 98, 22);
		contentPane.add(lbl_class);
		
		txtArea_paths = new JTextArea();
		txtArea_paths.setFont(new Font("Tahoma", Font.PLAIN, 11));
		txtArea_paths.setLineWrap(true);
		txtArea_paths.setEditable(false);
		JScrollPane paths_scroll = new JScrollPane(txtArea_paths);
		paths_scroll.setBounds(10, 212, 516, 34);
		contentPane.add(paths_scroll);
		
		JLabel lbl_obj = new JLabel("Simulation objects");
		lbl_obj.setFont(new Font("Tahoma", Font.BOLD, 18));
		lbl_obj.setBounds(356, 6, 170, 28);
		contentPane.add(lbl_obj);
		
		JButton btn_change_path_sims_folders = new JButton("<html> <center> Change<br>Path </center></html>");
		btn_change_path_sims_folders.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				JFileChooser fc = new JFileChooser();
				fc.setCurrentDirectory(new File(path_sims_folders));
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

				if(fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
					path_sims_folders = fc.getSelectedFile().getAbsolutePath();
				}
				
				updateClassList(path_sims_folders);
				updatePathsDisplay();
			}
		});
		btn_change_path_sims_folders.setBounds(190, 164, 66, 37);
		contentPane.add(btn_change_path_sims_folders);
		
		JButton btn_change_path_obj = new JButton("<html> <center> Change<br>Path </center></html>");
		btn_change_path_obj.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				JFileChooser fc = new JFileChooser();
				fc.setCurrentDirectory(new File(path_obj));
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

				if(fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
					path_obj = fc.getSelectedFile().getAbsolutePath();
				}
				
				updateObjList(path_obj);
				updatePathsDisplay();
			}
		});
		btn_change_path_obj.setBounds(280, 164, 66, 37);
		contentPane.add(btn_change_path_obj);
		
		// Initialize lists *******************************
		
		updateClassList(path_sims_folders);
		updateObjList(path_obj);
		updatePathsDisplay();
		
	}

	private static void updateClassList(String path){
		File file = new File(path);

		String[] simulations = file.list(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				if (dir.isDirectory()) {return true;} else {return false;}
			}
		});

		DefaultListModel<String> model = new DefaultListModel<>();
		for(String s: simulations){
			//model.addElement(f.getName().split("[.]")[0]);
			model.addElement(s);
		}
		list_sims_folders.setModel(model);

		if(list_sims_folders.getModel().getSize() > 0){
			list_sims_folders.setSelectedIndex(0);
		}
	}
	
	
	private static void updateObjList(String path){
		File file = new File(path);

		String[] obj_folders = file.list(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				if (dir.isDirectory()) {return true;} else {return false;}
			}
		});
		
		DefaultListModel<String> model = new DefaultListModel<>();
		for(String s: obj_folders){
			//model.addElement(f.getName().split("[.]")[0]);
			model.addElement(s);
		}
		list_obj.setModel(model);

	}

	private void updatePathsDisplay(){
		txtArea_paths.setText(	" Java files path: " + path_sims_folders + "\n" +
				" Objects path: " + path_obj);
	}

}
