
import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.DriverManager;

public class Main {

	public static void main(String[] args) {
		String driver = "org.apache.derby.jdbc.EmbeddedDriver";
		String dbName="derbyDemo";
		String connectionURL = "jdbc:derby:" + dbName + ";";
		String creationURL = "jdbc:derby:" + dbName + ";create=true";
		
		Connection conn = null;
		Statement s = null;
		PreparedStatement psInsert = null;
		ResultSet myWishes;
		
		String createTableString = "CREATE TABLE WISH_LIST " 
		                    + "(WISH_ID INT NOT NULL GENERATED ALWAYS AS IDENTITY"
		                    + " CONSTRAINT WISH_PK PRIMARY KEY, "
		                    + " ENTRY_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
		                    + " WISH_ITEM VARCHAR(32) NOT NULL)";
		String answer;

		try {
		  //try connecting to existing DB 
			conn = connectToExistingDB(connectionURL);
			if (conn == null) {
			  //if that doesn't work, creat the database
				System.out.println(dbName + " does not exist, creating ...");
				conn = DriverManager.getConnection(creationURL);
				
				//and set up the table
				s = conn.createStatement();
				s.execute(createTableString);				
			}
			
			//now we should have a database and a table structure
			System.out.println("Connected to database: " + dbName);
			chk4Table(conn);
			System.out.println("WISH_LIST table OK");
		}
		catch (Throwable e) {
			System.err.println(". . . Exception thrown:");
			e.printStackTrace(System.err);
		}
	}
	
	public static Connection connectToExistingDB(String connectionURL) throws SQLException {
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(connectionURL);
			System.out.println("DB exists: " + connectionURL);
		}
		catch (SQLException sqle) {
			String err = sqle.getSQLState();
			System.err.println("connectToExistingDB: caught exception: " + err + "\n" + sqle);
		}
		return conn;
	}
	public static boolean chk4Table(Connection conn) throws SQLException {
		try {
			Statement s = conn.createStatement();
			s.execute("UPDATE WISH_LIST " +
					  "SET ENTRY_DATE= CURRENT_TIMESTAMP, " +
					  "WISH_ITEM = 'TEST_ENTRY' " +
					  "WHERE 1=3");
		}
		catch (SQLException sqle) {
			String err = sqle.getSQLState();
			if (err.equals("42X05")) { // table does not exist
				return false;
			}
			else if (err.equals("42X14") || err.equals("442821")) { // incorrect table definition
				System.err.println("chk4db: Incorrect table definition");
				throw sqle;
			}
			else {
				System.err.println("chk4db: Unhandled SQLException");
				throw sqle;
			}
		}
		return true;
	}

}
