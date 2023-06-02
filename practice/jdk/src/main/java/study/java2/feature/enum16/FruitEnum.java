package study.java2.feature.enum16;

public enum FruitEnum {
  ORANGE("ORANGE", 1500) {
    private static int maxQuantity;

    public static void setMaxQuantity(int quantity) {
      maxQuantity = quantity;
    }

    public static int getMaxQuantity() {
      return maxQuantity;
    }
  },
  APPLE("APPLE", 1200);

  private String name;
  private int price;

  FruitEnum(String name, int price) {
    this.name = name;
    this.price = price;
  }
  public String getName() {
    return name;
  }
  public int getPrice() {
    return price;
  }
}
