package cl.fuelonline.security.config;

import cl.fuelonline.security.application.service.FirebaseTokenService;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Initializes the Firebase Admin SDK when credentials are configured.
 * Supports two modes:
 *   1. FIREBASE_CREDENTIALS_JSON env var with the JSON content (ideal for Railway/cloud).
 *   2. File path via app.security.firebase.credentials-path (local dev).
 * If neither is set, beans are not created and the filter falls back to dev mode.
 */
@Slf4j
@Configuration
@ConditionalOnExpression(
    "'${FIREBASE_CREDENTIALS_JSON:}' != '' || '${app.security.firebase.credentials-path:}' != ''")
public class FirebaseConfig {

    @Bean
    public FirebaseApp firebaseApp(SecurityProperties props) throws Exception {
        if (!FirebaseApp.getApps().isEmpty()) {
            return FirebaseApp.getInstance();
        }

        String json = System.getenv("FIREBASE_CREDENTIALS_JSON");
        InputStream is;

        if (json != null && !json.isBlank()) {
            log.info("Initializing Firebase Admin SDK from FIREBASE_CREDENTIALS_JSON env var");
            is = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
        } else {
            String path = props.firebase().credentialsPath();
            log.info("Initializing Firebase Admin SDK with credentials file: {}", path);
            is = new FileInputStream(path);
        }

        try (is) {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(is))
                    .build();
            return FirebaseApp.initializeApp(options);
        }
    }

    @Bean
    public FirebaseAuth firebaseAuth(FirebaseApp app) {
        return FirebaseAuth.getInstance(app);
    }

    @Bean
    public FirebaseTokenService firebaseTokenService(FirebaseAuth firebaseAuth) {
        return new FirebaseTokenService(firebaseAuth);
    }
}
