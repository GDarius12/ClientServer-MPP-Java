package Darius.repository.database;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import Darius.domain.Employee;
import Darius.repository.IEmployeeRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.Properties;

public class EmployeeDatabaseRepository implements IEmployeeRepository {
    private JdbcUtils jdbcUtils;
    private Logger logger = LogManager.getLogger(EmployeeDatabaseRepository.class);

    public EmployeeDatabaseRepository(Properties properties){
        logger.info("Initializam EmployeeDatabaseRepository cu proprietatile: {}", properties);

        jdbcUtils = new JdbcUtils(properties);
    }

    @Override
    public Optional<Employee> findOne(Integer integer) {
        logger.traceEntry("Se cauta un copil cu id: {}", integer);

        Connection connection = jdbcUtils.getConnection();

        Employee employee = null;

        try(PreparedStatement preparedStatement = connection.prepareStatement("select * from employees where id = ?")){
            preparedStatement.setInt(1, integer);
            try(ResultSet result = preparedStatement.executeQuery()){
                if (result.next()){
                    employee = getEmployee(result);
                }
            }
        } catch (SQLException e) {
            logger.error(e);
        }

        logger.traceExit();
        return Optional.ofNullable(employee);
    }
    private Employee getEmployee(ResultSet result) throws SQLException {
        int id = result.getInt("id");
        String name = result.getString("name");
        String surname = result.getString("surname");
        String cnp = result.getString("cnp");
        String Phone = result.getString("Phone");
        String address = result.getString("address");
        String username = result.getString("username");
        String password = result.getString("password");

        Employee employee = new Employee(name, surname, cnp, Phone, address, username, password);
        employee.setId(id);

        return employee;
    }

    @Override
    public Optional<Employee> findOneByUsername(String username) {
        logger.traceEntry("Se cauta un copil cu username: {}", username);

        Connection connection = jdbcUtils.getConnection();

        Employee employee = null;

        try(PreparedStatement preparedStatement = connection.prepareStatement("select * from employees where username = ?")){
            preparedStatement.setString(1, username);
            try(ResultSet result = preparedStatement.executeQuery()){
                if (result.next()){
                    employee = getEmployee(result);
                }
            }
        } catch (SQLException e) {
            logger.error(e);
        }

        logger.traceExit();
        return Optional.of(employee);
    }
    @Override
    public Iterable<Employee> findAll() {
        return null;
    }

    @Override
    public void save(Employee entity) {
        return;
    }

    @Override
    public void delete(Integer integer) {
        return;
    }

    @Override
    public void update(Employee entity) {
        return;
    }


}
