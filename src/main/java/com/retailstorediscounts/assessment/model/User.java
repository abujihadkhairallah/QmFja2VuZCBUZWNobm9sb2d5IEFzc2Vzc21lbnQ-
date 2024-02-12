package com.retailstorediscounts.assessment.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private Long id;
    private UserType type;
    private LocalDate createdDate;

    public User(UserType type) {
        this.type = type;
    }
}

