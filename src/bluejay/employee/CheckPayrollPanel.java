package bluejay.employee;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;

import bluejay.Employee;
import bluejay.Main;
import bluejayDB.EmployeeDatabase;
import net.miginfocom.swing.MigLayout;

public class CheckPayrollPanel extends JPanel implements Printable {

    private static final long serialVersionUID = 1L;
    private EmployeeDatabase db;
    private Employee currentEmployee;
    private JTable table;
    private DefaultTableModel model;
    private JTextArea txtrPayslip;

    public CheckPayrollPanel(Employee currentEmployee, EmployeeDatabase db) {
        this.db = db;
        this.currentEmployee = currentEmployee;
        setLayout(new BorderLayout());

        // Header panel with back button and title
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Main content pane with table and text area for payroll details
        JPanel mainPane = createMainPane();
        add(mainPane, BorderLayout.CENTER);

        // Load initial payroll data
        loadPayrollData();
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(0, 191, 255).darker());
        headerPanel.setLayout(new MigLayout("", "[left][grow]"));

        JButton backButton = createBackButton();
        JLabel titleLabel = new JLabel("Payroll");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));

        headerPanel.add(backButton, "cell 0 0");
        headerPanel.add(titleLabel, "cell 1 0");

        return headerPanel;
    }

    private JButton createBackButton() {
        ImageIcon backIcon = new ImageIcon(getClass().getResource("/images/back.png"));
        JButton backButton = new JButton(
                new ImageIcon(backIcon.getImage().getScaledInstance(35, 35, Image.SCALE_SMOOTH)));
        backButton.setOpaque(false);
        backButton.setContentAreaFilled(false);
        backButton.setBorderPainted(false);
        backButton.addActionListener(e -> navigateToEmployeePanel());
        return backButton;
    }

    private JPanel createMainPane() {
        JPanel mainPane = new JPanel(
                new MigLayout("wrap, fillx", "[grow]", "[][100px,center][][150px,center][][][][][]"));

        model = new DefaultTableModel(
                new String[] { "Date", "Employment Type", "Basic Salary", "Gross Pay", "Total Deduction", "Net Pay",
                        "Salary Period" },
                0);
        table = new JTable(model);

        txtrPayslip = new JTextArea();
        txtrPayslip.setEditable(false);
        txtrPayslip.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JScrollPane tableScrollPane = new JScrollPane(table);
        mainPane.add(tableScrollPane, "cell 0 1, growx");

        JScrollPane textAreaScrollPane = new JScrollPane(txtrPayslip);
        mainPane.add(textAreaScrollPane, "cell 0 3, grow");

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadPayrollData());
        mainPane.add(refreshButton, "flowx,cell 0 0,alignx left");

        JButton requestBTN = new JButton("Request");
        mainPane.add(requestBTN, "cell 0 0");

        JButton printButton = new JButton("Print PaySlip");
        printButton.addActionListener(e -> printPaySlip());

        JButton checkBTN = new JButton("check payslip");
        checkBTN.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            }
        });
        mainPane.add(checkBTN, "cell 0 4,alignx right");
        mainPane.add(printButton, "cell 0 5,alignx right");

        return mainPane;
    }

    private void loadPayrollData() {
        model.setRowCount(0); // Clear existing data
        try {
            db.loadEMPPayroll(currentEmployee, model, txtrPayslip); // Load payroll data
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to load payroll data: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void navigateToEmployeePanel() {
        Main.frame.replaceContentPane("Weld Well HRMS", new EmployeePanel(currentEmployee, db), getLayout());
    }

    private void printPaySlip() {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintable(this);
        if (job.printDialog()) {
            try {
                job.print();
            } catch (PrinterException e) {
                JOptionPane.showMessageDialog(this, "Failed to print: " + e.getMessage(), "Print Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    @Override
    public int print(Graphics g, PageFormat pf, int pageIndex) {
        if (pageIndex > 0) {
            return NO_SUCH_PAGE;
        }

        Graphics2D g2d = (Graphics2D) g;
        g2d.translate(pf.getImageableX(), pf.getImageableY());
        txtrPayslip.printAll(g); // Print contents of the textArea

        return PAGE_EXISTS;
    }
}
