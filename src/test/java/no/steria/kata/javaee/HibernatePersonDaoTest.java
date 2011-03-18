package no.steria.kata.javaee;

import static org.fest.assertions.Assertions.assertThat;

import javax.naming.NamingException;

import org.hibernate.cfg.Environment;
import org.hsqldb.jdbc.jdbcDataSource;
import org.junit.Before;
import org.junit.Test;
import org.mortbay.jetty.plus.naming.EnvEntry;

public class HibernatePersonDaoTest {

    private PersonDao personDao;

    @Test
    public void shouldFindCreatedPeople() throws Exception {
        personDao.beginTransaction();
        Person person = Person.withName("Darth", null);
        personDao.createPerson(person);
        assertThat(personDao.findPeople(null)).contains(person);
    }

    @Test
    public void shouldLimitFindToQuery() throws Exception {
        personDao.beginTransaction();
        Person matchingLastname = Person.withName("Darth", "Vader");
        Person matchingFirstname = Person.withName("Vader", "Darth");
        Person nonMatchingPerson = Person.withName("Anakin", "Skywalker");
        personDao.createPerson(matchingLastname); 
        personDao.createPerson(nonMatchingPerson);
        personDao.createPerson(matchingFirstname);

        assertThat(personDao.findPeople("vader")) //
            .contains(matchingLastname) //
            .contains(matchingFirstname) //
            .excludes(nonMatchingPerson);
    }

    @Test
    public void shouldCommitOrRollback() throws Exception {
        personDao.beginTransaction();
        Person commitedPerson = Person.withName("Darth", "Vader");
        personDao.createPerson(commitedPerson);
        personDao.endTransaction(true);

        personDao.beginTransaction();
        Person uncommitedPerson = Person.withName("Jar Jar", "Binks");
        personDao.createPerson(uncommitedPerson);
        personDao.endTransaction(false);

        personDao.beginTransaction();
        assertThat(personDao.findPeople(null)) //
            .contains(commitedPerson) //
            .excludes(uncommitedPerson);
    }

    @Before
    public void setupPersonDao() throws NamingException {
        personDao = createPersonDao();
    }

    private PersonDao createPersonDao() throws NamingException {
        String jndiDataSource = "jdbc/testDs";

        jdbcDataSource dataSource = new jdbcDataSource();
        dataSource.setDatabase("jdbc:hsqldb:mem:test");
        dataSource.setUser("sa");
        new EnvEntry(jndiDataSource, dataSource);

        System.setProperty(Environment.HBM2DDL_AUTO, "create");

        return new HibernatePersonDao(jndiDataSource);
    }

}
