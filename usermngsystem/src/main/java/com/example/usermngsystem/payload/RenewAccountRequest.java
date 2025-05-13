package com.example.usermngsystem.payload;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RenewAccountRequest {
    String username;
    int yearsToExtends;
}
