/**
 * Класс, содержащий информацию о товаре.
 */
public class ProductInfo {
    private int price;
    private int amount;

    public ProductInfo(int price, int amount) {
        this.price = price;
        this.amount = amount;
    }

    public int getPrice() {
        return price;
    }

    public int getAmount() {
        return amount;
    }
}
