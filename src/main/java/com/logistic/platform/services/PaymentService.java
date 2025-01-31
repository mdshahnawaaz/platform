package com.logistic.platform.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.logistic.platform.models.ProductRequest;
import com.logistic.platform.models.StripeResponse;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

@Service
public class PaymentService {
    
    @Value("${stripe.secretKey}")
    private  String sceretKey;

    public StripeResponse bookingPayment(ProductRequest productRequest)
    {
        Stripe.apiKey=sceretKey;

        SessionCreateParams.LineItem.PriceData.ProductData productData =
                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                            .setName(productRequest.getId())
                            .build();

        SessionCreateParams.LineItem.PriceData priceData =
                    SessionCreateParams.LineItem.PriceData.builder()
                                    .setCurrency(productRequest.getCurrency() != null ? productRequest.getCurrency() : "USD")
                                    .setUnitAmount(productRequest.getAmount())
                                    .setProductData(productData)
                                    .build();

        SessionCreateParams.LineItem lineItem =
                    SessionCreateParams.LineItem.builder()
                                    .setQuantity(productRequest.getQuantity())
                                    .setPriceData(priceData)
                                    .build();

        SessionCreateParams params =
                    SessionCreateParams.builder()
                                .setMode(SessionCreateParams.Mode.PAYMENT)
                                .setSuccessUrl("http://localhost:8080/logistics/success")
                                .setCancelUrl("http://localhost:8080/logistics/cancel")
                                .addLineItem(lineItem)
                                .build();

            // Create new session
            System.out.println(sceretKey);
        Session session = null;
            try {
                session = Session.create(params);
            } catch (StripeException e) {
                //log the error
                e.printStackTrace();
                throw new RuntimeException("Stripe session creation failed: " + e.getMessage());
            }
            System.out.println(session);
            return StripeResponse.builder()
                    .status("SUCCESS")
                    .message("Payment session created ")
                    .sessionId(session.getId())
                    .sessionUrl(session.getUrl())
                    .build();
    }

}
