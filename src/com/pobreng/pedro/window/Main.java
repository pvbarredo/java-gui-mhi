package com.pobreng.pedro.window;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;
import com.pobreng.pedro.model.CriticalValue;
import com.pobreng.pedro.model.Device;
import com.pobreng.pedro.model.Property;
import javax.swing.JSeparator;
import javax.swing.JScrollBar;
import java.awt.Scrollbar;
import javax.swing.JScrollPane;




public class Main {

	private JFrame frmPobrengPedro;
	private DefaultListModel<String> model ;
	private JList<String> list;
	private JTextField latitudeTextField;
	private JTextField longitudeTextField;
	private List<Device> devices = new ArrayList<>();
	private JLabel deviceUsername;
	private JLabel deviceName;
	private JTextField textField;
	private Map<String, JLabel> dynamicPropertyLabel = new HashMap<String, JLabel>();
	private Map<String, JTextField> dynamicPropertyTextfield = new HashMap<String, JTextField>();
	
	JPanel panel_1 ;
	private JButton btnSubmit;
	private JScrollPane scrollPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main window = new Main();
					window.frmPobrengPedro.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Main() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmPobrengPedro = new JFrame();
		frmPobrengPedro.setTitle("APC MHI - POBRENG PEDRO ");
		frmPobrengPedro.setBounds(100, 100, 861, 369);
		frmPobrengPedro.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmPobrengPedro.getContentPane().setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		frmPobrengPedro.getContentPane().add(panel, BorderLayout.WEST);

