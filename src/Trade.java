public class Trade{
    private final long buyOrderId;
    private final long sellOrderId;
    private final long timestamp;
    private final int price;
    private final int quantity; 

    public Trade(long timestamp, long buyOrderId, long sellOrderId, int price, int quantity) {
        this.buyOrderId = buyOrderId;
        this.sellOrderId = sellOrderId;
        this.timestamp = timestamp;
        this.price = price;
        this.quantity = quantity;
    }
    public long getBuyOrderId() {
        return buyOrderId;
    }
    public long getSellOrderId() {
        return sellOrderId;
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
@Override
public String toString() {
    return "Trade{" +
            "timestamp=" + timestamp +
            ", buyOrderId=" + buyOrderId +
            ", sellOrderId=" + sellOrderId +
            ", price=" + price +
            ", quantity=" + quantity +
            '}';
}
}