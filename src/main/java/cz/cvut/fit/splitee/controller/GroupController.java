package cz.cvut.fit.splitee.controller;

import cz.cvut.fit.splitee.controller.dto.BillDTO;
import cz.cvut.fit.splitee.controller.dto.GroupDTO;
import cz.cvut.fit.splitee.controller.dto.MembershipDTO;
import cz.cvut.fit.splitee.entity.Bill;
import cz.cvut.fit.splitee.entity.Group;
import cz.cvut.fit.splitee.entity.Membership;
import cz.cvut.fit.splitee.service.BillService;
import cz.cvut.fit.splitee.service.GroupService;
import cz.cvut.fit.splitee.service.MembershipService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

@CrossOrigin
@RestController
@RequestMapping(value = "/api/groups")
public class GroupController {
    @Autowired
    private GroupService groupService;
    @Autowired
    private MembershipService membershipService;
    @Autowired
    private BillService billService;
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
    @GetMapping("/{code}/bills")
    public Collection<BillDTO> listAllBills(@PathVariable String code) {
        ArrayList<Bill> entityList = new ArrayList<>(groupService.findAllBillsByCode(code));
        Collections.sort(entityList, Collections.reverseOrder());
        ArrayList<BillDTO> dtoList = new ArrayList<>();
        for (Bill bill : entityList) {
            // the type is not important here
            MembershipDTO payer = MembershipController.entityToDto(billService.findPayerById(bill.getId().intValue()));
            BillDTO dto = BillController.entityToDto(bill, payer);
            dtoList.add(dto);
        }
        return dtoList;
    }
    // addBill() --> In BillController
    // editBill() --> In BillController
    // deleteBill() --> In BillController

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    final class MemberPaid implements Comparable<MemberPaid>, Serializable {
        Long id;
        String name;
        BigDecimal amountPaid;

        @Override
        public int compareTo(MemberPaid m) {
            return amountPaid.compareTo(m.amountPaid);
        }
    }

    @GetMapping("/{code}/statistics")
    public ArrayList<MemberPaid> statsOnWhoPaysMost (@PathVariable String code) {
        // get all bills
        Collection<Bill> bills = groupService.findAllBillsByCode(code);
        Map<Long, BigDecimal> map =new HashMap<>();

        for (Bill bill : bills) {
            // get payer
            Membership payer = billService.findPayerById(bill.getId().intValue());
            // map<id, amount> <- amount is to be updated
            if (map.containsKey(payer.getId())) {
                map.put(payer.getId(), bill.getAmount().add(map.get(payer.getId())));
            }
            else {
                map.put(payer.getId(), bill.getAmount());
            }
        }
//        System.out.println("---------------------------------------------------------------");
//        System.out.println(map);
//        System.out.println("---------------------------------------------------------------");
        // array MemberPaid sorted by amount
        ArrayList<MemberPaid> data = new ArrayList<>();
        for (Map.Entry<Long, BigDecimal> pair : map.entrySet()) {
            MemberPaid m = new MemberPaid();
            m.id = pair.getKey();
            if(m.id == null) {  // remove later -> its only for testing database
                m.id = 9999999L;
                m.name = "null";
                m.amountPaid = pair.getValue();
            }
            else {
                Optional<Membership> opt = membershipService.findById(m.id.intValue());
                opt.ifPresent(membership -> m.name = membership.getName());
                m.amountPaid = pair.getValue();
            }

            data.add(m);
        }
        Collections.sort(data);
        return data;
    }
}
