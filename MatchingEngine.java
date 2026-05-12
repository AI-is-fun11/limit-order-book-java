import java.util.ArrayList;
import java.util.List;

public class MatchingEngine{
   private final OrderBook orderBook;

   public MatchingEngine(OrderBook orderBook)
   {
    this.orderBook=orderBook;

   }
   public List<Trade> processOrders(Order order)
   {
    if (order.getType()==OrderType.LIMIT)
    {
      return orderBook.addLimitOrder(order);


    }

    if (order.getType()==OrderType.MARKET)
    {

     return orderBook.addMarketOrder(order);

    }

    if (order.getType()==OrderType.CANCEL)
    {
        orderBook.cancelOrder(order.getOrderId());
        return new ArrayList<>();

    }

       return new ArrayList<>();

   }

}