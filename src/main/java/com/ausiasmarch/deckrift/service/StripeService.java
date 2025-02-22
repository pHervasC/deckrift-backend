package com.ausiasmarch.deckrift.service;

import com.ausiasmarch.deckrift.entity.CompraEntity;
import com.ausiasmarch.deckrift.entity.UsuarioEntity;
import com.ausiasmarch.deckrift.repository.CompraRepository;
import com.ausiasmarch.deckrift.repository.UsuarioRepository;
import com.stripe.Stripe;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StripeService {

    @Value("${stripe.secret.key}")
    private String secretKey;

    @Value("${stripe.success.url}")
    private String successUrl;

    @Value("${stripe.cancel.url}")
    private String cancelUrl;

    @Autowired
    CompraRepository compraRepository;

    @Autowired
    UsuarioRepository usuarioRepository;

    public String crearSesionDePago(Long usuarioId, int cantidadMonedas, double precio) {
        try {
            Stripe.apiKey = secretKey;

            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(successUrl + "?session_id={CHECKOUT_SESSION_ID}")
                    .setCancelUrl(cancelUrl)
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setQuantity(1L)
                                    .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                            .setCurrency("eur")
                                            .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                    .setName(cantidadMonedas + " Monedas")
                                                    .build())
                                            .setUnitAmount((long) (precio * 100))
                                            .build())
                                    .build())
                    .build();

            Session session = Session.create(params);

            // Guardar la compra en la BD
            UsuarioEntity usuario = usuarioRepository.findById(usuarioId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            CompraEntity compra = new CompraEntity();
            compra.setUsuario(usuario);
            compra.setCantidadMonedas(cantidadMonedas);
            compra.setGasto(precio);
            compra.setStripeSessionId(session.getId());
            compra.setEstado("pendiente");
            compraRepository.save(compra);

            return session.getId();
        } catch (Exception e) {
            throw new RuntimeException("Error al crear sesión de pago", e);
        }
    }

    public boolean confirmarPago(String sessionId) {
    try {
        Stripe.apiKey = secretKey;
        Session session = Session.retrieve(sessionId);

        if ("complete".equals(session.getStatus())) {
            Optional<CompraEntity> compraOptional = compraRepository.findByStripeSessionId(sessionId);

            if (compraOptional.isPresent()) {
                CompraEntity compra = compraOptional.get();
                UsuarioEntity usuario = compra.getUsuario();

                if (!"pendiente".equals(compra.getEstado())) {
                    return true;
                }

                // Actualizar las monedas del usuario
                Integer monedasActuales = usuario.getMonedas();
                if (monedasActuales == null) {
                    monedasActuales = 0;
                }
                usuario.setMonedas(monedasActuales + compra.getCantidadMonedas());
                usuarioRepository.save(usuario);

                // Actualizar el estado de la compra
                compra.setEstado("completado");
                compraRepository.save(compra);

                return true;
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return false;
}
}