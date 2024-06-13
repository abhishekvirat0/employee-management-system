import javax.swing.*;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.table.DefaultTableModel;

import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.Properties;
import java.awt.Font;

@SuppressWarnings("serial")
public class EmployeeManagementApp extends JFrame {

    private JTextField empIdField;
    private JTextField nameField;
    private JTextField emailField;
    private JComboBox<String> deptDropdown;
    private JComboBox<String> projDropdown;
    
    private JTextField phoneField;
    private JTextField jobTitleField;
    private JTextField salaryField;
    private JComboBox<String> locationDropdown;
    private JDatePickerImpl datePicker;
    
    private JTable employeeTable;
    private EmployeeTransaction employeeTransaction;

    public EmployeeManagementApp() {
        employeeTransaction = new EmployeeTransaction();
        initUI();
        loadEmployeeData();
    }

    private void initUI() {
        setTitle("Employee Management System");
        setSize(1000, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        add(panel);
        
        // Heading
        JLabel headingLabel = new JLabel("Employee Management System");
        headingLabel.setFont(new Font("Arial", Font.BOLD, 30));
        headingLabel.setBounds(300, 10, 600, 40);
        panel.add(headingLabel);

        
        // Employee ID
        JLabel empIdLabel = new JLabel("Employee ID:");
        empIdLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        empIdLabel.setBounds(10, 60, 120, 25);
        panel.add(empIdLabel);


        empIdField = new JTextField(20);
        empIdField.setBounds(140, 60, 200, 25);
        panel.add(empIdField);


        // Name
        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        nameLabel.setBounds(10, 100, 120, 25);
        panel.add(nameLabel);

        nameField = new JTextField(20);
        nameField.setBounds(140, 100, 200, 25);
        panel.add(nameField);


        // Email
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        emailLabel.setBounds(10, 140, 120, 25);
        panel.add(emailLabel);
        
        emailField = new JTextField(20);
        emailField.setBounds(140, 140, 200, 25);
        panel.add(emailField);


        // Dept ID
        JLabel deptIdLabel = new JLabel("Deptartment:");
        deptIdLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        deptIdLabel.setBounds(10, 180, 120, 25);
        panel.add(deptIdLabel);
        
        // Fetch department names from the database
        Map<String, Integer> departments = employeeTransaction.fetchDepartment();
        // Populate dropdown with department names
        deptDropdown = new JComboBox<>(departments.keySet().toArray(new String[0]));
        deptDropdown.setBounds(140, 180, 200, 25);
        panel.add(deptDropdown);
        
        // Project ID
        JLabel projectIdLabel = new JLabel("Project:");
        projectIdLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        projectIdLabel.setBounds(10, 220, 120, 25);
        panel.add(projectIdLabel);
        
        // Fetch projects names from the database
        Map<String, Integer> projects = employeeTransaction.fetchProject();
        projDropdown = new JComboBox<>(projects.keySet().toArray(new String[0]));
        projDropdown.setBounds(140, 220, 200, 25);
        panel.add(projDropdown);

        
        // Phone
        JLabel phoneLabel = new JLabel("Phone No:");
        phoneLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        phoneLabel.setBounds(500, 60, 120, 25);
        panel.add(phoneLabel);


        phoneField = new JTextField(20);
        phoneField.setBounds(600, 60, 200, 25);
        panel.add(phoneField);


        // JOB Title
        JLabel JobTitleLabel = new JLabel("Job Title:");
        JobTitleLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        JobTitleLabel.setBounds(500, 100, 120, 25);
        panel.add(JobTitleLabel);


        jobTitleField = new JTextField(20);
        jobTitleField.setBounds(600, 100, 200, 25);
        panel.add(jobTitleField);
        
        // DOB
        JLabel dobLabel = new JLabel("DOB:");
        dobLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        dobLabel.setBounds(500, 140, 120, 25);
        panel.add(dobLabel);
        
        UtilDateModel model = new UtilDateModel();
        Properties p = new Properties();
        p.put("text.today", "Today");
        p.put("text.month", "Month");
        p.put("text.year", "Year");
        JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
        datePicker = new JDatePickerImpl(datePanel,new DateLabelFormatter());
        datePicker.setBounds(600, 140, 200, 25);
        panel.add(datePicker);

        
        // Salary
        JLabel salaryLabel = new JLabel("Salary:");
        salaryLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        salaryLabel.setBounds(500, 180, 120, 25);
        panel.add(salaryLabel);


        salaryField = new JTextField(20);
        salaryField.setBounds(600, 180, 200, 25);
        panel.add(salaryField);
        
        // Salary
        JLabel locationLabel = new JLabel("Location:");
        locationLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        locationLabel.setBounds(500, 220, 120, 25);
        panel.add(locationLabel);


        Map<String, Integer> offices = employeeTransaction.fetchOffice();
        locationDropdown = new JComboBox<>(offices.keySet().toArray(new String[0]));
        locationDropdown.setBounds(600, 220, 200, 25);
        panel.add(locationDropdown);
        
        JButton addButton = new JButton("Add");
        addButton.setBounds(10, 260, 150, 25);
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addEmployee();
            }
        });
        panel.add(addButton);

        JButton updateButton = new JButton("Update Email");
        updateButton.setBounds(230, 260, 150, 25);
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateEmployeeEmail();
            }
        });
        panel.add(updateButton);

        JButton deleteButton = new JButton("Delete");
        deleteButton.setBounds(470, 260, 150, 25);
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteEmployee();
            }
        });
        panel.add(deleteButton);

        JButton transferButton = new JButton("Transfer");
        transferButton.setBounds(710, 260, 150, 25);
        transferButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                transferEmployee();
            }
        });
        panel.add(transferButton);

        employeeTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(employeeTable);
