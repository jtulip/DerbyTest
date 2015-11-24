package derbytest;

import java.io.*;

import org.apache.derby.jdbc.*;


public class MainMenu {
    
    public static void main(String[] args) {
        // main menu loop
        try {
        
            //Set up DataSource
            ClientConnectionPoolDataSource ds = new ClientConnectionPoolDataSource();
            ds.setServerName("localhost");
            ds.setPortNumber(1527);
            ds.setDatabaseName("CourseManager");
            
            
            try ( BufferedReader in = new BufferedReader(new InputStreamReader(System.in)); ) {
                            
                boolean stop = false;
                
                while (!stop) {
                    displayMenu();
                    String ans = in.readLine();
                    
                    switch (ans.toUpperCase()) {
                    
	                    case "S": StudentMenu.execute(ds, in);
	                              break;  
	          
	                    case "U": SubjectMenu.execute(ds, in);
	                              break;  
	          
	                    case "E": EnrolmentMenu.execute(ds, in);
                                  break;  
    
	                    case "A":AssessmentMenu.execute(ds, in);
                                  break;  
    
	                    case "Q": stop = true;
	                              break;
	                              
	                    default: System.out.println(String.format("%s not recognised", ans));
                    }
                }
                System.out.println("\nExiting.");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void displayMenu() {
        System.out.print(                
                "\n\nSelect :\n" +
                "\tS: Student menu\n" +
                "\tU: Subject menu\n" +
                "\tE: Enrolments menu\n" +
                "\tA: Assessments menu\n" +
                "\n\tQ: quit\n" +
                "\n" +
                "Selection : "
                );
    }

}
