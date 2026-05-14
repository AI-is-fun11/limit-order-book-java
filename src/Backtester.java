
import java.io.FileWriter;
import java.io.IOException;
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

 public void run ( int numOrders) throws IOException
 {
    FileWriter tradeWriter = new FileWriter("data/trades.csv");
    tradeWriter.write("timestamp,buyOrderId,sellOrderId,price,quantity\n");
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
        for (Trade trade : trades) {
        tradeWriter.write(
        trade.getTimestamp() + "," +
        trade.getBuyOrderId() + "," +
        trade.getSellOrderId() + "," +
        trade.getPrice() + "," +
        trade.getQuantity() + "\n"
              );
                                   }
    }
 }
}