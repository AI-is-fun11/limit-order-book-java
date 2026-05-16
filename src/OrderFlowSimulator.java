import java.util.Random;

public class OrderFlowSimulator
{
private final Random random;
private long timeStamp;

public OrderFlowSimulator()
{
    this.random=new Random();
    this.timeStamp=1; 

}
public long getTimestamp(){
    return timeStamp;
}
public OrderRequest generateRandomLimitOrder(int lowerBoundPrice,int upperBoundPrice,int lowerBoundQuantity,int upperBoundQuantity)
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
    OrderRequest request= new OrderRequest(timeStamp, OrderType.LIMIT, side, price, quantity, 0);
    timeStamp++; //right now times are not random.
    return request;


}

public OrderRequest generateRandomMarketOrder(int lowerBoundQuantity,int upperBoundQuantity)
{
    Side side;
    if (random.nextBoolean()){
       side=Side.BUY;
                             }
    else{
      side=Side.SELL;
        }
    int quantity= lowerBoundQuantity + random.nextInt(upperBoundQuantity+1-lowerBoundQuantity);
    OrderRequest request= new OrderRequest(timeStamp, OrderType.MARKET, side, 0, quantity, 0);
    timeStamp++;
    return request;
 }
 public OrderRequest generateRandomCancelOrder(long maxOrderId)
 {
   long cancelledOrderId =1+random.nextInt((int)maxOrderId);
   OrderRequest request= new OrderRequest(timeStamp, OrderType.CANCEL, Side.BUY, 0, 0, cancelledOrderId); //side and price are not relevant for cancel orders
   timeStamp++;
   return request;
 }
 public OrderRequest  generateRandomOrder(int probLimitOrder,int probMarketOrder,int lowerBoundPrice,int upperBoundPrice,int lowerBoundQuantity,int upperBoundQuantity, long maxOrderId )
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
    if (maxOrderId<1){
        return generateRandomLimitOrder(lowerBoundPrice, upperBoundPrice, lowerBoundQuantity, upperBoundQuantity);

    }
    return generateRandomCancelOrder(maxOrderId);


 }

 

}