package phan.hibernate.model;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

public class HibernateSessions {
    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        final ServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();
        return new MetadataSources(registry).buildMetadata().buildSessionFactory();
    }


    public static List<Country> fetchAllCountries() {
        Session session = sessionFactory.openSession();

        CriteriaBuilder builder = session.getCriteriaBuilder();

        CriteriaQuery<Country> criteria = builder.createQuery(Country.class);

        criteria.from(Country.class);

        List<Country> countries = session.createQuery(criteria).getResultList();

        session.close();

        return countries;
    }


    public static Country save(Country country) {
        // Open a session
        Session session = sessionFactory.openSession();

        // Begin a transaction
        session.beginTransaction();

        // Use the session to update the contact
        session.save(country);

        // Commit the transaction
        session.getTransaction().commit();

        // Close the session
        session.close();

        return country;
    }


    public static void update(Country country) {
        // Open a session
        Session session = sessionFactory.openSession();

        // Begin a transaction
        session.beginTransaction();

        // Use the session to update the contact
        session.update(country);

        // Commit the transaction
        session.getTransaction().commit();

        // Close the session
        session.close();
    }


    public static void delete(Country country) {
        // Open a session
        Session session = sessionFactory.openSession();

        // Begin a transaction
        session.beginTransaction();

        // Use the session to update the contact
        session.delete(country);

        // Commit the transaction
        session.getTransaction().commit();

        // Close the session
        session.close();
    }


    public static Country findByCode(String code) {
        // Open a session
        Session session = sessionFactory.openSession();

        // Retrieve the persistent object (or null if not found)
        Country country = session.get(Country.class, code);

        // Close the session
        session.close();

        // Return the object
        return country;
    }

}
