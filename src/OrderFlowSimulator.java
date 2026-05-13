import java.util.Random;

public class OrderFlowSimulator
{
private final Random random;
private long nextOrderId;
private long timeStamp;

public OrderFlowSimulator()
{
    this.random=new Random();
    this.nextOrderId=1;
    this.timeStamp=1; 

}
public long getTimestamp(){
    return timeStamp;
}
public long getNextOrderId(){
    return nextOrderId;
}
public Order generateRandomLimitOrder(int lowerBoundPrice,int upperBoundPrice,int lowerBoundQuantity,int upperBoundQuantity)
{
    Side side;
    if (random.nextBoolean()){
       side=Side.BUY;
                             }
    else{
      side=Side.SELL;
        }
    int price= lowerBoundPrice + random.nextInt(upperBoundPrice+1-lowerBoundPrice); // Had to add 1 to include upperbound
    int quantity= lowerBoundQuantity + random.nextInt(upperBoundQuantity+1-lowerBoundQuantity);
    Order order= new Order(nextOrderId++, timeStamp,side,OrderType.LIMIT,price,quantity);
    timeStamp++; //right now times are not random.
    return order;


}

public Order generateRandomMarketOrder(int lowerBoundQuantity,int upperBoundQuantity)
{
    Side side;
    if (random.nextBoolean()){
       side=Side.BUY;
                             }
    else{
      side=Side.SELL;
        }
    int quantity= lowerBoundQuantity + random.nextInt(upperBoundQuantity+1-lowerBoundQuantity);
    Order order= new Order(nextOrderId++, timeStamp,side,OrderType.MARKET,0,quantity);
    timeStamp++;
    return order;
 }
 public Order generateRandomCancelOrder(long maxOrderId)
 {
   long cancelledOrderId =1+random.nextInt((int)maxOrderId);
   Order order= new Order(cancelledOrderId, timeStamp,Side.BUY,OrderType.CANCEL,0,0); //side and price are not relevant for cancel orders
   timeStamp++;
   return order;
 }
 public Order  generateRandomOrder(int probLimitOrder,int probMarketOrder,int lowerBoundPrice,int upperBoundPrice,int lowerBoundQuantity,int upperBoundQuantity )
 {
    if (probLimitOrder+probMarketOrder>100 || probLimitOrder<0 || probMarketOrder<0 ){
        throw new IllegalArgumentException("Invalid Probabilities");
    }
    int P= random.nextInt(100);
    
    if(P<probLimitOrder){

       return generateRandomLimitOrder(lowerBoundPrice, upperBoundPrice, lowerBoundQuantity, upperBoundQuantity);
    }

    else if (P<probLimitOrder+probMarketOrder)
    {
      return generateRandomMarketOrder(lowerBoundQuantity, upperBoundQuantity);
    }
    if (nextOrderId<=1){
        return generateRandomLimitOrder(lowerBoundPrice, upperBoundPrice, lowerBoundQuantity, upperBoundQuantity);

    }
    return generateRandomCancelOrder(nextOrderId-1);


 }

}