import java.util.HashMap;
import java.util.Map;


public class MarketMaker {
    private int inventory;
    private final int baseSpread;
    private final int inventoryPenalty;
    private final Map<Long, Side> activeOrders;
    private final Map<Long, Integer> activeOrderQuantities;
    private  long cash;
    private final long  initialCash;
    public MarketMaker(int baseSpread, int inventoryPenalty,int cashIn) {
        this.inventory = 0;
        this.baseSpread = baseSpread;
        this.inventoryPenalty = inventoryPenalty;
        this.activeOrders = new HashMap<>();
        this.activeOrderQuantities = new HashMap<>();
        this.cash = cashIn;
        this.initialCash = cashIn;
    }



    public int getInventory() {
        return inventory;
    }
    public long getCash() {
    return cash;
                          }
    public Map<Long, Side> getActiveOrders() {
    return activeOrders;
      }



    public Integer calculateBidQuote(OrderBook orderBook){
     Double midPrice=orderBook.getMidPrice();
     if (midPrice==null){
        return null;
     }
     int inventoryAdjustment=inventory*inventoryPenalty;
     return (int)(midPrice-baseSpread-inventoryAdjustment); // Convert to integer
    }



    public Integer calculateAskQuote(OrderBook orderBook) {
    Double midPrice = orderBook.getMidPrice();

    if (midPrice == null) {
        return null;
    }

    int inventoryAdjustment = inventory * inventoryPenalty;  // Penalty is proportional to holding

    return (int) Math.round(midPrice + baseSpread - inventoryAdjustment); //Penalty means if more inverntory we are liklier to sell with lower price and buy with higher price to reduce inventory.    
    
    }



    public OrderRequest[] generateQuotes(OrderBook orderBook, long timestamp, int quantity) {
            if (calculateBidQuote(orderBook) == null || calculateAskQuote(orderBook) == null) 
                                           {
                return new OrderRequest[0];     
                                           }
            Integer bidQuote = calculateBidQuote(orderBook);
            Integer askQuote = calculateAskQuote(orderBook);
            OrderRequest bidOrder = new OrderRequest(timestamp,OrderType.LIMIT,Side.BUY, bidQuote,quantity, 0); 
            OrderRequest askOrder = new OrderRequest(timestamp,OrderType.LIMIT,Side.SELL, askQuote,quantity, 0);
            return new OrderRequest[]{bidOrder,askOrder};          }

    public void resultRecord(OrderRequest orderRequest,OrderResult orderResult)
    {      
        if (orderResult.getAcceptedOrderId()==null){
            return;
        }
        
        
        if (orderResult.getRemainingQuantity()!=null && orderResult.getRemainingQuantity()>0){
            activeOrders.put(orderResult.getAcceptedOrderId(), orderRequest.getSide());
            activeOrderQuantities.put(orderResult.getAcceptedOrderId(), orderResult.getRemainingQuantity());
          }}

public void adjustInventory(Trade trade){
    if (activeOrders.containsKey(trade.getBuyOrderId())) {
        inventory += trade.getQuantity();
        reduceActiveOrderQuantity(trade.getBuyOrderId(), trade.getQuantity());
        cash-= trade.getQuantity()*trade.getPrice();  
        }

    if (activeOrders.containsKey(trade.getSellOrderId())) {
        inventory -= trade.getQuantity();
        reduceActiveOrderQuantity(trade.getSellOrderId(), trade.getQuantity());
        cash+= trade.getQuantity()*trade.getPrice();     
        }
    }


private void reduceActiveOrderQuantity(Long orderId, int quantityFilled)

{
   Integer remainingQty= activeOrderQuantities.get(orderId);
   if (remainingQty==null){

    return;
   }

   if (quantityFilled>=remainingQty){
    activeOrderQuantities.remove(orderId);
    activeOrders.remove(orderId);
   }
   else{
      activeOrderQuantities.put(orderId, remainingQty-quantityFilled);


   }  }

   public Double getMarkToMarketValue(OrderBook orderBook)  // Calculate a heuristic PnL using midprice
   {
    Double midPrice = orderBook.getMidPrice();

    if (midPrice == null) {
        return null;
    }

    return cash + inventory * midPrice;
}
   public Double getProfit(OrderBook orderBook)  // Calculate a heuristic PnL using midprice
   {
    Double midPrice = orderBook.getMidPrice();

    if (midPrice == null) {
        return null;
    }

    return cash + inventory * midPrice- initialCash;
}
    public OrderRequest[] generateCancelRequests(long timestamp){
         OrderRequest[] cancelOrderRequests = new OrderRequest[activeOrders.size()];
         int idx=0;
        for (Long orderId : activeOrders.keySet()) {
        cancelOrderRequests[idx] = new OrderRequest(
            timestamp,
            OrderType.CANCEL,
            Side.BUY,
            0,
            0,
            orderId
        );
        idx++;
    }

       return cancelOrderRequests;

    }

    public void recordCancellation( OrderRequest cancelRequest, OrderResult cancelResult) {
        if (!cancelResult.isCancelSuccess()) {
            return;
        }
        activeOrders.remove(cancelRequest.getCancelOrderId());
        activeOrderQuantities.remove(cancelRequest.getCancelOrderId());
    }




}



