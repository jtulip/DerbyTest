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
          
                    case "LU": listAssessmentsBySubject();
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
        String sub = Utility.getSubjectCode(in_);
        
        //get assessment code
        String ass = Utility.getAssessmentCode(in_);
        
        //get assessment weight
        int wgt = Utility.getAssessmentWeight(in_);
       
        String raw = "INSERT INTO Assessments VALUES (?, ?, ?)";
        try (Connection con = ds_.getConnection();
                PreparedStatement sta = con.prepareStatement(raw, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY); ) {

        	sta.setString(1, sub);
           	sta.setString(2, ass);
           	sta.setInt(3, wgt);

            sta.execute();
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
        String sub = Utility.getSubjectCode(in_);
        
        //get assessment code
        String ass = Utility.getAssessmentCode(in_);
        
        //get assessment weight
        int wgt = Utility.getAssessmentWeight(in_);
        
        String raw1 = "SELECT * FROM Assessments WHERE SubjectCode = ? AND AssessmentCode = ?";
        String raw2 = "UPDATE Assessments SET AssessmentWeight = ? WHERE (SubjectCode = ?) AND (AssessmentCode = ?)";
        String raw3 = "SELECT * FROM Assessments WHERE SubjectCode = ? AND AssessmentCode = ?";
        try (Connection con = ds_.getConnection();
                PreparedStatement sta1 = con.prepareStatement(raw1, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                PreparedStatement sta2 = con.prepareStatement(raw2, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                PreparedStatement sta3 = con.prepareStatement(raw3, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);) {
            	         	
           	//display the original record
        	sta1.setString(1, sub);
           	sta1.setString(2, ass);
           		
            ResultSet res = sta1.executeQuery(); 
        
			if (res.next()) {
				System.out.println("\nOriginal record: ");				
	        	displayResults(res);
			}
        	else {
        		System.out.println(String.format("No such assessment: %s %s", sub, ass));
        		return;
        	}
			
			//update the record
		  	sta2.setInt(1, wgt);
		  	sta2.setString(2, sub);
		  	sta2.setString(3, ass);
        
            sta2.execute();
            
            //display updated record
          	sta3.setString(1, sub);
          	sta3.setString(2, ass);
          		
          	res = sta3.executeQuery(); 
               
			if (res.next()) {
				System.out.println("\nUpdated record: ");
	        	displayResults(res);
			}
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
        String sub = Utility.getSubjectCode(in_);

        //get assessment code
        String ass = Utility.getAssessmentCode(in_);
        
        String raw = String.format("SELECT * FROM Assessments WHERE (SubjectCode = ?) AND (AssessmentCode = ?)", sub, ass);
        try (Connection con = ds_.getConnection();
                PreparedStatement sta = con.prepareStatement(raw, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);) {
        	
		  	sta.setString(1, sub);
		  	sta.setString(2, ass);

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
     * Delete an assessment
     * 
     * @throws SQLException
     */
    private static void delete() throws IOException, SQLException {
        //get subject code
        String sub = Utility.getSubjectCode(in_);
        
        //get assessment code
        String ass = Utility.getAssessmentCode(in_);
        
        String raw = "DELETE FROM Assessments WHERE (SubjectCode = ?) AND (AssessmentCode = ?)";
         try (Connection con = ds_.getConnection();
                PreparedStatement sta = con.prepareStatement(raw, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);) {
        	
		  	sta.setString(1, sub);
		  	sta.setString(2, ass);

		  	sta.execute();        
        }
    }

    
    
    /**
     * Lists all assessments
     * 
     * @throws SQLException
     */
    private static void list() throws SQLException {
        String raw = "SELECT * FROM Assessments ORDER BY SubjectCode, AssessmentCode";
        try (Connection con = ds_.getConnection();
                PreparedStatement sta = con.prepareStatement(raw, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);) {
        	
		  	ResultSet res = sta.executeQuery();
        
        	if (res.next()) {
            	displayResults(res);
            }
        }
    }
    
    
    
    /**
     * Lists assessments by subject
     * 
     * @throws SQLException
     */
    private static void listAssessmentsBySubject() throws IOException, SQLException {
        //get subject code
        String sub = Utility.getSubjectCode(in_);
        
        String raw = "SELECT * FROM Assessments WHERE SubjectCode = ? ORDER BY AssessmentCode";
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
     * Internal utility method to display a query's results
     * 
     * @throws SQLException
     */   
    private static void displayResults(ResultSet res) throws SQLException {
    	res.first();
    	System.out.println(String.format("\n\t%s\t%s\t%s", "SubjectCode", "AssessmentCode", "AssessmentWeight"));
		do {
			System.out.println(String.format("\t%s\t\t%s\t\t%d", 
					res.getString("SubjectCode"), res.getString("AssessmentCode"), res.getInt("AssessmentWeight")));
		} while (res.next());
	}


}
