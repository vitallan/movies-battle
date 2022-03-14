package com.allanvital.moviesbattle.web.document;

import com.allanvital.moviesbattle.infra.ShowInDocumentPage;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Collections;


@Configuration
public class DocumentConfiguration {

    private ApiInfo apiInfo() {
        Contact contact = new Contact("Allan Vital", "https://allanvital.com", "allan.vital@gmail.com");
        return new ApiInfo("Movies Battle",
                "API for a game of battle of ratings between movies.",
                "1.0",
                "Terms of service",
                contact,
                "The MIT License",
                "https://opensource.org/licenses/MIT",
                Collections.emptyList());
    }

    @Bean
    public Docket docket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.withClassAnnotation(ShowInDocumentPage.class))
                .paths(PathSelectors.any())
                .build().useDefaultResponseMessages(false);
    }

}
