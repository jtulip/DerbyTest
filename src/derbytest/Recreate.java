package derbytest;

import java.sql.*;
import java.util.*;
import java.io.*;

public class Recreate {

    private static String dbName = "CourseManager";
    private static String connectionURL = "jdbc:derby://localhost:1527/" + dbName + ";";

    public static void main(String[] args) {

        Connection conn = null;
        
        //now connect and recreate it
        try {
            conn = DriverManager.getConnection(connectionURL + "create=true");
            if (conn == null) {
                System.out.println(dbName + " could not connect.");
                return;
            }
            System.out.println(dbName + " connected and created.");
            
            //now execute the SQL schema file
            String fileName = "CourseManager_Schema.sql";
            String delimiter = ";";
            File infile;
            Scanner scanner = null;
            System.out.println("---------------------------------------");                    
            try  {
                infile = new File(fileName);
                scanner = new Scanner(infile).useDelimiter(delimiter); 
                while (scanner.hasNext()) {
                    String s = scanner.next().trim();
                    if (!s.startsWith("--") || s.length() == 0) {
                        System.out.println(s);                    
                        System.out.println("---------------------------------------");                      
                        Statement statement = null;
                        try {
                            statement = conn.createStatement();
                            statement.execute(s);
                        }
                        catch (SQLException e) {
                            e.printStackTrace();
                            return;                            
                        }
                        finally {
                            statement.close();
                        }
                    }
                }
                
                
            }
            catch (FileNotFoundException e) {
                System.out.println(String.format("File: %s , not found.", fileName));
                return;
            }
            finally {
                if (scanner != null) scanner.close();
            }
            
            conn.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
    }

}
