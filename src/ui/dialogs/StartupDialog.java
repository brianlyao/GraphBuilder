package ui.dialogs;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * The dialog which appears when the program is first started up.
 * 
 * @author Brian
 */
public class StartupDialog extends JDialog {

	private static final long serialVersionUID = -3769963793891133521L;

	private static final int GAP_HEIGHT = 5;
	
	public static final int NEW_FILE_CHOICE = 1;
	public static final int OPEN_FILE_CHOICE = 2;
	public static final int EXIT_CHOICE = 3;
	
	private JLabel selectLabel;
	private JButton newFileButton;
	private JButton openFileButton;
	private JButton exitButton;
	
	private int choice;
	
	private StartupDialog() {
		super((JFrame) null, "GraphBuilder Start-Up");
		
		setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		setResizable(false);
		setVisible(false);
		
		selectLabel = new JLabel("Select an option:");
		newFileButton = new JButton("New File");
		newFileButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				choice = NEW_FILE_CHOICE;
				dispose();
			}
			
		});
		
		openFileButton = new JButton("Open Existing File");
		openFileButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				choice = OPEN_FILE_CHOICE;
				dispose();
			}
			
		});
		
		exitButton = new JButton("Exit GraphBuilder");
		exitButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				choice = EXIT_CHOICE;
				dispose();
			}
			
		});
		
		selectLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		newFileButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		openFileButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		exitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		add(Box.createVerticalStrut(GAP_HEIGHT));
		add(selectLabel);
		add(Box.createVerticalStrut(GAP_HEIGHT));
		add(newFileButton);
		add(Box.createVerticalStrut(GAP_HEIGHT));
		add(openFileButton);
		add(Box.createVerticalStrut(GAP_HEIGHT));
		add(exitButton);
		add(Box.createVerticalStrut(GAP_HEIGHT));
		
		pack();
		setLocationRelativeTo(null);
	}
	
	/**
	 * Display a dialog with choices for the user's first action, and return
	 * the user's choice.
	 * 
	 * @return The choice the user made.
	 */
	public static int getStartupOption() {
		StartupDialog dialog = new StartupDialog();
		dialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		dialog.setVisible(true);
		int choice = dialog.choice;
		dialog.setVisible(false);
		dialog.dispose();
		return choice;
	}
	
}
