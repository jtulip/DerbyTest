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
                scanner = new Scanner(infile).useDelimiter("\n"); 
                StringBuilder builder = new StringBuilder();
                while (scanner.hasNext()) {
                    String s = scanner.next().trim();
                    if (!s.startsWith("--") || s.length() == 0) {
                        builder.append(" ").append(s);
                        if (builder.toString().endsWith(delimiter)) {
                            String raw = builder.toString().split(delimiter)[0].trim();
                            System.out.println(raw + "\n----------------------------------\n");
                            Statement statement = null;
                            try {
                                statement = conn.createStatement();
                                statement.execute(raw);
                                builder.setLength(0);
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
                System.out.println("Finished");
                                
                
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
