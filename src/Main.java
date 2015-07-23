
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
    PreparedStatement psCreate = null;
    PreparedStatement psRead = null;
    PreparedStatement psUpdate = null;
    PreparedStatement psDelete = null;
    PreparedStatement psList = null;
		ResultSet rs;
		
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

			// now we should have a valid database and table structure
			
			// setup the prepared statements
			psCreate = conn.prepareStatement("INSERT INTO WISH_LIST(WISH_ITEM, WISH_DESC) VALUES (?, ?)");
			psRead   = conn.prepareStatement("SELECT * FROM WISH_LIST WHERE WISH_ITEM='?'");
			psList   = conn.prepareStatement("SELECT * FROM WISH_LIST");
			String menu = "\n    Enter selection: \n" +
          "         C - create a new record\n" +
          "         R - read a record\n" +
          "         U - update a record\n" +
          "         D - delete a  record\n" +
          "         L - list all records\n" +
          "     Selection: ";
			
			// now run the add/list record loop
			String answer ="";
			while (!answer.equals("q")) {
			  answer = getInput(menu);
			  if (answer.equals("q")) break;
			  
			  switch (answer) {
			  
        case "c":
        case "C":  createRecord(s);
                   break;
        case "r":
        case "R":  readRecord(s);
                   break;
        case "u":
        case "U":  updateRecord(s);
                   break;
        case "d":
        case "D":  deleteRecord(s);
                   break;
        case "l":
        case "L":  listRecords(s);
                   break;
			    case "q":
			    case "Q":  break;
			    default:   System.out.println("Unknown selection");
			  
			  }
			}

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

	
	
  private static void createRecord(Statement s) {
    System.out.println("Creating Record");
  }

  
  
  private static void readRecord(Statement s) {
    System.out.println("Reading Record");
  }

  
  
  private static void updateRecord(Statement s) {
    System.out.println("Updating Record");
  }

  
  
  private static void deleteRecord(Statement s) {
    System.out.println("Deleting Record");
  }

  
  
  private static void listRecords(Statement s) throws SQLException {
    System.out.println("Listing Records");
    ResultSet rs = s.executeQuery("SELECT * FROM WISH_LIST");
    while (rs.next()) {
      Timestamp dateTime = rs.getTimestamp(2);
      String item = rs.getString(3);
      String desc = rs.getString(4);

      System.out.println("On " + dateTime + " I wished for a " + desc + " " + item);      
    }
    rs.close();
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
	
	
	
	private static String getInput(String prompt) throws IOException {
	  BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	  String ans = "";
	  try {
	    while (ans.length() == 0) {
	      System.out.print(prompt);
	      ans = br.readLine().trim();
	    }
	  }
	  catch (IOException ioe) {
	    System.err.println("Could not read from System.in");
	    throw ioe;
	  }
	  return ans;
	}

}
