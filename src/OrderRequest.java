public class OrderRequest
{
  private final OrderType type;
  private final Side side;
  private final int price;
  private final int quantity;
  private final long cancelOrderId;
  private final long timestamp;
  public OrderRequest(long timestamp,OrderType type, Side side, int price, int quantity,long cancelOrderId)
  {
    this.type=type;
    this.side=side;
    this.price=price;
    this.quantity=quantity;
    this.cancelOrderId=cancelOrderId;
    this.timestamp=timestamp;
  }
  public OrderType getType() {
    return type;}
    public Side getSide() {
        return side;
    }
    public int getPrice() {
        return price;
    }   
    public int getQuantity() {
        return quantity;
    }
    public long getCancelOrderId() {
        return cancelOrderId;
    }
    public long getTimestamp(){
        return timestamp;
    }


}