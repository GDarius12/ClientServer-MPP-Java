package Darius.repository.database;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import Darius.domain.Event;
import Darius.repository.IEventRepository;
import Darius.domain.AgeGroup;
import Darius.domain.Sprint;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

public class EventDatabaseRepository implements IEventRepository {
    private JdbcUtils jdbcUtils;
    private static final Logger logger = LogManager.getLogger(EventDatabaseRepository.class);

    public EventDatabaseRepository(Properties properties) {
        logger.info("Initializam EventDatabaseRepository");
        jdbcUtils = new JdbcUtils(properties);
    }

    @Override
    public Optional<Event> findOne(Integer integer) {
        logger.traceEntry("Gaseste un event cu id {}", integer);

        Connection connection = jdbcUtils.getConnection();

        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT avs.id AS eventId, avs.sprintId, avs.date, " +
                        "s.id AS sprintId, s.distance, " +
                        "ag.id AS ageGroupId, ag.Lower, ag.Upper " +
                        "FROM events avs " +
                        "JOIN sprints s ON avs.sprintId = s.id " +
                        "JOIN eventDetails asd ON asd.eventId = avs.id " +
                        "JOIN ageGroups ag ON asd.ageGroupId = ag.id " +
                        "WHERE avs.id = ?")) {

            preparedStatement.setInt(1, integer);
            try (ResultSet result = preparedStatement.executeQuery()) {
                Event event = null;

                while (result.next()) {
                    int eventId = result.getInt("eventId");
                    int sprintId = result.getInt("sprintId");
                    float distance = result.getFloat("distance");

                    LocalDateTime dateTime = LocalDateTime.parse(result.getString("date"), Event.DATE_TIME_FORMATTER);

                    Sprint sprint = new Sprint(distance);
                    sprint.setId(sprintId);

                    AgeGroup ageGroup = new AgeGroup(
                            result.getInt("Lower"),
                            result.getInt("Upper")
                    );
                    ageGroup.setId(result.getInt("ageGroupId"));

                    if (event == null) {
                        event = new Event(sprint, new ArrayList<>(), dateTime);
                        event.setId(eventId);
                    }

                    event.getAgeGroups().add(ageGroup);
                }

                return Optional.ofNullable(event);
            }
        } catch (SQLException e) {
            logger.error(e);
        }

        return Optional.empty();
    }

    @Override
    public Iterable<Event> findAll() {
        logger.traceEntry("Gaseste toate eventurile");

        Connection connection = jdbcUtils.getConnection();

        List<Event> events = new ArrayList<>();

        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "select avs.id as eventId, avs.sprintId, avs.date, " +
                        "s.id as sprintId, s.distance, " +
                        "ag.id as ageGroupId, ag.Lower, ag.Upper " +
                        "from events avs " +
                        "join sprints s on avs.sprintId = s.id " +
                        "join eventDetails asd on asd.eventId = avs.id " +
                        "join ageGroups ag on asd.ageGroupId = ag.id")) {
            try (ResultSet result = preparedStatement.executeQuery()) {
                Map<Integer, Event> sprintMap = new HashMap<>();

                while (result.next()) {
                    int eventId = result.getInt("eventId");
                    int sprintId = result.getInt("sprintId");
                    float distance = result.getFloat("distance");
                    LocalDateTime dateTime = LocalDateTime.parse(result.getString("date"), Event.DATE_TIME_FORMATTER);

                    Sprint sprint = new Sprint(distance);
                    sprint.setId(sprintId);

                    AgeGroup ageGroup = new AgeGroup(
                            result.getInt("Lower"),
                            result.getInt("Upper")
                    );
                    ageGroup.setId(result.getInt("ageGroupId"));

                    Event event = sprintMap.get(eventId);

                    if (event == null) {
                        event = new Event(sprint, new ArrayList<>(), dateTime);
                        event.setId(eventId);
                        sprintMap.put(eventId, event);
                        events.add(event);
                    }

                    event.getAgeGroups().add(ageGroup);
                }
            }
        } catch (SQLException e) {
            logger.error(e);
        }

        return events;
    }
    @Override
    public Optional<Event> findOneBySprintId(int id) {
        logger.traceEntry("Gaseste toate eventurule dupa id-ul unui sprint {}", id);

        Connection connection = jdbcUtils.getConnection();

        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT avs.id AS eventId, avs.sprintId, avs.date, " +
                        "s.id AS sprintId, s.distance, " +
                        "ag.id AS ageGroupId, ag.Lower, ag.Upper " +
                        "FROM events avs " +
                        "JOIN sprints s ON avs.sprintId = s.id " +
                        "JOIN eventDetails asd ON asd.eventId = avs.id " +
                        "JOIN ageGroups ag ON asd.ageGroupId = ag.id " +
                        "WHERE s.id = ?")) {

            preparedStatement.setInt(1, id);
            try(ResultSet result = preparedStatement.executeQuery()){
                Event event = null;

                while (result.next()) {
                    int eventId = result.getInt("eventId");
                    int sprintId = result.getInt("sprintId");
                    float distance = result.getFloat("distance");

                    LocalDateTime dateTime = LocalDateTime.parse(result.getString("date"), Event.DATE_TIME_FORMATTER);

                    Sprint sprint = new Sprint(distance);
                    sprint.setId(sprintId);

                    AgeGroup ageGroup = new AgeGroup(
                            result.getInt("Lower"),
                            result.getInt("Upper")
                    );
                    ageGroup.setId(result.getInt("ageGroupId"));

                    if (event == null) {
                        event = new Event(sprint, new ArrayList<>(), dateTime);
                        event.setId(eventId);
                    }

                    event.getAgeGroups().add(ageGroup);
                }

                return Optional.ofNullable(event);
            }
        } catch (SQLException e) {
            logger.error(e);
        }

        return Optional.empty();
    }
    @Override
    public Iterable<Event> findAllByAgeGroupId(int id) {
        logger.traceEntry("Se cauta toate Eventurile dupa ageGroup id");

        Connection connection = jdbcUtils.getConnection();

        List<Event> eventuri = new ArrayList<>();

        try(PreparedStatement preparedStatement = connection.prepareStatement(
                "select avs.id as eventId, avs.sprintId, avs.date, " +
                        "s.id as sprintId, s.distance, " +
                        "ag.id as ageGroupId, ag.Lower, ag.Upper " +
                        "from events avs " +
                        "join sprints s on avs.sprintId = s.id " +
                        "join eventDetails asd on asd.eventId = avs.id " +
                        "join ageGroups ag on asd.ageGroupId = ag.id " +
                        "where ag.id = ?")){
            preparedStatement.setInt(1, id);
            try(ResultSet result = preparedStatement.executeQuery()){
                Map<Integer, Event> sprintMap = new HashMap<>();

                while(result.next()){
                    int eventid = result.getInt("eventId");
                    int sprintId = result.getInt("sprintId");
                    float distance = result.getFloat("distance");
                    LocalDateTime dateTime = LocalDateTime.parse(result.getString("date"), Event.DATE_TIME_FORMATTER);

                    Sprint sprint = new Sprint(distance);
                    sprint.setId(sprintId);

                    AgeGroup ageGroup = new AgeGroup(
                            result.getInt("Lower"),
                            result.getInt("Upper")
                    );
                    ageGroup.setId(result.getInt("ageGroupId"));

                    Event events = sprintMap.get(eventid);

                    if (events == null){
                        events = new Event(sprint, new ArrayList<>(), dateTime);
                        events.setId(eventid);
                        sprintMap.put(eventid, events);
                        eventuri.add(events);
                    }

                    events.getAgeGroups().add(ageGroup);
                }
            }
        } catch (SQLException e) {
            logger.error(e);
        }

        return eventuri;
    }
    @Override
    public void save(Event entity) {
        return;
    }

    @Override
    public void delete(Integer integer) {
        return;
    }

    @Override
    public void update(Event entity) {
        return;
    }

}
