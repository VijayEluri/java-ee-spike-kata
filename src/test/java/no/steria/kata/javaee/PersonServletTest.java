package no.steria.kata.javaee;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

public class PersonServletTest {

    private PersonServlet personServlet = new PersonServlet();
    private HttpServletRequest req = mock(HttpServletRequest.class);
    private HttpServletResponse res = mock(HttpServletResponse.class);
    private StringWriter html = new StringWriter();
    private PersonDao personDao = mock(PersonDao.class);

    @Before
    public void setupServlet() throws IOException {
        personServlet.setPersonDao(personDao);
        when(res.getWriter()).thenReturn(new PrintWriter(html));
    }

    @Test
    public void shouldDisplayCreateForm() throws Exception {
        httpRequest("GET", "/create.html");

        personServlet.service(req, res);

        verify(res).setContentType("text/html");

        assertThat(html.toString())
            .contains("<form method='post' action='create.html'")
            .contains("<input type='text' name='full_name' value=''")
            .contains("<input type='submit' name='create' value='Create Person'");

        assertValidHtml();
    }

    @Test
    public void shouldCreatePerson() throws Exception {
        httpRequest("POST", "/create.html");
        when(req.getParameter("full_name")).thenReturn("Johannes");

        personServlet.service(req, res);

        InOrder order = inOrder(personDao);
        order.verify(personDao).beginTransaction();
        order.verify(personDao).createPerson(Person.withName("Johannes"));
        order.verify(personDao).endTransaction(true);

        verify(res).sendRedirect("/");
    }

    @Test
    public void showSearchPage() throws Exception {
        httpRequest("GET", "/find.html");

        personServlet.service(req, res);

        assertThat(html.toString())
            .contains("<form method='get' action='find.html'")
            .contains("<input type='text' name='name_query' value=''")
            .contains("<input type='submit' name='find' value='Find Person'");

        assertValidHtml();
    }

    @Test
    public void shouldSearchForPeople() throws Exception {
        httpRequest("GET", "/find.html");
        when(req.getParameter("name_query")).thenReturn("Ivar");

        personServlet.service(req, res);

        verify(personDao).findPeople("Ivar");
    }

    @Test
    public void shouldDisplaySearchResult() throws Exception {
        httpRequest("GET", "/find.html");
        List<Person> people = Arrays.asList(Person.withName("Johannes"), Person.withName("Ivar"));
        when(personDao.findPeople(anyString())).thenReturn(people);

        personServlet.service(req, res);

        assertThat(html.toString())
            .contains("<ul>")
            .contains("<li>Johannes</li>")
            .contains("<li>Ivar</li>")
            .contains("</ul>");
    }

    @Test
    public void shouldEchoSearchParameters() throws Exception {
        httpRequest("GET", "/find.html");
        when(req.getParameter("name_query")).thenReturn("Ivar");

        personServlet.service(req, res);

        assertThat(html.toString())
            .contains("<form")
            .contains("<input type='text' name='name_query' value='Ivar'");
    }

    private Document assertValidHtml() throws DocumentException {
        return DocumentHelper.parseText(html.toString());
    }

    private void httpRequest(String method, String pathInfo) {
        when(req.getMethod()).thenReturn(method);
        when(req.getPathInfo()).thenReturn(pathInfo);
    }


}
