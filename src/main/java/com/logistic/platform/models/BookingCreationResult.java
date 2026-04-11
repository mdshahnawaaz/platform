package com.logistic.platform.models;

public record BookingCreationResult(
        Booking booking,
        PricingQuote pricingQuote) {
}
