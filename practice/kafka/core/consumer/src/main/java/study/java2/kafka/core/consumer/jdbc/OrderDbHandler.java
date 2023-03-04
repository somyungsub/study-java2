package study.java2.kafka.core.consumer.jdbc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import study.java2.kafka.core.consumer.jdbc.dto.OrderDto;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public class OrderDbHandler {
  private static final Logger log = LoggerFactory.getLogger(OrderDbHandler.class);
  private static final String INSERT_ORDER_SQL = "INSERT INTO orders " +
    "(ord_id, shop_id, menu_name, user_name, phone_number, address, order_time) "+
    "values (?, ?, ?, ?, ?, ?, ?)";
  private final JdbcConnector jdbcConnector;

  public OrderDbHandler(JdbcConnector jdbcConnector) {
    this.jdbcConnector = jdbcConnector;
  }

  public void insertOrder(OrderDto orderDto)  {
    try(PreparedStatement pstmt = this.jdbcConnector.getConnection().prepareStatement(INSERT_ORDER_SQL)) {
      pstmt.setString(1, orderDto.orderId());
      pstmt.setString(2, orderDto.shopId());
      pstmt.setString(3, orderDto.menuName());
      pstmt.setString(4, orderDto.userName());
      pstmt.setString(5, orderDto.phoneNumber());
      pstmt.setTimestamp(7, Timestamp.valueOf(orderDto.orderTime()));
      pstmt.executeUpdate();
    } catch(SQLException e) {
      log.error(e.getMessage());
    }
  }

  public void insertOrders(List<OrderDto> orders) {
    log.info("======= insert Orders st ============");
    try(PreparedStatement pstmt = this.jdbcConnector.getConnection().prepareStatement(INSERT_ORDER_SQL)) {
      for(OrderDto orderDto : orders) {
        pstmt.setString(1, orderDto.orderId());
        pstmt.setString(2, orderDto.shopId());
        pstmt.setString(3, orderDto.menuName());
        pstmt.setString(4, orderDto.userName());
        pstmt.setString(5, orderDto.phoneNumber());
        pstmt.setString(6, orderDto.address());
        pstmt.setTimestamp(7, Timestamp.valueOf(orderDto.orderTime()));
        pstmt.addBatch();
      }
      pstmt.executeUpdate();
      log.info("======= insert Orders end ============");
    } catch(SQLException e) {
      log.info(e.getMessage());
    }
  }

  public void close() {
    log.info("###### OrderDBHandler is closing");
    this.jdbcConnector.close();
  }

  public void test() {
    try(PreparedStatement preparedStatement = this.jdbcConnector.getConnection().prepareStatement("SELECT 'test~ orderDbHandler'")) {
      ResultSet resultSet = preparedStatement.executeQuery();
      if (resultSet.next()) {
        System.out.println(resultSet.getString(1));
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
