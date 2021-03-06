package com.allanvital.moviesbattle.security.endpoint;

import com.allanvital.moviesbattle.infra.ShowInDocumentPage;
import com.allanvital.moviesbattle.security.JwtTokenUtil;
import com.allanvital.moviesbattle.security.dto.AuthRequest;
import com.allanvital.moviesbattle.security.dto.AuthenticationTokenResponse;
import com.allanvital.moviesbattle.web.endpoint.resource.BattleResource;
import com.allanvital.moviesbattle.web.model.User;
import com.allanvital.moviesbattle.web.repository.UserRepository;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@ShowInDocumentPage
@RestController
@Api(tags = { "Authentication" })
@RequestMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
public class AuthenticationEndpoint {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    public AuthenticationEndpoint(AuthenticationManager authenticationManager,
                                  JwtTokenUtil jwtTokenUtil, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Operation(summary = "Log in the user",
            tags = {"Authentication"},
            responses = {
                    @ApiResponse(description = "User is logged and the JWT token is displayed in the body", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthenticationTokenResponse.class))),
                    @ApiResponse(responseCode = "401", description = "Login fail")})
    @PostMapping("/login")
    public ResponseEntity<AuthenticationTokenResponse> login(@RequestBody AuthRequest request) {
        try {
            Authentication authenticate = authenticationManager
                    .authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    request.getEmail(), request.getPassword()
                            )
                    );

            User user = (User) authenticate.getPrincipal();
            String token = jwtTokenUtil.generateAccessToken(user);
            AuthenticationTokenResponse response = new AuthenticationTokenResponse(token);
            return ResponseEntity.ok()
                    .header(
                            HttpHeaders.AUTHORIZATION,
                            token
                    )
                    .body(response);
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @Operation(summary = "Create a new user",
            tags = {"Authentication"},
            responses = {
                    @ApiResponse(description = "User is created and ready to login", responseCode = "201"),
                    @ApiResponse(responseCode = "401", description = "Login fail")})
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody AuthRequest request) {
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
