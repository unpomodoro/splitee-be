package cz.cvut.fit.splitee.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class GroupDTO {

    private String code;
    private String name;
    private String currency;
    private String photo;
    private String description;

    public GroupDTO(String name, String currency, String photo, String description) {
        code = "";
        this.name = name;
        this.currency = currency;
        this.photo = photo;
        this.description = description;
    }
}
