package derbytest;

import java.io.*;
import java.sql.*;

import org.apache.derby.jdbc.ClientConnectionPoolDataSource;

public class StudentMenu {
    
    private static ClientConnectionPoolDataSource ds_;
    private static BufferedReader in_;
    
    

    /**
     * Display and process the available menu options for the Student table
     * 
     */
    public static void execute(ClientConnectionPoolDataSource ds, BufferedReader in) {
        ds_ = ds;
        in_ = in;
        
        String menu = String.format(                
                "\n\nSelect :\n" +
                        "\tC: create Student\n" +
                        "\tR: read Student\n" +
                        "\tU: update Student\n" +
                        "\tD: delete Student\n" +
                        "\tL: list all Students\n" +
                        "\tG: calculate Student grades\n" +
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
                    
                    case "G": calcGrade();
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
	 * Create a new Student record 
	 * 
	 * @throws SQLException
	 */
    private static void create() throws IOException, SQLException {        
        //get student id
    	int sid = getStudentId();
        
        //get firstname
    	String fname = getFirstName();
        
        //get lastname
        String lname = getLastName();
        
        String raw = String.format("INSERT INTO Students VALUES (%d, '%s', '%s')", sid, fname, lname);
        try (Connection con = ds_.getConnection();
             Statement sta = con.createStatement(); ) {
        
            sta.execute(raw);
        }
        catch (SQLIntegrityConstraintViolationException e) {
            System.out.print(String.format("Invalid StudentId - %d already exists", sid));            
        }
    }
 
    
    
    /**
     * Read a student
     * 
     * @throws SQLException
     */
    private static void read() throws IOException, SQLException {
        //get student id
        int sid = getStudentId();
        
        String raw = String.format("SELECT * FROM Students WHERE StudentId = %d", sid);
        try (Connection con = ds_.getConnection();
             Statement sta = con.createStatement();
             ResultSet res = sta.executeQuery(raw); ) {
        
        	displayStudentHeader();
			while (res.next()) {
				displayStudent(res.getInt("StudentId"), res.getString("FirstName"), res.getString("LastName"));
			}
        }
    }
    
    
    
    /**
     * Update a student. 
	 * <p>	
	 * Only first name and last name may be changed.
     * 
     * @throws SQLException
     */
    private static void update() throws IOException, SQLException {
        //get student id
        int sid = getStudentId();
                
        String raw = String.format("SELECT * FROM Students WHERE StudentId = %d", sid);
        try (Connection con = ds_.getConnection();
             Statement sta = con.createStatement();
             ResultSet res = sta.executeQuery(raw); ) {
        
			if (res.next()) {
				System.out.println("\nOriginal record: ");
	        	displayStudentHeader();
				displayStudent(res.getInt("StudentId"), res.getString("FirstName"), res.getString("LastName"));
			}
        	else {
        		System.out.println(String.format("No such student: %d", sid));
        		return;
        	}
        }
        
        //get new first name
        String fname = getFirstName();
        //get new last name
        String lname = getLastName();
        
        //update record
        raw = String.format("UPDATE Students SET FirstName='%s', LastName='%s' WHERE StudentId=%d", fname, lname, sid);
        try (Connection con = ds_.getConnection();
             Statement sta = con.createStatement(); ) {
        
            sta.execute(raw);
        }

        raw = String.format("SELECT * FROM Students WHERE StudentId = %d", sid);
        try (Connection con = ds_.getConnection();
             Statement sta = con.createStatement();
             ResultSet res = sta.executeQuery(raw); ) {
        
			if (res.next()) {
				System.out.println("\nUpdated record: ");
	        	displayStudentHeader();
				displayStudent(res.getInt("StudentId"), res.getString("FirstName"), res.getString("LastName"));
			}
        }
    }
    

    
    /**
     * Delete a student. 
     * 
     * @throws SQLException
     */
    private static void delete() throws IOException, SQLException {
        //get student id
        int sid = getStudentId();
                
        String raw = String.format("DELETE FROM Students WHERE StudentId = %d", sid);
        try (Connection con = ds_.getConnection();
             Statement sta = con.createStatement(); ) {
        	
        	sta.execute(raw);
        }        
    }

    
    
    /**
     * List all students
     * 
     * @throws SQLException
     */
     private static void list() throws SQLException {
     	String raw = String.format("SELECT * FROM Students");
     	try (Connection con = ds_.getConnection();
     			Statement sta = con.createStatement();
     			ResultSet res = sta.executeQuery(raw); ) {
    
     		displayStudentHeader();
 			while (res.next()) {
 				displayStudent(res.getInt("StudentId"), res.getString("FirstName"), res.getString("LastName"));
 			}
     	}
     }
    
    
    
     /**
      * Calculate a student's grades
      * 
      * @throws SQLException
      */
    private static void calcGrade() throws IOException, SQLException {
          //get student id
          int sid = getStudentId();
                  
          
      	String raw1 = String.format("SELECT SubjectCode FROM Enrolments where StudentId = %d", sid);
      	try (Connection con = ds_.getConnection();
      			Statement sta1 = con.createStatement();
      			ResultSet res1 = sta1.executeQuery(raw1); ) {
     
  			while (res1.next()) {
  				String sub = res1.getString("SubjectCode");
  				String raw2 = String.format("SELECT Marks.AssessmentCode, Marks.Mark, Assessments.AssessmentWeight " +
  	                "FROM Marks, Assessments " +
  	                "WHERE Marks.SubjectCode = Assessments.SubjectCode " +
  	                    "AND Marks.AssessmentCode = Assessments.AssessmentCode " +
  	                    "AND Marks.StudentId = %d " +
  	                    "AND Marks.SubjectCode='%s' " +
  	                    "ORDER BY Marks.AssessmentCode ", sid, sub);
	  	      	try (Statement sta2 = con.createStatement();
	  	      			ResultSet res2 = sta2.executeQuery(raw2);) {
	  	      		
	  	      		java.util.ArrayList<String> codes = new java.util.ArrayList<String>();
	  	      		java.util.ArrayList<Float> marks = new java.util.ArrayList<Float>();
	  	      		java.util.ArrayList<Integer> weights = new java.util.ArrayList<Integer>();
	  	      		
	  	      		float totalWeight = 0;
	  	      		
	  	  			while (res2.next()) {
		  	      		codes.add(res2.getString("AssessmentCode"));
		  	      		marks.add(res2.getFloat("Mark"));
		  	      		weights.add(res2.getInt("AssessmentWeight"));
		  	      		totalWeight += weights.get(weights.size()-1);
	  	  			}
	  	  			java.util.Iterator<Float> markIter = marks.iterator();
	  	  			java.util.Iterator<Integer> weightIter = weights.iterator();

	  				System.out.println(String.format("%s\t%5.1f", sub, totalWeight));

	  	  			float totalMarks = 0.0f;
	  	  			for (String code : codes) {
	  	  				float mark = markIter.next();
	  	  				float weight = weightIter.next();
	  	  				float markFrac = mark / 100;
	  	  				float realMark = markFrac * weight;
	  	  				totalMarks += realMark;
	  	  				
		  	      		System.out.println(String.format("%s\t%5.2f\t%5.2f\t%5.2f\t%5.1f", 
		  	      				                        code, mark, markFrac, weight, realMark));
	  	  			}
	  	  			System.out.println(String.format("Total marks: %5.1f, Out of: %5.1f = %5.1f%%", totalMarks, totalWeight, totalMarks/totalWeight*100 ));
	  	      	}	  	          
  			}
  			System.out.println("\n\n");
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
     * Internal utility method to return a first name
     * 
     * @throws IOException
     * @return the student's first name
     */   
    private static String getFirstName() throws IOException {	    
	    String fname = getName("\nEnter first name: ");
	    return fname;   
    }
    
    
    
    /**
     * Internal utility method to return a last name
     * 
     * @throws IOException
     * @return the student's last name
     */   
    private static String getLastName() throws IOException {	    
	    String lname = getName("\nEnter last name: ");
	    return lname;   
    }
    
    
    
    /**
     * Internal utility method to return a any name
     * 
     * @throws IOException
     * @return the name
     */   
    private static String getName(String prompt)  throws IOException {	    
	    //get name
	    System.out.print(prompt);
	    String name = "";
	    while (true) {
	        name = in_.readLine().trim();
	        if ( 0 == name.length() || name.length() > 32 || !name.matches("[a-zA-Z]+")) {
	            System.out.println("Invalid input - name must be alpha only, 0 < length < 32");
	        }
	        else {
	            break;
	        }
	    }
	    return name;   
    }

    
    
   /**
     * Internal utility method to display student header line
     * 
     */   
    private static void displayStudentHeader() {
    	System.out.println(String.format("\n\t%s\t%s\t%s", "StudentId", "FirstName","LastName"));
    }
    

    
    /**
     * Internal utility method to display a student's data fields
     * 
     */   
    private static void displayStudent(int studentId, String firstName, String lastName) {
		System.out.println(String.format("\t%d\t\t%s\t\t%s", studentId, firstName, lastName));
     }

    
}
