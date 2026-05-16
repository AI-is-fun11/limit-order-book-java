import java.util.List;
//We are adding this class cause we want the matching engine to crete orderiDS and allow all players to be able to  track the results of their orders.
public class OrderResult
{
 private final  Long acceptedOrderId; //Long enables null values
 private final boolean cancelSuccess;
 private final List<Trade> trades;
 private final Integer remainingQuantity;

 public OrderResult(Long acceptedOrderId, boolean cancelSuccess, List<Trade> trades, Integer remainingQuantity)
 {
    this.acceptedOrderId=acceptedOrderId;
    this.cancelSuccess=cancelSuccess;
    this.trades=trades;
    this.remainingQuantity=remainingQuantity;

 }

 public Long getAcceptedOrderId() {
    return acceptedOrderId; }
 
 public boolean isCancelSuccess() {
    return cancelSuccess;}
public List<Trade> getTrades() {
    return trades;}
public Integer getRemainingQuantity() {
    return remainingQuantity;
}


}