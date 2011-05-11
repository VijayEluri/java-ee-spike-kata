package no.steria.kata.javaee;

import static org.fest.assertions.Assertions.assertThat;

import javax.naming.NamingException;

import org.eclipse.jetty.plus.jndi.EnvEntry;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.hibernate.cfg.Environment;
import org.hsqldb.jdbc.jdbcDataSource;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

public class PersonWebTest {

    @Test
    public void shouldFindSavedPerson() throws Exception {
        int localPort = startWebserver();

        String baseUrl = "http://localhost:" + localPort + "/";

        WebDriver browser = createWebDriver();
        browser.get(baseUrl);
        browser.findElement(By.linkText("Create person")).click();
        browser.findElement(By.name("full_name")).sendKeys("Darth Vader");
        browser.findElement(By.name("createPerson")).click();

        browser.findElement(By.linkText("Create person")).click();
        browser.findElement(By.name("full_name")).sendKeys("Luke Skywalker");
        browser.findElement(By.name("createPerson")).click();

        browser.get(baseUrl);
        browser.findElement(By.linkText("Find people")).click();
        assertThat(browser.getPageSource())
            .contains("Darth Vader");

        browser.findElement(By.name("name_query")).sendKeys("vader");
        browser.findElement(By.name("findPeople")).click();
        assertThat(browser.getPageSource())
            .excludes("Luke Skywalker")
            .contains("Darth Vader");
    }

    private int startWebserver() throws NamingException, Exception {
        String jndiDataSource = "jdbc/personDs";

        jdbcDataSource dataSource = new jdbcDataSource();
        dataSource.setDatabase("jdbc:hsqldb:mem:webtest");
        dataSource.setUser("sa");
        new EnvEntry(jndiDataSource, dataSource);

        System.setProperty(Environment.HBM2DDL_AUTO, "create");
        
        new HibernatePersonDao(jndiDataSource);

        Server server = new Server(0);
        server.setHandler(new WebAppContext("src/main/webapp", "/"));
        server.start();

        int localPort = server.getConnectors()[0].getLocalPort();
        return localPort;
    }

    private HtmlUnitDriver createWebDriver() {
        return new HtmlUnitDriver() {
            @Override
            public WebElement findElement(By by) {
                try {
                    return super.findElement(by);
                } catch (NoSuchElementException e) {
                    throw new NoSuchElementException("Can't find " + by + " in " + getPageSource());
                }
            }
        };
    }

}
