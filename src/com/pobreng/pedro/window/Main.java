package com.pobreng.pedro.window;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.DataOutputStream;
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
import com.pobreng.pedro.model.Data;
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
	private Map<String, JLabel> dynamicPropertyLabel = new HashMap<String, JLabel>();
	private Map<String, JTextField> dynamicDataTextField = new HashMap<String, JTextField>();
	
	JPanel panel_1 ;
	private JButton btnSubmit;
	private JScrollPane scrollPane;
	private JButton btnClearAllDevice;
	private JButton btnSeedData;
	private static String APP_URL = "http://localhost:8000";
	
	Device selectedDevice;

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
		            selectedDevice = devices.get(list.locationToIndex(evt.getPoint()));
		            btnSubmit.setEnabled(true);
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
				
				for (Property property : selectedDevice.getType().getProperty()) {
					JLabel newLabel = new JLabel("Property : " + property.getName() + "--" + property.getUnit());
					dynamicPropertyLabel.put(String.valueOf(property.getId()),newLabel);
					panel_1.add(newLabel, "6,"+ y_axis +", right, default");
					
					JTextField newDataTextField = new JTextField();
					newDataTextField.setColumns(10);
					dynamicDataTextField.put(String.valueOf(property.getId()),newDataTextField);
					panel_1.add(newDataTextField, "8,"+ y_axis +", fill, default");
					
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
				
				for(Map.Entry<String, JTextField> propertyTextField : dynamicDataTextField.entrySet()) {
					JTextField removeTextField = propertyTextField.getValue();
					dynamicDataTextField.remove(removeTextField);
					panel_1.remove(propertyTextField.getValue());
					panel_1.revalidate();
					panel_1.repaint(); 
				}
				
				
				dynamicDataTextField.clear();
			
				
			}
		 });
		 list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		panel.setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
				ColumnSpec.decode("91px"),
				FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
				ColumnSpec.decode("28px"),},
			new RowSpec[] {
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.LINE_GAP_ROWSPEC,
				RowSpec.decode("23px"),
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,}));
		
		JButton btnGetDevice = new JButton("GET DEVICES");
		
		btnGetDevice.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				try {

					URL url = new URL(APP_URL + "/device");
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
		
		btnSeedData = new JButton("Seed Data");
		btnSeedData.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {

					URL url = new URL(APP_URL + "/deviceData/seedData");
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.setRequestMethod("GET");
					conn.setRequestProperty("Accept", "application/json");

					if (conn.getResponseCode() != 200) {
						throw new RuntimeException("Failed : HTTP error code : "
								+ conn.getResponseCode());
					}

					

					conn.disconnect();

				  } catch (MalformedURLException ex) {

					ex.printStackTrace();

				  } catch (IOException ex) {

					ex.printStackTrace();

				  }
				
			}
		});
		panel.add(btnSeedData, "2, 2, 3, 1");
		
		btnClearAllDevice = new JButton("Clear All Datas");
		btnClearAllDevice.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {

					URL url = new URL(APP_URL + "/deviceData/deleteAll");
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.setRequestMethod("GET");
					conn.setRequestProperty("Accept", "application/json");

					if (conn.getResponseCode() != 200) {
						throw new RuntimeException("Failed : HTTP error code : "
								+ conn.getResponseCode());
					}

					

					conn.disconnect();

				  } catch (MalformedURLException ex) {

					ex.printStackTrace();

				  } catch (IOException ex) {

					ex.printStackTrace();

				  }
			}
		});
		panel.add(btnClearAllDevice, "2, 4, 3, 1");
		panel.add(btnGetDevice, "2, 6, 3, 1, left, top");
		
		
		
		panel.add(list, "2, 8, 2, 1, left, center");
		
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
		btnSubmit.setEnabled(false);
		btnSubmit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				System.out.println(selectedDevice.getId());
				System.out.println(latitudeTextField.getText());
				System.out.println(longitudeTextField.getText());
		
				for (Map.Entry<String, JTextField> entry : dynamicDataTextField.entrySet()) {
					JTextField dataValue  = entry.getValue();
				    System.out.println("Key = " + entry.getKey() + ", Value = " + dataValue.getText());
				    
				    try {
				    	String urlParameters = "device_id="+selectedDevice.getId()+"&property_id=" + entry.getKey() + "&value=" + dataValue.getText()+"&latitude="+latitudeTextField.getText()+"&longitude="+longitudeTextField.getText();
				    	URL url = new URL(APP_URL + "/deviceData/addDeviceData?" + urlParameters);
						HttpURLConnection conn = (HttpURLConnection) url.openConnection();
						
						conn.setRequestMethod("GET");
						conn.setRequestProperty("Accept", "application/json");

						if (conn.getResponseCode() != 200) {
							throw new RuntimeException("Failed : HTTP error code : "
									+ conn.getResponseCode());
						}
						
						int responseCode = conn.getResponseCode();
						System.out.println("\nSending 'POST' request to URL : " + url);
						System.out.println("Post parameters : " + urlParameters);
						System.out.println("Response Code : " + responseCode);

						BufferedReader in = new BufferedReader(
						        new InputStreamReader(conn.getInputStream()));
						String inputLine;
						StringBuffer response = new StringBuffer();

						while ((inputLine = in.readLine()) != null) {
							response.append(inputLine);
						}
						in.close();
						
						//print result
						System.out.println(response.toString());
						
					} catch (Exception e2) {
						// TODO: handle exception
					}
				}
				
			}
		});
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
