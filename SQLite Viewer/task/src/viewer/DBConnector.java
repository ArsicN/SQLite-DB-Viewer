package viewer;

import org.sqlite.SQLiteDataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DBConnector {
	private final String dbName;
	private SQLiteDataSource dataSource;

	public DBConnector(String dbName) {
		this.dbName = dbName;
	}

	public List<String> getTables() {
		String sql = "SELECT name FROM sqlite_master WHERE type ='table' AND name NOT LIKE 'sqlite_%';";
		createDataSource();
		List<String> tables = new ArrayList<>();

		try (Connection conn = dataSource.getConnection()) {
			if (conn.isValid(5)) {
				try (Statement statement = conn.createStatement()) {
					try (ResultSet resultSet = statement.executeQuery(sql)) {
						while (resultSet.next()) {
							tables.add(resultSet.getString("name"));
						}
					}
				}
			}
		} catch (
				SQLException ex) {
			ex.printStackTrace();
		}
		return tables;
	}

	public Object[][] execQuery(String sql) {
		createDataSource();

		List<List<Object>> dataList = new ArrayList<>();
		List<Object> rowData;
		int columnNum;

		try (Connection conn = dataSource.getConnection()) {
			if (conn.isValid(5)) {
				try (Statement statement = conn.createStatement()) {
					try (ResultSet resultSet = statement.executeQuery(sql)) {
						columnNum = resultSet.getMetaData().getColumnCount();

						while (resultSet.next()) {
							rowData = new ArrayList<>();
							for (int i = 0; i < columnNum; i++) {
								int type = resultSet.getMetaData().getColumnType(i + 1);
								switch (type) {
									case 4: //returns 4 for Integer
										rowData.add(resultSet.getInt(i + 1));
										break;
									case 7: //returns 7 for Real
										rowData.add(resultSet.getFloat(i + 1));
										break;
									case 12: //returns 12 for Text/Varchar
										rowData.add(resultSet.getString(i + 1));
										break;
									case 2004: //returns 2004 for Blob
										rowData.add(resultSet.getBlob(i + 1));
										break;
								}
							}
							dataList.add(rowData);
						}
					}
				}
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
			return new String[][]{{"error"}};
		}
		Object[][] data = new Object[dataList.size()][];

		for (int i = 0; i < dataList.size(); i++) {
			List<Object> row = dataList.get(i);
			data[i] = row.toArray(new Object[0]);
		}
		return data;
	}

	public String[] getColumnNames(String sql) {
		createDataSource();
		String[] columnNames = new String[0];

		try (Connection conn = dataSource.getConnection()) {
			if (conn.isValid(5)) {
				try (Statement statement = conn.createStatement()) {
					try (ResultSet resultSet = statement.executeQuery(sql)) {
						int columnNum = resultSet.getMetaData().getColumnCount();
						columnNames = new String[columnNum];
						for (int i = 1; i <= columnNum; i++) {
							columnNames[i - 1] = resultSet.getMetaData().getColumnLabel(i);
						}
					}
				}
			}
		} catch (
				SQLException ex) {
			ex.printStackTrace();
			return new String[]{"error"};
		}
		return columnNames;
	}

	private void createDataSource() {
		dataSource = new SQLiteDataSource();
		String url = "jdbc:sqlite:";
		dataSource.setUrl(url + dbName);
	}
}
