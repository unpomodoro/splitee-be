package cz.cvut.fit.splitee.controller;

import cz.cvut.fit.splitee.controller.dto.DebtDTO;
import cz.cvut.fit.splitee.controller.dto.MembershipDTO;
import cz.cvut.fit.splitee.entity.Account;
import cz.cvut.fit.splitee.entity.Debt;
import cz.cvut.fit.splitee.entity.Group;
import cz.cvut.fit.splitee.entity.Membership;
import cz.cvut.fit.splitee.service.AccountService;
import cz.cvut.fit.splitee.service.GroupService;
import cz.cvut.fit.splitee.service.MembershipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping(value = "/api/members")
public class MembershipController {
    @Autowired
    private MembershipService membershipService;
    @Autowired
    private GroupService groupService;
    @Autowired
    private AccountService accountService;

    static Membership dtoToEntity(MembershipDTO m) {
        return new Membership(m.getName(), m.getPhoto(), m.getBankAccount());
    }

    //TODO pass in the user's account id
    static MembershipDTO entityToDto(Membership m) {
        return new MembershipDTO(m.getId(), m.getName(), m.getPhoto(), m.getBankAccount(), m.getDebt(),
                (m.getAccount() != null ? m.getAccount().getId() : null));
    }

    // This is used to add the first member when creating a group - it is the user themselves + connecting to the account
    @PostMapping("/{groupCode}/{createBy}")
    public ResponseEntity createInGroup (@PathVariable String groupCode, @PathVariable Integer createBy, @RequestBody MembershipDTO dto) {
        // always new
        Optional<Group> optGroup = groupService.findByCode(groupCode);
        Optional<Account> optAcc = accountService.findById(createBy);
        if (optGroup.isPresent() && optAcc.isPresent()) {
            Membership member = dtoToEntity(dto); // CANT PASS Group in, need to set
            member.setGroup(optGroup.get());
            member.setAccount(optAcc.get());
            MembershipDTO dtoNew = entityToDto(membershipService.createOrUpdate(member));
            //group.getMemberships().add(member);
            //groupService.createOrUpdate(group);
            return ResponseEntity.created(null).body(dtoNew);
        }
        else {
            if(optGroup.isPresent()) {
                return ResponseEntity.badRequest().body("This account does not exist");
            }
            return ResponseEntity.badRequest().body("This group does not exist");
        }
    }

    // This is used to add other members
    @PostMapping("/{groupCode}")
    public ResponseEntity createInGroup (@PathVariable String groupCode, @RequestBody MembershipDTO dto) {
        // always new
        Optional<Group> optional = groupService.findByCode(groupCode);
        if (optional.isPresent()) {
            Membership member = dtoToEntity(dto); // CANT PASS Group in, need to set
            member.setGroup(optional.get());
            MembershipDTO dtoNew = entityToDto(membershipService.createOrUpdate(member));
            //group.getMemberships().add(member);
            //groupService.createOrUpdate(group);
            return ResponseEntity.created(null).body(dtoNew);
        }
        else {
            return ResponseEntity.badRequest().body("This group does not exist");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity findById (@PathVariable Integer id) {
        Optional<Membership> optional = membershipService.findById(id);
        if (optional.isPresent()) {
            MembershipDTO dto = entityToDto(optional.get());
            return ResponseEntity.ok(dto);
        }
        else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity update(@PathVariable Integer id, @RequestBody MembershipDTO dto) {
        Optional<Membership> optional = membershipService.findById(id);
        if (optional.isPresent()) {
            Membership membership = optional.get();
            // DTO has all old and new fields
            membership.setName(dto.getName());
            membership.setPhoto(dto.getPhoto());
            membership.setBankAccount(dto.getBankAccount());
            MembershipDTO newDto = entityToDto(membershipService.createOrUpdate(membership));
            return ResponseEntity.ok(newDto);
        }
        return ResponseEntity.badRequest().body("This membership does not exist.");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable Integer id) {
        try {
            membershipService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("{memberId}/add/{accId}")               // Account association
    public ResponseEntity addAccount(@PathVariable Integer memberId, @PathVariable Integer accId) {

        Optional<Membership> optMem = membershipService.findById(memberId);
        Optional<Account> optAcc = accountService.findById(accId);
        if(optAcc.isPresent() && optMem.isPresent()) {
            Membership membership = optMem.get();
            Account account = optAcc.get();

            membership.setAccount(account);
            account.getMemberships().add(membership);

            membership.setBankAccount(account.getBankAccount());
            membership.setPhoto(membership.getPhoto());

            accountService.createOrUpdate(account);
            MembershipDTO dtoNew = entityToDto(membershipService.createOrUpdate(membership));
            return ResponseEntity.ok(dtoNew);
        }
        if(optAcc.isEmpty()) {
            return ResponseEntity.badRequest().body("This account does not exist!");
        }
        return ResponseEntity.badRequest().body("This member does not exist!");
    }

    @GetMapping("/{id}/debts")      // returns all debt-relation to all members in group
    public Collection<DebtDTO> listAllDebts(@PathVariable Integer id) {
        // All these debts has 'id' as 'owes' side
        // Need member's info
        Collection<Debt> entityList = membershipService.findAllDebtsById(id);
        Collection<DebtDTO> dtoList = new ArrayList<>();
        for (Debt debt : entityList) {
            DebtDTO dto = DebtController.entityToDto(debt.getOwes(), debt.getGetsBack(), debt.getAmount());
            dtoList.add(dto);
        }
        return dtoList;
    }

}
