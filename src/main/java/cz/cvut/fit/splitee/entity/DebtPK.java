package cz.cvut.fit.splitee.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@NoArgsConstructor
@EqualsAndHashCode
@Embeddable
@Getter @Setter
@AllArgsConstructor
public class DebtPK  implements Serializable {

    @Column(name = "member_id_owes")
    private Long owesId;

    @Column(name = "member_id_gets_back")
    private Long getsBackId;
}
