package com.retailstorediscounts.assessment.model;

import lombok.Data;
import java.util.List;

@Data
public class Bill {
    private List<BillItems> items;
}

