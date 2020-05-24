package pl.edu.agh.mwo.invoice;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pl.edu.agh.mwo.invoice.product.*;

import java.math.BigDecimal;
import java.util.Map;

public class InvoiceTest {
    private Invoice invoice;

    @Before
    public void createEmptyInvoiceForTheTest() {
        invoice = new Invoice();
    }

    @Test
    public void testEmptyInvoiceHasEmptySubtotal() {
        Assert.assertThat(BigDecimal.ZERO, Matchers.comparesEqualTo(invoice.getNetTotal()));
    }

    @Test
    public void testEmptyInvoiceHasEmptyTaxAmount() {
        Assert.assertThat(BigDecimal.ZERO, Matchers.comparesEqualTo(invoice.getTaxTotal()));
    }

    @Test
    public void testEmptyInvoiceHasEmptyTotal() {
        Assert.assertThat(BigDecimal.ZERO, Matchers.comparesEqualTo(invoice.getGrossTotal()));
    }

    @Test
    public void testInvoiceHasTheSameSubtotalAndTotalIfTaxIsZero() {
        Product taxFreeProduct = new TaxFreeProduct("Warzywa", new BigDecimal("199.99"));
        invoice.addProduct(taxFreeProduct);
        Assert.assertThat(invoice.getNetTotal(), Matchers.comparesEqualTo(invoice.getGrossTotal()));
    }

    @Test
    public void testInvoiceHasProperSubtotalForManyProducts() {
        invoice.addProduct(new TaxFreeProduct("Owoce", new BigDecimal("200")));
        invoice.addProduct(new DairyProduct("Maslanka", new BigDecimal("100")));
        invoice.addProduct(new OtherProduct("Wino", new BigDecimal("10")));
        Assert.assertThat(new BigDecimal("310"), Matchers.comparesEqualTo(invoice.getNetTotal()));
    }

    @Test
    public void testInvoiceHasProperTaxValueForManyProduct() {
        // tax: 0
        invoice.addProduct(new TaxFreeProduct("Pampersy", new BigDecimal("200")));
        // tax: 8
        invoice.addProduct(new DairyProduct("Kefir", new BigDecimal("100")));
        // tax: 2.30
        invoice.addProduct(new OtherProduct("Piwko", new BigDecimal("10")));
        Assert.assertThat(new BigDecimal("10.30"), Matchers.comparesEqualTo(invoice.getTaxTotal()));
    }

    @Test
    public void testInvoiceHasProperTotalValueForManyProduct() {
        // price with tax: 200
        invoice.addProduct(new TaxFreeProduct("Maskotki", new BigDecimal("200")));
        // price with tax: 108
        invoice.addProduct(new DairyProduct("Maslo", new BigDecimal("100")));
        // price with tax: 12.30
        invoice.addProduct(new OtherProduct("Chipsy", new BigDecimal("10")));
        Assert.assertThat(new BigDecimal("320.30"), Matchers.comparesEqualTo(invoice.getGrossTotal()));
    }

    @Test
    public void testInvoiceHasPropoerSubtotalWithQuantityMoreThanOne() {
        // 2x kubek - price: 10
        invoice.addProduct(new TaxFreeProduct("Kubek", new BigDecimal("5")), 2);
        // 3x kozi serek - price: 30
        invoice.addProduct(new DairyProduct("Kozi Serek", new BigDecimal("10")), 3);
        // 1000x pinezka - price: 10
        invoice.addProduct(new OtherProduct("Pinezka", new BigDecimal("0.01")), 1000);
        Assert.assertThat(new BigDecimal("50"), Matchers.comparesEqualTo(invoice.getNetTotal()));
    }

