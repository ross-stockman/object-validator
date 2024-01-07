package dev.stockman.validator;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record Address(
    @Size(max=20) String street,
    String apartment,
    @NotNull(message = "city is required") String city
) { }
