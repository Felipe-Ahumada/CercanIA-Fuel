package cl.fuelonline.security.application.service;

import cl.fuelonline.security.application.dto.AuthResponse;
import cl.fuelonline.security.application.dto.ChangePasswordRequest;
import cl.fuelonline.security.application.dto.ForgotPasswordRequest;
import cl.fuelonline.security.application.dto.LoginRequest;
import cl.fuelonline.security.application.dto.RegisterRequest;
import cl.fuelonline.security.application.dto.ResetPasswordRequest;
import cl.fuelonline.security.application.exception.AuthenticationFailedException;
import cl.fuelonline.security.domain.model.PasswordResetToken;
import cl.fuelonline.security.domain.repository.PasswordResetTokenRepository;
import cl.fuelonline.shared.exception.ResourceNotFoundException;
import cl.fuelonline.user.application.exception.UserAlreadyExistsException;
import cl.fuelonline.user.domain.model.AuthProvider;
import cl.fuelonline.user.domain.model.Role;
import cl.fuelonline.user.domain.model.User;
import cl.fuelonline.shared.util.RutUtils;
import cl.fuelonline.user.domain.repository.RoleRepository;
import cl.fuelonline.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LocalAuthService {

    private static final int USER_ROLE_ID = 2;
    private static final int OTP_EXPIRY_MINUTES = 15;
    private static final SecureRandom RANDOM = new SecureRandom();

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final PasswordResetTokenRepository resetTokenRepository;
    private final JavaMailSender mailSender;

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByEmailIgnoreCase(req.email())) {
            throw new UserAlreadyExistsException("Email ya registrado: " + req.email());
        }
        final String normalizedRut = RutUtils.normalize(req.rut());
        if (userRepository.existsByRut(normalizedRut)) {
            throw new UserAlreadyExistsException("RUT ya registrado: " + normalizedRut);
        }

        Role role = roleRepository.findById(USER_ROLE_ID)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + USER_ROLE_ID));

        User user = User.builder()
                .email(req.email().toLowerCase())
                .passwordHash(passwordEncoder.encode(req.password()))
                .authProvider(AuthProvider.LOCAL)
                .rut(normalizedRut)
                .firstName(req.firstName())
                .middleName(req.middleName())
                .lastName(req.lastName())
                .secondLastName(req.secondLastName())
                .birthDate(req.birthDate())
                .role(role)
                .build();

        userRepository.save(user);
        return toAuthResponse(user);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest req) {
        User user = userRepository.findByEmailIgnoreCase(req.email())
                .orElseThrow(() -> new AuthenticationFailedException("Credenciales inválidas"));

        if (user.getAuthProvider() != AuthProvider.LOCAL || user.getPasswordHash() == null) {
            throw new AuthenticationFailedException(
                    "Esta cuenta usa Google Sign-In. Inicia sesión con Google.");
        }

        if (!passwordEncoder.matches(req.password(), user.getPasswordHash())) {
            throw new AuthenticationFailedException("Credenciales inválidas");
        }

        return toAuthResponse(user);
    }

    @Transactional
    public void changePassword(String email, ChangePasswordRequest req) {
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new AuthenticationFailedException("Usuario no encontrado"));

        if (user.getAuthProvider() != AuthProvider.LOCAL || user.getPasswordHash() == null) {
            throw new AuthenticationFailedException(
                    "El cambio de contraseña solo está disponible para cuentas locales");
        }
        if (!passwordEncoder.matches(req.currentPassword(), user.getPasswordHash())) {
            throw new AuthenticationFailedException("La contraseña actual es incorrecta");
        }
        user.setPasswordHash(passwordEncoder.encode(req.newPassword()));
    }

    @Transactional
    public void requestPasswordReset(ForgotPasswordRequest req) {
        User user = userRepository.findByEmailIgnoreCase(req.email()).orElse(null);
        // Respond identically whether the email exists or not (prevent enumeration)
        if (user == null || user.getAuthProvider() != AuthProvider.LOCAL) return;

        resetTokenRepository.deleteAllByEmail(req.email().toLowerCase());

        String otp = String.format("%06d", RANDOM.nextInt(1_000_000));
        resetTokenRepository.save(PasswordResetToken.builder()
                .email(req.email().toLowerCase())
                .tokenHash(hashOtp(otp))
                .expiresAt(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES))
                .build());

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(req.email());
        msg.setSubject("CercanIA Fuel — Código de recuperación");
        msg.setText("Tu código para restablecer la contraseña es: " + otp +
                "\nVálido por " + OTP_EXPIRY_MINUTES + " minutos.\n\nSi no solicitaste esto, ignora este mensaje.");
        mailSender.send(msg);
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest req) {
        PasswordResetToken prt = resetTokenRepository
                .findByEmailIgnoreCaseAndTokenHashAndUsedFalseAndExpiresAtAfter(
                        req.email(), hashOtp(req.otp()), LocalDateTime.now())
                .orElseThrow(() -> new AuthenticationFailedException("Código inválido o expirado"));

        User user = userRepository.findByEmailIgnoreCase(req.email())
                .orElseThrow(() -> new AuthenticationFailedException("Usuario no encontrado"));

        user.setPasswordHash(passwordEncoder.encode(req.newPassword()));
        prt.setUsed(Boolean.TRUE);
    }

    private static String hashOtp(String otp) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(otp.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder(hash.length * 2);
            for (byte b : hash) hex.append(String.format("%02x", b));
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }

    private AuthResponse toAuthResponse(User user) {
        String token = jwtService.generateToken(user);
        String role = user.getRole() != null ? user.getRole().getName() : "USER";
        return new AuthResponse(token, user.getId(), user.getEmail(), role);
    }
}
