package viewer;

import javax.swing.*;

public class ApplicationRunner {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(SQLiteViewer::new);
	}
}
