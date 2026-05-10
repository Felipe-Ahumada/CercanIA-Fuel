package cl.fuelonline.station.integration.cne.config;

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
 * RestClient bean specific to calling the CNE API.
 * Only created when app.cne.enabled = true.
 *
 * Uses Apache HttpClient 5 (HttpComponentsClientHttpRequestFactory) instead of
 * the default SimpleClientHttpRequestFactory because the CNE API responds with
 * Content-Encoding: gzip and Apache HttpClient decompresses automatically.
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
