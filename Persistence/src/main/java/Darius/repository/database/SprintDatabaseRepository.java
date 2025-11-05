package Darius.repository.database;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import Darius.domain.Sprint;
import Darius.repository.ISprintRepository;
import Darius.repository.exception.RepositoryException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

@Component
public class SprintDatabaseRepository implements ISprintRepository {
    private JdbcUtils jdbcUtils;
    private static final Logger loggger = LogManager.getLogger(SprintDatabaseRepository.class);

    @Autowired
    public SprintDatabaseRepository(Properties properties) {
        jdbcUtils = new JdbcUtils(properties);
    }

    @Override
    public Optional<Sprint> findOne(Integer integer) {
        loggger.traceEntry("Cautam Sprintul cu id: {}", integer);

        Connection connection = jdbcUtils.getConnection();
        Sprint sprint = null;
        try (PreparedStatement preparedStatement = connection.prepareStatement("select * from sprints where id = ?")) {
            preparedStatement.setInt(1, integer);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    sprint = getSprint(resultSet);
                }
            }
        } catch (SQLException e) {
            loggger.error(e);
        }

        loggger.traceExit();
        return Optional.ofNullable(sprint);
    }

    @Override
    public Iterable<Sprint> findAll() {
        loggger.traceEntry("Se cauta toate Sprinturile");

        Connection connection = jdbcUtils.getConnection();

        List<Sprint> sprints = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement("select * from sprints")) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Sprint sprint = getSprint(resultSet);

                    sprints.add(sprint);
                }
            }
        } catch (SQLException e) {
            loggger.error(e);
        }
        loggger.traceExit();
        return sprints;
    }

    @Override
    public void save(Sprint entity) {
        loggger.traceEntry("Salvam un Sprint {}", entity);

        if (entity == null) {
            throw new IllegalArgumentException("The entity can not be null!");
        }

        Connection connection = jdbcUtils.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement("insert into sprints(distance) values (?)")) {
            preparedStatement.setFloat(1, entity.getDistance());

            int result = preparedStatement.executeUpdate();
            loggger.trace("Saved {} instances", result);
        } catch (SQLException e) {
            loggger.error(e);
        }

        loggger.traceExit();
    }

    @Override
    public void delete(Integer integer) {
        loggger.traceEntry("Se sterge Sprintul cu id: {}", integer);

        Connection connection = jdbcUtils.getConnection();

        if (findOne(integer).isEmpty()) {
            throw new RepositoryException("Sprint nor found!\n");
        }

        try (PreparedStatement preparedStatement = connection.prepareStatement("delete from sprints where id = ?")) {
            preparedStatement.setInt(1, integer);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            loggger.error(e);
        }

        loggger.traceExit();
    }

    @Override
    public void update(Sprint entity) {
        loggger.traceEntry("Se face update la sprint {}", entity);

        if (entity == null) {
            throw new IllegalArgumentException("The entity can not be null!");
        }
        if (findOne(entity.getId()).isEmpty()) {
            throw new RepositoryException("Sprint not found!\n");
        }
        Connection connection = jdbcUtils.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement("update sprints set distance = ? where id = ?")) {
            preparedStatement.setFloat(1, entity.getDistance());
            preparedStatement.setInt(2, entity.getId());

            int result = preparedStatement.executeUpdate();
            loggger.trace("Saved {} instances", result);
        } catch (SQLException e) {
            loggger.error(e);
        }

        loggger.traceExit();
    }

    private Sprint getSprint(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        float distance = resultSet.getFloat("distance");

        Sprint sprint = new Sprint(distance);
        sprint.setId(id);
        return sprint;
    }

    @Override
    public Sprint SprintSave(Sprint sprint) {
        loggger.traceEntry("Salvam un sprint {}", sprint);

        if (sprint == null) {
            throw new IllegalArgumentException("The entity can not be null!");
        }
        Connection connection = jdbcUtils.getConnection();

        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO sprints(distance) VALUES (?)")) {
            preparedStatement.setFloat(1, sprint.getDistance());

            int result = preparedStatement.executeUpdate();
            loggger.trace("Saved {} instances", result);

            if (result > 0) {
                try (Statement stmt = connection.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT last_insert_rowid()")) {
                    if (rs.next()) {
                        int id = rs.getInt(1);
                        sprint.setId(id);
                        loggger.trace("Generated id {}", id);
                    }
                }
            }
        } catch (SQLException e) {
            loggger.error(e);
        }
        return loggger.traceExit(sprint);
    }
}