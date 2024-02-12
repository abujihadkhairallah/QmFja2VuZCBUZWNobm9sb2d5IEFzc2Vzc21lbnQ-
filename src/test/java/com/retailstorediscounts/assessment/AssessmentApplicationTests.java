package com.retailstorediscounts.assessment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import com.retailstorediscounts.assessment.model.Bill;
import com.retailstorediscounts.assessment.model.BillItem;
import com.retailstorediscounts.assessment.model.ItemType;
import com.retailstorediscounts.assessment.model.User;
import com.retailstorediscounts.assessment.model.UserType;
import com.retailstorediscounts.assessment.service.DiscountService;

import java.time.LocalDate;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class AssessmentApplicationTests {


    @InjectMocks
    private DiscountService discountService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCalculateNetPayableAmount_Employee() {
        User user = createUser(UserType.EMPLOYEE);
        Bill bill = createBill(
                new BillItem("Electronics", 500, ItemType.NON_GROCERY),
                new BillItem("Fruits", 50, ItemType.GROCERY));

        double netPayableAmount = discountService.calculateNetPayableAmount(bill, user);

        assertEquals(380.0, netPayableAmount); // 30% discount on non-grocery items
    }

    @Test
    public void testCalculateNetPayableAmount_Affiliate() {
        User user = createUser(UserType.AFFILIATE);
        Bill bill = createBill(
                new BillItem("Clothing", 200, ItemType.NON_GROCERY),
                new BillItem("Vegetables", 100, ItemType.GROCERY));

        double netPayableAmount = discountService.calculateNetPayableAmount(bill, user);

        assertEquals(270.0, netPayableAmount); // 10% discount on non-grocery items
    }

    @Test
    public void testCalculateNetPayableAmount_CustomerOverTwoYears() {
        LocalDate twoYearsAgo = LocalDate.now().minusYears(2);
        User user = createUser(UserType.CUSTOMER, twoYearsAgo);
        Bill bill = createBill(
                new BillItem("Books", 100, ItemType.NON_GROCERY),
                new BillItem("Dairy", 20, ItemType.GROCERY));

        double netPayableAmount = discountService.calculateNetPayableAmount(bill, user);

        assertEquals(110.0, netPayableAmount); // 5% discount on non-grocery items
    }

    @Test
    public void testCalculateNetPayableAmount_CustomerNotOverTwoYears() {
        LocalDate oneYearAgo = LocalDate.now().minusYears(1);
        User user = createUser(UserType.CUSTOMER, oneYearAgo);
        Bill bill = createBill(
                new BillItem("Furniture", 400, ItemType.NON_GROCERY),
                new BillItem("Grains", 30, ItemType.GROCERY));

        double netPayableAmount = discountService.calculateNetPayableAmount(bill, user);

        assertEquals(410.0, netPayableAmount); // No discount for being a customer under two years
    }

    @Test
    public void testCalculateNetPayableAmount_NoDiscountApplied() {
        User user = createUser(UserType.CUSTOMER);
        Bill bill = createBill(
                new BillItem("Furniture", 100, ItemType.NON_GROCERY),
                new BillItem("Vegetables", 20, ItemType.GROCERY));

        double netPayableAmount = discountService.calculateNetPayableAmount(bill, user);

        assertEquals(115.0, netPayableAmount); // no percentage discounts applied, but discount 5$ applicable for every 100$
    }

    @Test
    public void testCalculateNetPayableAmount_OnlyGroceryItems() {
        User user = createUser(UserType.EMPLOYEE);
        Bill bill = createBill(
                new BillItem("Fruits", 50, ItemType.GROCERY),
                new BillItem("Vegetables", 70, ItemType.GROCERY));

        double netPayableAmount = discountService.calculateNetPayableAmount(bill, user);

        assertEquals(115.0, netPayableAmount); // No percentage discounts applicable on grocery items
    }

    @Test
    public void testCalculateNetPayableAmount_MixedItemsWithNoUser() {
        Bill bill = createBill(
                new BillItem("Electronics", 500, ItemType.NON_GROCERY),
                new BillItem("Fruits", 50, ItemType.GROCERY));

        double netPayableAmount = discountService.calculateNetPayableAmount(bill, null);

        assertEquals(525.0, netPayableAmount); // No user provided, no percentage discounts applied, but discount 5$ applicable for every 100$
	  }


    private User createUser(UserType userType) {
        return new User(userType);
    }

    private User createUser(UserType userType, LocalDate createdDate) {
        User user = new User(userType);
        user.setCreatedDate(createdDate);
        return user;
    }

	public Bill createBill(BillItem... items) {
        return new Bill(Arrays.asList(items));
    }

}

