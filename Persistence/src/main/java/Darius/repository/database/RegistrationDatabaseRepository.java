package Darius.repository.database;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import Darius.domain.*;
import Darius.repository.IRegistrationRepository;
import Darius.repository.exception.RepositoryException;
import Darius.utils.Calculeaza_Varsta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

public class RegistrationDatabaseRepository implements IRegistrationRepository {
    private JdbcUtils jdbcUtils;
    private Logger logger = LogManager.getLogger(RegistrationDatabaseRepository.class);

    public RegistrationDatabaseRepository(Properties properties){
        logger.traceEntry("Se initializeaza RegistrationDatabaseRepository cu proprietatile: {}", properties);

        jdbcUtils = new JdbcUtils(properties);
    }

    @Override
    public Optional<Registration> findOne(Integer integer) {
        logger.traceEntry("Cauta o inregistrare dupa id");

        Connection connection = jdbcUtils.getConnection();

        try(PreparedStatement preparedStatement = connection.prepareStatement(
                "select r.id as registrationId, r.personId, r.date, " +
                        "c.id as childId, c.name, c.surname, c.cnp, " +
                        "avs.id as eventId, avs.date as sprintDate, " +
                        "s.id as sprintId, s.distance, " +
                        "ag.id as ageGroupId, ag.Lower, ag.Upper " +
                        "from registrations r " +
                        "join children c on r.personId = c.id " +
                        "join registrationDetails rd on r.id = rd.registrationId " +
                        "join events avs on rd.eventId = avs.id " +
                        "join sprints s on avs.sprintId = s.id " +
                        "join eventDetails asd on asd.eventId = avs.id " +
                        "join ageGroups ag on asd.ageGroupId = ag.id " +
                        "where r.id = ?"
        )){
            preparedStatement.setInt(1, integer);
            try(ResultSet result = preparedStatement.executeQuery()){
                Registration registration = null;
                while(result.next()){
                    int registrationId = result.getInt("registrationId");
                    int childId = result.getInt("childId");
                    String name = result.getString("name");
                    String surname = result.getString("surname");
                    String cnp = result.getString("cnp");
                    LocalDateTime registrationDate = LocalDateTime.parse(result.getString("date"), Registration.DATE_TIME_FORMATTER);

                    Person person = new Child(name, surname, cnp);
                    person.setId(childId);


                    if (registration == null) {
                        registration = new Registration(person, new ArrayList<>(), registrationDate);
                        registration.setId(registrationId);
                    }

                    Sprint sprint = new Sprint(result.getFloat("distance"));
                    sprint.setId(result.getInt("sprintId"));

                    int eventId = result.getInt("eventId");
                    Event event = registration.getEvents().stream()
                            .filter(s -> s.getId().equals(eventId))
                            .findFirst()
                            .orElse(null);

                    if (event == null) {
                        event = new Event(
                                sprint,
                                new ArrayList<>(),
                                LocalDateTime.parse(result.getString("sprintDate"), Event.DATE_TIME_FORMATTER)
                        );
                        event.setId(eventId);
                        registration.getEvents().add(event);
                    }

                    AgeGroup ageGroup = new AgeGroup(
                            result.getInt("Lower"),
                            result.getInt("Upper")
                    );
                    ageGroup.setId(result.getInt("ageGroupId"));

                    if (!event.getAgeGroups().contains(ageGroup)) {
                        event.getAgeGroups().add(ageGroup);
                    }
                }

                return Optional.ofNullable(registration);
            }
        } catch (SQLException e) {
            logger.error(e);
        }

        return Optional.empty();
    }

