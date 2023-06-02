package study.java8;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class FP {

  public <T> List<T> testFlat(List<List<T>> list) {
    return list.stream()
      .flatMap(Collection::stream)
      .collect(Collectors.toList());
  }
}
