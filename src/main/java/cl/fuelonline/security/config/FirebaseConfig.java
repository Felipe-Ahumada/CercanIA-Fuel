package cl.fuelonline.security.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Inicializa el Firebase Admin SDK cuando hay credenciales configuradas.
 * Si no hay credenciales, los beans no se crean y el filtro entra en modo dev.
 */
@Slf4j
@Configuration
@ConditionalOnExpression("'${app.security.firebase.credentials-path:}' != ''")
public class FirebaseConfig {

    @Bean
    public FirebaseApp firebaseApp(SecurityProperties props) throws Exception {
        if (!FirebaseApp.getApps().isEmpty()) {
            return FirebaseApp.getInstance();
        }

        String path = props.firebase().credentialsPath();
        log.info("Inicializando Firebase Admin SDK con credenciales: {}", path);

        try (InputStream is = new FileInputStream(path)) {
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
}
