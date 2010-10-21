package no.steria.kata.javaee;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Environment;
import org.hibernate.context.ThreadLocalSessionContext;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

public class HibernatePersonDao implements PersonDao {

    private SessionFactory sessionFactory;

    public HibernatePersonDao(String jndiSource) {
        AnnotationConfiguration cfg = new AnnotationConfiguration();
        cfg.setProperty(Environment.DATASOURCE, jndiSource);
        cfg.setProperty(Environment.CURRENT_SESSION_CONTEXT_CLASS, ThreadLocalSessionContext.class.getName());
        cfg.addAnnotatedClass(Person.class);
        sessionFactory = cfg.buildSessionFactory();
    }

    @Override
    public void createPerson(Person person) {
        getSession().save(person);
    }

    @Override
    public void beginTransaction() {
        getSession().beginTransaction();
    }

    @Override
    public void endTransaction(boolean commit) {
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Person> findPeople(String nameQuery) {
        Criteria criteria = getSession().createCriteria(Person.class);
        if (nameQuery != null) {
            criteria.add(Restrictions.ilike("name", nameQuery, MatchMode.ANYWHERE));
        }
        return criteria.list();
    }

    private Session getSession() {
        return sessionFactory.getCurrentSession();
    }

}
