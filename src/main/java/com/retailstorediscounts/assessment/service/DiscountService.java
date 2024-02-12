package com.retailstorediscounts.assessment.service;

import org.springframework.stereotype.Service;

import com.retailstorediscounts.assessment.model.Bill;
import com.retailstorediscounts.assessment.model.BillItem;
import com.retailstorediscounts.assessment.model.ItemType;
import com.retailstorediscounts.assessment.model.User;
import com.retailstorediscounts.assessment.model.UserType;

import java.time.LocalDate;

@Service
public class DiscountService {

    public double calculateNetPayableAmount(Bill bill, User user) {
        double totalAmount = calculateTotalAmount(bill);
        double discount = calculateDiscount(bill, user);
        double payableAmount =  totalAmount - discount;
        double netPayableAmount =  calculateDiscountForEveryOneHundredDollar(payableAmount);

        return  netPayableAmount;
    }

    private double calculateDiscountForEveryOneHundredDollar(double payableAmount) {
            return payableAmount - ((int) ((payableAmount) / 100)) * 5;
    }

    private double calculateTotalAmount(Bill bill) {
        return bill.getItems().stream().mapToDouble(BillItem::getPrice).sum();
    }

    private double calculateDiscount(Bill bill, User user) {
        double discount = 0;
        for (BillItem item : bill.getItems()) {
            if (item.getType() != ItemType.GROCERY) {
                discount += calculateItemDiscount(item, user);
            }
        }
        return discount;
    }

    private double calculateItemDiscount(BillItem item, User user) {
        double discount = 0;
        if (user != null && user.getType() != null) {
            if (user.getType() == UserType.EMPLOYEE) {
                discount = item.getPrice() * 0.3;
            } else if (user.getType() == UserType.AFFILIATE) {
                discount = item.getPrice() * 0.1;
            } else if (user.getType() == UserType.CUSTOMER && isCustomerSinceOverTwoYears(user)) {
                discount = item.getPrice() * 0.05;
            }
        }
        return discount;
    }

    private boolean isCustomerSinceOverTwoYears(User user) {
        LocalDate twoYearsAgo = LocalDate.now().minusYears(2).plusDays(1);
        return user.getCreatedDate() != null && (user.getCreatedDate().isBefore(twoYearsAgo));
    }
}
