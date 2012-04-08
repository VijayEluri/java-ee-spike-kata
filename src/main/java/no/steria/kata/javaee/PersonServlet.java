package no.steria.kata.javaee;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PersonServlet extends HttpServlet {

    private static final long serialVersionUID = 7744195856599544243L;
    private PersonDao personDao;

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try (Transaction transaction = personDao.beginTransaction()) {
            super.service(req, resp);
            transaction.setCommit();
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        PrintWriter writer = resp.getWriter();
        if (req.getPathInfo().equals("/findPeople.html")) {
            String nameQuery = req.getParameter("name_query");
            List<Person> people = personDao.findPeople(nameQuery);
            showSearchPage(writer, nameQuery, people);
        } else {
            showCreatePage(writer, "", null);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String fullName = req.getParameter("full_name");
        String fullNameError = validateName(fullName);

        if (fullNameError == null) {
            personDao.createPerson(Person.withName(fullName));
            resp.sendRedirect("/");
        } else {
            resp.setContentType("text/html");
            showCreatePage(resp.getWriter(), fullName, fullNameError);
        }
    }

    private String validateName(String fullName) {
        String errorMessage = null;
        if (fullName.equals("")) {
            errorMessage = "Full name must be given";
        } else if (containsIllegalCharacters(fullName)) {
            errorMessage = "Full name contains illegal characters";
        }
        return errorMessage;
    }

    private void showCreatePage(PrintWriter writer, String fullName, String fullNameError) {
        writer.append("<html>");
        writer.append("<head><style>#error { color: red; }</style></head>");

        if (fullNameError != null) {
            writer.append("<div id='error'>").append(fullNameError).append("</div>");
        }
        writer
            .append("<form method='post' action='createPerson.html'>")
            .append("<p>")
            .append("<label for='full_name'><b>Full name:</b></label>")
            .append("<input type='text' name='full_name' value='" + htmlEscape(fullName) + "'/>")
            .append("</p>")
            .append("<input type='submit' name='createPerson' value='Create person'/>")
            .append("</form>");
        writer.append("</html>");
    }

    private String htmlEscape(String fullName) {
        return fullName.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;");
    }

    private void showSearchPage(PrintWriter writer, String nameQuery, List<Person> people) {
        if (nameQuery == null) nameQuery = "";
        writer
            .append("<html>")
            .append("<form method='get' action='findPeople.html'>")
            .append("<input type='text' name='name_query' value='" + nameQuery + "'/>")
            .append("<input type='submit' name='findPeople' value='Find people'/>")
            .append("</form>");

        writer.append("<ul>");
        for (Person person : people) {
            writer.append("<li>").append(person.getFullName()).append("</li>");
        }
        writer
            .append("</ul>")
            .append("</html>");
    }

    private boolean containsIllegalCharacters(String fullName) {
        String illegals = "<>&";
        for (char illegal : illegals.toCharArray()) {
            if (fullName.contains(Character.toString(illegal))) return true;
        }
        return false;
    }

    public void setPersonDao(PersonDao personDao) {
        this.personDao = personDao;
    }

    @Override
    public void init() throws ServletException {
        setPersonDao(new HibernatePersonDao("jdbc/personDs"));
    }
}
