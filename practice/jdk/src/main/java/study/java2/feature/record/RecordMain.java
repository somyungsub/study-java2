package study.java2.feature.record;

import study.java2.feature.record.model.Person;

public class RecordMain {
  public static void main(String[] args) {
    Person person = new Person("person", 30);
    System.out.println("person = " + person);
    System.out.println(person.age());
    System.out.println(person.name());

    Person person1 = new Person("ss", -1);
    System.out.println("person1 = " + person1);
  }
}
