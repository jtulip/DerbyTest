package derbytest;

import java.io.*;
import java.sql.*;

import org.apache.derby.jdbc.ClientConnectionPoolDataSource;

public class MarkMenu {
    
    private static ClientConnectionPoolDataSource ds_;
    private static BufferedReader in_;
    

    
    /**
     * Display and process the available menu options for the Marks table
     * 
     */
    public static void execute(ClientConnectionPoolDataSource ds, BufferedReader in) {
        ds_ = ds;
        in_ = in;

        String menu = String.format(                
                "\n\nSelect :\n" +
                        "\tC:  create Mark\n" +
                        "\tR:  read Mark\n" +
                        "\tU:  update Mark\n" +
                        "\tD:  delete Mark\n" +
                        "\tL:  list all Marks\n" +
                        "\tLu: list Marks by Subject and Student\n" +
                        "\tLs: list Marks by Student and Subject\n" +
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
          
                    case "LU": listMarksBySubject();
                              break;  
          
                    case "LS": listMarksByStudent();
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
     * Creates a new record in the Marks table
     * 
     * @throws SQLException
     */
    private static void create() throws IOException, SQLException {        
        //get student id
        int sid = getStudentId();
        
        //get subject code
        String sub = getSubjectCode();
        
        //get assessment code
        String ass = getAssessmentCode();
        
        //get actual mark
        int mark = getMark();
        
        String raw = "INSERT INTO Marks VALUES (?, ?, ?, ?)";
        //String raw = String.format("INSERT INTO Marks VALUES (%d, '%s', '%s', %d)", sid, sub, ass, mark);
        try (Connection con = ds_.getConnection();
             PreparedStatement sta = con.prepareStatement(raw, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY); ) {
        	
        	sta.setInt(1, sid);
        	sta.setString(2, sub);
        	sta.setString(3, ass);
        	sta.setInt(4, mark);
        	
            sta.execute();
        }
        catch (SQLIntegrityConstraintViolationException e) {
            System.out.print(String.format("Invalid Mark - studentId %d, subjectCode %s, or assessment code %s does not exist", sid, sub, ass));            
        }
    }
    

    
    /**
     * Read a mark
     * 
     * @throws SQLException
     */
    private static void read() throws IOException, SQLException {
        //get student id
        int sid = getStudentId();
        
        //get subject code
        String sub = getSubjectCode();
        
        //get assessment code
        String ass = getAssessmentCode();
        
        String raw = "SELECT * FROM Marks WHERE StudentId = ? AND SubjectCode = ? AND AssessmentCode = ?";
        try (Connection con = ds_.getConnection();
             PreparedStatement sta = con.prepareStatement(raw, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);) {
        		
        	sta.setInt(1, sid);
        	sta.setString(2, sub);
        	sta.setString(3, ass);
        		
            ResultSet res = sta.executeQuery(); 
        
			if (res.next()) {
	        	displayResults(res);
			}
        	else {
        		System.out.println(String.format("No such mark: %d %s %s", sid, sub, ass));
        		return;
        	}
        }
    }
    
        
    
    /**
     * Update a mark. 
	 * <p>	
	 * Only actual mark value may be changed.
     * 
     * @throws IOException, SQLException
      */
    private static void update() throws IOException, SQLException {
        //get student id
        int sid = getStudentId();
                
        //get subject code
        String sub = getSubjectCode();
        
        //get assessment code
        String ass = getAssessmentCode();
        
        //get actual mark
        int mark = getMark();
        
        //display original record
        String raw = "SELECT * FROM Marks WHERE StudentId = ? AND SubjectCode = ? AND AssessmentCode = ?";
        try (Connection con = ds_.getConnection();
             PreparedStatement sta = con.prepareStatement(raw, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);) {
    		
           	sta.setInt(1, sid);
           	sta.setString(2, sub);
           	sta.setString(3, ass);
           		
            ResultSet res = sta.executeQuery(); 
        
			if (res.next()) {
				System.out.println("\nOriginal record: ");				
	        	displayResults(res);
			}
        	else {
        		System.out.println(String.format("No such mark: %d %s %s", sid, sub, ass));
        		return;
        	}
        }
        
        //update record
        raw = "UPDATE Marks SET Mark = ? WHERE StudentId = ? AND SubjectCode = ? AND AssessmentCode = ?";
        try (Connection con = ds_.getConnection();
             PreparedStatement sta = con.prepareStatement(raw, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);) {
    		
           	sta.setInt(1, mark);
           	sta.setInt(2, sid);
           	sta.setString(3, sub);
           	sta.setString(4, ass);
        
            sta.execute();
        }

        //display updated record
        raw = "SELECT * FROM Marks WHERE StudentId = ? AND SubjectCode = ? AND AssessmentCode = ?";
        try (Connection con = ds_.getConnection();
                PreparedStatement sta = con.prepareStatement(raw, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);) {
    		
              	sta.setInt(1, sid);
              	sta.setString(2, sub);
              	sta.setString(3, ass);
              		
               ResultSet res = sta.executeQuery(); 
                   
			if (res.next()) {
				System.out.println("\nUpdated record: ");
	        	displayResults(res);
			}
        }
    }
    

    
    /**
     * Delete a mark
     * 
     * @throws IOException, SQLException
      */
    private static void delete() throws IOException, SQLException {
        //get student id
        int sid = getStudentId();
                
        //get subject code
        String sub = getSubjectCode();
        
        //get assessment code
        String ass = getAssessmentCode();
        
        String raw = String.format("DELETE FROM Marks WHERE StudentId = %d AND SubjectCode = '%s' AND AssessmentCode = '%s'", sid, sub, ass);
        try (Connection con = ds_.getConnection();
             PreparedStatement sta = con.prepareStatement(raw, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);) {
    		
		  	sta.setInt(1, sid);
		  	sta.setString(2, sub);
		  	sta.setString(3, ass);
              		
        	sta.execute(raw);
        }
    }

    
    
    /**
     * Lists all rows in the table
     * 
     * @throws SQLException
     */
    private static void list() throws SQLException {
    	String raw = String.format("SELECT * FROM Marks");
        try (Connection con = ds_.getConnection();
             PreparedStatement sta = con.prepareStatement(raw, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);) {
        	
        	ResultSet res = sta.executeQuery();
        	
        	displayResults(res);
		}
    }
    
    
    
    /**
     * Lists Marks by subject
     * 
     * @throws IOException, SQLException
      */
    private static void listMarksBySubject() throws IOException, SQLException {
        //get subject code
        String sub = getSubjectCode();
        
        String raw = "SELECT * FROM Marks WHERE SubjectCode = ?";
        try (Connection con = ds_.getConnection();
             PreparedStatement sta = con.prepareStatement(raw, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);) {
        	
		  	sta.setString(1, sub);

		  	ResultSet res = sta.executeQuery();

        	displayResults(res);
        }
    }
    

    
    /**
     * Lists Marks by student
     * 
     * @throws IOException, SQLException
     */
    private static void listMarksByStudent() throws IOException, SQLException {
        //get student id
        int sid = getStudentId();
        
        String raw = "SELECT * FROM Marks WHERE StudentId = ?";
        try (Connection con = ds_.getConnection();
             PreparedStatement sta = con.prepareStatement(raw, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);) {
        	
   		  	sta.setInt(1, sid);

   		  	ResultSet res = sta.executeQuery();
   		  	
        	displayResults(res);
        
        }
    }

    
    
    /**
     * Internal utility method to return a student ID
     * 
     * @throws IOException
     * @return the student id
     */   
    private static int getStudentId() throws IOException {
	    //get student id
	    System.out.print("\nEnter StudentID: ");
	    int sid = 0;
	    while ( sid <= 0 ) {
	        try {
	            sid = Integer.parseInt(in_.readLine());
	            if (sid <= 0 ) {
	                System.out.println("Invalid input - id must be integer > 0");
	            }
	        }
	        catch (NumberFormatException nfe) {
	            System.out.println("Invalid input - id must be integer > 0");
	        }
	    }
	    return sid;
    }

    
    
    /**
     * Internal utility method to return a mark
     * 
     * @throws IOException
     * @return the student id
     */   
    private static int getMark() throws IOException {
	    //get student id
	    System.out.print("\nEnter Mark: ");
	    int mark = 0;
	    while ( mark <= 0 ) {
	        try {
	            mark = Integer.parseInt(in_.readLine());
	            if (mark <= 0 ) {
	                System.out.println("Invalid input - mark must be integer > 0");
	            }
	        }
	        catch (NumberFormatException nfe) {
	            System.out.println("Invalid input - mark must be integer > 0");
	        }
	    }
	    return mark;
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
	            System.out.println("Invalid input - subject code must have length 6," + 
	                               " first 3 characters alpha, last 3 characters numeric");
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
	            System.out.println("Invalid input - assessment code must have length 4," + 
	                               " first 3 characters alpha, last character alphanumeric");
	        }
	        else {
	            break;
	        }
	    }
	    return ass;
    }

    
    /**
     * Internal utility method to display a query's results
     * 
     * @throws SQLException
     */   
    private static void displayResults(ResultSet res) throws SQLException {
    	res.first();
    	System.out.println(String.format("\n\t%s\t%s\t%s\t%s", 
    			"StudentId", "SubjectCode", "AssessmentCode", "Mark"));
		do {
			System.out.println(String.format("\t%d\t\t%s\t\t%s\t\t%d", res.getInt("StudentId"), 
					res.getString("SubjectCode"), res.getString("AssessmentCode"), res.getInt("Mark")));
		} while (res.next());
	}

}
