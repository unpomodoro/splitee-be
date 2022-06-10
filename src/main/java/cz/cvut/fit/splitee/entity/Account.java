package cz.cvut.fit.splitee.entity;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Entity(name = "account")
@Table(name = "account")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id", nullable = false, updatable = false)
    private Long id;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String name;

    private String photo;
    @Column(name = "bank_account")
    private String bankAccount;

    @OneToMany(mappedBy = "account")
    private Set<Membership> memberships;

    public Account(String email, String password, String name, String photo, String bankAccount) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.photo = photo;
        this.bankAccount = bankAccount;
    }

    public Account(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", photo='" + photo + '\'' +
                ", bankAccount='" + bankAccount + '\'' +
                '}';
    }
}

