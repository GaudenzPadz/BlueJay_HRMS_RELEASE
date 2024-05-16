package bluejay;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Date;

import bluejayDB.EmployeeDatabase;

public class Payroll {
    private String employeeId;
    private String employeeName;
    private String employeeDepartment;
    private String employeeWorkType;
    private double ratePerHour;
    private int daysWorked;
    private int overtimeHours;
    private double bonus;
    private double grossPay;
    private double totalDeductions;
    private double netPay;
    private Date date;

    // Deductions
    private double pagIbig;
    private double philHealth;
    private double sss;
    private double cashAdvanced;

    public Payroll(String employeeId, String employeeName, String employeeDepartment, String employeeWorkType,
                   double ratePerHour, int daysWorked, int overtimeHours, double bonus, double grossPay,
                   double totalDeductions, double netPay, Date date) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.employeeDepartment = employeeDepartment;
        this.employeeWorkType = employeeWorkType;
        this.ratePerHour = ratePerHour;
        this.daysWorked = daysWorked;
        this.overtimeHours = overtimeHours;
        this.bonus = bonus;
        this.grossPay = grossPay;
        this.totalDeductions = totalDeductions;
        this.netPay = netPay;
        this.date = date;
    }

    // Getters and Setters
    public String getEmployeeId() {
        return employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public String getEmployeeDepartment() {
        return employeeDepartment;
    }

    public String getEmployeeWorkType() {
        return employeeWorkType;
    }

    public double getRatePerHour() {
        return ratePerHour;
    }

    public int getDaysWorked() {
        return daysWorked;
    }

    public int getOvertimeHours() {
        return overtimeHours;
    }

    public double getBonus() {
        return bonus;
    }

    public double getGrossPay() {
        return grossPay;
    }

    public double getTotalDeductions() {
        return totalDeductions;
    }

    public double getNetPay() {
        return netPay;
    }

    public Date getDate() {
        return date;
    }

    // Method to load deductions from the database
    public void loadDeductions(EmployeeDatabase db) {
        ResultSet rs = db.getDeductions();
        try {
            if (rs.next()) {
                sss = rs.getDouble("SSS");
                pagIbig = rs.getDouble("PAG_IBIG");
                philHealth = rs.getDouble("PHILHEALTH");
                cashAdvanced = rs.getDouble("advanced");
                rs.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to calculate gross pay
    public double calculateGrossPay() {
        return (ratePerHour * 8 * daysWorked) + (ratePerHour * 1.5 * overtimeHours) + bonus;
    }

    // Method to calculate total deductions
    public double calculateTotalDeductions() {
        return pagIbig + philHealth + sss + cashAdvanced;
    }

    // Method to calculate net pay
    public double calculateNetPay() {
        grossPay = calculateGrossPay();
        totalDeductions = calculateTotalDeductions();
        return grossPay - totalDeductions;
    }
}