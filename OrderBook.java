
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;



public class OrderBook{
    private final TreeMap<Integer, Queue<Order>> bids; 
    private final TreeMap<Integer, Queue<Order>> asks; 
    private final Map<Long, Order> orderMap;
    //best ask is the lowest price and best bid is the highest
    public OrderBook(){
        bids=new TreeMap<>((a,b)->b-a); // Use comparator to sort bids in descending order
        asks=new TreeMap<>();
        orderMap=new HashMap<>();
    } 




    public Integer getBestBid() 
    {
    
     while (!bids.isEmpty())
    {
     int bestBid=bids.firstKey();
     Queue<Order> queue= bids.get(bestBid);

     while (!queue.isEmpty() && queue.peek().isFilled())

     {
     // Orders with 0 qty are removed from the book
       queue.poll();
     }
     if (!queue.isEmpty())
     {
    
       return bestBid;
     }

     // Empty price levels are removed from the book so that best bid is always at a price with non zero depth
     bids.remove(bestBid);

    }
    return null;
    }


    public Integer getBestAsk() {
     
     
     
     
     while(!asks.isEmpty()){
     int bestAsk= asks.firstKey();
     Queue<Order> queue=asks.get(bestAsk);
     while (!queue.isEmpty() && queue.peek().isFilled())
     {
        queue.poll();
    
     }
     
     if(!queue.isEmpty()){
        return bestAsk;
     }
     
     asks.remove(bestAsk);
     }

     return null;
}
    public Integer getSpread() {
     Integer bestBid = getBestBid();
     Integer bestAsk = getBestAsk();

     if (bestBid == null || bestAsk == null) {
        return null;
     }

    return bestAsk - bestBid;
}
    public Double getMidPrice() {
     Integer bestBid = getBestBid();
     Integer bestAsk = getBestAsk();

     if (bestBid == null || bestAsk == null) {
        return null;
    }

     return (bestBid + bestAsk) / 2.0;
}
//Limit orders are those with a price associated, they are matched accordingly and leftovers are added to the queues
public List<Trade> addLimitOrder(Order order) {
    TreeMap<Integer, Queue<Order>> oppositeSide;
    List<Trade> trades = new ArrayList<>();
    TreeMap<Integer, Queue<Order>> bookSide;

    if (order.getSide() == Side.BUY) 
    {
    oppositeSide = asks;
    }   
    else 
    {
    oppositeSide = bids;
    }
    
    
    // Check if order is unfullfilled and if we can indeed match it
    while (!order.isFilled() && canMatch(order)) {

    int bestPrice = oppositeSide.firstKey();
    Queue<Order> queue = oppositeSide.get(bestPrice);
    
    while (!queue.isEmpty() && queue.peek().isFilled()) //Remove orders that are empty  (or Cancelled)
    {

        queue.poll();
        
    }   

    //an empty queue effectively means that price lev is useless
    if (queue.isEmpty()) 
    {
    oppositeSide.remove(bestPrice);
    continue;
    }
    // The top order now is the restingorder we use
    Order restingOrder = queue.peek();
    int tradeQuantity = Math.min(order.getQuantity(), restingOrder.getQuantity()); //obvious that we can only meet the smaller order

    long buyOrderId = (order.getSide() == Side.BUY) ? order.getOrderId() : restingOrder.getOrderId();
    long sellOrderId = (order.getSide() == Side.SELL) ? order.getOrderId() : restingOrder.getOrderId();
    // One liner if to make sure that we correctly assign the buy and sell sides of the trade
    // if the order is buy then the resting order is sell which is ensure by picking the opposite side in the beginning

    Trade trade = new Trade
    (
     order.getTimestamp(),
    buyOrderId,
    sellOrderId,
    restingOrder.getPrice(),
    tradeQuantity      
    );

    trades.add(trade);
    order.reduceQuantity(tradeQuantity);
    restingOrder.reduceQuantity(tradeQuantity);

    if (restingOrder.isFilled())
    {
    queue.poll();
    orderMap.remove(restingOrder.getOrderId());
    }

    if (queue.isEmpty())
    {
    oppositeSide.remove(bestPrice);
    }



}
if (!order.isFilled()) {
    // Add to the correct side 
    if (order.getSide() == Side.BUY) {
        bookSide = bids;
    } else {
        bookSide = asks;
    }

    if (!bookSide.containsKey(order.getPrice())) {
        bookSide.put(order.getPrice(), new LinkedList<>());
    }
    bookSide.get(order.getPrice()).add(order);
    orderMap.put(order.getOrderId(), order);
}
return trades;
}
//Order Depth is the net quantity atany price level including all orders
public Integer getDepth(Side side, int price) {
    TreeMap<Integer, Queue<Order>> bookSide;
    
    if (side == Side.BUY) {
        bookSide = bids;
    } else {
        bookSide = asks;
    }

    if (!bookSide.containsKey(price)) {
        return 0; // No orders at this price level
    }

    return bookSide.get(price).stream().mapToInt(Order::getQuantity).sum(); // Conv to stream call--> the quantity of each order--> sum them
}
public boolean cancelOrder(long orderId){

        Order order= orderMap.get(orderId);
        if (order==null){
            return false;
        }
        order.reduceQuantity(order.getQuantity());
        orderMap.remove(orderId);
        return true;
    }
// A sellers ask is their lowerbound and a buyers bid is their upperbound
private boolean canMatch(Order inord) {
 if (inord.getSide()==Side.BUY){
     Integer bestAsk=getBestAsk();
     //return false if there isnt a best ask and return false if the best ask is bigger than the bid right now
     return bestAsk!=null && inord.getPrice()>=bestAsk; 
     
    
}
else{
    Integer bestBid=getBestBid();
    return bestBid!=null && inord.getPrice()<=bestBid;
}



}


public List<Trade> addMarketOrder(Order order)
{
 List<Trade> trades= new ArrayList<>();
 TreeMap<Integer,Queue<Order>> oppositeSide;
 if (order.getSide() == Side.BUY) 
    {
    oppositeSide = asks;
    }   
    else 
    {
    oppositeSide = bids;
    }

 
 while(!order.isFilled() && !oppositeSide.isEmpty())
 {
    int bestPrice= oppositeSide.firstKey();
    Queue<Order> queue= oppositeSide.get(bestPrice);
    while (!queue.isEmpty() && queue.peek().isFilled())
    {
        queue.poll();

    }

    if (queue.isEmpty())
    {
      oppositeSide.remove(bestPrice);
      continue;

    }  
    
    Order restingOrder =queue.peek();
    int tradeQuantity= Math.min(restingOrder.getQuantity(),order.getQuantity());
    long buyOrderId = (order.getSide()==Side.BUY)? order.getOrderId() :  restingOrder.getOrderId();
    long sellOrderId= (order.getSide()==Side.SELL)? order.getOrderId() : restingOrder.getOrderId();

    restingOrder.reduceQuantity(tradeQuantity);
    order.reduceQuantity(tradeQuantity);
    Trade trade=new Trade 
    (
     order.getTimestamp(),
     buyOrderId,
     sellOrderId,
     bestPrice,
     tradeQuantity);
     trades.add(trade);
     if (restingOrder.isFilled()){
         queue.poll();
         orderMap.remove(restingOrder.getOrderId());
     }
     
     if (queue.isEmpty()){

        oppositeSide.remove(bestPrice);

     } 
     }
     
     return trades;

     }





}