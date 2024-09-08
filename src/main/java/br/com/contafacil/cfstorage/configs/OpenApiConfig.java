package br.com.contafacil.cfstorage.configs;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${swagger.api-gateway-url}")
    String apiGatewayUrl;

    @Bean
    OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Storage Service API")
                        .description("ContaFacil Storage Service API")
                        .version("v1.0.0")
                        .contact(new Contact().name("BonnaroTec").email("lucasbonnafavaro@gmail.com")))
                .servers(List.of(new Server().url(apiGatewayUrl)));
    }
}
