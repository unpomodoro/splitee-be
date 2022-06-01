package cz.cvut.fit.splitee.entity;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity(name = "debt")
@Table(name = "debt")
@Getter @Setter
@EqualsAndHashCode
@NoArgsConstructor
public class Debt {

    @EmbeddedId
    private DebtPK id;

    @ManyToOne
    @MapsId("owesId")
    @JoinColumn(name = "member_id_owes")
    private Membership owes;

    @ManyToOne
    @MapsId("getsBackId")
    @JoinColumn(name = "member_id_gets_back")
    private Membership getsBack;

    @Column(nullable = false)
    private BigDecimal amount;

    public Debt(Membership owes, Membership getsBack, BigDecimal amount) {
        this.owes = owes;
        this.getsBack = getsBack;
        this.amount = amount;
    }

    public Debt(BigDecimal amount) {
    }
}
