package pl.edu.agh.mwo.invoice.product;

import java.math.BigDecimal;

public abstract class Product {

    private final BigDecimal excise = new BigDecimal("5.56");

    private final boolean isExciseApplicable;

    private BigDecimal appliedExcise;

    private final String name;

    private final BigDecimal price;

    private final BigDecimal taxPercent;

    protected Product(String name, BigDecimal price, BigDecimal tax, boolean isExciseApplicable) {
        if (name == null || name.equals("") || price == null || tax == null || tax.compareTo(new BigDecimal(0)) < 0
                || price.compareTo(new BigDecimal(0)) < 0) {
            throw new IllegalArgumentException();
        }
        this.name = name;
        this.price = price;
        this.taxPercent = tax;
        this.isExciseApplicable = isExciseApplicable;
        this.appliedExcise = getExcise();
    }

    public String getName() {
        return name;
    }

    public BigDecimal getExcise() {
        if (!isExciseApplicable) {
            return BigDecimal.ZERO;
        }
        return excise;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public BigDecimal getTaxPercent() {
        return taxPercent;
    }

    public BigDecimal getPriceWithTax() {
        return price.multiply(taxPercent).add(price).add(appliedExcise);
    }
}
