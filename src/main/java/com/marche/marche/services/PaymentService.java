package com.marche.marche.services;

import com.stripe.Stripe;
import org.springframework.stereotype.Service;

import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.checkout.SessionCreateParams;

@Service
public class PaymentService {

    public PaymentService() {
        // Initialiser Stripe avec votre clé secrète
        Stripe.apiKey = "sk_test_51PzdXHP9aFNqJB145aUNNWmROJAMdOGxzTDfpMUi4jRVREBTA0hw0rAJp3FYTJ23U47mcVn6XshWIE8TD6gBYaZk00JZikVkLh";
    }

    public Session createCheckoutSession(long prix) throws Exception {
        // Paramètres pour créer une session de paiement
        SessionCreateParams params = SessionCreateParams.builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:5173/payment-success")
                .setCancelUrl("http://localhost:5173/payment-failure")
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("mga")
                                                .setUnitAmount(prix) // Montant en centimes
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName("Produits")
                                                                .build())
                                                .build())
                                .setQuantity(1L)
                                .build())
                .build();

        // Créer la session de paiement
        return Session.create(params);
    }

    public String createPaymentIntent(Long amount) throws Exception {
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amount * 100) // Stripe accepte les paiements en centimes
                .setCurrency("mga") // ou une autre devise si besoin
                .addPaymentMethodType("card")
                .build();

        PaymentIntent intent = PaymentIntent.create(params);
        return intent.getClientSecret(); // Retourner le clientSecret
    }
}
