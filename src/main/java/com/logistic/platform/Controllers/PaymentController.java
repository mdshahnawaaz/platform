package com.logistic.platform.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.logistic.platform.models.ProductRequest;
import com.logistic.platform.models.StripeResponse;
import com.logistic.platform.services.PaymentService;

@Controller
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/checkout")
    public ResponseEntity<StripeResponse> checkoutProducts(@RequestParam Long Amount) {
        ProductRequest productRequest=new ProductRequest();
        productRequest.setAmount(Amount*100);
        productRequest.setId("1");
        productRequest.setQuantity((long) 1);
        productRequest.setCurrency("INR");
        StripeResponse stripeResponse = paymentService.bookingPayment(productRequest);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(stripeResponse);
    }

}
