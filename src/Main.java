import java.util.List;
public class Main {
    public static void main(String[] args) {
OrderBook testBook = new OrderBook();

Order sell1 = new Order(10, 1, Side.SELL, OrderType.LIMIT, 100, 5);
Order sell2 = new Order(11, 2, Side.SELL, OrderType.LIMIT, 105, 5);
Order marketBuy = new Order(12, 3, Side.BUY, OrderType.MARKET, 0, 7);

testBook.addLimitOrder(sell1);
testBook.addLimitOrder(sell2);

List<Trade> trades = testBook.addMarketOrder(marketBuy);

System.out.println("Trades: " + trades);
System.out.println("Ask depth at 100: " + testBook.getDepth(Side.SELL, 100));
System.out.println("Ask depth at 105: " + testBook.getDepth(Side.SELL, 105));
System.out.println("Best ask: " + testBook.getBestAsk());
    }
   
}