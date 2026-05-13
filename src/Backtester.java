import java.util.List;

public class Backtester{
 OrderBook orderBook;
 MatchingEngine matchingEngine;
 OrderFlowSimulator  sim;
 public Backtester(){
   this.orderBook=new OrderBook();
   this.matchingEngine= new MatchingEngine(orderBook);
   this.sim= new OrderFlowSimulator();
 }

 public void run ( int numOrders)
 {
    for (int i = 0; i < numOrders; i++) {
        Order order = sim.generateRandomOrder(
            60,   // limit prob
            30,   // market prob
            95,   // min price
            105,  // max price
            1,    // min qty
            10    // max qty
        );
        int originalQuantity = order.getQuantity();
        List<Trade> trades = matchingEngine.processOrder(order);

        System.out.println("Order: " + order.getType() + " " + order.getSide()
            + " price=" + order.getPrice()
            + " qty=" + order.getQuantity());

        if (!trades.isEmpty()) {
            System.out.println("Trades: " + trades);
        }

        System.out.println("Best bid: " + orderBook.getBestBid()
            + " Best ask: " + orderBook.getBestAsk()
            + " Spread: " + orderBook.getSpread()
            + " Mid: " + orderBook.getMidPrice());
    }
 }
}