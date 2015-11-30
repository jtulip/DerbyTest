package derbytest;

import java.io.BufferedReader;
import java.io.IOException;

public class Utility {

	
	
    /**
     * Utility method to return a student ID
     * 
     * @throws IOException
     * @return the student id
     */   
	public static final int getStudentId(BufferedReader in) throws IOException {
	    //get student id
	    System.out.print("\nEnter StudentID: ");
	    int sid = 0;
	    while ( sid <= 0 ) {
	        try {
	            sid = Integer.parseInt(in.readLine());
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
    public static final String getFirstName(BufferedReader in) throws IOException {	    
	    String fname = getName("\nEnter first name: ", in);
	    return fname;   
    }
    
    
    
    /**
     * Utility method to return a last name
     * 
     * @throws IOException
     * @return the student's last name
     */   
    public static final String getLastName(BufferedReader in) throws IOException {	    
	    String lname = getName("\nEnter last name: ", in);
	    return lname;   
    }
    
    
    
    /**
     * Utility method to return a name
     * 
     * @throws IOException
     * @return the name
     */   
    public static final String getName(String prompt, BufferedReader in)  throws IOException {	    
	    //get name
	    System.out.print(prompt);
	    String name = "";
	    while (true) {
	        name = in.readLine().trim();
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
     * Utility method to return a subject code
     * 
     * @throws IOException
     * @return the subject code
     */   
    public static final String getSubjectCode(BufferedReader in) throws IOException {
	    System.out.print("\nEnter subject code: ");
	    String sub = "";
	    while (true) {
	        sub = in.readLine().trim();
	        if ( sub.length() != 6 || !sub.matches("[a-zA-Z][a-zA-Z][a-zA-Z][1-9][0-9][0-9]")) {
	            System.out.println("Invalid input - subject code must have length 6, first 3 characters alpha, last 3 characters numeric");
	        }
	        else {
	            break;
	        }
	    }
	    return sub.toUpperCase();
    }

    
    
    /**
     * Internal utility method to return a subject name
     * 
     * @throws IOException
     * @return the subject name
     */   
    public static final String getSubjectName(BufferedReader in) throws IOException {
        System.out.print("\nEnter subject name: ");
        String name = "";
        while (true) {
            name = in.readLine().trim();
            if (name.length() == 0 || name.length() > 64 ) {
                System.out.println("Invalid input - subject name cannot be empty, or over length 64");
            }
            else {
                break;
            }
        }
    	return name;
    }
    

    
    /**
     * Internal utility method to return an assessment code
     * 
     * @throws IOException
     * @return the assessment code
     */   
    public static final String getAssessmentCode(BufferedReader in) throws IOException {
	    System.out.print("\nEnter Assessment Code: ");
	    String ass = "";
	    while (true) {
	        ass = in.readLine().trim();
	        if ( ass.length() != 4 || !ass.matches("[a-zA-Z][a-zA-Z][a-zA-Z][a-zA-Z0-9]")) {
	            System.out.println("Invalid input - assessment code must have length 4, first 3 characters alpha, last character alphanumeric");
	        }
	        else {
	            break;
	        }
	    }
	    return ass;
    }

    
    
    /**
     * Internal utility method to return an assessment weight
     * 
     * @throws IOException
     * @return the assessment weight
     */   
    public static final int getAssessmentWeight(BufferedReader in) throws IOException {
	    //get assessment weight
	    System.out.print("\nEnter Assessment Weight: ");
	    int wgt = 0;
	    while ( wgt <= 0 ) {
	        try {
	            wgt = Integer.parseInt(in.readLine());
	            if (wgt <= 0 ) {
	                System.out.println("Invalid input - weight must be integer > 0");
	            }
	        }
	        catch (NumberFormatException nfe) {
	            System.out.println("Invalid input - weight must be integer > 0");
	        }
	    }
	    return wgt;
    }

    
    
    /**
     * Internal utility method to return a mark
     * 
     * @throws IOException
     * @return the student id
     */   
     public static final int getMark(BufferedReader in) throws IOException {
	    //get student id
	    System.out.print("\nEnter Mark: ");
	    int mark = 0;
	    while ( mark <= 0 ) {
	        try {
	            mark = Integer.parseInt(in.readLine());
	            if (mark <= 0 ) {
	                System.out.println("Invalid input - mark must be integer > 0");
	            }
	        }
	        catch (NumberFormatException nfe) {
	            System.out.println("Invalid input - mark must be integer > 0");
	        }
	    }
	    return mark;
    }

}
