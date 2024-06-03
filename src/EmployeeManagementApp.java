import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EmployeeManagementApp extends JFrame {

    private JTextField empIdField;
    private JTextField nameField;
    private JTextField emailField;
    private JTextField deptIdField;
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
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(null);
        add(panel);

        JLabel empIdLabel = new JLabel("Employee ID:");
        empIdLabel.setBounds(10, 20, 80, 25);
        panel.add(empIdLabel);

        empIdField = new JTextField(20);
        empIdField.setBounds(100, 20, 165, 25);
        panel.add(empIdField);

        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setBounds(10, 50, 80, 25);
        panel.add(nameLabel);

        nameField = new JTextField(20);
        nameField.setBounds(100, 50, 165, 25);
        panel.add(nameField);

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setBounds(10, 80, 80, 25);
        panel.add(emailLabel);

        emailField = new JTextField(20);
        emailField.setBounds(100, 80, 165, 25);
        panel.add(emailField);

        JLabel deptIdLabel = new JLabel("Dept ID:");
        deptIdLabel.setBounds(10, 110, 80, 25);
        panel.add(deptIdLabel);

        deptIdField = new JTextField(20);
        deptIdField.setBounds(100, 110, 165, 25);
        panel.add(deptIdField);
        
        JLabel projectIdLabel = new JLabel("Project ID:");
        projectIdLabel.setBounds(10, 140, 80, 25);
        panel.add(projectIdLabel);

        projectIdField = new JTextField(20);
        projectIdField.setBounds(100, 140, 165, 25);
        panel.add(projectIdField);

        JButton addButton = new JButton("Add");
        addButton.setBounds(10, 170, 80, 25);
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addEmployee();
            }
        });
        panel.add(addButton);

        JButton updateButton = new JButton("Update Email");
        updateButton.setBounds(100, 170, 150, 25);
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateEmployeeEmail();
            }
        });
        panel.add(updateButton);

        JButton deleteButton = new JButton("Delete");
        deleteButton.setBounds(260, 170, 80, 25);
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteEmployee();
            }
        });
        panel.add(deleteButton);

        JButton transferButton = new JButton("Transfer");
        transferButton.setBounds(350, 170, 100, 25);
        transferButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                transferEmployee();
            }
        });
        panel.add(transferButton);

        employeeTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(employeeTable);
        scrollPane.setBounds(10, 210, 760, 340);
        panel.add(scrollPane);
    }
    private void loadEmployeeData() {
        try {
            ResultSet rs = employeeTransaction.getAllEmployees();
            DefaultTableModel model = new DefaultTableModel(
            		new String[]{
            				"Emp ID", "Name", "Email", "Dept ID", "Project ID"
            		}, 0);
            
            while (rs.next()) {
                model.addRow(
                		new Object[]{
                				rs.getInt("emp_id"), 
                				rs.getString("name"), 
                				rs.getString("email"), 
                				rs.getInt("dept_id"),
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
            int empId = Integer.parseInt(empIdField.getText());
            String name = nameField.getText();
            String email = emailField.getText();
            int deptId = Integer.parseInt(deptIdField.getText());
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
            int empId = Integer.parseInt(empIdField.getText());
            int deptId = Integer.parseInt(deptIdField.getText());
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
