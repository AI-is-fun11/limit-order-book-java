import java.util.ArrayList;
import java.util.List;

public class MatchingEngine{
   private final OrderBook orderBook;
   private long nextOrderId=1;
   public MatchingEngine(OrderBook orderBook)
   {
    this.orderBook=orderBook;

   }

   public long getLastOrderId() {
    return nextOrderId - 1;
                                }

public OrderResult processOrderRequest(OrderRequest request, long timestamp){
    
    int price= (request.getType()==OrderType.LIMIT)? request.getPrice() : 0;

    if (request.getType() == OrderType.CANCEL) {
        boolean cancelSuccess = orderBook.cancelOrder(request.getCancelOrderId());
        return new OrderResult(null, cancelSuccess, new ArrayList<>(),null);
    }
     long orderId=nextOrderId++;
     Order order =new Order(orderId,timestamp,request.getSide(),request.getType(),price,request.getQuantity());
     List<Trade> trades;

     if (request.getType() == OrderType.LIMIT) {
    trades = orderBook.addLimitOrder(order);
     } 
     else                {
     trades = orderBook.addMarketOrder(order);
                            }
     return new OrderResult(orderId,false,trades,order.getQuantity());
     
    
}

}