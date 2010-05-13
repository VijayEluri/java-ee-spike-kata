package no.steria.kata.javaee;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Person {

	public static Person withName(String name) {
		Person person = new Person();
		person.name = name;
		return person;
	}

	@SuppressWarnings("unused")
	@Id @GeneratedValue
	private Long id;

	private String name;

	public String getName() {
		return name;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Person)) return false;
		Person person = (Person)obj;
		return nullSafeEquals(name, person.name);
	}

	private<T> boolean nullSafeEquals(T a, T b) {
		return a != null ? a.equals(b) : b == null;
	}

	@Override
	public String toString() {
		return "Person<" + name + ">";
	}

	@Override
	public int hashCode() {
		return name != null ? name.hashCode() : -1;
	}
}
