package bluejay;

import java.awt.image.BufferedImage;
import java.awt.Graphics;
import java.io.ByteArrayOutputStream;
import java.sql.Date;
import java.time.LocalDateTime;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class Employee {

	private int id;
	private String firstName;
	private String middleName;
	private String lastName;
	private String address;
	private String workType;
	private double basicSalary;
	private double grossPay;
	private double netPay;
	private String gender;
	private int absents;
	private int late;
	private int dayOff;
	private double daysWorked;
	private double overtime;
	private Date DOB;
	private double SSS;
	private double PAG_IBIG;
	private double PHILHEALTH;
	private String telNumber;
	private String email;
	private Date dateHired;
	private ImageIcon profileImage;
	private String department;
	private double ratePerDay;
	private double ratePerHour;
	private LocalDateTime timeIN;
	private LocalDateTime timeOUT;
	private String created_at;

	public Employee(int id, String firstName, String middleName, String lastName, String address, String department,
			String workType, double basicSalary, double ratePerDay, double grossPay, double netPay, String gender) {
		// Initialize
		this.id = id;
		this.firstName = firstName;
		this.middleName = middleName;
		this.lastName = lastName;
		this.address = address;
		this.department = department;
		this.workType = workType;
		this.basicSalary = basicSalary;
		this.ratePerDay = ratePerDay;
		this.grossPay = grossPay;
		this.netPay = netPay;
		this.gender = gender;

		this.absents = 0;
		this.late = 0;
		this.dayOff = 0;
		this.overtime = 0.0;
	}

	public Employee(int employeeId, String updatedFirstName, String updatedLastName, String updatedAddress,
			String updatedDepartment, String updatedWorkType, double updatedRate) {
		// Initialize
		this.id = employeeId;
		this.firstName = updatedFirstName;
		this.lastName = updatedLastName;
		this.address = updatedAddress;
		this.department = updatedDepartment;
		this.workType = updatedWorkType;
		this.basicSalary = updatedRate;

	}

	public Employee() {
	}

	// GETTERS
	public int getId() {
		return id;
	}

	public String getDepartment() {

		return department;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getAddress() {
		return address;
	}

	public String getWorkType() {
		return workType;
	}

	public double getBasicSalary() {
		return basicSalary;
	}

	public double getRatePerHour() {
		return ratePerHour;
	}

	public String getGender() {
		return gender;
	}

	public double getGrossPay() {
		return grossPay;
	}

	public double getNetPay() {
		return netPay;
	}

	public double getPAG_IBIG() {
		return PAG_IBIG;
	}

	public double getSSS() {
		return SSS;
	}

	public double getPHILHEALTH() {
		return PHILHEALTH;
	}

	public Date getDOB() {
		return DOB;
	}

	public int getAbsents() {
		return absents;
	}

	public String getTelNumber() {
		return telNumber;
	}

	public int getLate() {
		return late;
	}

	public int getDayOff() {
		return dayOff;
	}

	public double getOvertime() {
		return overtime;
	}

	public String getEmail() {
		return email;
	}

	public double getDaysWorked() {
		return daysWorked;
	}

	public Date getDateHired() {
		return dateHired;
	}

	public double getRatePerDay() {

		return ratePerDay;
	}

	public ImageIcon getProfileImage() {
		return profileImage;
	}

	public byte[] getProfileImageBytes() {
		if (this.profileImage != null) {
			try {
				BufferedImage bi = new BufferedImage(profileImage.getIconWidth(), profileImage.getIconHeight(),
						BufferedImage.TYPE_INT_RGB);
				Graphics g = bi.createGraphics();
				profileImage.paintIcon(null, g, 0, 0);
				g.dispose();
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ImageIO.write(bi, "png", baos);
				return baos.toByteArray();
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		} else {
			return null;
		}
	}

	// SETTERS
	public void setId(int id) {
		this.id = id;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public void setDepartment(String string) {

		this.department = string;
	}

	public void setWorkType(String workType) {
		this.workType = workType;
	}

	public void setBasicSalary(double basicSalary) {
		this.basicSalary = basicSalary;
	}

	public void setGrossPay(double grossPay) {
		this.grossPay = grossPay;
	}

	public void setNetPay(double netPay) {
		this.netPay = netPay;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public void setPAG_IBIG(double PAG_IBIG) {
		this.PAG_IBIG = PAG_IBIG;
	}

	public void setSSS(double SSS) {
		this.SSS = SSS;
	}

	public void setPHILHEALTH(double PHILHEALTH) {
		this.PHILHEALTH = PHILHEALTH;
	}

	public void setDOB(Date DOB) {
		this.DOB = DOB;
	}

	public void setAbsents(int absents) {
		this.absents = absents;
	}

	public void setLate(int late) {
		this.late = late;
	}

	public void setDayOff(int dayOff) {
		this.dayOff = dayOff;
	}

	public void setOvertime(double overtime) {
		this.overtime = overtime;
	}

	public void setTelNUmber(String telNumber) {
		this.telNumber = telNumber;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setDateHired(Date dateHired) {
		this.dateHired = dateHired;
	}

	public void setDaysWorked(double daysWorked) {
		this.daysWorked = daysWorked;
	}

	public void setRatePerDay(double double1) {

		this.ratePerDay = double1;
	}

	public void setRatePerHour(double ratePerHour) {
		this.ratePerHour = ratePerHour;
	}

	public void setProfileImage(byte[] imageData) {
		if (imageData != null) {
			this.profileImage = new ImageIcon(imageData);
		} else {
			this.profileImage = null; // Handle null image
		}
	}

	// METHODS TO CALCULATE

	// Method to calculate the total deduction

	public double calculateGrossPay(int workedHours, int overtimeHours) {
		int calculatedWorkedHours = (overtimeHours != 0) ? workedHours + overtimeHours : workedHours;
		System.out.println("gross pay calculating (debug)");
		System.out.println("worked hours: " + workedHours);
		System.out.println("Overtime hours: " + overtimeHours);
		
		return calculatedWorkedHours ;
	}

	public double totalDeductions() {
		return getPAG_IBIG() + getPHILHEALTH() + getSSS();
	}

	public double calculateNetPay() {
		double grossPay = getGrossPay();
		double totalDeductions = totalDeductions();
		return grossPay - totalDeductions;
	}

	public void setTimeIn(LocalDateTime TimeIn) {
		this.timeIN = TimeIn;
	}

	public void setTimeOut(LocalDateTime timeOUT) {
		this.timeOUT = timeOUT;

	}

	public LocalDateTime getTimeIN() {

		return timeIN;
	}

	public LocalDateTime getTimeOUT() {

		return timeOUT;
	}

	public void setPayrollDataCreated(String currentDate) {
		this.created_at = currentDate;
	}

	public String getCreated_At()
	{
		return created_at;
	}
	
}