package rafa.GUI;


import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class NetworksViewAuxJFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	private JPanel panel;
	
	public NetworksViewAuxJFrame(NetworkGraph sketch) {
		
		addWindowListener(new WindowAdapter() {
			public void windowOpened(WindowEvent arg0) {
				int border = 10;
				int w = getContentPane().getWidth() - border * 2;
				int h = getContentPane().getHeight() - border * 2;
				panel.setBounds(border, border, w, h);
				sketch.setAuxSize(panel.getWidth(), panel.getHeight());
			}
		});
		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent arg0) {
				int border = 10;
				int w = getContentPane().getWidth() - border * 2;
				int h = getContentPane().getHeight() - border * 2;
				panel.setBounds(border, border, w, h);
				sketch.resizeWindow(panel.getWidth(), panel.getHeight());
			}
		});
		
		setTitle("View Networks");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setVisible(true);
		setLocation(10, 10);
		setSize(600, 600);
		getContentPane().setLayout(null);
		
		panel = new JPanel(null);
		panel.setBounds(10, 11, 564, 539);
		panel.add(sketch);
		sketch.init();
		
		getContentPane().add(panel);
		
	}
}
