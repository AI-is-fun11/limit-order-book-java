
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class Backtester{
 private final OrderBook orderBook;
 private final MatchingEngine matchingEngine;
 private final OrderFlowSimulator sim;

 public Backtester(){
   this.orderBook=new OrderBook();
   this.matchingEngine= new MatchingEngine(orderBook);
   this.sim= new OrderFlowSimulator();
 }

 public void run ( int numOrders) throws IOException
 {
    try (
        FileWriter tradeWriter = new FileWriter("data/trades.csv");
        FileWriter orderWriter = new FileWriter("data/orders.csv");
        FileWriter snapshotWriter = new FileWriter("data/book_snapshots.csv")
    ) {
        tradeWriter.write("timestamp,buyOrderId,sellOrderId,price,quantity\n");
        orderWriter.write("timestamp,acceptedOrderId,type,side,price,originalQuantity,remainingQuantity,cancelOrderId,cancelSuccess\n");
        snapshotWriter.write("event,timestamp,bestBid,bestAsk,spread,midPrice\n");

        for (int i = 0; i < numOrders; i++) {
            OrderRequest request = sim.generateRandomOrder(
                60,   // limit prob
                30,   // market prob
                95,   // min price
                105,  // max price
                1,    // min qty
                10,    // max qty
                matchingEngine.getLastOrderId()
            );
            OrderResult result = matchingEngine.processOrderRequest(request, request.getTimestamp());
            List<Trade> trades = result.getTrades();
           
            orderWriter.write(
                request.getTimestamp() + "," +
                result.getAcceptedOrderId() + "," +
                request.getType() + "," +
                request.getSide() + "," +
                request.getPrice() + "," +
                request.getQuantity() + "," +
                result.getRemainingQuantity() + "," +
                request.getCancelOrderId() + "," +
                result.isCancelSuccess() + "\n"
            );

            for (Trade trade : trades) {
                tradeWriter.write(
                    trade.getTimestamp() + "," +
                    trade.getBuyOrderId() + "," +
                    trade.getSellOrderId() + "," +
                    trade.getPrice() + "," +
                    trade.getQuantity() + "\n"
                );
            }

            Integer bestBid = orderBook.getBestBid();
            Integer bestAsk = orderBook.getBestAsk();
            Integer spread = orderBook.getSpread();
            Double midPrice = orderBook.getMidPrice();

            snapshotWriter.write(
                i + "," +
                request.getTimestamp() + "," +
                bestBid + "," +
                bestAsk + "," +
                spread + "," +
                midPrice + "\n"
            );

            System.out.println("Request: " + request.getType()
                                + " " + request.getSide()
                                + " price=" + request.getPrice()
                                + " qty=" + request.getQuantity()
                                + " acceptedId=" + result.getAcceptedOrderId()
                                + " remainingQty=" + result.getRemainingQuantity()
                                + " cancelId=" + request.getCancelOrderId()
                                + " cancelSuccess=" + result.isCancelSuccess());

            if (!trades.isEmpty()) {
                                System.out.println("Trades: " + trades);
                                   }

            System.out.println("Best bid: " + bestBid
                + " Best ask: " + bestAsk
                + " Spread: " + spread
                + " Mid: " + midPrice);
        }
    }
 }
}
