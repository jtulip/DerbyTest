package derbytest;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLIntegrityConstraintViolationException;

import org.apache.derby.jdbc.*;

public class DebugPrepStatement {

	public static void main(String[] args) {
		// main menu loop
		try {

			// Set up DataSource
			//ClientConnectionPoolDataSource ds = new ClientConnectionPoolDataSource();
			ClientDataSource ds = new ClientDataSource();
			ds.setServerName("localhost");
			ds.setPortNumber(1527);
			ds.setDatabaseName("CourseManager");

			try (BufferedReader in = new BufferedReader(new InputStreamReader(System.in));) {
/*				
		        //get student id
		        int sid = Utility.getStudentId(in);
		        
		        //get subject code
		        String sub = Utility.getSubjectCode(in);
		        
		        //get assessment code
		        String ass = Utility.getAssessmentCode(in);
*/		        
		        
		        String raw = "SELECT StudentId, SubjectCode, AssessmentCode, Mark FROM Marks WHERE StudentId = ? AND SubjectCode = ? AND AssessmentCode = ?";
		        //String raw = String.format("SELECT * FROM Marks WHERE StudentId = %d AND SubjectCode = '%s' AND AssessmentCode = '%s'", sid, sub, ass );
		        try (Connection con = ds.getConnection();
		              ) {
//ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY	
		        	PreparedStatement sta = con.prepareStatement(raw);
		        	sta.setInt(1, 1);
		        	sta.setString(2, "ITC203");
		        	sta.setString(3, "Asg1");
//		        	sta.setInt(4, mark);
		        	
		            ResultSet res = sta.executeQuery();
		            
		            if (res.next()) {
			        	//res.first();
			        	System.out.println(String.format("\n\t%s\t%s\t%s\t%s", 
			        			"StudentId", "SubjectCode", "AssessmentCode", "Mark"));
			    		do {
			    			System.out.println(String.format("\t%d\t\t%s\t\t%s\t\t%d", res.getInt("StudentId"), 
			    					res.getString("SubjectCode"), res.getString("AssessmentCode"), res.getInt("Mark")));
			    		} while (res.next());
		            }
		        }
		        catch (SQLIntegrityConstraintViolationException e) {
		            System.out.print(String.format("Invalid Mark"));            
		        }
				System.out.println("\nExiting.");
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}


}
