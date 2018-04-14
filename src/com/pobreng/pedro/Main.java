package com.pobreng.pedro;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;

import org.json.JSONArray;
import org.json.JSONObject;




public class Main {

	private JFrame frmPobrengPedro;

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
		frmPobrengPedro.setBounds(100, 100, 450, 300);
		frmPobrengPedro.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmPobrengPedro.getContentPane().setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		frmPobrengPedro.getContentPane().add(panel, BorderLayout.WEST);
		
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

						List<String> list = new ArrayList<String>();
						JSONArray array = obj.getJSONArray("data");
						for(int i = 0 ; i < array.length() ; i++){
						    System.out.println(array.getJSONObject(i).getString("name"));
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
		panel.add(btnGetDevice, "2, 2, left, top");
		
		JList list = new JList();
		list.setModel(new AbstractListModel() {
			String[] values = new String[] {"name"};
			public int getSize() {
				return values.length;
			}
			public Object getElementAt(int index) {
				return values[index];
			}
		});
		
		panel.add(list, "2, 4, left, center");
		
		JPanel panel_1 = new JPanel();
		frmPobrengPedro.getContentPane().add(panel_1, BorderLayout.CENTER);
		
		JLabel lblPobrengPedro = new JLabel("POBRENG PEDRO");
		panel_1.add(lblPobrengPedro);
	}

}
