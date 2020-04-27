package pl.edu.agh.mwo.invoice;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import pl.edu.agh.mwo.invoice.product.Product;

public class Invoice {

    private static int nextInvoiceID = 1;
    private int invoiceId;
    private Map<Product, Integer> products = new HashMap<>();

    public Invoice() {
        this.invoiceId = nextInvoiceID;
        nextInvoiceID++;
    }

    public void addProduct(Product product) {
        addProduct(product, 1);
    }

    public void addProduct(Product product, Integer quantity) {
        isProductValid(product, quantity);
        Product existingProduct = checkExistingProduct(product);
        if (existingProduct != null) {
            products.put(existingProduct, products.get(existingProduct) + quantity);
        } else {
            products.put(product, quantity);
        }
    }

    public Product checkExistingProduct(Product product) {
        for (Product key : products.keySet()) {
            if (key.getName().equals(product.getName()) &&
                key.getPrice().equals(product.getPrice()) &&
                key.getClass().equals(product.getClass())) {
                return key;
            }
        }
        return null;
    }

    public void isProductValid(Product product, Integer quantity) {
        if (product == null || quantity <= 0) {
            throw new IllegalArgumentException();
        }
    }

    public BigDecimal getNetTotal() {
        BigDecimal totalNet = BigDecimal.ZERO;
        for (Product product : products.keySet()) {
            BigDecimal quantity = new BigDecimal(products.get(product));
            totalNet = totalNet.add(product.getPrice().multiply(quantity));
        }
        return totalNet;
    }

    public BigDecimal getTaxTotal() {
        return getGrossTotal().subtract(getNetTotal());
    }

    public BigDecimal getGrossTotal() {
        BigDecimal totalGross = BigDecimal.ZERO;
        for (Product product : products.keySet()) {
            BigDecimal quantity = new BigDecimal(products.get(product));
            totalGross = totalGross.add(product.getPriceWithTax().multiply(quantity));
        }
        return totalGross;
    }

    public int getNumber() {
        return invoiceId;
    }

    public int getProductsCount() {
        return products.size();
    }

    public Map<Product, Integer> getProducts() {
        return products;
    }

    public String getProductsListed() {
        StringBuilder productList = new StringBuilder();
        products.forEach((product, quantity) -> {
            String productData =
                    product.getName() + ", " +
                    quantity  + ", " +
                    (product.getPrice().multiply(BigDecimal.valueOf(quantity))) + ", " +
                    (product.getPriceWithTax().multiply(BigDecimal.valueOf(quantity))) + "\n";
            productList.append(productData);
        });
        return productList.toString();
    }

    public String getInvoiceHeader() {
        return "Faktura nr " + invoiceId + "\n";
    }

    public String getInvoiceFooter() {
        return "Liczba pozycji: " + getProductsCount();
    }

    public String print() {
        return getInvoiceHeader() +
                getProductsListed() +
                getInvoiceFooter();
    }
}
