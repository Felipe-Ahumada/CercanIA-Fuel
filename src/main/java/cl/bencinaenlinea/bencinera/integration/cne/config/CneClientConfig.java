package cl.bencinaenlinea.bencinera.integration.cne.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestClient;

import java.util.List;

/**
 * Bean RestClient especifico para llamar la API CNE.
 * Solo se crea cuando app.cne.enabled = true.
 *
 * Usa Apache HttpClient 5 (HttpComponentsClientHttpRequestFactory) en lugar del
 * SimpleClientHttpRequestFactory por defecto, porque la API CNE responde con
 * Content-Encoding: gzip y Apache HttpClient descomprime automaticamente.
 */
@Configuration
@EnableConfigurationProperties(CneProperties.class)
@ConditionalOnProperty(prefix = "app.cne", name = "enabled", havingValue = "true")
public class CneClientConfig {

    @Bean("cneRestClient")
    public RestClient cneRestClient(CneProperties props) {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout((int) props.timeout().toMillis());
        factory.setConnectionRequestTimeout((int) props.timeout().toMillis());

        return RestClient.builder()
                .baseUrl(props.apiUrl())
                .requestFactory(factory)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + props.token())
                .messageConverters(converters -> converters.stream()
                        .filter(c -> c instanceof MappingJackson2HttpMessageConverter)
                        .map(c -> (MappingJackson2HttpMessageConverter) c)
                        .findFirst()
                        .ifPresent(c -> c.setSupportedMediaTypes(List.of(
                                MediaType.APPLICATION_JSON,
                                MediaType.valueOf("text/json"),
                                MediaType.TEXT_HTML,
                                MediaType.TEXT_PLAIN,
                                MediaType.ALL))))
                .build();
    }
}