//        scrollPane.setBounds(10, 210, 760, 340);
        scrollPane.setBounds(10, 310, 760, 240);
        panel.add(scrollPane);
    }
    

    private void loadEmployeeData() {
        try {
            ResultSet rs = employeeTransaction.getAllEmployees();
            DefaultTableModel model = new DefaultTableModel(
            		new String[]{
            				"Emp ID", "Name", "Email", "Dept Name", "Project"
            		}, 0);
            
            while (rs.next()) {
                model.addRow(
                		new Object[]{
                				rs.getInt("emp_id"), 
                				rs.getString("name"), 
                				rs.getString("email"), 
                				rs.getString("dept_name"),
                				rs.getString("project_name")}
                		);
            }
            
            employeeTable.setModel(model);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addEmployee() {
        try {
        	Map<String, Integer> departments = employeeTransaction.fetchDepartment();
        	Map<String, Integer> projects = employeeTransaction.fetchProject();
        	Map<String, Integer> offices = employeeTransaction.fetchOffice();
        	
            int empId = Integer.parseInt(empIdField.getText());
            String name = nameField.getText();
            String email = emailField.getText();
            
            String phone = phoneField.getText();
            
            java.util.Date utilDate = (java.util.Date) ((UtilDateModel) datePicker.getModel()).getValue();
            java.sql.Date dob = new java.sql.Date(utilDate.getTime());
            
//            Date dob = (Date) datePicker.getModel().getValue();
            String jobTitle = jobTitleField.getText();
            double salary = Double.parseDouble(salaryField.getText());
            
            String selectedDepartmentName = (String) deptDropdown.getSelectedItem();
            int deptId = departments.get(selectedDepartmentName);
            
            String selectedProjectName = (String) projDropdown.getSelectedItem();
            int projectId = projects.get(selectedProjectName);
            
            String selectedOfficeName = (String) locationDropdown.getSelectedItem();
            int officeId = offices.get(selectedOfficeName);
            
            // Create an Employee object
            Employee employee = new Employee(empId, name, email, phone, dob, jobTitle, salary, deptId, officeId, projectId);

            // Add employee using the EmployeeTransaction class
            employeeTransaction.addEmployee(employee);
            
//            employeeTransaction.addEmployee(empId, name, email, phone, dob, jobTitle, salary, deptId, officeId, projectId);
            loadEmployeeData();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateEmployeeEmail() {
        try {
            int empId = Integer.parseInt(empIdField.getText());
            String email = emailField.getText();
            employeeTransaction.updateEmployeeEmail(empId, email);
            loadEmployeeData();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteEmployee() {
        try {
            int empId = Integer.parseInt(empIdField.getText());
            employeeTransaction.deleteEmployee(empId);
            loadEmployeeData();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void transferEmployee() {
        try {
        	Map<String, Integer> departments = employeeTransaction.fetchDepartment();
            int empId = Integer.parseInt(empIdField.getText());
            String selectedDepartmentName = (String) deptDropdown.getSelectedItem();
            
            // Retrieve the department ID based on the selected department name
            int deptId = departments.get(selectedDepartmentName);
            
//            int deptId = Integer.parseInt(deptIdField.getText());
            employeeTransaction.transferEmployee(empId, deptId);
            loadEmployeeData();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new EmployeeManagementApp().setVisible(true);
            }
        });
    }
    
    // for DataLabelFormat
    public class DateLabelFormatter extends AbstractFormatter {

        private String datePattern = "yyyy-MM-dd"; // data pattern
        private SimpleDateFormat dateFormatter = new SimpleDateFormat(datePattern);

        @Override
        public Object stringToValue(String text) throws ParseException {
            return dateFormatter.parseObject(text);
        }

        @Override
        public String valueToString(Object value) throws ParseException {
            if (value != null) {
                Calendar cal = (Calendar) value;
                return dateFormatter.format(cal.getTime());
            }

            return "";
        }

    }
}
