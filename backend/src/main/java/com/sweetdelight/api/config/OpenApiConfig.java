package com.sweetdelight.api.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    public static final String BEARER_AUTH = "BearerAuth";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("SweetDelight Cake & Chocolate Shop API")
                        .version("1.0.0")
                        .description("Production-grade backend API for an artisanal Indian cake and chocolate shop. "
                                + "Features include catalog browsing, server-side delivery radius calculation via Haversine formula, "
                                + "promotional banners & coupons, verified Razorpay payment integration, order lifecycle tracking, "
                                + "party room reservation with overlap prevention, and WhatsApp/Console notifications.")
                        .contact(new Contact().name("SweetDelight Support").email("support@sweetdelight.com"))
                        .license(new License().name("Apache 2.0").url("https://springdoc.org")))
                .tags(List.of(
                        new Tag().name("Catalog").description("Public endpoints to browse categories, products, and variants"),
                        new Tag().name("Delivery").description("Public delivery check using Haversine formula against shop radius"),
                        new Tag().name("Banners").description("Public active banners and promotional coupons"),
                        new Tag().name("Orders").description("Customer order placement and retrieval"),
                        new Tag().name("Payments").description("Razorpay order generation, signature verification, and webhooks"),
                        new Tag().name("Tracking").description("Customer real-time order status timeline and rider location"),
                        new Tag().name("Party Rooms").description("Customer party room reservation for cake cutting"),
                        new Tag().name("Admin Settings").description("Store manager delivery radius and shop coordinates"),
                        new Tag().name("Admin Orders").description("Store manager order status updates, rider coordinates, and refunds"),
                        new Tag().name("Admin Banners").description("Store manager promotional banners and coupon codes CRUD"),
                        new Tag().name("Admin Products").description("Store manager product catalog and variant CRUD")
                ))
                .addSecurityItem(new SecurityRequirement().addList(BEARER_AUTH))
                .components(new Components()
                        .addSecuritySchemes(BEARER_AUTH, new SecurityScheme()
                                .name(BEARER_AUTH)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Paste your Supabase access token (JWT). Roles: CUSTOMER or ADMIN.")));
    }
}
