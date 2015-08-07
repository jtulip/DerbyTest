
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.DriverManager;
//import java.sql.Timestamp;

public class Main {
	
	private static PreparedStatement psCreate = null;
	private static PreparedStatement psRead = null;
	private static PreparedStatement psUpdate = null;
	private static PreparedStatement psDelete = null;
	private static PreparedStatement psList = null;
	private static PreparedStatement psFind = null;
	
	private static String createStr = "INSERT INTO WISH_LIST(WISH_ITEM, WISH_DESC) VALUES (?, ?)";
	private static String readStr   = "SELECT * FROM WISH_LIST WHERE WISH_ID=?";
	private static String updateStr = "UPDATE WISH_LIST SET WISH_DESC=? WHERE WISH_ID=?";
	private static String deleteStr = "DELETE FROM WISH_LIST WHERE WISH_ID=?";
	private static String listStr   = "SELECT * FROM WISH_LIST";
	private static String findStr   = "SELECT * FROM WISH_LIST WHERE WISH_ITEM=?";
	
	private static String createTableString = " CREATE TABLE WISH_LIST" + 
	                                          " (WISH_ID INT NOT NULL GENERATED ALWAYS AS IDENTITY" +
			                                  " CONSTRAINT WISH_PK PRIMARY KEY," + 
	                                          " ENTRY_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
			                                  " WISH_ITEM VARCHAR(32) NOT NULL, " + 
	                                          " WISH_DESC VARCHAR(32))";


	public static void main(String[] args) {
		//String driver = "org.apache.derby.jdbc.EmbeddedDriver";
		String dbName = "derbyDemo";
		String connectionURL = "jdbc:derby:" + dbName + ";";
		String creationURL = "jdbc:derby:" + dbName + ";create=true";

		Connection conn = null;
		Statement s = null;
		try {
			// try connecting to existing DB
			conn = connectToExistingDB(connectionURL);
			if (conn == null) {
				// if that doesn't work, create the database
				System.out.println(dbName + " does not exist, creating ...");
				conn = DriverManager.getConnection(creationURL);
			}
			System.out.println("Connected to database: " + dbName);

			// check for valid table
			if (!chk4Table(conn)) {
				// and set it up if it doesn't exit
				s = conn.createStatement();
				s.execute(createTableString);
				s.close();
			}
			System.out.println("WISH_LIST table OK");

			// now we should have a valid database and table structure

			// setup the prepared statements
			psCreate = conn.prepareStatement(createStr);
			psRead = conn.prepareStatement(readStr);
			psUpdate = conn.prepareStatement(updateStr);
			psDelete = conn.prepareStatement(deleteStr);
			psList = conn.prepareStatement(listStr);
			psFind = conn.prepareStatement(findStr);
			
			String menu = 	"\n    Enter selection: \n" + 
							"         C - create a new record\n" + 
							"         R - read a record\n" + 
							"         U - update a record\n" + 
							"         D - delete a  record\n" + 
							"         L - list all records\n\n" + 
							"         F - find records by item\n\n" + 
							"         Q - quits\n\n" +
							"     Selection: ";

			// now run the add/list record loop
			String answer = "";
			while (!answer.equals("q")) {				
				answer = getInput(menu);

				switch (answer) {

				case "c":
				case "C":
					createRecord();
					break;
				case "r":
				case "R":
					readRecord();
					break;
				case "u":
				case "U":
					updateRecord();
					break;
				case "d":
				case "D":
					deleteRecord();
					break;
				case "l":
				case "L":
					listRecords();
					break;
				case "f":
				case "F":
					findRecordsByItem();
					break;
				case "q":
				case "Q":
					break;
				default:
					System.out.println("Unknown selection");

				}
			}

			// clean up
			System.out.println("Exiting.");
			// psInsert.close();
			conn.close();

			// shut down derby (only for embedded mode)
			try {
				DriverManager.getConnection("jdbc:derby:;shutdown=true");
			} catch (SQLException sqle) {
				if (!sqle.getSQLState().equals("XJ015")) {
					System.err.println("DB did not shutdown normally");
				} else {
					System.out.println("DB shutdown normally");
				}
			}
		} catch (Throwable e) {
			System.err.println(". . . Exception thrown:" + e);
			e.printStackTrace(System.err);
		}
	}


