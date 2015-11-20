package derbytest;

import java.io.*;
import java.sql.*;

import org.apache.derby.jdbc.ClientConnectionPoolDataSource;

public class StudentMenu {
    
    private static ClientConnectionPoolDataSource ds_;
    private static BufferedReader in_;
    
    
    public static void execute(ClientConnectionPoolDataSource ds, BufferedReader in) {
        // menu display loop
    
        ds_ = ds;
        in_ = in;
        
        boolean stop = false;
        
        while (!stop) {
            try {
                displayMenu();
                String ans = in.readLine();
                
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
                        "\tC: create Student\n" +
                        "\tR: read Student\n" +
                        "\tU: update Student\n" +
                        "\tD: delete Student\n" +
                        "\tL: list all Student\n" +
                        "\n\tX: return to main\n" +
                "\n" +
                "Selection : "
                );
    }

    
    /**
     * Lists all rows in the table
     * 
     * @throws SQLException
     */
    private static void list() throws SQLException {
        try (Connection con = ds_.getConnection();
             Statement sta = con.createStatement();
             ResultSet res = sta.executeQuery( "SELECT * FROM Students"); ) {
        
            while (res.next()) {
                System.out.println(res.getInt("StudentId") + "    " + res.getString("FirstName") + "    " + res.getString("LastName"));
            }
        }
    }
    
    
    
    /**
     * Creates a new record in the Students table
     * 
     * @throws SQLException
     */
    private static void create() throws IOException, SQLException {        
        //get student id
        System.out.print("Enter new StudentID: ");
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
        
        //get firstname
        System.out.print("Enter first name: ");
        String fname = "";
        while (true) {
            fname = in_.readLine().trim();
            if ( 0 == fname.length() || fname.length() > 32 || !fname.matches("[a-zA-Z]+")) {
                System.out.println("Invalid input - fname must be alpha only, 0 < length < 32");
            }
            else {
                break;
            }
        }
        
        //get lastname
        System.out.print("Enter last name: ");
        String lname = "";
        while (true) {
            lname = in_.readLine().trim();
            if (lname.length() == 0 || lname.length() > 32 || !fname.matches("[a-zA-Z]+")) {
                System.out.println("Invalid input - lname must be alpha only, 0 < length < 32");
            }
            else {
                break;
            }
        }
        
        try (Connection con = ds_.getConnection();
             Statement sta = con.createStatement(); ) {
        
            sta.execute(String.format("INSERT INTO Students VALUES (%d, '%s', '%s')", sid, fname, lname));
        }
        catch (SQLIntegrityConstraintViolationException e) {
            System.out.print(String.format("Invalid StudentId - %d already exists", sid));            
        }
    }
    
    
}
