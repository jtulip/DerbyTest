
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.Timestamp;

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
		
		String createTableString = "CREATE TABLE WISH_LIST" 
		                    + " (WISH_ID INT NOT NULL GENERATED ALWAYS AS IDENTITY"
		                    + " CONSTRAINT WISH_PK PRIMARY KEY,"
		                    + " ENTRY_DATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
		                    + " WISH_ITEM VARCHAR(32) NOT NULL, "
		                    + " WISH_DESC VARCHAR(32))";

		try {
		  // try connecting to existing DB 
			conn = connectToExistingDB(connectionURL);
			if (conn == null) {
			  // if that doesn't work, create the database
				System.out.println(dbName + " does not exist, creating ...");
				conn = DriverManager.getConnection(creationURL);				
			}			
      System.out.println("Connected to database: " + dbName);
      s = conn.createStatement();
      
      // check for valid table
      if (!chk4Table(conn)) {
      // and set it up if it doesn't exit
        s.execute(createTableString);               
      }
      System.out.println("WISH_LIST table OK");
/**
			// now we should have a valid database and table structure
			
			// setup the prepared statement
			psInsert = conn.prepareStatement("INSERT INTO WISH_LIST(WISH_ITEM, WISH_DESC) VALUES (?, ?)");
			
			// now run the add/list record loop
			String answer ="";
			while (!answer.equals("q")) {
			  answer = getWishItem();
			  if (answer.equals("q")) break;
			  
			  // update the prepared statement with the wish item
			  psInsert.setString(1, answer);
			  // insert wish item into table
			  psInsert.executeUpdate();
			  
			  // Now list all the records
			  myWishes = s.executeQuery("SELECT ENTRY_DATE, WISH_ITEM " +
			                            "FROM WISH_LIST " +
			                            "ORDER BY ENTRY_DATE");
			  System.out.println("--------------------------------------");
			  while (myWishes.next()) {
			    Timestamp dateTime = myWishes.getTimestamp(1);
			    String wish = myWishes.getString(2);
			    
			    System.out.println("On " + dateTime + " I wished for a " + wish);
			  }
        System.out.println("--------------------------------------");
			  myWishes.close();
			}
**/
			// clean up
      System.out.println("Exiting.");
			//psInsert.close();
			s.close();
			conn.close();
			
			//shut down derby (only for embedded mode)
			try {
			  DriverManager.getConnection("jdbc:derby:;shutdown=true");
			}
			catch (SQLException sqle) {
			  if (!sqle.getSQLState().equals("XJ015")) {
			    System.err.println("DB did not shutdown normally");
			  }
			  else {
          System.out.println("DB shutdown normally");
			  }
			}
		}
		catch (Throwable e) {
			System.err.println(". . . Exception thrown:" + e);
			e.printStackTrace(System.err);
		}
	}
	
	
	
	private static Connection connectToExistingDB(String connectionURL) throws SQLException {
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
	
	
	
	private static boolean chk4Table(Connection conn) throws SQLException {
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
	
	
	
	private static String getWishItem() throws IOException {
	  BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	  String ans = "";
	  try {
	    while (ans.length() == 0) {
	      System.out.println("Enter wish (q to exit): ");
	      ans = br.readLine();
	    }
	  }
	  catch (IOException ioe) {
	    System.err.println("Could not read from System.in");
	    throw ioe;
	  }
	  return ans;
	}

}
