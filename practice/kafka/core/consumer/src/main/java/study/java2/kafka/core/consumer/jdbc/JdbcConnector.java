package study.java2.kafka.core.consumer.jdbc;

import java.sql.*;

public class JdbcConnector {
  private final String url;
  private final String user;
  private final String password;

  public JdbcConnector(String url, String user, String password) {
    this.url = url;
    this.user = user;
    this.password = password;
  }
  public Connection getConnection() {
    try {
      return DriverManager.getConnection(url, user, password);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public void close() {
    try {
      this.getConnection().close();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
