package cz.cvut.fit.splitee.entity;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@NoArgsConstructor
@EqualsAndHashCode
@Embeddable
@Getter @Setter
@AllArgsConstructor
public class SplitPK implements Serializable {

    @Column(name = "bill_id")
    private Long billId;

    @Column(name = "member_id")
    private Long membershipId;
}
