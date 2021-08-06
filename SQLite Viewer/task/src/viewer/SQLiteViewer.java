package viewer;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.util.List;

public class SQLiteViewer extends JFrame {
	private final JButton openButton = new JButton("Open");
	private final JButton executeButton = new JButton("Execute");
	private final JTextArea queryField = new JTextArea();
	private final JTextField dbNameField = new JTextField();
	private final JComboBox<String> tableChoiceBox = new JComboBox<>();
	private final JSplitPane splitBottomArea = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
	private TableModel model;
	private JTable table;
	private String dbName;

	public SQLiteViewer() {
		super("SQLite Viewer");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(900, 700);
		setLayout(new BorderLayout());
		addComponents();
		setResizable(false);
		setLocationRelativeTo(null);
		setVisible(true);
	}

	private void addComponents() {
		addDatabaseOpener();

		//Container for positioning of the rest of the components
		JPanel centerArea = new JPanel();
		centerArea.setLayout(new BorderLayout());
		centerArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		addTableChoice(centerArea);
		addUISplit(centerArea);
	}

	private void addDatabaseOpener() {
		//Add panel and components for opening a databse
		JPanel openDBArea = new JPanel();
		openDBArea.setLayout(new BorderLayout());
		openDBArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		add(openDBArea, BorderLayout.NORTH);

		dbNameField.setName("FileNameTextField");
		openDBArea.add(dbNameField, BorderLayout.CENTER);

		openButton.setName("OpenFileButton");
		openButton.addActionListener(e -> {
			dbName = dbNameField.getText();
			File file = new File(".\\" + dbName);
			if (!file.exists() || "".equals(dbName)) {
				JOptionPane.showMessageDialog(new Frame(), "File doesn't exist!");
				disableInteraction();
				return;
			}

			List<String> tables = new DBConnector(dbName).getTables();
			tableChoiceBox.removeAllItems();
			for (String table : tables) {
				tableChoiceBox.addItem(table);
			}
			enableInteraction();
		});
		openDBArea.add(openButton, BorderLayout.EAST);
	}

	private void addTableChoice(JPanel centerArea) {
		//Adding a ComboBox
		tableChoiceBox.setName("TablesComboBox");
		tableChoiceBox.addActionListener(e ->
				queryField.setText("SELECT * FROM " + tableChoiceBox.getSelectedItem() + ';'));
		centerArea.add(tableChoiceBox, BorderLayout.NORTH);
	}

	private void addUISplit(JPanel centerArea) {
		centerArea.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				splitBottomArea.setDividerLocation(0.25);
				splitBottomArea.setResizeWeight(0.25);
			}
		});
		add(centerArea, BorderLayout.CENTER);

		//Creation of a JSplitPane to divide the rest of the UI in 25% query and 75% table sections
		splitBottomArea.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
		splitBottomArea.setEnabled(false);
		splitBottomArea.setDividerSize(0);
		centerArea.add(splitBottomArea, BorderLayout.CENTER);

		addSQLExecutionArea();
		addResultTable();
	}

	private void addSQLExecutionArea() {
		//Query text and execute button positioning
		JPanel queryExecuteArea = new JPanel();
		queryExecuteArea.setLayout(new BorderLayout());
		queryExecuteArea.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
		splitBottomArea.add(queryExecuteArea);

		queryField.setName("QueryTextArea");
		queryExecuteArea.add(queryField, BorderLayout.CENTER);
		queryField.setEnabled(false);

		//Execute query button positioning
		JPanel executeBtnAlign = new JPanel();
		executeBtnAlign.setLayout(new BoxLayout(executeBtnAlign, BoxLayout.Y_AXIS));
		queryExecuteArea.add(executeBtnAlign, BorderLayout.EAST);

		executeButton.setName("ExecuteQueryButton");
		executeButton.addActionListener(e -> {
			if (!"".equals(queryField.getText())) {
				DBConnector db = new DBConnector(dbName);
				String[] columnNames = db.getColumnNames(queryField.getText());
				if (null == columnNames || "error".equals(columnNames[0])) {
					JOptionPane.showMessageDialog(new Frame(), "Invalid SQL");
					return;
				}
				Object[][] tableData = db.execQuery(queryField.getText());
				if (null == tableData || "error".equals(tableData[0][0])) {
					JOptionPane.showMessageDialog(new Frame(), "Invalid SQL");
					return;
				}

				model = new DBTableModel(columnNames, tableData);
				table.setModel(model);

				for (int i = 1; i < tableData.length; i++) {
					for (int j = 0; j < tableData[0].length; j++) {
						model.setValueAt(tableData[i][j], i, j);
					}
				}
			}
		});
		executeButton.setEnabled(false);
		executeBtnAlign.add(executeButton);
	}

	private void addResultTable() {
		//JTable area positioning
		JPanel tableArea = new JPanel();
		tableArea.setLayout(new BorderLayout());
		tableArea.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
		splitBottomArea.add(tableArea);

		model = new DBTableModel();
		table = new JTable(model);
		table.setName("Table");
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		table.setAutoCreateRowSorter(true);
		tableArea.add(scrollPane);
	}

	private void enableInteraction() {
		queryField.setEnabled(true);
		executeButton.setEnabled(true);
	}

	private void disableInteraction() {
		queryField.setEnabled(false);
		executeButton.setEnabled(false);
	}
}
