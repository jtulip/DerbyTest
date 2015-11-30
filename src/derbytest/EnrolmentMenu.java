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
        
        String raw = String.format("INSERT INTO Enrolments VALUES ('%s', %d)", sub, sid);
        try (Connection con = ds_.getConnection();
             Statement sta = con.createStatement(); ) {
        
            sta.execute(raw);
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
               
        String raw = String.format("SELECT * FROM Enrolments WHERE (SubjectCode = '%s') AND (StudentId = %d)", sub, sid);
        try (Connection con = ds_.getConnection();
             Statement sta = con.createStatement();
             ResultSet res = sta.executeQuery(raw); ) {
        
			if (res.next()) {
	        	displayEnrolmentHeader();
				displayEnrolment(res.getString("SubjectCode"), res.getInt("StudentId"));
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
        
        String raw = String.format("DELETE FROM Enrolments WHERE (SubjectCode = '%s') AND (StudentId = %d)", sub, sid);
        try (Connection con = ds_.getConnection();
             Statement sta = con.createStatement(); ) {
        
        	sta.execute(raw);
        }
    }

    
    
    /**
     * Lists all rows in the table
     * 
     * @throws SQLException
     */
    private static void list() throws SQLException {
    	String raw = String.format("SELECT * FROM Enrolments");
        try (Connection con = ds_.getConnection();
             Statement sta = con.createStatement();
             ResultSet res = sta.executeQuery(raw); ) {
        
        	displayEnrolmentHeader();
			while (res.next()) {
				displayEnrolment(res.getString("SubjectCode"), res.getInt("StudentId"));
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
        
        String raw = String.format("SELECT * FROM Enrolments WHERE SubjectCode = '%s'", sub);
        try (Connection con = ds_.getConnection();
             Statement sta = con.createStatement();
             ResultSet res = sta.executeQuery(raw); ) {
        

        	displayEnrolmentHeader();
        	while (res.next()) {
        		displayEnrolment(res.getString("SubjectCode"), res.getInt("StudentId"));
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
        
        String raw = String.format("SELECT * FROM Enrolments WHERE StudentId = %d", sid);
        try (Connection con = ds_.getConnection();
             Statement sta = con.createStatement();
             ResultSet res = sta.executeQuery(raw); ) {
        
        	displayEnrolmentHeader();
			while (res.next()) {
				displayEnrolment(res.getString("SubjectCode"), res.getInt("StudentId"));
			}
        }
    }
    
    
    
    /**
     * Internal utility method to display enrolment header line
     * 
     */   
    private static void displayEnrolmentHeader() {
    	System.out.println(String.format("\n\t%s\t%s", "SubjectCode","StudentId"));
    }


    
    /**
     * Internal utility method to display an enrolment's data fields
     * 
     */   
    private static void displayEnrolment(String subjectCode, int studentId) {
		System.out.println(String.format("\t%s\t\t%d", subjectCode, studentId));
     }

}
