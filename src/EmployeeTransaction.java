import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

/**
 * @author Abhishek

 * @version June 2024
 */

public class EmployeeTransaction {

public void addEmployee(Employee employee) throws SQLException {

    // SQL statement to insert a new employee into the 'employees' table
    String insertEmployee = "INSERT INTO employees ("
            + "emp_id, "
            + "name, "
            + "email, "
            + "phone, "
            + "dob, "
            + "job_title, "
            + "salary, "
            + "hire_date, "
            + "dept_id, "
            + "office_id"
            + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    
    // SQL statement to check if the email already exists in the 'employees' table
    String checkEmail = "SELECT COUNT(*) FROM employees WHERE email = ?";
    
    // SQL statement to add offices, 
    
    // SQL statement to assign the new employee to a project
    String insertProjectAssignment = "INSERT INTO project_assignments (emp_id, project_id) VALUES (?, ?)";
    
    // SQL statement to randomly assign a supervisor to the new employee
    String getRandomSupervisor = "SELECT emp_id FROM employees ORDER BY RAND() LIMIT 1";
    
    // SQL statement to insert into the 'supervisors' table
    String insertSupervisor = "INSERT INTO supervisors (emp_id, supervisor_id) VALUES (?, ?)";

    try (Connection conn = DatabaseUtil.getConnection()) {
        // Disable auto-commit to handle transactions manually
        conn.setAutoCommit(false);

        try (PreparedStatement psCheckEmail = conn.prepareStatement(checkEmail);
             PreparedStatement psInsertEmployee = conn.prepareStatement(insertEmployee);
             PreparedStatement psInsertProjectAssignment = conn.prepareStatement(insertProjectAssignment);
        	 PreparedStatement psGetRandomSupervisor = conn.prepareStatement(getRandomSupervisor);
             PreparedStatement psInsertSupervisor = conn.prepareStatement(insertSupervisor)
            ) {

            if (isEmailExists(psCheckEmail, employee.getEmail())) {
                conn.rollback();
                throw new SQLException("Email already exists.");
            }

            insertEmployee(psInsertEmployee, employee);
            assignToProject(psInsertProjectAssignment, employee.getEmpId(), employee.getProjectId());
            assignRandomSupervisor(psGetRandomSupervisor, psInsertSupervisor, employee.getEmpId());

            conn.commit();
            // Commit the transaction if everything is successful
            conn.commit();
        } catch (SQLException e) {
        	conn.rollback(); // Rollback the transaction in case of any exception and throw the exception
//        	if (e.getErrorCode() == 1062) { 
//                JOptionPane.showInputDialog(null,"User already Exist with this Id");
//        	}   
            handleSQLException(e);// MySQL error code for duplicate entry
            throw e;
        }
    }
}

private boolean isEmailExists(PreparedStatement psCheckEmail, String email) throws SQLException {
    psCheckEmail.setString(1, email);
    try (ResultSet rs = psCheckEmail.executeQuery()) {
        return rs.next() && rs.getInt(1) > 0;
    }
}

private void insertEmployee(PreparedStatement psInsertEmployee, Employee employee) throws SQLException {
    Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
    psInsertEmployee.setInt(1, employee.getEmpId());
    psInsertEmployee.setString(2, employee.getName());
    psInsertEmployee.setString(3, employee.getEmail());
    psInsertEmployee.setString(4, employee.getPhone());
    psInsertEmployee.setDate(5, employee.getDob());
    psInsertEmployee.setString(6, employee.getJobTitle());
    psInsertEmployee.setDouble(7, employee.getSalary());
    psInsertEmployee.setTimestamp(8, currentTimestamp);
    psInsertEmployee.setInt(9, employee.getDeptId());
    psInsertEmployee.setInt(10, employee.getOfficeId());
    psInsertEmployee.executeUpdate();
}

private void assignToProject(PreparedStatement psInsertProjectAssignment, int empId, int projectId) throws SQLException {
    psInsertProjectAssignment.setInt(1, empId);
    psInsertProjectAssignment.setInt(2, projectId);
    psInsertProjectAssignment.executeUpdate();
}

private void assignRandomSupervisor(PreparedStatement psGetRandomSupervisor, PreparedStatement psInsertSupervisor, int empId) throws SQLException {
    try (ResultSet rsSupervisor = psGetRandomSupervisor.executeQuery()) {
        if (rsSupervisor.next()) {
            int supervisorId = rsSupervisor.getInt("emp_id");
            psInsertSupervisor.setInt(1, empId);
            psInsertSupervisor.setInt(2, supervisorId);
            psInsertSupervisor.executeUpdate();
        } else {
            throw new SQLException("No existing employees found to assign as a supervisor.");
        }
    }
}

private void handleSQLException(SQLException e) {
    if (e.getErrorCode() == 1062) {
        JOptionPane.showMessageDialog(null, "User already exists with this ID.");
    } else {
        e.printStackTrace();
    }
}


public void updateEmployeeEmail(int empId, String newEmail) throws SQLException {
        String updateEmail = "UPDATE employees SET email = ? WHERE emp_id = ?";
        String checkEmail = "SELECT COUNT(*) FROM employees WHERE email = ?";

        try (Connection conn = DatabaseUtil.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement psCheckEmail = conn.prepareStatement(checkEmail);
                 PreparedStatement psUpdateEmail = conn.prepareStatement(updateEmail)) {

                psCheckEmail.setString(1, newEmail);
                ResultSet rs = psCheckEmail.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    conn.rollback();
                    throw new SQLException("Email already exists.");
                }

                psUpdateEmail.setString(1, newEmail);
                psUpdateEmail.setInt(2, empId);
                psUpdateEmail.executeUpdate();

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    public void deleteEmployee(int empId) throws SQLException {
        String checkProjects = "SELECT COUNT(*) FROM project_assignments WHERE emp_id = ?";
        String deleteEmployee = "DELETE FROM employees WHERE emp_id = ?";
        String updateDeptCount = "UPDATE departments SET employee_count = employee_count - 1 WHERE dept_id = (SELECT dept_id FROM employees WHERE emp_id = ?)";

        try (Connection conn = DatabaseUtil.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement psCheckProjects = conn.prepareStatement(checkProjects);
                 PreparedStatement psDeleteEmployee = conn.prepareStatement(deleteEmployee);
                 PreparedStatement psUpdateDeptCount = conn.prepareStatement(updateDeptCount)) {

                psCheckProjects.setInt(1, empId);
                ResultSet rs = psCheckProjects.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    conn.rollback();
                    throw new SQLException("Employee is assigned to active projects.");
                }

                psUpdateDeptCount.setInt(1, empId);
                psUpdateDeptCount.executeUpdate();

                psDeleteEmployee.setInt(1, empId);
                psDeleteEmployee.executeUpdate();

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    public void transferEmployee(int empId, int newDeptId) throws SQLException {
        String updateEmployeeDept = "UPDATE employees SET dept_id = ? WHERE emp_id = ?";
        String updateOldDeptCount = "UPDATE departments SET employee_count = employee_count - 1 WHERE dept_id = (SELECT dept_id FROM employees WHERE emp_id = ?)";
        String updateNewDeptCount = "UPDATE departments SET employee_count = employee_count + 1 WHERE dept_id = ?";

        try (Connection conn = DatabaseUtil.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement psUpdateEmployeeDept = conn.prepareStatement(updateEmployeeDept);
                 PreparedStatement psUpdateOldDeptCount = conn.prepareStatement(updateOldDeptCount);
                 PreparedStatement psUpdateNewDeptCount = conn.prepareStatement(updateNewDeptCount)) {

                psUpdateOldDeptCount.setInt(1, empId);
                psUpdateOldDeptCount.executeUpdate();

                psUpdateEmployeeDept.setInt(1, newDeptId);
                psUpdateEmployeeDept.setInt(2, empId);
                psUpdateEmployeeDept.executeUpdate();

                psUpdateNewDeptCount.setInt(1, newDeptId);
                psUpdateNewDeptCount.executeUpdate();

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    public ResultSet getAllEmployees() throws SQLException {
        
        String query = "SELECT e.*, d.dept_name as dept_name, p.project_name as project_name FROM employees e "
                + "LEFT JOIN departments d ON e.dept_id = d.dept_id "
                + "LEFT JOIN project_assignments pa ON e.emp_id = pa.emp_id "
                + "LEFT JOIN projects p ON pa.project_id = p.project_id";
        
        Connection conn = DatabaseUtil.getConnection();
        PreparedStatement ps = conn.prepareStatement(query);
        return ps.executeQuery();
    }
    
    public Map<String, Integer> fetchDepartment() {
    	Map<String, Integer> departments = new HashMap<>();

        Statement stmt = null;
        ResultSet rs = null;

        try(Connection conn = DatabaseUtil.getConnection()) {

            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT dept_id, dept_name FROM departments");

            // Retrieve department names from the result set and add them to the list
            while (rs.next()) {
            	int departmentId = rs.getInt("dept_id");
                String departmentName = rs.getString("dept_name");
               
				departments.put(departmentName, departmentId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return departments;
    }
    
    public Map<String, Integer> fetchProject() {
    	Map<String, Integer> projects = new HashMap<>();

        Statement stmt = null;
        ResultSet rs = null;

        try(Connection conn = DatabaseUtil.getConnection()) {

            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT project_id, project_name FROM projects");

            // Retrieve department names from the result set and add them to the list
            while (rs.next()) {
            	int projectId = rs.getInt("project_id");
                String projectName = rs.getString("project_name");
               
                projects.put(projectName, projectId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return projects;
    }
    
    public Map<String, Integer> fetchOffice() {
    	Map<String, Integer> offices = new HashMap<>();

        Statement stmt = null;
        ResultSet rs = null;

        try(Connection conn = DatabaseUtil.getConnection()) {

            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT office_id, location FROM offices");

            // Retrieve department names from the result set and add them to the list
            while (rs.next()) {
            	int officeId = rs.getInt("office_id");
                String location = rs.getString("location");
               
                offices.put(location, officeId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return offices;
    }
    
    // get all Employees in same Location
    public static String[] getEmpSameLoc() {
        Statement stmt = null;
        ResultSet rs = null;
        List<String> dataList = new ArrayList<>();

        try (Connection conn = DatabaseUtil.getConnection()) {
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT\r\n"
                    + "    A.name AS employee_name,\r\n"
                    + "    B.office_id AS office_id,\r\n"
                    + "    O.location AS office_loc\r\n"
                    + "FROM\r\n"
                    + "    Employees A\r\n"
                    + "    INNER JOIN Employees B ON A.office_id = B.office_id AND A.emp_id <> B.emp_id\r\n"
                    + "    LEFT JOIN Offices O ON A.office_id = O.office_id;");

            // Retrieve data from the result set and add them to the list
            while (rs.next()) {
                String empName = rs.getString("employee_name");
                String officeId = rs.getString("office_id");
                String location = rs.getString("office_loc");
                
                String rowData = empName + "," + officeId + "," + location;
                dataList.add(rowData);
                
            }
          
            rs.close();
    	    } catch (SQLException e) {
    	        e.printStackTrace();
    	    
    	    }
            String[] tbData = dataList.toArray(new String[0]);
            return tbData;
        }
        
    // get all Employees who are assigned to a project
    public static String[] getEmpWithAssignedProject()
        {
        	  Statement stmt = null;
        	  ResultSet rs = null;
        	  List<String> dataList = new ArrayList<>();
        	  
        	  try (Connection conn = DatabaseUtil.getConnection()) {
        	        stmt = conn.createStatement();
        	        rs = stmt.executeQuery("SELECT \r\n"
        	        		+ "    e.emp_id,\r\n"
        	        		+ "    e.name AS employee_name,\r\n"
        	        		+ "    p.project_id,\r\n"
        	        		+ "    p.project_name\r\n"
        	        		+ "FROM \r\n"
        	        		+ "    employees e\r\n"
        	        		+ "JOIN \r\n"
        	        		+ "    project_assignments pa ON e.emp_id = pa.emp_id\r\n"
        	        		+ "JOIN \r\n"
        	        		+ "    projects p ON pa.project_id = p.project_id");
        	   
    	        while (rs.next()) {
    	            String empId = rs.getString("emp_id");
    	            String empName = rs.getString("employee_name");
    	            String projectId = rs.getString("project_id");
    	            String projectName = rs.getString("project_name");
    	            
    	            System.out.println("empId : " + empId);
    	            
    	            
    	            String rowData = empId + "," + empName + "," + projectId + "," + projectName;
    	            dataList.add(rowData);
    	            
    	            }
    	        rs.close();
    		    } 
        	    catch (SQLException e) {
    		        e.printStackTrace();
    		    }
        	  String[] tbData = dataList.toArray(new String[0]);
              return tbData;
        }
       
    // get all Employee with salary more than 60k
    public static String[] getEmpWithSalaryMoreThan60000()
        {
        	  Statement stmt = null;
        	  ResultSet rs = null;
        	  List<String> dataList = new ArrayList<>();
        	  
        	  try (Connection conn = DatabaseUtil.getConnection()) {
        	        stmt = conn.createStatement();
        	        rs = stmt.executeQuery("SELECT e.emp_id,e.salary , e.name as employee_name, e.email, d.dept_name, o.location\r\n"
        	        		+ "FROM employees e\r\n"
        	        		+ "INNER JOIN departments d ON e.dept_id = d.dept_id\r\n"
        	        		+ "INNER JOIN offices o ON e.office_id = o.office_id\r\n"
        	        		+ "WHERE e.salary > 60000");
        	   
    	        while (rs.next()) {
    	            String empId = rs.getString("emp_id");
    	            String empSal = rs.getString("salary");
    	            String empName = rs.getString("employee_name");
    	            String empEmail = rs.getString("email");
    	            String deptName = rs.getString("dept_name");
    	            String officeLoc = rs.getString("location");
    	            
    	            System.out.println("empId : " + empId);
    	            
    	            
    	            String rowData = empId + ","  +  empSal + "," + empName + "," + empEmail + "," + deptName + "," + officeLoc;
    	            dataList.add(rowData);
    	            
    	            }
    	        rs.close();
    		    } 
        	    catch (SQLException e) {
    		        e.printStackTrace();
    		    }
        	  String[] tbData = dataList.toArray(new String[0]);
              return tbData;
        }
    
    public static String[] getEmpWithSuperVisors()
    {
    	  Statement stmt = null;
    	  ResultSet rs = null;
    	  List<String> dataList = new ArrayList<>();
    	  
    	  try (Connection conn = DatabaseUtil.getConnection()) {
    	        stmt = conn.createStatement();
    	        rs = stmt.executeQuery("SELECT e.emp_id AS emp_id,\r\n"
    	        		+ "       e.name AS emp_Name,\r\n"
    	        		+ "       s.emp_id AS supervisor_ID,\r\n"
    	        		+ "       s.name AS supervisor_name\r\n"
    	        		+ "FROM employees e\r\n"
    	        		+ "INNER JOIN supervisors sup ON e.emp_id = sup.emp_id\r\n"
    	        		+ "INNER JOIN employees s ON sup.supervisor_id = s.emp_id;\r\n"
    	        		+ "");
    	   
	        while (rs.next()) {
	            String empId = rs.getString("emp_id");
	            String empName = rs.getString("emp_name");
	            String superVname = rs.getString("supervisor_id");
	         
	            
	            System.out.println("empId : " + empId + "sup" + superVname);
	            
	            
	            String rowData = empId + ","  +  empName + "," + empName + "," + superVname ;
	            dataList.add(rowData);
	            
	            }
	        rs.close();
		    } 
    	    catch (SQLException e) {
		        e.printStackTrace();
		    }
    	  String[] tbData = dataList.toArray(new String[0]);
          return tbData;
    }

}
