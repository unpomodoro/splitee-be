package cz.cvut.fit.splitee.controller;

import cz.cvut.fit.splitee.controller.dto.AccountDTO;
import cz.cvut.fit.splitee.controller.dto.GroupDTO;
import cz.cvut.fit.splitee.entity.Account;
import cz.cvut.fit.splitee.entity.Group;
import cz.cvut.fit.splitee.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping(value = "/api/account")
public class AccountController {
    @Autowired
    private AccountService accountService;
    @Autowired
    PasswordEncoder encoder;

    static AccountDTO entityToDto(Account acc) {
        return new AccountDTO(acc.getEmail(), acc.getPassword(), acc.getName(), acc.getPhoto(), acc.getBankAccount());
    }
    static Account dtoToEntity(AccountDTO dto) {
        return new Account(dto.getEmail(), dto.getPassword(), dto.getName(), dto.getPhoto(), dto.getBankAccount());
    }

    @PostMapping
    public Account register(@RequestBody AccountDTO dto) {
        Account account = dtoToEntity(dto);
        return accountService.createOrUpdate(account);
    }

    // this method can only be called by the user themselves -> the account always exists.
    @GetMapping("/{id}")
    public ResponseEntity findById(@PathVariable("id") Integer id) {
        Optional<Account> optional = accountService.findById(id);
        if (optional.isPresent()) {
            AccountDTO dto = entityToDto(optional.get());
            return ResponseEntity.ok(dto);
        }
        else return ResponseEntity.notFound().build();
    }

    // update account
    @PatchMapping("/{id}")
    public ResponseEntity update(@PathVariable("id") Integer id, @RequestBody AccountDTO dto) {
        Optional<Account> optional = accountService.findById(id);
        if(optional.isPresent()) {
            Account acc = optional.get();
            // NOTE: email unchangeable ?
            if (dto.getPassword() != null) acc.setPassword(encoder.encode(dto.getPassword()));
            if (dto.getName() != null) acc.setName(dto.getName());
            if (dto.getPhoto() != null) acc.setPhoto(dto.getPhoto());
            if (dto.getBankAccount() != null) acc.setBankAccount(dto.getBankAccount());
            AccountDTO newDto = entityToDto(accountService.createOrUpdate(acc));
            return ResponseEntity.ok(newDto);
        }
        return ResponseEntity.badRequest().body("This account does not exist!");    //redundant...
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable Integer id) {
        try {
            accountService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @CrossOrigin
    @GetMapping("/{id}/groups")
    public Collection<GroupDTO> listAllGroups(@PathVariable("id") Integer id) {
        Collection<Group> groupsEntity = accountService.findAllGroupsById(id);
        Collection<GroupDTO> groupsDTO = new ArrayList<>();
        for (Group group : groupsEntity) {
            GroupDTO dto = GroupController.entityToDto(group);
            groupsDTO.add(dto);
        }
        return groupsDTO;
    }

     // memberAssociate() -> In MembershipController
}
