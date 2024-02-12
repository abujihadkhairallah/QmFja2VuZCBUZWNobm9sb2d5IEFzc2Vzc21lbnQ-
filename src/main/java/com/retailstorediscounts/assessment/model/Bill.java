package com.retailstorediscounts.assessment.model;

import lombok.Data;
import java.util.List;

@Data
public class Bill {

    private List<BillItem> items;

    public Bill(List<BillItem> asList) {
        this.items = asList;
    }

    
}

