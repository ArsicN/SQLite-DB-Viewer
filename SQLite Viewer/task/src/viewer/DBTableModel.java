package viewer;

import javax.swing.table.AbstractTableModel;

public class DBTableModel extends AbstractTableModel {

	private String[] headers = {};
	private Object[][] data = {};

	public DBTableModel() {
	}

	public DBTableModel(String[] headers, Object[][] data) {
		this.headers = headers;
		this.data = data;
		fireTableDataChanged();
	}

	@Override
	public int getRowCount() {
		return data.length;
	}

	@Override
	public int getColumnCount() {
		return headers.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return data[rowIndex][columnIndex];
	}

	@Override
	public String getColumnName(int column) {
		return headers[column];
	}

	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		data[rowIndex][columnIndex] = value;
		fireTableCellUpdated(rowIndex, columnIndex);
	}
}