    @Override
    public Iterable<Registration> findAll() {
        logger.traceEntry("Gaseste toate inregistrarile");

        List<Registration> registrations = new ArrayList<>();

        Connection connection = jdbcUtils.getConnection();

        try(PreparedStatement preparedStatement = connection.prepareStatement(
                "select r.id as registrationId, r.personId, r.date, " +
                        "c.id as childId, c.name, c.surname, c.cnp, " +
                        "avs.id as eventId, avs.date as sprintDate, " +
                        "s.id as sprintId, s.distance, " +
                        "ag.id as ageGroupId, ag.Lower, ag.Upper " +
                        "from registrations r " +
                        "join children c on r.personId = c.id " +
                        "join registrationDetails rd on r.id = rd.registrationId " +
                        "join events avs on rd.eventId = avs.id " +
                        "join sprints s on avs.sprintId = s.id " +
                        "join eventDetails asd on asd.eventId = avs.id " +
                        "join ageGroups ag on asd.ageGroupId = ag.id")){
            try(ResultSet result = preparedStatement.executeQuery()){
                Map<Integer, Registration> registrationMap = new HashMap<>();

                while(result.next()){
                    int registrationId = result.getInt("registrationId");
                    int childId = result.getInt("childId");
                    String name = result.getString("name");
                    String surname = result.getString("surname");
                    String cnp = result.getString("cnp");
                    LocalDateTime registrationDate = LocalDateTime.parse(result.getString("date"), Registration.DATE_TIME_FORMATTER);

                    Person person = new Child(name, surname, cnp);
                    person.setId(childId);

                    Registration registration = registrationMap.get(registrationId);
                    if (registration == null) {
                        registration = new Registration(person, new ArrayList<>(), registrationDate);
                        registration.setId(registrationId);
                        registrationMap.put(registrationId, registration);
                        registrations.add(registration);
                    }

                    Sprint sprint = new Sprint(result.getFloat("distance"));
                    sprint.setId(result.getInt("sprintId"));

                    int eventId = result.getInt("eventId");
                    Event event = registration.getEvents().stream()
                            .filter(s -> s.getId().equals(eventId))
                            .findFirst()
                            .orElse(null);

                    if (event == null) {
                        event = new Event(
                                sprint,
                                new ArrayList<>(),
                                LocalDateTime.parse(result.getString("sprintDate"), Event.DATE_TIME_FORMATTER)
                        );
                        event.setId(eventId);
                        registration.getEvents().add(event);
                    }

                    AgeGroup ageGroup = new AgeGroup(
                            result.getInt("Lower"),
                            result.getInt("Upper")
                    );
                    ageGroup.setId(result.getInt("ageGroupId"));

                    if (!event.getAgeGroups().contains(ageGroup)) {
                        event.getAgeGroups().add(ageGroup);
                    }
                }
            }
        } catch (SQLException e) {
            logger.error(e);
        }

        return registrations;
    }

