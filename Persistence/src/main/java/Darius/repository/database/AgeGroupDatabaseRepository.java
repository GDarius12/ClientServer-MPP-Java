package Darius.repository.database;

import org.apache.logging.log4j.LogManager;
import Darius.domain.AgeGroup;
import Darius.repository.IAgeGroupRepository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import Darius.domain.AgeGroup;
import Darius.repository.IAgeGroupRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
public class AgeGroupDatabaseRepository implements IAgeGroupRepository {
    private JdbcUtils jdbcUtils;
    private static final Logger logger = LogManager.getLogger(AgeGroupDatabaseRepository.class);

    public AgeGroupDatabaseRepository(Properties properties){
        jdbcUtils = new JdbcUtils(properties);
    }

    @Override
    public Optional<AgeGroup> findOne(Integer integer) {
        return Optional.empty();
    }

    @Override
    public Iterable<AgeGroup> findAll() {

        logger.traceEntry("Gaseste toate AgeGroup-urile");

        List<AgeGroup> ageGroups = new ArrayList<>();

        Connection connection = jdbcUtils.getConnection();

        AgeGroup ageGroup = null;

        try(PreparedStatement preparedStatement = connection.prepareStatement("select * from ageGroups")){
            try(ResultSet resultSet = preparedStatement.executeQuery()){
                while (resultSet.next()){
                    ageGroup = getAgeGroup(resultSet);

                    ageGroups.add(ageGroup);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return ageGroups;
    }
    @Override
    public Optional<AgeGroup> findOneByAge(int age) {
        AgeGroup ageGroup = null;

        Connection connection = jdbcUtils.getConnection();

        try(PreparedStatement preparedStatement = connection.prepareStatement("select * from ageGroups where Lower <= ? and ? <= Upper")){
            preparedStatement.setInt(1, age);
            preparedStatement.setInt(2, age);

            try(ResultSet resultSet = preparedStatement.executeQuery()){
                if (resultSet.next()){
                    ageGroup = getAgeGroup(resultSet);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.ofNullable(ageGroup);
    }
    private AgeGroup getAgeGroup(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        int lower = resultSet.getInt("Lower");
        int upper = resultSet.getInt("Upper");

        AgeGroup ageGroup = new AgeGroup(lower, upper);
        ageGroup.setId(id);

        return ageGroup;
    }
    @Override
    public void save(AgeGroup entity) {
        return;
    }

    @Override
    public void delete(Integer integer) {
        return;
    }

    @Override
    public void update(AgeGroup entity) {
        return;
    }

}
