package derbytest;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.*;

import org.apache.derby.jdbc.ClientConnectionPoolDataSource;

public class SubjectMenu {
    
    private static ClientConnectionPoolDataSource ds_;
    private static BufferedReader in_;

    
    
    /**
     * Display and process the available menu options for the Subject table
     * 
     */
    public static void execute(ClientConnectionPoolDataSource ds, BufferedReader in) {
        ds_ = ds;
        in_ = in;
        
        String menu = String.format(                
                "\n\nSelect :\n" +
                        "\tC: create Subject\n" +
                        "\tR: read Subject\n" +
                        "\tU: update Subject\n" +
                        "\tD: delete Subject\n" +
                        "\tL: list all Subjects\n" +
                        "\n\tX: return to main\n" +
                "\n" +
                "Selection : "
                );
        
        // menu display loop
        boolean stop = false;
        
        while (!stop) {
            try {
                System.out.print(menu);
                String ans = in_.readLine();
                
                switch (ans.toUpperCase()) {
                
                	case "C": create();
                	          break;  
      
	                case "R": read();
                              break;
                    
	                case "U": update();
                              break;  
                              
	                case "D": delete();
	                		  break;
	      
                    case "L": list();
                              break;  
                    
                    case "X": stop = true;
                              break;
                              
                    default: System.out.println(String.format("%s not recognised", ans));
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("\nReturning to Main.");
    }

    
    
    /**
     * Create a new subject
     * 
     * @throws SQLException
     */
    private static void create() throws IOException, SQLException {        
        //get subject code
        String sub = getSubjectCode();
        
        //get subject name
        String name = getSubjectName();
        
        String raw = String.format("INSERT INTO Subjects VALUES ('%s', '%s')", sub, name);
        try (Connection con = ds_.getConnection();
             Statement sta = con.createStatement(); ) {
        
            sta.execute(raw);
        }
        catch (SQLIntegrityConstraintViolationException e) {
            System.out.print(String.format("Invalid SubjectCode - %s already exists", sub));            
        }
    }
    
    

    /**
     * Read a subject from a row in the table
     * 
     * @throws SQLException
     */
    private static void read() throws IOException, SQLException {
        //get subject code
        String sub  = getSubjectCode();
        
        String raw = String.format("SELECT * FROM Subjects WHERE SubjectCode = '%s'", sub);
        try (Connection con = ds_.getConnection();
             Statement sta = con.createStatement();
             ResultSet res = sta.executeQuery(raw); ) {
        
			if (res.next()) {
	        	displaySubjectHeader();
				displaySubject(res.getString("SubjectCode"), res.getString("SubjectName"));
			}
        }
    }
    
    
    
    /**
     * Update a subject. Only subject name may be changed.
     * 
     * @throws SQLException
     */
    private static void update() throws IOException, SQLException {
        //get subject code
        String sub  = getSubjectCode();
                
        String raw = String.format("SELECT * FROM Subjects WHERE SubjectCode = '%s'", sub);
        try (Connection con = ds_.getConnection();
             Statement sta = con.createStatement();
             ResultSet res = sta.executeQuery(raw); ) {
        
			if (res.next()) {
				System.out.println("\nOriginal record: ");
	        	displaySubjectHeader();
				displaySubject(res.getString("SubjectCode"), res.getString("SubjectName"));
			}
        	else {
        		System.out.println(String.format("No such subject: %s", sub));
        		return;
        	}
        }
        
        //get new first name
        String name = getSubjectName();
        
        //update record
        raw = String.format("UPDATE Subjects SET SubjectName='%s' WHERE SubjectCode='%s'", name, sub);
        try (Connection con = ds_.getConnection();
             Statement sta = con.createStatement(); ) {
        
            sta.execute(raw);
        }

        raw = String.format("SELECT * FROM Subjects WHERE SubjectCode = '%s'", sub);
        try (Connection con = ds_.getConnection();
             Statement sta = con.createStatement();
             ResultSet res = sta.executeQuery(raw); ) {
        
			if (res.next()) {
				System.out.println("\nOriginal record: ");
	        	displaySubjectHeader();
				displaySubject(res.getString("SubjectCode"), res.getString("SubjectName"));
			}
        }
    }
    

    
    /**
     * Delete a subject. 
     * 
     * @throws SQLException
     */
    private static void delete() throws IOException, SQLException {
        //get subject code
        String sub  = getSubjectCode();
                
        String raw = String.format("DELETE FROM Subjects WHERE SubjectCode = %s", sub);
        try (Connection con = ds_.getConnection();
             Statement sta = con.createStatement(); ) {
        	
        	sta.execute(raw);
        }        
    }

    
    
    /**
     * List all subjects
     * 
     * @throws SQLException
     */
    private static void list() throws SQLException {
        String raw = String.format("SELECT * FROM Subjects");
        try (Connection con = ds_.getConnection();
             Statement sta = con.createStatement();
             ResultSet res = sta.executeQuery(raw); ) {
        
        	displaySubjectHeader();
			while (res.next()) {
				displaySubject(res.getString("SubjectCode"), res.getString("SubjectName"));
			}
        }
    }
    

    
    /**
     * Internal utility method to return a subject code
     * 
     * @throws IOException
     * @return the subject code
     */   
    private static String getSubjectCode() throws IOException {
	    System.out.print("\nEnter subject code: ");
	    String sub = "";
	    while (true) {
	        sub = in_.readLine().trim();
	        if ( sub.length() != 6 || !sub.matches("[a-zA-Z][a-zA-Z][a-zA-Z][1-9][0-9][0-9]")) {
	            System.out.println("Invalid input - subject code must have length 6, first 3 characters alpha, last 3 characters numeric");
	        }
	        else {
	            break;
	        }
	    }
	    return sub;
    }

    
    
    /**
     * Internal utility method to return a subject name
     * 
     * @throws IOException
     * @return the subject name
     */   
    private static String getSubjectName() throws IOException {
        System.out.print("\nEnter subject name: ");
        String name = "";
        while (true) {
            name = in_.readLine().trim();
            if (name.length() == 0 || name.length() > 64 ) {
                System.out.println("Invalid input - subject name cannot be empty, or over length 64");
            }
            else {
                break;
            }
        }
    	return name;
    }
    

    
    /**
     * Internal utility method to display subject header line
     * 
     */   
    private static void displaySubjectHeader() {
		System.out.println(String.format("\n\t%s\t%s", "SubjectCode", "SubjectName"));
    }
    

    
    /**
     * Internal utility method to display subject header line
     * 
     */   
    private static void displaySubject(String subjectCode, String subjectName) {
		System.out.println(String.format("\t%s\t\t%s", subjectCode, subjectName));
    }

}
