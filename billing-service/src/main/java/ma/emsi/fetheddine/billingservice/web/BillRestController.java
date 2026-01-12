package ma.emsi.fetheddine.billingservice.web;

import ma.emsi.fetheddine.billingservice.entities.Bill;
import ma.emsi.fetheddine.billingservice.feign.CustomerRestClient;
import ma.emsi.fetheddine.billingservice.feign.ProductRestClient;
import ma.emsi.fetheddine.billingservice.repository.BillRepository;
import ma.emsi.fetheddine.billingservice.repository.ProductItemRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class BillRestController {

    private BillRepository billRepository;

    private ProductItemRepository productItemRepository;

    private CustomerRestClient customerRestClient;

    private ProductRestClient productRestClient;

    public BillRestController(BillRepository billRepository, ProductItemRepository productItemRepository,
            CustomerRestClient customerRestClient, ProductRestClient productRestClient) {
        this.billRepository = billRepository;
        this.productItemRepository = productItemRepository;
        this.customerRestClient = customerRestClient;
        this.productRestClient = productRestClient;
    }

    /**
     * Get a single bill with enriched customer and product data.
     * Supports both /bills/{id} and /fullBill/{id} paths for compatibility.
     */
    @GetMapping(path = { "/bills/{id}", "/fullBill/{id}" })
    public Bill getBill(@PathVariable Long id) {
        Bill bill = billRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bill not found with id: " + id));
        enrichBill(bill);
        return bill;
    }

    /**
     * Get all bills for a specific customer with enriched data.
     */
    @GetMapping(path = "/fullBill/search/byCustomer/{customerId}")
    public List<Bill> getBillsByCustomer(@PathVariable Long customerId) {
        List<Bill> bills = billRepository.findByCustomerId(customerId);
        bills.forEach(this::enrichBill);
        return bills;
    }

    /**
     * Get all bills with enriched customer and product data.
     */
    @GetMapping(path = "/fullBill/all")
    public List<Bill> getAllBills() {
        List<Bill> bills = billRepository.findAll();
        bills.forEach(this::enrichBill);
        return bills;
    }

    /**
     * Helper method to enrich a bill with customer and product details.
     */
    private void enrichBill(Bill bill) {
        try {
            bill.setCustomer(customerRestClient.getCustomerById(bill.getCustomerId()));
        } catch (Exception e) {
            // Customer service might be unavailable, continue without customer data
            System.err.println("Could not fetch customer for bill " + bill.getId() + ": " + e.getMessage());
        }

        bill.getProductItems().forEach(productItem -> {
            try {
                productItem.setProduct(productRestClient.getProductById(productItem.getProductId()));
            } catch (Exception e) {
                // Product service might be unavailable, continue without product data
                System.err.println("Could not fetch product " + productItem.getProductId() + ": " + e.getMessage());
            }
        });
    }
}
