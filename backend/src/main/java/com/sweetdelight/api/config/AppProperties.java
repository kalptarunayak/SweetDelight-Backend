package com.sweetdelight.api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private Jwt jwt = new Jwt();
    private Razorpay razorpay = new Razorpay();
    private Shop shop = new Shop();
    private Notify notify = new Notify();
    private Cors cors = new Cors();

    public Jwt getJwt() {
        return jwt;
    }

    public void setJwt(Jwt jwt) {
        this.jwt = jwt;
    }

    public Razorpay getRazorpay() {
        return razorpay;
    }

    public void setRazorpay(Razorpay razorpay) {
        this.razorpay = razorpay;
    }

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    public Notify getNotify() {
        return notify;
    }

    public void setNotify(Notify notify) {
        this.notify = notify;
    }

    public Cors getCors() {
        return cors;
    }

    public void setCors(Cors cors) {
        this.cors = cors;
    }

    public static class Jwt {
        private String supabaseSecret = "test-supabase-jwt-secret-key-at-least-32-chars-long";

        public String getSupabaseSecret() {
            return supabaseSecret;
        }

        public void setSupabaseSecret(String supabaseSecret) {
            this.supabaseSecret = supabaseSecret;
        }
    }

    public static class Razorpay {
        private String keyId = "rzp_test_placeholder";
        private String keySecret = "placeholder_secret";
        private String webhookSecret = "placeholder_webhook_secret";

        public String getKeyId() {
            return keyId;
        }

        public void setKeyId(String keyId) {
            this.keyId = keyId;
        }

        public String getKeySecret() {
            return keySecret;
        }

        public void setKeySecret(String keySecret) {
            this.keySecret = keySecret;
        }

        public String getWebhookSecret() {
            return webhookSecret;
        }

        public void setWebhookSecret(String webhookSecret) {
            this.webhookSecret = webhookSecret;
        }
    }

    public static class Shop {
        private boolean codEnabled = true;

        public boolean isCodEnabled() {
            return codEnabled;
        }

        public void setCodEnabled(boolean codEnabled) {
            this.codEnabled = codEnabled;
        }
    }

    public static class Notify {
        private String mode = "console";
        private String whatsappToken = "";
        private String whatsappPhoneId = "";
        private String adminNumber = "+919876543210";

        public String getMode() {
            return mode;
        }

        public void setMode(String mode) {
            this.mode = mode;
        }

        public String getWhatsappToken() {
            return whatsappToken;
        }

        public void setWhatsappToken(String whatsappToken) {
            this.whatsappToken = whatsappToken;
        }

        public String getWhatsappPhoneId() {
            return whatsappPhoneId;
        }

        public void setWhatsappPhoneId(String whatsappPhoneId) {
            this.whatsappPhoneId = whatsappPhoneId;
        }

        public String getAdminNumber() {
            return adminNumber;
        }

        public void setAdminNumber(String adminNumber) {
            this.adminNumber = adminNumber;
        }
    }

    public static class Cors {
        private String allowedOrigins = "http://localhost:5173,https://your-github-username.github.io";

        public String getAllowedOrigins() {
            return allowedOrigins;
        }

        public void setAllowedOrigins(String allowedOrigins) {
            this.allowedOrigins = allowedOrigins;
        }

        public List<String> getAllowedOriginsList() {
            if (allowedOrigins == null || allowedOrigins.isBlank()) {
                return List.of("*");
            }
            return List.of(allowedOrigins.split(","));
        }
    }
}