    @Test
    public void testInvoiceHasPropoerTotalWithQuantityMoreThanOne() {
        // 2x chleb - price with tax: 10
        invoice.addProduct(new TaxFreeProduct("Chleb", new BigDecimal("5")), 2);
        // 3x chedar - price with tax: 32.40
        invoice.addProduct(new DairyProduct("Chedar", new BigDecimal("10")), 3);
        // 1000x pinezka - price with tax: 12.30
        invoice.addProduct(new OtherProduct("Pinezka", new BigDecimal("0.01")), 1000);
        Assert.assertThat(new BigDecimal("54.70"), Matchers.comparesEqualTo(invoice.getGrossTotal()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvoiceWithZeroQuantity() {
        invoice.addProduct(new TaxFreeProduct("Tablet", new BigDecimal("1678")), 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvoiceWithNegativeQuantity() {
        invoice.addProduct(new DairyProduct("Zsiadle mleko", new BigDecimal("5.55")), -1);
    }

    @Test
    public void testInvoiceHasNumber() {
        int number = invoice.getNumber();
        Assert.assertTrue(number > 0);
    }

    @Test
    public void testTwoInvoicesHaveDifferentNumbers() {
        int number = invoice.getNumber();
        int number2 = new Invoice().getNumber();
        Assert.assertNotEquals(number, number2);
    }

    @Test
    public void testTwoInvoicesHaveConsequentNumbers() {
        int number = invoice.getNumber();
        int number2 = new Invoice().getNumber();
        Assert.assertTrue(number < number2);
    }

    @Test
    public void testInvoiceHeaderHasInvoiceNumber() {
        int number = invoice.getNumber();
        String invoiceHeader = invoice.getInvoiceHeader();
        Assert.assertTrue(invoiceHeader.matches("Faktura nr " + number + "\n"));
    }

    @Test
    public void testQuantityOfSameProductsOnOneInvoice() {
        invoice.addProduct(new DairyProduct("Kefir", new BigDecimal("50")), 3);
        invoice.addProduct(new DairyProduct("Kefir", new BigDecimal("50")), 4);
        Map<Product, Integer> products = invoice.getProducts();
        Product existingProduct = invoice.checkExistingProduct(new DairyProduct("Kefir", new BigDecimal("50")));
        Assert.assertEquals(7, (int) products.get(existingProduct));
    }

    @Test
    public void testQuantityOfProductsWithSameNameAndPriceButDifferentTypeOnOneInvoice() {
        invoice.addProduct(new DairyProduct("Kefir", new BigDecimal("50")), 3);
        invoice.addProduct(new TaxFreeProduct("Kefir", new BigDecimal("50")), 4);
        Map<Product, Integer> products = invoice.getProducts();
        Product existingProduct = invoice.checkExistingProduct(new TaxFreeProduct("Kefir", new BigDecimal("50")));
        Assert.assertEquals(4, (int) products.get(existingProduct));
    }

    @Test
    public void testCountSameProductsOnInvoiceFooter() {
        invoice.addProduct(new DairyProduct("Kefir", new BigDecimal("50")), 3);
        invoice.addProduct(new DairyProduct("Kefir", new BigDecimal("50")), 4);
        String printedInvoice = invoice.getInvoiceFooter();
        Assert.assertTrue(printedInvoice.contains("Liczba pozycji: 1"));
    }

    @Test
    public void testCountDifferentProductsOnInvoiceFooter() {
        invoice.addProduct(new DairyProduct("Kefir", new BigDecimal("50")), 3);
        invoice.addProduct(new TaxFreeProduct("Kefir", new BigDecimal("50")), 4);
        String printedInvoice = invoice.getInvoiceFooter();
        Assert.assertTrue(printedInvoice.contains("Liczba pozycji: 2"));
    }

    @Test
    public void testSameProductsOnPrintedInvoice() {
        invoice.addProduct(new DairyProduct("Kefir", new BigDecimal("50")), 3);
        invoice.addProduct(new DairyProduct("Kefir", new BigDecimal("50")), 4);
        String printedInvoice = invoice.print();
        Assert.assertTrue(printedInvoice.contains("Kefir, 7, 350, 378.00"));
    }

    @Test
    public void testDifferentProductsOnPrintedInvoice() {
        invoice.addProduct(new DairyProduct("Kefir", new BigDecimal("50")), 3);
        invoice.addProduct(new TaxFreeProduct("Kefir", new BigDecimal("50")), 4);
        String printedInvoice = invoice.print();
        Assert.assertTrue(printedInvoice.contains("Kefir, 3, 150, 162.00"));
        Assert.assertTrue(printedInvoice.contains("Kefir, 4, 200, 200"));
    }

    @Test
    public void testProductsWithExcise() {
        invoice.addProduct(new FuelCanister("Pb98", new BigDecimal("100")), 3);
        invoice.addProduct(new BottleOfWineProduct("Patyk", new BigDecimal("150")), 4);
        String printedInvoice = invoice.print();
        Assert.assertTrue(printedInvoice.contains("Pb98, 3, 300, 316.68"));
        Assert.assertTrue(printedInvoice.contains("Patyk, 4, 600, 760.24"));
    }


}
