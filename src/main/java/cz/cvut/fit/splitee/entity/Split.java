package cz.cvut.fit.splitee.entity;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity(name = "split")
@Table(name = "split")
@Getter @Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Split implements Serializable {
    public enum TYPE {
        EQUAL,
        EXACT,
        PERCENTAGE,
        SHARE
    }

    @EmbeddedId
    private SplitPK id;

    @ManyToOne
    @MapsId("billId")
    @JoinColumn(name = "bill_id")
    private Bill bill;

    @ManyToOne
    @MapsId("membershipId")
    @JoinColumn(name = "member_id")
    private Membership membership;

    @Column(nullable = false)
    private BigDecimal amount;
    @Column(nullable = false)
    private TYPE type;
    @Column(nullable = false)
    private BigDecimal value;
    @Column(nullable = false)
    private boolean isPayer;
    @Column(nullable = false)
    private BigDecimal amountPaid;

    public Split(Bill bill, Membership membership, BigDecimal amount, TYPE type, BigDecimal value,
                 boolean isPayer, BigDecimal amountPaid) {
        this.bill = bill;
        this.membership = membership;
        this.amount = amount;
        this.type = type;
        this.value = value;
        this.isPayer = isPayer;
        this.amountPaid = amountPaid;

    }
}
