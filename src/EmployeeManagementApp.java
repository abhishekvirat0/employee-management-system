import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.awt.Font;

public class EmployeeManagementApp extends JFrame {

    private JTextField empIdField;
    private JTextField nameField;
    private JTextField emailField;
//    private JTextField deptIdField;
    private JComboBox<String> deptDropdown;
    private JTextField projectIdField;
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
        JLabel projectIdLabel = new JLabel("Project ID:");
        projectIdLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        projectIdLabel.setBounds(10, 220, 120, 25);
        panel.add(projectIdLabel);
        
        projectIdField = new JTextField(20);
        projectIdField.setBounds(140, 220, 200, 25);
        panel.add(projectIdField);

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
            				"Emp ID", "Name", "Email", "Dept Name", "Project ID"
            		}, 0);
            
            while (rs.next()) {
                model.addRow(
                		new Object[]{
                				rs.getInt("emp_id"), 
                				rs.getString("name"), 
                				rs.getString("email"), 
                				rs.getString("dept_name"),
                				rs.getInt("project_id")}
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
        	System.out.println((String) deptDropdown.getSelectedItem());
            int empId = Integer.parseInt(empIdField.getText());
            String name = nameField.getText();
            String email = emailField.getText();
            String selectedDepartmentName = (String) deptDropdown.getSelectedItem();
            
            // Retrieve the department ID based on the selected department name
            int deptId = departments.get(selectedDepartmentName);
            int projectId = Integer.parseInt(projectIdField.getText());
            employeeTransaction.addEmployee(empId, name, email, deptId, projectId);
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
}
