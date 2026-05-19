package cl.fuelonline.user.domain.model;

import cl.fuelonline.shared.persistence.BaseAuditEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(
    name = "user",
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_user_email",        columnNames = "email"),
        @UniqueConstraint(name = "uq_user_rut",          columnNames = "rut"),
        @UniqueConstraint(name = "uq_user_firebase_uid", columnNames = "firebase_uid")
    },
    indexes = {
        @Index(name = "idx_user_role",          columnList = "role_id"),
        @Index(name = "idx_user_firebase_uid", columnList = "firebase_uid")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLRestriction("active = true")
public class User extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(columnDefinition = "VARCHAR(36)", length = 36, updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "role_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_user_role"))
    private Role role;

    @Column(nullable = false, length = 180)
    private String email;

    /** Firebase Authentication UID. Null if the user has not authenticated yet. */
    @Column(name = "firebase_uid", length = 128)
    private String firebaseUid;

    @Column(name = "auth_provider", nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private AuthProvider authProvider = AuthProvider.LOCAL;

    @Column(name = "password_hash", length = 255)
    private String passwordHash;

    @Column(length = 12)
    private String rut;

    @Column(name = "first_name", nullable = false, length = 80)
    private String firstName;

    @Column(name = "middle_name", length = 80)
    private String middleName;

    @Column(name = "last_name", nullable = false, length = 80)
    private String lastName;

    @Column(name = "second_last_name", length = 80)
    private String secondLastName;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = Boolean.TRUE;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Vehicle> vehicles = new ArrayList<>();

    public void deactivate() { this.active = Boolean.FALSE; }
    public void reactivate()  { this.active = Boolean.TRUE;  }
}