    @Override
    public void save(Registration entity) {
        logger.traceEntry("Saving child {}", entity);

        int age = Calculeaza_Varsta.Varsta_CNP(entity.getPerson().getCnp());
        for (var events : entity.getEvents()){
            boolean ok = false;
            for (var ageGroup : events.getAgeGroups()){
                if (ageGroup.getLower() <= age && ageGroup.getUpper() >= age) {
                    ok = true;
                    break;
                }
            }
            if (!ok){
                throw new RepositoryException("Aceasta persoana nu poate participa la acest sprint!");
            }
        }

        var registrationExist = findOneByPerson(entity.getPerson());
        if (registrationExist.isPresent()){
            if (registrationExist.get().getEvents().size() == Registration.numberOfEvents) {
                throw new RepositoryException("Aceasta persoana face deja parte din 2 sprinturi!");
            }
        }

        Connection connection = jdbcUtils.getConnection();
        try{
            int registrationId = 0;
            try(PreparedStatement preparedStatement = connection.prepareStatement("insert into registrations(personId, date) values (?, ?) returning id")) {
                logger.traceEntry("Saving registration table");
                preparedStatement.setInt(1, entity.getPerson().getId());
                preparedStatement.setString(2, String.valueOf(entity.getDateTime().format(Registration.DATE_TIME_FORMATTER)));
                try(ResultSet result = preparedStatement.executeQuery()){
                    if (result.next()){
                        registrationId = result.getInt("id");
                    }
                }
            }

            try(PreparedStatement preparedStatement = connection.prepareStatement("insert into registrationDetails(registrationId, eventId) values (?, ?)")){
                logger.trace("Saving in registrationDetails table");
                preparedStatement.setInt(1, registrationId);
                for (var events : entity.getEvents()){
                    preparedStatement.setInt(2, events.getId());
                    preparedStatement.executeUpdate();
                }
            }

        } catch (SQLException e) {
            logger.error("Error during save: ", e);
            throw new RepositoryException("Eroare la salvarea în baza de date: " + e.getMessage());
        }

    }
    @Override
    public Optional<Registration> findOneByPerson(Person person) {
        logger.traceEntry("Gaseste o inregistrare dupa o persoana");

        Connection connection = jdbcUtils.getConnection();

        try(PreparedStatement preparedStatement = connection.prepareStatement(
                "select r.id as registrationId, r.personId, r.date, " +
                        "c.id as childId, c.name, c.surname, c.cnp, " +
                        "avs.id as eventId, avs.date as sprintDate, " +
                        "s.id as sprintId, s.distance, " +
                        "ag.id as ageGroupId, ag.Lower, ag.Upper " +
                        "from registrations r " +
                        "join children c on r.personId = c.id " +
                        "join registrationDetails rd on r.id = rd.registrationId " +
                        "join events avs on rd.eventId = avs.id " +
                        "join sprints s on avs.sprintId = s.id " +
                        "join eventDetails asd on asd.eventId = avs.id " +
                        "join ageGroups ag on asd.ageGroupId = ag.id " +
                        "where c.id = ?"
        )){
            preparedStatement.setInt(1, person.getId());
            try(ResultSet result = preparedStatement.executeQuery()){
                Registration registration = null;
                while(result.next()){
                    int registrationId = result.getInt("registrationId");
                    int childId = result.getInt("childId");
                    String name = result.getString("name");
                    String surname = result.getString("surname");
                    String ccnp = result.getString("cnp");
                    LocalDateTime registrationDate = LocalDateTime.parse(result.getString("date"), Registration.DATE_TIME_FORMATTER);

                    Person person1 = new Child(name, surname, ccnp);
                    person1.setId(childId);


                    if (registration == null) {
                        registration = new Registration(person1, new ArrayList<>(), registrationDate);
                        registration.setId(registrationId);
                    }

                    Sprint sprint = new Sprint(result.getFloat("distance"));
                    sprint.setId(result.getInt("sprintId"));

                    int eventId = result.getInt("eventId");
                    Event event = registration.getEvents().stream()
                            .filter(s -> s.getId().equals(eventId))
                            .findFirst()
                            .orElse(null);

                    if (event == null) {
                        event = new Event(
                                sprint,
                                new ArrayList<>(),
                                LocalDateTime.parse(result.getString("sprintDate"), Event.DATE_TIME_FORMATTER)
                        );
                        event.setId(eventId);
                        registration.getEvents().add(event);
                    }

                    AgeGroup ageGroup = new AgeGroup(
                            result.getInt("Lower"),
                            result.getInt("Upper")
                    );
                    ageGroup.setId(result.getInt("ageGroupId"));

                    if (!event.getAgeGroups().contains(ageGroup)) {
                        event.getAgeGroups().add(ageGroup);
                    }
                }

                return Optional.ofNullable(registration);
            }
        } catch (SQLException e) {
            logger.error(e);
        }

        return Optional.empty();
    }

    @Override
    public Optional<Registration> findOneByEvent(Event event) {
        logger.traceEntry("Gaseste o inregistrare dupa un event");

        Connection connection = jdbcUtils.getConnection();

        try(PreparedStatement preparedStatement = connection.prepareStatement(
                "select r.id as registrationId, r.personId, r.date, " +
                        "c.id as childId, c.name, c.surname, c.cnp, " +
                        "avs.id as eventId, avs.date as sprintDate, " +
                        "s.id as sprintId, s.distance, " +
                        "ag.id as ageGroupId, ag.Lower, ag.Upper " +
                        "from registrations r " +
                        "join children c on r.personId = c.id " +
                        "join registrationDetails rd on r.id = rd.registrationId " +
                        "join events avs on rd.eventId = avs.id " +
                        "join sprints s on avs.sprintId = s.id " +
                        "join eventDetails asd on asd.eventId = avs.id " +
                        "join ageGroups ag on asd.ageGroupId = ag.id " +
                        "where avs.id = ?"
        )){
            preparedStatement.setInt(1, event.getId());
            try(ResultSet result = preparedStatement.executeQuery()){
                Registration registration = null;
                while(result.next()){
                    int registrationId = result.getInt("registrationId");
                    int childId = result.getInt("childId");
                    String name = result.getString("name");
                    String surname = result.getString("surname");
                    String ccnp = result.getString("cnp");
                    LocalDateTime registrationDate = LocalDateTime.parse(result.getString("date"), Registration.DATE_TIME_FORMATTER);

                    Person person1 = new Child(name, surname, ccnp);
                    person1.setId(childId);


                    if (registration == null) {
                        registration = new Registration(person1, new ArrayList<>(), registrationDate);
                        registration.setId(registrationId);
                    }

                    Sprint sprint = new Sprint(result.getFloat("distance"));
                    sprint.setId(result.getInt("sprintId"));

                    int eventId = result.getInt("eventId");
                    Event event1 = registration.getEvents().stream()
                            .filter(s -> s.getId().equals(eventId))
                            .findFirst()
                            .orElse(null);

                    if (event1 == null) {
                        event1 = new Event(
                                sprint,
                                new ArrayList<>(),
                                LocalDateTime.parse(result.getString("sprintDate"), Event.DATE_TIME_FORMATTER)
                        );
                        event1.setId(eventId);
                        registration.getEvents().add(event1);
                    }

                    AgeGroup ageGroup = new AgeGroup(
                            result.getInt("Lower"),
                            result.getInt("Upper")
                    );
                    ageGroup.setId(result.getInt("ageGroupId"));

                    if (!event1.getAgeGroups().contains(ageGroup)) {
                        event1.getAgeGroups().add(ageGroup);
                    }
                }

                return Optional.ofNullable(registration);
            }
        } catch (SQLException e) {
            logger.error(e);
        }

        return Optional.empty();
    }

