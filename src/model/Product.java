package model;

public class Product {
    private String itemCode;
    private String description;
    private String category;
    private double unitPrice;
    private int reorderLevel;
    private int currentStock;
    private String barcode;

    public Product() {
    }

    public Product(String itemCode, String description, String category, double unitPrice, int reorderLevel, int currentStock, String barcode) {
        this.itemCode = itemCode;
        this.description = description;
        this.category = category;
        this.unitPrice = unitPrice;
        this.reorderLevel = reorderLevel;
        this.currentStock = currentStock;
        this.barcode = barcode;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getReorderLevel() {
        return reorderLevel;
    }

    public void setReorderLevel(int reorderLevel) {
        this.reorderLevel = reorderLevel;
    }

    public int getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(int currentStock) {
        this.currentStock = currentStock;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public boolean isLowStock() {
        return currentStock <= reorderLevel;
    }

    @Override
    public String toString() {
        return "Product{" +
                "itemCode='" + itemCode + '\'' +
                ", description='" + description + '\'' +
                ", category='" + category + '\'' +
                ", unitPrice=" + unitPrice +
                ", reorderLevel=" + reorderLevel +
                ", currentStock=" + currentStock +
                ", barcode='" + barcode + '\'' +
                '}';
    }
}
