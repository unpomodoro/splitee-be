package cz.cvut.fit.splitee.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Set;

@Entity(name = "bill")
@Table(name = "bill")
@Getter @Setter
@NoArgsConstructor
public class Bill implements Comparable<Bill> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bill_id", nullable = false, updatable = false)
    private Long id;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private Timestamp date;

    private String notes;

    @ManyToOne
    @JoinColumn(name="group_id")
    private Group group;

    //private Long group_id;
    @OneToMany(mappedBy = "bill", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Split> splits;

    public Bill(String description, BigDecimal amount, Timestamp date, String notes, Group group) {
        this.description = description;
        this.amount = amount;
        this.date = date;
        this.notes = notes;
        this.group = group;
    }

    @Override
    public int compareTo(Bill b) {
        return getDate().compareTo(b.getDate());
    }
}
