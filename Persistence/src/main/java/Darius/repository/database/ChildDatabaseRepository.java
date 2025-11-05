package Darius.repository.database;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import Darius.domain.Child;
import Darius.repository.IChildRepository;
import Darius.repository.exception.RepositoryException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class ChildDatabaseRepository implements IChildRepository {

    private JdbcUtils jdbcUtils;
    private static final Logger logger = LogManager.getLogger(ChildDatabaseRepository.class);

    public ChildDatabaseRepository(Properties properties){
        logger.info("Initializam ChildDatabaseRepository cu proprietatile: {}", properties);

        jdbcUtils = new JdbcUtils(properties);
    }

    @Override
    public Optional<Child> findOne(Integer integer) {
        logger.traceEntry("Gaseste un copil cu id: {}", integer);

        Connection connection = jdbcUtils.getConnection();

        Child child = null;

        try(PreparedStatement preparedStatement = connection.prepareStatement("select * from children where id = ?")){

            preparedStatement.setInt(1, integer);
            try(ResultSet result = preparedStatement.executeQuery()){
                if (result.next()){
                    child = getChild(result);
                }
            }
        } catch (SQLException e) {
            logger.error(e);
        }

        logger.traceExit();
        return Optional.ofNullable(child);
    }
    @Override
    public Optional<Child> findOneByCnp(String cnp) {
        logger.traceEntry("Finding the child with cnp {}", cnp);

        Connection connection = jdbcUtils.getConnection();

        Child child = null;

        try(PreparedStatement preparedStatement = connection.prepareStatement("select * from children where cnp = ?")){
            preparedStatement.setString(1, cnp);
            try(ResultSet result = preparedStatement.executeQuery()){
                if (result.next()){
                    child = getChild(result);
                }
            }
        } catch (SQLException e) {
            logger.error(e);
        }

        logger.traceExit();
        return Optional.ofNullable(child);
    }

    private Child getChild(ResultSet result) throws SQLException {
        int id = result.getInt("id");
        String name = result.getString("name");
        String surname = result.getString("surname");
        String cnp = result.getString("cnp");

        Child child = new Child(name, surname, cnp);
        child.setId(id);
        return child;
    }
    @Override
    public Iterable<Child> findAll() {
        logger.traceEntry("Gaseste toti copiii");

        Connection connection = jdbcUtils.getConnection();

            List<Child> copil = new ArrayList<>();

        try(PreparedStatement preparedStatement = connection.prepareStatement("select * from children")){
            try(ResultSet result = preparedStatement.executeQuery()){
                while (result.next()){
                    Child child = getChild(result);

                    copil.add(child);
                }
            }
        } catch (SQLException e) {
            logger.error(e);
        }

        logger.traceExit();
        return copil;
    }

    @Override
    public void save(Child entity) {
        logger.traceEntry("Salvam un copil {}", entity);


        var child = findOneByCnp(entity.getCnp());
        if (child.isPresent()){
            throw new RepositoryException("Exista deja o persoana cu acest cnp!");
        }

        Connection connection = jdbcUtils.getConnection();
        try(PreparedStatement preparedStatement = connection.prepareStatement("insert into children(name, surname, cnp) values (?, ?, ?)")){
            preparedStatement.setString(1, entity.getName());
            preparedStatement.setString(2, entity.getSurname());
            preparedStatement.setString(3, entity.getCnp());

            int result = preparedStatement.executeUpdate();
            logger.trace("Am salvat {} instante", result);
        } catch (SQLException e) {
            logger.error(e);
        }

        logger.traceExit();
    }


    @Override
    public void delete(Integer integer) {
        logger.traceEntry("Stergem copilul cu id: {}", integer);

        Connection connection = jdbcUtils.getConnection();

        try(PreparedStatement preparedStatement = connection.prepareStatement("delete from children where id = ?")){
            preparedStatement.setInt(1, integer);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error(e);
        }

        logger.traceExit();
    }

    @Override
    public void update(Child entity) {
        logger.traceEntry("Modificam copilul {}", entity);

        var child = findOneByCnp(entity.getCnp());
        if (child.isPresent()){
            if (!Objects.equals(child.get().getId(), entity.getId())){
                throw new RepositoryException("Exista deja un copil cu acest cnp!");
            }
        }

        Connection connection = jdbcUtils.getConnection();
        try(PreparedStatement preparedStatement = connection.prepareStatement("update children set name = ?, surname = ?, cnp = ? where id = ?")){
            preparedStatement.setString(1, entity.getName());
            preparedStatement.setString(2, entity.getSurname());
            preparedStatement.setString(3, entity.getCnp());
            preparedStatement.setInt(4, entity.getId());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error(e);
        }

        logger.traceExit();
    }


}