		 model  = new DefaultListModel();
		 list = new JList(model);
		 list.addMouseListener(new MouseAdapter() {
		 	@Override
		 	public void mouseClicked(MouseEvent evt) {
		 	
		        if (evt.getClickCount() == 2) {
		            Device selectedDevice = devices.get(list.locationToIndex(evt.getPoint()));
		            setDataForm(selectedDevice);
		        }
		 	}

			private void setDataForm(Device selectedDevice) {
				deviceName.setText(selectedDevice.getName());
				deviceUsername.setText(selectedDevice.getUsername());
				addPropertyComponent(selectedDevice);
			}
			
			private void addPropertyComponent(Device selectedDevice) {
				removePropertyComponent();
				int y_axis = 14;
				int x_axis = 6; 
				
				for (Property property : selectedDevice.getType().getProperty()) {
					JLabel newLabel = new JLabel("Property : " + property.getName());
					dynamicPropertyLabel.put(String.valueOf(property.getId()),newLabel);
					panel_1.add(newLabel, "6,"+ y_axis +", right, default");
					y_axis++;
					
					for (CriticalValue criticalValue : property.getCritical_value()) {
						JLabel newLabel2 = new JLabel("--"+ criticalValue.getDescription() + " - Condition: " + criticalValue.getCondition());
						dynamicPropertyLabel.put(String.valueOf(criticalValue.getId())+ "Critical",newLabel2);
						panel_1.add(newLabel2, "8,"+ y_axis +", right, default");
						
						y_axis++;
						
						JLabel newlabel3 = new JLabel("Min Value");
						dynamicPropertyLabel.put(String.valueOf(criticalValue.getId())+ y_axis+ "MinVal",newlabel3);
						panel_1.add(newlabel3, "6,"+ y_axis +", right, default");
						
						JTextField newCriticalValueTextField = new JTextField();
						newCriticalValueTextField.setColumns(10);
						dynamicPropertyTextfield.put(String.valueOf(criticalValue.getId())+ y_axis+"Min",newCriticalValueTextField);
						panel_1.add(newCriticalValueTextField, "8,"+ y_axis +", fill, default");
						
						y_axis++;
						
						JLabel newlabel4 = new JLabel("Max Value");
						dynamicPropertyLabel.put(String.valueOf(criticalValue.getId())+y_axis+ "MinVal",newlabel4);
						panel_1.add(newlabel4, "6,"+ y_axis +", right, default");
						
						JTextField maxTextField = new JTextField();
						maxTextField.setColumns(10);
						dynamicPropertyTextfield.put(String.valueOf(criticalValue.getId())+y_axis+ "Max",maxTextField);
						panel_1.add(maxTextField, "8,"+ y_axis +", fill, default");
						
						y_axis++;
						
						
					}
					y_axis++;
					
				}

				panel_1.revalidate();
				panel_1.repaint(); 
			}
			
			private void removePropertyComponent() {
				
				for(Map.Entry<String, JLabel> propertyLabel : dynamicPropertyLabel.entrySet()) {
					JLabel removeLabel = propertyLabel.getValue();
					dynamicPropertyLabel.remove(removeLabel);
					panel_1.remove(removeLabel);
					panel_1.revalidate();
					panel_1.repaint(); 
				}
				
				for(Map.Entry<String, JTextField> propertyTextField : dynamicPropertyTextfield.entrySet()) {
					JTextField removeTextField = propertyTextField.getValue();
					dynamicPropertyTextfield.remove(removeTextField);
					panel_1.remove(propertyTextField.getValue());
					panel_1.revalidate();
					panel_1.repaint(); 
				}
				
				
				
			
				
			}
		 });
		 list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		panel.setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
				ColumnSpec.decode("91px"),
				FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
				ColumnSpec.decode("28px"),},
			new RowSpec[] {
				FormSpecs.LINE_GAP_ROWSPEC,
				RowSpec.decode("23px"),
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,}));
		
		JButton btnGetDevice = new JButton("GET DEVICE");
		
		btnGetDevice.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				
				try {

					URL url = new URL("http://localhost:8000/device");
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.setRequestMethod("GET");
					conn.setRequestProperty("Accept", "application/json");

					if (conn.getResponseCode() != 200) {
						throw new RuntimeException("Failed : HTTP error code : "
								+ conn.getResponseCode());
					}

					BufferedReader br = new BufferedReader(new InputStreamReader(
						(conn.getInputStream())));

					String output;
					System.out.println("Output from Server .... \n");
					while ((output = br.readLine()) != null) {
						
						JSONObject obj = new JSONObject(output);

						JSONArray array = obj.getJSONArray("data");
						Gson gson = new Gson();
						model.removeAllElements();
						for(int i = 0 ; i < array.length() ; i++){
							
							JSONObject deviceObject = array.getJSONObject(i);
							Device device = gson.fromJson(deviceObject.toString(), Device.class);
							devices.add(device);		
							model.addElement(device.getName());		
						   
						}
						
						
					}

					conn.disconnect();

				  } catch (MalformedURLException e) {

					e.printStackTrace();

				  } catch (IOException e) {

					e.printStackTrace();

				  }

				
				
			}
		});
		panel.add(btnGetDevice, "2, 2, 2, 1, left, top");
		
		
		
		panel.add(list, "2, 4, 2, 1, left, center");
		
		panel_1 = new JPanel();
		frmPobrengPedro.getContentPane().add(panel_1, BorderLayout.CENTER);
		panel_1.setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),},
			new RowSpec[] {
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,}));
		
		JLabel lblName = new JLabel("Device Username");
		panel_1.add(lblName, "6, 4, right, default");
		
		deviceUsername = new JLabel("Device Username");
		panel_1.add(deviceUsername, "8, 4");
		
		btnSubmit = new JButton("Submit");
		panel_1.add(btnSubmit, "10, 4");
		
		JLabel lblNewLabel = new JLabel("Device Name");
		panel_1.add(lblNewLabel, "6, 6, right, default");
		
		deviceName = new JLabel("Device Name");
		panel_1.add(deviceName, "8, 6");
		
		JLabel lblLatitude = new JLabel("Latitude");
		panel_1.add(lblLatitude, "6, 8, right, default");
		
		latitudeTextField = new JTextField();
		latitudeTextField.setColumns(10);
		panel_1.add(latitudeTextField, "8, 8, fill, default");
		
		JLabel lblLongitude = new JLabel("Longitude");
		panel_1.add(lblLongitude, "6, 10, right, default");
		
		longitudeTextField = new JTextField();
		longitudeTextField.setColumns(10);
		panel_1.add(longitudeTextField, "8, 10, fill, default");
		
		scrollPane = new JScrollPane();
		frmPobrengPedro.getContentPane().add(scrollPane, BorderLayout.SOUTH);
		
	}

}