    @Override
    public Iterable<Registration> findAllBySprintAndGroupAge(Sprint sprint, AgeGroup ageGroup) {
        logger.traceEntry("Gaseste toate inregistrarile dupa sprint si age group");

        List<Registration> registrations = new ArrayList<>();

        Connection connection = jdbcUtils.getConnection();

        try(PreparedStatement preparedStatement = connection.prepareStatement(
                "select r.id as registrationId, r.personId, r.date, " +
                        "c.id as childId, c.name, c.surname, c.cnp, " +
                        "avs.id as eventId, avs.date as sprintDate, " +
                        "s.id as sprintId, s.distance, " +
                        "ag.id as ageGroupId, ag.Lower, ag.Upper " +
                        "from registrations r " +
                        "join children c on r.personId = c.id " +
                        "join registrationDetails rd on r.id = rd.registrationId " +
                        "join events avs on rd.eventId = avs.id " +
                        "join sprints s on avs.sprintId = s.id " +
                        "join eventDetails asd on asd.eventId = avs.id " +
                        "join ageGroups ag on asd.ageGroupId = ag.id " +
                        "where s.id = ? and ag.id = ?")){
            preparedStatement.setInt(1, sprint.getId());
            preparedStatement.setInt(2, ageGroup.getId());
            try(ResultSet result = preparedStatement.executeQuery()){
                Map<Integer, Registration> registrationMap = new HashMap<>();

                while(result.next()) {
                    String cnp = result.getString("cnp");
                    int age = Calculeaza_Varsta.Varsta_CNP(cnp);
                    if (age >= ageGroup.getLower() && age <= ageGroup.getUpper()) {
                        int registrationId = result.getInt("registrationId");
                        int childId = result.getInt("childId");
                        String name = result.getString("name");
                        String surname = result.getString("surname");
                        LocalDateTime registrationDate = LocalDateTime.parse(result.getString("date"), Registration.DATE_TIME_FORMATTER);

                        Person person = new Child(name, surname, cnp);
                        person.setId(childId);

                        Registration registration = registrationMap.get(registrationId);
                        if (registration == null) {
                            registration = new Registration(person, new ArrayList<>(), registrationDate);
                            registration.setId(registrationId);
                            registrationMap.put(registrationId, registration);
                            registrations.add(registration);
                        }

                        Sprint sprint1 = new Sprint(result.getFloat("distance"));
                        sprint1.setId(result.getInt("sprintId"));

                        int eventId = result.getInt("eventId");
                        Event event = registration.getEvents().stream()
                                .filter(s -> s.getId().equals(eventId))
                                .findFirst()
                                .orElse(null);

                        if (event == null) {
                            event = new Event(
                                    sprint1,
                                    new ArrayList<>(),
                                    LocalDateTime.parse(result.getString("sprintDate"), Event.DATE_TIME_FORMATTER)
                            );
                            event.setId(eventId);
                            registration.getEvents().add(event);
                        }

                        AgeGroup ageGroup1 = new AgeGroup(
                                result.getInt("Lower"),
                                result.getInt("Upper")
                        );
                        ageGroup1.setId(result.getInt("ageGroupId"));

                        if (!event.getAgeGroups().contains(ageGroup1)) {
                            event.getAgeGroups().add(ageGroup1);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            logger.error(e);
        }

        return registrations;
    }
    @Override
    public void delete(Integer integer) {
        return;
    }

    @Override
    public void update(Registration entity) {
        return;
    }


}
