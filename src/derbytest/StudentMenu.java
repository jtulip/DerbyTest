package derbytest;

import java.io.BufferedReader;
import java.sql.*;

import org.apache.derby.jdbc.ClientConnectionPoolDataSource;

public class StudentMenu {
    
    private static ClientConnectionPoolDataSource ds_;

    public static void execute(ClientConnectionPoolDataSource ds, BufferedReader in) {
        // menu display loop
        try {
        
            ds_ = ds;
            
            boolean stop = false;
            
            while (!stop) {
                displayMenu();
                String ans = in.readLine();
                
                switch (ans.toUpperCase()) {
                
                    case "L": list();
                              break;  
                    
                    case "X": stop = true;
                              break;
                              
                    default: System.out.println(String.format("%s not recognised", ans));
                }
            }
            System.out.println("\nReturning to Main.");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
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
    
    private static void list() throws SQLException {
        try (Connection con = ds_.getConnection();
             Statement sta = con.createStatement();
             ResultSet res = sta.executeQuery( "SELECT * FROM Students"); ) {
        
            while (res.next()) {
                System.out.println(res.getInt("StudentId") + "    " + res.getString("FirstName") + "    " + res.getString("LastName"));
            }
        }
    }
    
    
}
