
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class Backtester{
 private final OrderBook orderBook;
 private final MatchingEngine matchingEngine;
 private final OrderFlowSimulator sim;
private final MarketMaker marketMaker;
private final boolean verbose=false;
 public Backtester(){
   this.orderBook=new OrderBook();
   this.matchingEngine= new MatchingEngine(orderBook);
   this.sim= new OrderFlowSimulator();
   this.marketMaker = new MarketMaker(2, 1, 100000);
 }

 public void run ( int numOrders) throws IOException
 {
    try (
        FileWriter tradeWriter = new FileWriter("data/trades.csv");
        FileWriter orderWriter = new FileWriter("data/orders.csv");
        FileWriter snapshotWriter = new FileWriter("data/book_snapshots.csv");
        FileWriter pnlWriter = new FileWriter("data/pnl.csv");
    ) {
        tradeWriter.write("timestamp,buyOrderId,sellOrderId,price,quantity\n");
        orderWriter.write("timestamp,acceptedOrderId,type,side,price,originalQuantity,remainingQuantity,cancelOrderId,cancelSuccess\n");
        snapshotWriter.write("event,timestamp,bestBid,bestAsk,spread,midPrice\n");
        pnlWriter.write("timestamp,inventory,cash,markToMarketValue,profit,activeOrderCount\n");
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
                marketMaker.adjustInventory(trade);
                tradeWriter.write(
                    trade.getTimestamp() + "," +
                    trade.getBuyOrderId() + "," +
                    trade.getSellOrderId() + "," +
                    trade.getPrice() + "," +
                    trade.getQuantity() + "\n"
                );
            }
            
        OrderRequest[] cancelRequests = marketMaker.generateCancelRequests(request.getTimestamp());

        for (OrderRequest cancelRequest : cancelRequests) {
            OrderResult cancelResult = matchingEngine.processOrderRequest(cancelRequest, cancelRequest.getTimestamp());
        marketMaker.recordCancellation(cancelRequest, cancelResult);
        orderWriter.write(
        cancelRequest.getTimestamp() + "," +
        cancelResult.getAcceptedOrderId() + "," +
        cancelRequest.getType() + "," +
        cancelRequest.getSide() + "," +
        cancelRequest.getPrice() + "," +
        cancelRequest.getQuantity() + "," +
        cancelResult.getRemainingQuantity() + "," +
        cancelRequest.getCancelOrderId() + "," +
        cancelResult.isCancelSuccess() + "\n");}


        OrderRequest[] quoteRequests = marketMaker.generateQuotes(orderBook, request.getTimestamp(), 1);

        for (OrderRequest quoteRequest : quoteRequests) {
              OrderResult quoteResult = matchingEngine.processOrderRequest(quoteRequest, quoteRequest.getTimestamp());
               marketMaker.resultRecord(quoteRequest, quoteResult);

        for (Trade trade : quoteResult.getTrades()) {
        marketMaker.adjustInventory(trade);
            tradeWriter.write(
        trade.getTimestamp() + "," +
        trade.getBuyOrderId() + "," +
        trade.getSellOrderId() + "," +
        trade.getPrice() + "," +
        trade.getQuantity() + "\n"); }

        orderWriter.write(
       quoteRequest.getTimestamp() + "," +
       quoteResult.getAcceptedOrderId() + "," +
       quoteRequest.getType() + "," +
      quoteRequest.getSide() + "," +
      quoteRequest.getPrice() + "," +
      quoteRequest.getQuantity() + "," +
      quoteResult.getRemainingQuantity() + "," +
      quoteRequest.getCancelOrderId() + "," +
       quoteResult.isCancelSuccess() + "\n");
                                              }

            Integer bestBid = orderBook.getBestBid();
            Integer bestAsk = orderBook.getBestAsk();
            Integer spread = orderBook.getSpread();
            Double midPrice = orderBook.getMidPrice();
            Double markToMarketValue = marketMaker.getMarkToMarketValue(orderBook);
            Double profit = marketMaker.getProfit(orderBook);
            snapshotWriter.write(
                i + "," +
                request.getTimestamp() + "," +
                bestBid + "," +
                bestAsk + "," +
                spread + "," +
                midPrice + "\n"            
            );



            pnlWriter.write(
            request.getTimestamp() + "," +
            marketMaker.getInventory() + "," +
            marketMaker.getCash() + "," +
            markToMarketValue + "," +
            profit + "," +
            marketMaker.getActiveOrders().size() + "\n"
                         );

                if (verbose) {
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
                + " Mid: " + midPrice);}
        }
    }}



}
