package packagedelivery;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;

/**
 * Graphical interface
 * 
 * @author Rui Henriques
 */
public class GUI extends JFrame {

	private static final long serialVersionUID = 1L;

	static JTextField speed;
	static JPanel boardPanel;
	static JButton run, reset, step;



	public GUI() {
		setTitle("Package Delivery");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(null);
		setSize(555, 625);
		add(createButtonPanel());

		Board.initialize();
		Board.associateGUI(this);

		boardPanel = new JPanel();
		boardPanel.setSize(new Dimension(100, 20));
		boardPanel.setLocation(new Point(20, 60));


		boardPanel.setLayout(new GridLayout(10, 10));


		update();
		add(boardPanel);
	}


	
	public void update() {
		boardPanel.invalidate();
	}

	private Component createButtonPanel() {
		JPanel panel = new JPanel();
		panel.setSize(new Dimension(600, 50));
		panel.setLocation(new Point(0, 0));

		step = new JButton("Step");
		panel.add(step);
		step.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (run.getText().equals("Run"))
					Board.step();
				else
					Board.stop();
			}
		});
		reset = new JButton("Reset");
		panel.add(reset);
		reset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Board.reset();
			}
		});
		run = new JButton("Run");
		panel.add(run);
		run.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (run.getText().equals("Run")) {
					int time = -1;
					try {
						time = Integer.valueOf(speed.getText());
					} catch (Exception e) {
						JTextPane output = new JTextPane();
						output.setText("Please insert an integer value to set the time per step\nValue inserted = "
								+ speed.getText());
						JOptionPane.showMessageDialog(null, output, "Error", JOptionPane.PLAIN_MESSAGE);
					}
					if (time > 0) {
						Board.run(time);
						run.setText("Stop");
					}
				} else {
					Board.stop();
					run.setText("Run");
				}
			}
		});
		speed = new JTextField(" time per step in [1,100] ");
		speed.setMargin(new Insets(5, 5, 5, 5));
		panel.add(speed);

		return panel;
	}
}
