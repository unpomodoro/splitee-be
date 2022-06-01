package cz.cvut.fit.splitee.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity(name = "group")
@Table(name = "group_g")
@Getter @Setter
@NoArgsConstructor
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_id")
    private Long id;
    @Column(name = "share_code")
    private String code;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String currency;
    private String photo;
    private String description;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Membership> memberships;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Bill> bills;

    public Group(String code, String name, String currency, String photo, String description) {
        this.code = code;      // generated in GroupController
        this.name = name;
        this.currency = currency;
        this.photo = photo;
        this.description = description;
    }

    public boolean equals(Group b) {
        return( this.getCode() == b.getCode() &&
                this.getName() == b.getName() &&
                this.getCurrency() == b.getCurrency() &&
                this.getPhoto() == b.getPhoto() &&
                this.getDescription() == b.getDescription());

    }
}
