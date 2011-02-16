package no.steria.kata.javaee;

import static org.fest.assertions.Assertions.assertThat;

import org.joda.time.DateMidnight;
import org.joda.time.DateTimeUtils;
import org.junit.Test;

public class PersonTest {

    @Test
    public void shouldGetNames() throws Exception {
        assertThat(Person.withName("Darth", "Vader").getName()).isEqualTo("Darth Vader");
        assertThat(Person.withName("Darth", "Vader").getFirstName()).isEqualTo("Darth");
        assertThat(Person.withName("Darth", "Vader").getLastName()).isEqualTo("Vader");
    }

    @Test
    public void shouldBeEqualWhenNameIsEqual() throws Exception {
        assertThat(Person.withName("Darth", "Vader")) //
            .isEqualTo(Person.withName("Darth", "Vader")) //
            .isNotEqualTo(Person.withName("Anakin", "Skywalker")) //
            .isNotEqualTo(Person.withName(null, null)) //
            .isNotEqualTo(new Object()) //
            .isNotEqualTo(null) //
        ;

        assertThat(Person.withName(null, null)) //
            .isEqualTo(Person.withName(null, null))
            .isNotEqualTo(Person.withName("Darth", null)) //
        ;
    }

    @Test
    public void shouldBaseHashcodeOnName() throws Exception {
        assertThat(Person.withName("Darth", "Vader").hashCode()).as("hashCode") //
            .isEqualTo(Person.withName("Darth", "Vader").hashCode()) //
            .isNotEqualTo(Person.withName("Anakin", "Skywalker").hashCode()) //
            .isNotEqualTo(Person.withName(null, null).hashCode());
    }
    
    @Test
    public void shouldCalculateAgeBasedOnCurrentTime() throws Exception {
        setCurrentTime(new DateMidnight(2011, 2, 16));
        Person johannes = Person.withName("Johannes", "Brodwall");
        johannes.setBirthDate(new DateMidnight(1975, 12, 21));
        
        assertThat(johannes.getAge()).isEqualTo(35);
    }

    private void setCurrentTime(DateMidnight dateMidnight) {
        DateTimeUtils.setCurrentMillisFixed(dateMidnight.getMillis());
    }
    
}
