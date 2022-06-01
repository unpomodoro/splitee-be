package cz.cvut.fit.splitee.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity(name = "membership")
@Table(name = "membership")
@Getter @Setter
@NoArgsConstructor
public class Membership {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id", nullable = false, updatable = false)
    private Long id;
    @Column(nullable = false)
    private String name;

    private String photo;
    @Column(name = "bank_account")
    private String bankAccount;

    @ManyToOne
    @JoinColumn(name="account_id", nullable = true)
    private Account account;

    @ManyToOne
    @JoinColumn(name="group_id")
    private Group group;

    @OneToMany(mappedBy = "membership")
    private Set<Split> splits;

    @OneToMany(mappedBy = "owes", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Debt> debtsOwes;

    @OneToMany(mappedBy = "getsBack", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Debt> debtsGetBack;

    public Membership(String name, String photo, String bankAccount) {
        this.name = name;
        this.photo = photo;
        this.bankAccount = bankAccount;
    }
}
