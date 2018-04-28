package com.pobreng.pedro.window;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.TextArea;

public class Dialog {

	public JFrame dialogMessage;
	
	/**
	 * Create the application.
	 */
	public Dialog(String message) {
		initialize(message);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize(String message) {
		dialogMessage = new JFrame();
		dialogMessage.setTitle("Message");
		dialogMessage.setBounds(100, 100, 517, 241);
		dialogMessage.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		JPanel panel = new JPanel();
		dialogMessage.getContentPane().add(panel, BorderLayout.CENTER);
		
		TextArea txtArea = new TextArea();
		txtArea.setText(message);
		panel.add(txtArea);
	}

}
