package derbytest;

import java.io.*;
import java.sql.*;

import org.apache.derby.jdbc.ClientConnectionPoolDataSource;

public class EnrolmentMenu {
    
    private static ClientConnectionPoolDataSource ds_;
    private static BufferedReader in_;
    

    
    /**
     * Display and process the available menu options for the Enrolments table
     * 
     */
    public static void execute(ClientConnectionPoolDataSource ds, BufferedReader in) {
        ds_ = ds;
        in_ = in;

        String menu = String.format(                
                "\n\nSelect :\n" +
                        "\tC:  create Enrolment\n" +
                        "\tR:  read Enrolment\n" +
                        "\tD:  delete Enrolment\n" +
                        "\tL:  list all Enrolments\n" +
                        "\tLu: list Enrolments by Subject\n" +
                        "\tLs: list Enrolments by Student\n" +
                        "\n\tX: return to main\n" +
                "\n" +
                "Selection : "
                );

        // menu display loop
        boolean stop = false;
        
        while (!stop) {
            try {
                System.out.print(menu);
                String ans = in.readLine();
                
                switch (ans.toUpperCase()) {
                
	                case "C": create();
	                          break;  
	      
	                case "R": read();
	                          break;  
	      
	                case "D": delete();
	                          break;  
	      
                    case "L": list();
                              break;  
          
                    case "LU": listEnrolmentsBySubject();
                              break;  
          
                    case "LS": listEnrolmentsByStudent();
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
     * Creates a new record in the Enrolments table
     * 
     * @throws SQLException
     */
    private static void create() throws IOException, SQLException {        
        //get subject code
        String sub = Utility.getSubjectCode(in_);
        
        //get student id
        int sid = Utility.getStudentId(in_);
        
        String raw = "INSERT INTO Enrolments VALUES (?, ?)";
        try (Connection con = ds_.getConnection();
                PreparedStatement sta = con.prepareStatement(raw, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);) {
                		
    		  	sta.setString(1, sub);
    		  	sta.setInt(2, sid);
                  		
            	sta.execute();
        }
        catch (SQLIntegrityConstraintViolationException e) {
            System.out.print(String.format("Invalid Enrolment - studentId %d or subjectCode %s does not exist", sid, sub));            
        }
    }
    

    
    /**
     * Read an enrolment
     * 
     * @throws SQLException
     */
    private static void read() throws IOException, SQLException {
        //get subject code
        String sub = Utility.getSubjectCode(in_);

        //get student id
        int sid = Utility.getStudentId(in_);
               
        String raw = "SELECT * FROM Enrolments WHERE (SubjectCode = ?) AND (StudentId = ?)";
        try (Connection con = ds_.getConnection();
                PreparedStatement sta = con.prepareStatement(raw, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);) {
        
		  	sta.setString(1, sub);
		  	sta.setInt(2, sid);
		  	
            ResultSet res = sta.executeQuery(); 
            
			if (res.next()) {
	        	displayResults(res);
			}
			else {
				System.out.println("No such enrolment");
			}
        }
    }
    
        
    
    /**
     * Delete an enrolment
     * 
     * @throws SQLException
     */
    private static void delete() throws IOException, SQLException {
        //get subject code
        String sub = Utility.getSubjectCode(in_);
        
        //get student id
        int sid = Utility.getStudentId(in_);
        
        String raw = "DELETE FROM Enrolments WHERE (SubjectCode = ?) AND (StudentId = ?)";
        try (Connection con = ds_.getConnection();
                PreparedStatement sta = con.prepareStatement(raw, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);) {
            
		  	sta.setString(1, sub);
		  	sta.setInt(2, sid);
		  	
            sta.execute(); 
        }
    }

    
    
    /**
     * Lists all rows in the table
     * 
     * @throws SQLException
     */
    private static void list() throws SQLException {
    	String raw = String.format("SELECT * FROM Enrolments ORDER BY StudentId, SubjectCode");
        try (Connection con = ds_.getConnection();
                PreparedStatement sta = con.prepareStatement(raw, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);) {
        	
             ResultSet res = sta.executeQuery();
        
			if (res.next()) {
				displayResults(res);
			}
        }
    }
    
    
    
    /**
     * Lists enrolments by subject
     * 
     * @throws SQLException
     */
    private static void listEnrolmentsBySubject() throws IOException, SQLException {
        //get subject code
        String sub = Utility.getSubjectCode(in_);
        
        String raw = "SELECT * FROM Enrolments WHERE SubjectCode = ?";
        try (Connection con = ds_.getConnection();
                PreparedStatement sta = con.prepareStatement(raw, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);) {
        	
		  	sta.setString(1, sub);

		  	ResultSet res = sta.executeQuery();
        
        	if (res.next()) {
				displayResults(res);
            }
        }
    }
    

    
    /**
     * Lists enrolments by student
     * 
     * @throws SQLException
     */
    private static void listEnrolmentsByStudent() throws IOException, SQLException {
        //get student id
        int sid = Utility.getStudentId(in_);
        
        String raw = "SELECT * FROM Enrolments WHERE StudentId = ?";
        try (Connection con = ds_.getConnection();
                PreparedStatement sta = con.prepareStatement(raw, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);) {
        	
		  	sta.setInt(1, sid);

		  	ResultSet res = sta.executeQuery();
        
		  	if (res.next()) {
				displayResults(res);
			}
        }
    }
    
    
    
    /**
     * Internal utility method to display a query's results
     * 
     * @throws SQLException
     */   
    private static void displayResults(ResultSet res) throws SQLException {
    	res.first();
    	System.out.println(String.format("\n\t%s\t%s", "SubjectCode","StudentId"));
		do {
			System.out.println(String.format("\t%s\t\t%d", res.getString("SubjectCode"), res.getInt("StudentId"))); 
		} while (res.next());
	}

}
