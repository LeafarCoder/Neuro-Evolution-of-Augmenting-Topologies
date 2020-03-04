package rafa.GUI;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import rafa.NEAT.Population;
import rafa.NEAT.Population.Parameters;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ParametersJDialog extends JFrame{

	private static final long serialVersionUID = 1L;

	private ArrayList<Parameters> popParameters;


	
	public ParametersJDialog(NEATWindow NEATWindow_Obj, String popParametersLastFolderPath){
		setResizable(false);

		this.popParameters = NEATWindow_Obj.getPopParameters();
		
		setTitle("Population parameters");
		// setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 853, 459);
		getContentPane().setBackground(Color.WHITE);
		getContentPane().setLayout(null);
		setVisible(true);
		
		Object[] header = {"Parameter","Value","Description"};
		DefaultTableModel model = new DefaultTableModel(header, 0);
		JTable table_param = new JTable(model){
			private static final long serialVersionUID = 1L;

			public boolean isCellEditable(int row,int column){
		           if(column == 0 || column == 2) return false; else return true;
		       }
		};
		JScrollPane scroll = new JScrollPane(table_param);
		scroll.setBounds(10, 60, 827, 359);
		scroll.setViewportView(table_param);
		getContentPane().add(scroll);
		
		// add table change listener
	    table_param.getModel().addTableModelListener(new TableModelListener(){
	      public void tableChanged(TableModelEvent e) {
	    	  if(model.getRowCount() == 0) return;
	    	  if(e.getType() != TableModelEvent.UPDATE)return;
	    	  int row = e.getFirstRow();
	    	  String name = (String)model.getValueAt(row, 0);
	    	  double value = Double.parseDouble(model.getValueAt(row, 1)+"");
	    	  String descr = (String)model.getValueAt(row, 2);
	    	  popParameters.remove(row);
	    	  Population aux_pop = new Population();
	    	  popParameters.add(row, aux_pop.new Parameters(name, value, descr));
	      }
	    });
		
		JButton btnLoadParameters = new JButton("Load Parameters File");
		btnLoadParameters.setBounds(10, 11, 133, 38);
		getContentPane().add(btnLoadParameters);
		btnLoadParameters.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setCurrentDirectory(new File(popParametersLastFolderPath));
				fileChooser.setFileFilter(new FileNameExtensionFilter("JSON Files", "json"));
				String filePath = null;

				if(fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
					filePath = fileChooser.getSelectedFile().toString();
				}
				
				if(filePath != null){
					updatePopParameterTable(filePath, table_param);
				}
				
			}
		});

		JButton btnSaveParameters = new JButton("Save Parameters File");
		btnSaveParameters.setBounds(153, 11, 133, 38);
		btnSaveParameters.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				savePopParameters(table_param, popParametersLastFolderPath);
			}
		});
		getContentPane().add(btnSaveParameters);
		
		updatePopParameterTable("", table_param);

		table_param.setRowSelectionInterval(0, 0);
		
		table_param.getColumnModel().getColumn(0).setPreferredWidth(100);
		table_param.getColumnModel().getColumn(1).setPreferredWidth(5);
		table_param.getColumnModel().getColumn(2).setPreferredWidth(500);
		
	}

	private void updatePopParameterTable(String filePath, JTable table_param){
		// load components of population here
		JSONParser parser = new JSONParser();
		DefaultTableModel model = (DefaultTableModel)table_param.getModel();
		// Erase last table
		model.setRowCount(0);
		
		if(filePath == ""){
			Object[]  oj = {"",0,""};
			for(int i = 0; i < popParameters.size(); i++){
				oj[0] = popParameters.get(i).getName();
				oj[1] = popParameters.get(i).getValue();
				oj[2] = popParameters.get(i).getDescription();
				model.addRow(oj);
			}
		}else{
			try{
				Object obj = parser.parse(new FileReader(filePath));
				
				JSONObject jsonObject = (JSONObject)obj;
				
				JSONArray param = (JSONArray)jsonObject.get("Parameters");
				JSONArray val = (JSONArray)jsonObject.get("Values");
				JSONArray descr = (JSONArray)jsonObject.get("Description");
				
				Object[]  oj = {"",0,""};
				
				// new Hashtable for popParameters
				popParameters = new ArrayList<Parameters>();
				
				Population aux_pop = new Population();
				for(int i = 0; i < param.size(); i++){

					oj[0] = (String) param.get(i);
					oj[2] = (String) descr.get(i);

					if(val.get(i) instanceof Long){
						oj[1] = Long.valueOf(val.get(i)+"").doubleValue();
						popParameters.add(aux_pop.new Parameters((String)param.get(i), Long.valueOf(val.get(i)+"").doubleValue(), (String)descr.get(i)));
					}else{
						oj[1] = (double)val.get(i);
						popParameters.add(aux_pop.new Parameters((String)param.get(i),(double)val.get(i), (String)descr.get(i)));
					}
					
					model.addRow(oj);
				}


			} catch (FileNotFoundException er) {
	            er.printStackTrace();
	        } catch (IOException er) {
	            er.printStackTrace();
	        } catch (org.json.simple.parser.ParseException er) {
				er.printStackTrace();
			}
		}
		
	}
	
	private void savePopParameters(JTable table, String popParametersLastFolderPath){
		DefaultTableModel model = (DefaultTableModel)table.getModel();
		
		JSONObject params = new JSONObject();

		JSONArray parameters = new JSONArray();
		JSONArray values = new JSONArray();
		JSONArray descriptions = new JSONArray();
		
		for(int i = 0; i < model.getRowCount(); i++){
			parameters.add(model.getValueAt(i, 0));
			values.add(model.getValueAt(i, 1));
			descriptions.add(model.getValueAt(i, 2));
		}

		params.put("Parameters", parameters);
		params.put("Values", values);
		params.put("Description", descriptions);
		
		// ask for folder to save
		JFileChooser fc = new JFileChooser();
		fc.setFileFilter(new FileNameExtensionFilter("JSON files", ".json"));
		
		if(popParametersLastFolderPath != ""){
			fc.setCurrentDirectory(new File(popParametersLastFolderPath));
		}
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		if(fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION){
			popParametersLastFolderPath = fc.getSelectedFile().getAbsolutePath();

			FileWriter file;
			try {
				String path = fc.getSelectedFile().getAbsolutePath();
				if(!path.endsWith(".json")){
					path += ".json";
				}
				file = new FileWriter(path);
				file.write(params.toJSONString());
				file.flush();
				file.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	

}
