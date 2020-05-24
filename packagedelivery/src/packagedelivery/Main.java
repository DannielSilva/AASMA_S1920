package packagedelivery;

import java.awt.EventQueue;
import java.io.*;

/**
 * Multi-agent system creation
 * 
 * @author Rui Henriques
 */
public class Main {

	public static void main(String[] args) throws IOException {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUI frame = new GUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

}
