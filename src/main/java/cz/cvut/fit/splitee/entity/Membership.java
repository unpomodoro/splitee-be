package cz.cvut.fit.splitee.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
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
    private Set<Debt> debtsGetsBack;

    public Membership(String name, String photo, String bankAccount) {
        this.name = name;
        this.photo = photo;
        this.bankAccount = bankAccount;
    }

    public BigDecimal getDebt() {
        // returns 0 if all debts record are 0
        // returns amount owes (all POSITIVE values in debtsOwes and NEGATIVE values in debtGetsBack)
        // returns amount gets back (all NEGATIVE values in debtOwes and POSITIVE values in debtGetsBack)
        BigDecimal owes = BigDecimal.ZERO;
        BigDecimal getsBack = BigDecimal.ZERO;

        if(debtsOwes != null && debtsGetsBack != null) {
            for (Debt d : debtsOwes) {
                if (d.getAmount().compareTo(BigDecimal.ZERO) > 0) {
                    owes = owes.add(d.getAmount());
                } else {
                    getsBack = getsBack.add(d.getAmount().negate());
                }
            }
            for (Debt d : debtsGetsBack) {
                if (d.getAmount().compareTo(BigDecimal.ZERO) > 0) {
                    getsBack = getsBack.add(d.getAmount());
                } else {
                    owes = owes.add(d.getAmount().negate());
                }
            }
        }

        if (owes.compareTo(BigDecimal.ZERO) > 0) {  // prioritize owes amount
            return owes.negate();
        } else {                                    // returns amount the person shall getback or 0
            return getsBack;
        }
    }
}