	private static void createRecord() throws IOException, SQLException {
		System.out.println("Creating Record");
		String item = getInput("Enter wish item: ");
		String desc = getInput("Enter wish description: ");
		
		psCreate.setString(1,item);
		psCreate.setString(2,desc);
		psCreate.executeUpdate();		
	}

	
	private static void readRecord() throws IOException, SQLException {
		//psRead = conn.prepareStatement("SELECT * FROM WISH_LIST WHERE WISH_ID='?'");
		System.out.println("Reading Record");
		int wishId = new Integer(getInput("Enter wish item id: ")).intValue();
		psRead.setInt(1, wishId);
		ResultSet rs = psRead.executeQuery();
		
		while (rs.next()) {
			int wishId2 = rs.getInt(1);
			String item = rs.getString(3);
			String desc = rs.getString(4);

			System.out.println("Id: " + wishId2 + " | " + item + " | " + desc);
		}
		rs.close();
	}
	
	
	private static void updateRecord() throws IOException, SQLException {
		System.out.println("Updating Record");
		int wishId = new Integer(getInput("Enter wish item id: ")).intValue();
		String newDesc = getInput("Enter new description: ");
		
		psUpdate.setString(1, newDesc);
		psUpdate.setInt(2, wishId);
		psUpdate.executeUpdate();				
	}

	private static void deleteRecord() throws IOException, SQLException {
		System.out.println("Deleting Record");
		int wishId = new Integer(getInput("Enter wish item id: ")).intValue();
		
		psDelete.setInt(1, wishId);
		psDelete.executeUpdate();				
	}


	private static void listRecords() throws SQLException {
		System.out.println("Listing Records");
		ResultSet rs = psList.executeQuery();
		while (rs.next()) {
			int wishId = rs.getInt(1);
			//Timestamp dateTime = rs.getTimestamp(2);
			String item = rs.getString(3);
			String desc = rs.getString(4);

			System.out.println("Id: " + wishId + " | " + item + " | " + desc);
		}
		rs.close();
	}

	private static void findRecordsByItem() throws IOException, SQLException {
		System.out.println("Finding Records");
		String itemStr = getInput("Enter wish item: ");
		psFind.setString(1, itemStr);
		
		ResultSet rs = psFind.executeQuery();
		while (rs.next()) {
			int wishId = rs.getInt(1);
			//Timestamp dateTime = rs.getTimestamp(2);
			String item = rs.getString(3);
			String desc = rs.getString(4);

			System.out.println("Id: " + wishId + " | " + item + " | " + desc);
		}
		rs.close();
	}


	private static String getInput(String prompt) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String ans = "";
		try {
			while (ans.length() == 0) {
				System.out.print(prompt);
				ans = br.readLine().trim();
			}
		} catch (IOException ioe) {
			System.err.println("Could not read from System.in");
			throw ioe;
		}
		return ans;
	}

	
	private static Connection connectToExistingDB(String connectionURL) throws SQLException {
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(connectionURL);
			System.out.println("DB exists: " + connectionURL);
		} catch (SQLException sqle) {
			String err = sqle.getSQLState();
			System.err.println("connectToExistingDB: caught exception: " + err + "\n" + sqle);
		}
		return conn;
	}

	
	private static boolean chk4Table(Connection conn) throws SQLException {
		try {
			Statement s = conn.createStatement();
			s.execute("UPDATE WISH_LIST " + "SET ENTRY_DATE= CURRENT_TIMESTAMP, " + "WISH_ITEM = 'TEST_ENTRY' "
					+ "WHERE 1=3");
		} catch (SQLException sqle) {
			String err = sqle.getSQLState();
			if (err.equals("42X05")) { // table does not exist
				return false;
			} else if (err.equals("42X14") || err.equals("442821")) { // incorrect
																		// table
																		// definition
				System.err.println("chk4db: Incorrect table definition");
				throw sqle;
			} else {
				System.err.println("chk4db: Unhandled SQLException");
				throw sqle;
			}
		}
		return true;
	}
	

}
