package derbytest;

import java.io.*;
import java.sql.*;

import org.apache.derby.jdbc.ClientConnectionPoolDataSource;

public class AssessmentMenu {
    
    private static ClientConnectionPoolDataSource ds_;
    private static BufferedReader in_;
    

    
    /**
     * Display and process the available menu options for the Assessments table
     * 
     */
    public static void execute(ClientConnectionPoolDataSource ds, BufferedReader in) {
        ds_ = ds;
        in_ = in;

        String menu = String.format(                
                "\n\nSelect :\n" +
                        "\tC:  create Assessment\n" +
                        "\tR:  read   Assessment\n" +
                        "\tU:  update Assessment\n" +
                        "\tD:  delete Assessment\n" +
                        "\tL:  list all Assessments\n" +
                        "\tLu: list Assessments by Subject\n" +
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
	      
	                case "U": update();
                              break;  

	                case "D": delete();
                              break;  

                    case "L": list();
                              break;  
          
                    case "LU": listEnrolmentsBySubject();
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
     * Creates a new record in the Assessments table
     * 
     * @throws SQLException
     */
    private static void create() throws IOException, SQLException {        
        //get subject code
        String sub = getSubjectCode();
        
        //get assessment code
        String ass = getAssessmentCode();
        
        //get assessment weight
        int wgt = getAssessmentWeight();
        
        try (Connection con = ds_.getConnection();
             Statement sta = con.createStatement(); ) {
        
            sta.execute(String.format("INSERT INTO Assessments VALUES ('%s', '%s', %d)", sub, ass, wgt));
        }
        catch (SQLIntegrityConstraintViolationException e) {
            System.out.print(String.format("Invalid Assessment - subjectCode %s does not exist", sub));            
        }
    }
    

    
    /**
     * Update a record in the Assessments table
     * 
     * @throws SQLException
     */
    private static void update() throws IOException, SQLException {        
        //get subject code
        String sub = getSubjectCode();
        
        //get assessment code
        String ass = getAssessmentCode();
        
        //get assessment weight
        int wgt = getAssessmentWeight();
        
        String raw = String.format("UPDATE Assessments SET AssessmentWeight = %d WHERE (SubjectCode = '%s') AND (AssessmentCode = '%s')", wgt, sub, ass);
        try (Connection con = ds_.getConnection();
             Statement sta = con.createStatement(); ) {
        
            sta.execute(raw);
        }
        catch (SQLIntegrityConstraintViolationException e) {
            System.out.print(String.format("Invalid Assessment - subjectCode %s does not exist", sub));            
        }
    }

    
    
    /**
     * Read an assessment
     * 
     * @throws SQLException
     */
    private static void read() throws IOException, SQLException {
        //get subject code
        String sub = getSubjectCode();

        //get assessment code
        String ass = getAssessmentCode();
        
        String raw = String.format("SELECT * FROM Assessments WHERE (SubjectCode = '%s') AND (AssessmentCode = '%s')", sub, ass);
        try (Connection con = ds_.getConnection();
             Statement sta = con.createStatement();
             ResultSet res = sta.executeQuery(raw); ) {
        
			if (res.next()) {
	        	displayAssessmentHeader();
				displayAssessment(res.getString("SubjectCode"), res.getString("AssessmentCode"), res.getInt("AssessmentWeight"));
			}
			else {
				System.out.println("No such enrolment");
			}
        }
    }
    
        
    
    /**
     * Delete an assessment
     * 
     * @throws SQLException
     */
    private static void delete() throws IOException, SQLException {
        //get subject code
        String sub = getSubjectCode();
        
        //get assessment code
        String ass = getAssessmentCode();
        
        String raw = String.format("DELETE FROM Assessments WHERE (SubjectCode = '%s') AND (AssessmentCode = '%s')", sub, ass);
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
        try (Connection con = ds_.getConnection();
             Statement sta = con.createStatement();
             ResultSet res = sta.executeQuery( "SELECT * FROM Assessments"); ) {
        
        	displayAssessmentHeader();
			while (res.next()) {
				displayAssessment(res.getString("SubjectCode"), res.getString("AssessmentCode"), res.getInt("AssessmentWeight"));
			}
        }
    }
    
    
    
    /**
     * Lists assessments by subject
     * 
     * @throws SQLException
     */
    private static void listEnrolmentsBySubject() throws IOException, SQLException {
        //get subject code
        String sub = getSubjectCode();
        
        try (Connection con = ds_.getConnection();
             Statement sta = con.createStatement();
             ResultSet res = sta.executeQuery( String.format("SELECT * FROM Assessments WHERE SubjectCode = '%s'", sub)); ) {
        

        	displayAssessmentHeader();
        	while (res.next()) {
				displayAssessment(res.getString("SubjectCode"), res.getString("AssessmentCode"), res.getInt("AssessmentWeight"));
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
	    System.out.print("\nEnter Subject Code: ");
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
	    return sub.toUpperCase();
    }
    
    
    
    /**
     * Internal utility method to return an assessment code
     * 
     * @throws IOException
     * @return the assessment code
     */   
    private static String getAssessmentCode() throws IOException {
	    System.out.print("\nEnter Assessment Code: ");
	    String ass = "";
	    while (true) {
	        ass = in_.readLine().trim();
	        if ( ass.length() != 4 || !ass.matches("[a-zA-Z][a-zA-Z][a-zA-Z][a-zA-Z0-9]")) {
	            System.out.println("Invalid input - assessment code must have length 4, first 3 characters alpha, last character alphanumeric");
	        }
	        else {
	            break;
	        }
	    }
	    return ass;
    }

    
    
    /**
     * Internal utility method to return an assessment weight
     * 
     * @throws IOException
     * @return the assessment weight
     */   
    private static int getAssessmentWeight() throws IOException {
	    //get assessment weight
	    System.out.print("\nEnter Assessment Weight: ");
	    int wgt = 0;
	    while ( wgt <= 0 ) {
	        try {
	            wgt = Integer.parseInt(in_.readLine());
	            if (wgt <= 0 ) {
	                System.out.println("Invalid input - weight must be integer > 0");
	            }
	        }
	        catch (NumberFormatException nfe) {
	            System.out.println("Invalid input - weight must be integer > 0");
	        }
	    }
	    return wgt;
    }

    
    
    /**
     * Internal utility method to display assessment header line
     * 
     */   
    private static void displayAssessmentHeader() {
    	System.out.println(String.format("\n\t%s\t%s\t%s", "SubjectCode", "AssessmentCode", "AssessmentWeight"));
    }


    
    /**
     * Internal utility method to display an assessment's data fields
     * 
     */   
    private static void displayAssessment(String subjectCode, String assessmentCode, int assessmentWeight) {
		System.out.println(String.format("\t%s\t\t%s\t\t%d", subjectCode, assessmentCode, assessmentWeight));
     }

}
