package cz.cvut.fit.splitee.controller;

import cz.cvut.fit.splitee.dto.BillDTO;
import cz.cvut.fit.splitee.dto.GroupDTO;
import cz.cvut.fit.splitee.dto.MembershipDTO;
import cz.cvut.fit.splitee.entity.Bill;
import cz.cvut.fit.splitee.entity.Group;
import cz.cvut.fit.splitee.entity.Membership;
import cz.cvut.fit.splitee.service.AccountService;
import cz.cvut.fit.splitee.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping(value = "/api/groups")
public class GroupController {
    @Autowired
    private GroupService groupService;
    @Autowired
    private AccountService accountService;

    final Integer CODELEN = 7;
    static Group dtoToEntity(GroupDTO g) {
        return new Group(g.getCode(), g.getName(), g.getCurrency(), g.getPhoto(), g.getDescription());
    }
    static GroupDTO entityToDto(Group g) {
        return new GroupDTO(g.getCode(), g.getName(), g.getCurrency(), g.getPhoto(), g.getDescription());
    }

    // method to generate 8-char share code for group
    private String genCode() {
        String genStr = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvxyz";
        StringBuilder code = new StringBuilder(CODELEN);
        for (int i = 0; i < CODELEN; i++) {
            int index = (int)(genStr.length() * Math.random());
            code.append(genStr.charAt(index));
        }
        return code.toString();
    }

    @PostMapping
    public ResponseEntity create(@RequestBody GroupDTO dto) {
        String code;
        do {
            code = genCode();
        } while (groupService.findByCode(code).isPresent());
        dto.setCode(code);
        Group group = dtoToEntity(dto);
        groupService.createOrUpdate(group);
        return ResponseEntity.created(null).body(dto);
    }

    @GetMapping("/{code}")
    public ResponseEntity findByCode (@PathVariable String code) {
        Optional<Group> optional = groupService.findByCode(code);
        if (optional.isPresent()) {
            GroupDTO dto = entityToDto(optional.get());
            return ResponseEntity.ok(dto);
        }
        else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{code}")
    public ResponseEntity update(@PathVariable String code, @RequestBody GroupDTO dto) {
        Optional<Group> optional = groupService.findByCode(code);
        if (optional.isPresent()) {
            Group group = optional.get();
            // DTO has all old and new fields
            group.setName(dto.getName());
            group.setCurrency(dto.getCurrency());
            group.setPhoto(dto.getPhoto());
            group.setDescription((dto.getDescription()));
            GroupDTO newDto = entityToDto(groupService.createOrUpdate(group));
            return ResponseEntity.ok(newDto);
        }
        return ResponseEntity.badRequest().body("This group does not exist.");
    }

    @DeleteMapping("/{code}")
    public ResponseEntity delete(@PathVariable String code) {
        try {
            groupService.deleteByCode(code);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{code}/members")
    public Collection<MembershipDTO> listAllMembers(@PathVariable String code) {
        Collection<Membership> entityList = groupService.findAllMembersByCode(code);
        Collection<MembershipDTO> dtoList = new ArrayList<>();
        for (Membership member : entityList) {
            MembershipDTO dto = MembershipController.entityToDto(member);
            dtoList.add(dto);
        }
        return dtoList;
    }

    @GetMapping("/{code}/members/noacc")
    public Collection<MembershipDTO> listAllMembersWithoutAccount(@PathVariable String code) {
        Collection<Membership> entityList = groupService.findAllMembersWithoutAccount(code);
        Collection<MembershipDTO> dtoList = new ArrayList<>();
        for (Membership member : entityList) {
            MembershipDTO dto = MembershipController.entityToDto(member);
            dtoList.add(dto);
        }
        return dtoList;
    }
    // addMember() --> In MembershipController
    // editMember() -> if(hasAccount()) can't edit | else ok --> In MembershipController
    // deleteMember() --> In MembershipController
    @GetMapping("/{id}/bills")
    public Collection<BillDTO> listAllBills(@PathVariable String code) {
        Collection<Bill> entityList = groupService.findAllBillsByCode(code);
        Collection<BillDTO> dtoList = new ArrayList<>();
        for (Bill bill : entityList) {
            BillDTO dto = BillController.entityToDto(bill);
            dtoList.add(dto);
        }
        return dtoList;
    }
    // addBill() --> In BillController
    // editBill() --> In BillController
    // deleteBill() --> In BillController
}
