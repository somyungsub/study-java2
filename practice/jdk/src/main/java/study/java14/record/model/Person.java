package study.java14.record.model;

public record Person(String name, int age) {
  public Person{
    if (age < 0) {
      throw new RuntimeException("age cannot be negative");
    }
  }

  public static Person of(String name, int age){
    return new Person(name, age);
  }
}
