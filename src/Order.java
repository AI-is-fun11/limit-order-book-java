
public class Order {
    private final long orderId;
    private final long timestamp;
    private final Side side;
    private final OrderType type;
    private final int price;
    private int quantity;
    private final int originalQuantity;
    public Order(long orderId, long timestamp, Side side, OrderType type, int price, int quantity) {
        this.orderId = orderId;
        this.timestamp = timestamp;
        this.side = side;
        this.type = type;
        this.price = price;
        this.quantity = quantity;
        this.originalQuantity=quantity;
    }
    public long getOrderId() {
        return orderId;
    }
    public Side getSide() {
        return side;
    }
    public long getTimestamp() {
        return timestamp;
    }
    public int getPrice() {
        return price;
    }
    public int getQuantity() {
        return quantity;
    }
    public boolean isFilled() {
    return quantity == 0;
}
    public OrderType getType() {
    return type;
}
    public int getOriginalQuantity(){
        return originalQuantity;
    } 
public void reduceQuantity(int filledQuantity){
    if(filledQuantity < 0 || filledQuantity > quantity){
        throw new IllegalArgumentException("Filled quantity cannot be greater than the remaining quantity.");
    }
    this.quantity -= filledQuantity;
}
@Override
public String toString(){
       return "Order{" +
            "orderId=" + orderId +
            ", timestamp=" + timestamp +
            ", side=" + side +
            ", type=" + type +
            ", price=" + price +
            ", originalQuantity="+ originalQuantity+
            ", leftQuantity=" + quantity +
            '}';


}
}
