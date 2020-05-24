package pl.edu.agh.mwo.invoice.product;

import java.math.BigDecimal;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import pl.edu.agh.mwo.invoice.product.Product;

public class ProductTest {
    @Test
    public void testProductNameIsCorrect() {
        Product product = new OtherProduct("buty", new BigDecimal("100"));
        Assert.assertEquals("buty", product.getName());
    }

    @Test
    public void testProductPriceAndTaxWithDefaultTax() {
        Product product = new OtherProduct("Ogorki", new BigDecimal("100"));
        Assert.assertThat(new BigDecimal("100"), Matchers.comparesEqualTo(product.getPrice()));
        Assert.assertThat(new BigDecimal("0.23"), Matchers.comparesEqualTo(product.getTaxPercent()));
    }

    @Test
    public void testProductPriceAndTaxWithDairyProduct() {
        Product product = new DairyProduct("Szarlotka", new BigDecimal("100"));
        Assert.assertThat(new BigDecimal("100"), Matchers.comparesEqualTo(product.getPrice()));
        Assert.assertThat(new BigDecimal("0.08"), Matchers.comparesEqualTo(product.getTaxPercent()));
    }

    @Test
    public void testPriceWithTax() {
        Product product = new DairyProduct("Oscypek", new BigDecimal("100"));
        Assert.assertThat(new BigDecimal("108"), Matchers.comparesEqualTo(product.getPriceWithTax()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testProductWithNullName() {
        new OtherProduct(null, new BigDecimal("100"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testProductWithEmptyName() {
        new TaxFreeProduct("", new BigDecimal("100"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testProductWithNullPrice() {
        new DairyProduct("Banany", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testProductWithNegativePrice() {
        new TaxFreeProduct("Mandarynki", new BigDecimal("-1"));
    }

    @Test
    public void testExciseForApplicableProduct() {
        Product product = new BottleOfWineProduct("Tokaj", new BigDecimal("50"));
        Assert.assertThat(new BigDecimal("5.56"), Matchers.comparesEqualTo(product.getExcise()));
    }

    @Test
    public void testExciseForNonApplicableProduct() {
        Product product = new DairyProduct("Jogurt", new BigDecimal("10.0"));
        Assert.assertThat(BigDecimal.ZERO, Matchers.comparesEqualTo(product.getExcise()));
    }

    @Test
    public void testExciseIsTheSameForApplicableProducts() {
        Product product1 = new BottleOfWineProduct("Lesny Dzban", new BigDecimal("5"));
        Product product2 = new BottleOfWineProduct("Patyk", new BigDecimal("150"));
        Product product3 = new FuelCanister("Pb95", new BigDecimal("1500"));
        Assert.assertThat(new BigDecimal("5.56"), Matchers.comparesEqualTo(product1.getExcise()));
        Assert.assertThat(new BigDecimal("5.56"), Matchers.comparesEqualTo(product2.getExcise()));
        Assert.assertThat(new BigDecimal("5.56"), Matchers.comparesEqualTo(product3.getExcise()));
    }

    @Test
    public void testPriceForProductsWithApplicableTaxAndExcise() {
        Product product = new BottleOfWineProduct("Amarena", new BigDecimal("100"));
        Assert.assertThat(new BigDecimal("128.56"), Matchers.comparesEqualTo(product.getPriceWithTax()));
    }

    @Test
    public void testPriceForProductsWithApplicableExciseButWithoutTax() {
        Product product = new FuelCanister("Pb98", new BigDecimal("100"));
        Assert.assertThat(new BigDecimal("105.56"), Matchers.comparesEqualTo(product.getPriceWithTax()));
    }



}
