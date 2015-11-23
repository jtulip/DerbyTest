package derbytest;

import java.io.*;
import java.sql.*;

import org.apache.derby.jdbc.ClientConnectionPoolDataSource;

public class EnrolmentMenu {
    
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

    
    
    private static void displayMenu() {
        System.out.print(                
                "\n\nSelect :\n" +
                        "\tC: create Enrolment\n" +
                        "\tR: read Enrolment\n" +
                        "\tU: update Enrolment\n" +
                        "\tD: delete Enrolment\n" +
                        "\tL: list all Enrolments\n" +
                        "\tLu: list Enrolments by Subject\n" +
                        "\tLs: list Enrolments by Student\n" +
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
             ResultSet res = sta.executeQuery( "SELECT * FROM Enrolments"); ) {
        
            while (res.next()) {
                System.out.println(res.getString("SubjectCode") + "    " + res.getString("StudentId"));
            }
        }
    }
    
    
    
    /**
     * Creates a new record in the Enrolments table
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
        
        
        try (Connection con = ds_.getConnection();
             Statement sta = con.createStatement(); ) {
        
            sta.execute(String.format("INSERT INTO Enrolments VALUES ('%s', %d)", sub, sid));
        }
        catch (SQLIntegrityConstraintViolationException e) {
            System.out.print(String.format("Invalid Enrolment - studentId %d or subjectCode %s does not exist", sid, sub));            
        }
    }
    
    
    /**
     * Lists enrolments by subject
     * 
     * @throws SQLException
     */
    private static void listEnrolmentsBySubject() throws IOException, SQLException {
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
        
        try (Connection con = ds_.getConnection();
             Statement sta = con.createStatement();
             ResultSet res = sta.executeQuery( String.format("SELECT * FROM Enrolments WHERE SubjectCode = '%s'", sub)); ) {
        
            while (res.next()) {
                System.out.println(res.getString("SubjectCode") + "    " + res.getString("StudentId"));
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
        
        try (Connection con = ds_.getConnection();
             Statement sta = con.createStatement();
             ResultSet res = sta.executeQuery( String.format("SELECT * FROM Enrolments WHERE StudentId = %d", sid)); ) {
        
            while (res.next()) {
                System.out.println(res.getString("SubjectCode") + "    " + res.getString("StudentId"));
            }
        }
    }
    
}
