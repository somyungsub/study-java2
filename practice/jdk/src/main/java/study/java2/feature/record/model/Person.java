package study.java2.feature.record.model;

public record Person(String name, int age) {
  public Person{
    if (age < 0) {
      throw new RuntimeException("age cannot be negative");
    }
  }
}
