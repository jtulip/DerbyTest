package derbytest;
import java.awt.CardLayout;
import java.awt.Container;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import student.AddStudentPanel;

public class DerbyTestGUI {

  private JFrame frame;
  private JPanel cards;

  /**
   * Launch the application.
   */
  public static void main(String[] args) {
    EventQueue.invokeLater(new Runnable() {
      public void run() {
        try {
          DerbyTestGUI window = new DerbyTestGUI();
          window.frame.setVisible(true);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  /**
   * Create the application.
   */
  public DerbyTestGUI() {
    initialize();

    JPanel addStudentPanel = new AddStudentPanel();
    
    cards = new JPanel(new CardLayout());
    cards.add(addStudentPanel, "Add Student");
    
    Container pane = frame.getContentPane();
    pane.add(cards, null);
  }

  /**
   * Initialize the contents of the frame.
   */
  private void initialize() {
    frame = new JFrame();
    frame.setBounds(100, 100, 750, 550);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    JMenuBar menuBar = new JMenuBar();
    frame.setJMenuBar(menuBar);
    
    JMenu mnStudent = new JMenu("Student");
    menuBar.add(mnStudent);
    
    JMenuItem mntmAddStudent = new JMenuItem("Add Student");
    mntmAddStudent.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        System.out.println("Add Student selected");
        CardLayout cl = (CardLayout)(cards.getLayout());
        cl.show(cards, "Add Student");
      }
    });
    mnStudent.add(mntmAddStudent);
    
    JMenuItem mntmEditStudent = new JMenuItem("Edit Student");
    mnStudent.add(mntmEditStudent);
    
    JMenuItem mntmDeleteStudent = new JMenuItem("Delete Student");
    mnStudent.add(mntmDeleteStudent);
  }

}
