package bluejay;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

import bluejayDB.EmployeeDatabase;

public class Payroll {
    private int employeeId;
    private double ratePerHour;
    private double pagIbig;
    private double philHealth;
    private double sss;
    private double cashAdvanced;
    private LocalDateTime timeIn;
    private LocalDateTime timeOut;

    public Payroll(int employeeId, double ratePerHour, double pagIbig, double philHealth, double sss,
            double cashAdvanced) {
        this.employeeId = employeeId;
        this.ratePerHour = ratePerHour;
        this.pagIbig = pagIbig;
        this.philHealth = philHealth;
        this.sss = sss;
        this.cashAdvanced = cashAdvanced;
    }

    public void setTIME(LocalDateTime timeIn, LocalDateTime timeOut) {
        this.timeIn = timeIn;
        this.timeOut = timeOut;
    }

    private void loadDeductions(EmployeeDatabase db) {
        ResultSet rs = db.getDeductions();
        try {
            if (rs.next()) {
                sss = rs.getInt("SSS");
                pagIbig = rs.getInt("PAG_IBIG");
                philHealth = rs.getInt("PHILHEALTH");
                cashAdvanced = rs.getInt("advanced");

                rs.close();

            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public double calculateGrossPay(int workedHours) {
        return workedHours * ratePerHour;
    }

    public double calculateTotalDeductions() {
        return pagIbig + philHealth + sss + cashAdvanced;
    }

    public double calculateNetPay(int workedHours) {
        double grossPay = calculateGrossPay(workedHours);
        double totalDeductions = calculateTotalDeductions();
        return grossPay - totalDeductions;
    }

}