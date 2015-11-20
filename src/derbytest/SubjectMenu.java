package derbytest;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.*;

import org.apache.derby.jdbc.ClientConnectionPoolDataSource;

public class SubjectMenu {
    
    private static ClientConnectionPoolDataSource ds_;
    private static BufferedReader in_;

    public static void execute(ClientConnectionPoolDataSource ds, BufferedReader in) {
        ds_ = ds;
        in_ = in;
        
        // menu display loop        
        boolean stop = false;
        
        while (!stop) {
            try {
                displayMenu();
                String ans = in_.readLine();
                
                switch (ans.toUpperCase()) {
                
                    case "C": create();
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

    
    
    private static void displayMenu() {
        System.out.print(                
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
    }

    
    
    private static void list() throws SQLException {
        try (Connection con = ds_.getConnection();
             Statement sta = con.createStatement();
             ResultSet res = sta.executeQuery( "SELECT * FROM Subjects"); ) {
        
            while (res.next()) {
                System.out.println(res.getString("SubjectCode") + "    " + res.getString("SubjectName"));
            }
        }
    }
    

    
    /**
     * Creates a new record in the Subjects table
     * 
     * @throws SQLException
     */
    private static void create() throws IOException, SQLException {        
        //get subject code
        System.out.print("Enter subject code: ");
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
        
        //get subject name
        System.out.print("Enter subject name: ");
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
        
        try (Connection con = ds_.getConnection();
             Statement sta = con.createStatement(); ) {
        
            sta.execute(String.format("INSERT INTO Subjects VALUES ('%s', '%s')", sub, name, name));
        }
        catch (SQLIntegrityConstraintViolationException e) {
            System.out.print(String.format("Invalid SubjectCode - %s already exists", sub));            
        }
    }
    
    

}
