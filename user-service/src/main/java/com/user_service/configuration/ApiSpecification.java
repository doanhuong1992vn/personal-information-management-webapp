package com.user_service.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                title = "User Management API",
                description = "API endpoints to get data for User Management web/app",
                version = "1.0-alpha",
                contact = @Contact(
                        name = "GLSoft Company",
                        url = "https://www.webglsoft.com/",
                        email = "INFO@WEBGLSOFT.COM"
                )
        ),
        servers = {
                @Server(
                        url = "http://localhost:8080",
                        description = "REST Server URL"
                )
        },
        security = @SecurityRequirement(
                name = "BearerTokenAuth"
        )
)
@SecurityScheme(
        name = "BearerTokenAuth",
        description = "JWT Authorization",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class ApiSpecification {

}
