package cz.cvut.fit.splitee.controller;

import cz.cvut.fit.splitee.dto.AccountDTO;
import cz.cvut.fit.splitee.dto.GroupDTO;
import cz.cvut.fit.splitee.entity.Account;
import cz.cvut.fit.splitee.entity.Group;
import cz.cvut.fit.splitee.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping(value = "/api/account")
public class AccountController {
    @Autowired
    private AccountService accountService;

    static AccountDTO entityToDto(Account acc) {
        return new AccountDTO(acc.getEmail(), acc.getPassword(), acc.getName(), acc.getPhoto(), acc.getBankAccount());
    }
    static Account dtoToEntity(AccountDTO dto) {
        return new Account(dto.getEmail(), dto.getPassword(), dto.getName(), dto.getPhoto(), dto.getBankAccount());
    }

    // here is Account in the parameter because we dont have password in DTO
    //TODO make this return responseEntity
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
    @PutMapping("/{id}")
    public ResponseEntity update(@PathVariable("id") Integer id, @RequestBody AccountDTO dto) {
        Optional<Account> optional = accountService.findById(id);
        if(optional.isPresent()) {
            Account acc = optional.get();
            // NOTE: email unchangeable ?
            acc.setPassword(dto.getPassword());
            acc.setName(dto.getName());
            acc.setPhoto(dto.getPhoto());
            acc.setBankAccount(dto.getBankAccount());
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
