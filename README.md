# Database Schema and Triggers

## Table Schemas

### Departments Table
```sql
CREATE TABLE departments (
    dept_id INT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    employee_count INT DEFAULT 0
);
```

### Employees Table

```sql
CREATE TABLE employees (
    emp_id INT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    dept_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (dept_id) REFERENCES departments(dept_id) ON DELETE SET NULL
);
```

### Project Assignments Table

```sql
CREATE TABLE project_assignments (
    project_id INT,
    emp_id INT,
    PRIMARY KEY (project_id, emp_id),
    FOREIGN KEY (emp_id) REFERENCES employees(emp_id)
);
```

### Adding isActive Column

```sql
ALTER TABLE project_assignments
ADD COLUMN isActive INT DEFAULT 1;
```

### Deleted Employees Table

```sql

CREATE TABLE deleted_employees (
    emp_id INT,
    name VARCHAR(100),
    email VARCHAR(100),
    dept_id INT,
    deleted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

Referential Integrity Constraints
Several referential integrity constraints are used to maintain the relationships between tables. These constraints ensure that data integrity is maintained and that foreign key values in child tables reference valid primary key values in parent tables.

## Triggers

To view triggers in the database:

- [ ] SHOW TRIGGERS;


Unique Email Trigger
Ensures that no duplicate email addresses are inserted into the employees table.

```sql
DELIMITER //

CREATE TRIGGER unique_email BEFORE INSERT ON employees
FOR EACH ROW
BEGIN
  DECLARE count INT;
  SELECT COUNT(*) INTO count FROM employees WHERE email = NEW.email;
  IF count > 0 THEN
    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Duplicate email address';
  END IF;
END;
//

DELIMITER ;
```

### Update Employee Count Trigger
Increases the employee_count in the departments table when a new employee is added.

```sql
DELIMITER //

CREATE TRIGGER update_employee_count AFTER INSERT ON employees
FOR EACH ROW
BEGIN
  IF NEW.dept_id IS NOT NULL THEN
    UPDATE departments SET employee_count = employee_count + 1 WHERE dept_id = NEW.dept_id;
  END IF;
END;
//

DELIMITER ;
```
### Decrease Employee Count Trigger
Decreases the employee_count in the departments table when an employee is deleted.

```sql
DELIMITER //

CREATE TRIGGER decrease_employee_count AFTER DELETE ON employees
FOR EACH ROW
BEGIN
  IF OLD.dept_id IS NOT NULL THEN
    UPDATE departments SET employee_count = employee_count - 1 WHERE dept_id = OLD.dept_id;
  END IF;
END;
//

DELIMITER ;
```

### Log Deleted Employees Trigger
Logs details of deleted employees into the deleted_employees table.

```sql
DELIMITER //

CREATE TRIGGER log_deleted_employees AFTER DELETE ON employees
FOR EACH ROW
BEGIN
  INSERT INTO deleted_employees (emp_id, name, email, dept_id)
  VALUES (OLD.emp_id, OLD.name, OLD.email, OLD.dept_id);
END;
//

DELIMITER ;
```

## Normalization

Normalization is a way of organizing data in a database to reduce redundancy (duplicate data) and ensure data integrity. 
[cleaning up your data so it's neat, tidy, and efficient to use.]

First Normal Form (1NF)

    Concept: Ensure that each column contains atomic (indivisible) values.
    
    Implementation:
    All tables (departments, employees, project_assignments, deleted_employees) have atomic columns.

Second Normal Form (2NF)

    Concept: Ensure that the table is in 1NF and all non-key attributes are fully functionally dependent on the primary key.
    
    Implementation:
    **Employees Table** :emp_id is the primary key. name, email, dept_id, created_at, and updated_at depend on emp_id.
    **Project Assignments Table**: Composite primary key (project_id, emp_id). isActive depends on the composite key.
    **Deleted Employees Table**: No primary key, but emp_id, name, email, dept_id, and deleted_at are attributes of deleted employees.
