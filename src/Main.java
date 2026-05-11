public class Main {
    public static void main(String[] args) {
        OrderBook book = new OrderBook();

        Order buy1 = new Order(1, 1, Side.BUY, OrderType.LIMIT, 100, 10);
        Order buy2 = new Order(2, 2, Side.BUY, OrderType.LIMIT, 101, 5);
        Order sell1 = new Order(3, 3, Side.SELL, OrderType.LIMIT, 105, 7);
        Order buy3 = new Order(4, 4, Side.BUY, OrderType.LIMIT, 101, 20);
        System.out.println("Depth before cancel: " + book.getDepth(Side.BUY, 101));

      

        
        book.addLimitOrder(buy1);
        book.addLimitOrder(buy2);
        book.addLimitOrder(sell1);
        book.addLimitOrder(buy3);
        System.out.println("Best bid: " + book.getBestBid());
        System.out.println("Best ask: " + book.getBestAsk());
        System.out.println("Spread: " + book.getSpread());
        System.out.println("Mid price: " + book.getMidPrice());
        System.out.println("Bid depth at 101: " + book.getDepth(Side.BUY, 101));
        boolean cancelled = book.cancelOrder(2);

        System.out.println("Cancelled order 2: " + cancelled);
        System.out.println("Depth after cancel: " + book.getDepth(Side.BUY, 101));
    }
   
}