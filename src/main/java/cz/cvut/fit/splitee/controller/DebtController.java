package cz.cvut.fit.splitee.controller;

import cz.cvut.fit.splitee.controller.dto.DebtDTO;
import cz.cvut.fit.splitee.entity.Debt;
import cz.cvut.fit.splitee.entity.DebtPK;
import cz.cvut.fit.splitee.entity.Membership;
import cz.cvut.fit.splitee.service.DebtService;
import cz.cvut.fit.splitee.service.MembershipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

@CrossOrigin
@RestController
@RequestMapping(value = "/api/debts")
public class DebtController {
    @Autowired
    private DebtService debtService;
    @Autowired
    private MembershipService membershipService;

    // Parameter is not Debt because of the owes/getsBack sides complication
    public static DebtDTO entityToDto(Membership owes, Membership getsBack, BigDecimal amount) {
        return new DebtDTO(MembershipController.entityToDto(owes), MembershipController.entityToDto(getsBack), amount);
    }

    // For mass create when creating a group
    @PostMapping("/{groupCode}")
    public ResponseEntity createWithGroup (@RequestBody ArrayList<Integer> memberIds) {
        int memberCnt = memberIds.size();

        if (memberCnt <= 1) {
            return ResponseEntity.ok("Not enough members to create debts");
        }
        else {
            Collection<DebtDTO> debtsDTO = new ArrayList<>();

            for ( int i = 0; i < memberCnt; i++) {
                for (int j = i + 1; j < memberCnt; j++) {
                    Integer owes = memberIds.get(i);
                    Integer getsBack = memberIds.get(j);
                    Optional<Membership> optOwes = membershipService.findById(owes);
                    Optional<Membership> optGetsBack = membershipService.findById(getsBack);
                    if (optOwes.isPresent() && optGetsBack.isPresent()) {
                        Debt debt = new Debt(optOwes.get(), optGetsBack.get(), BigDecimal.ZERO);
                        debt.setId(new DebtPK(owes.longValue(), getsBack.longValue()));
                        debtService.createOrUpdate(debt);
                        debtsDTO.add(entityToDto(optOwes.get(), optGetsBack.get(), BigDecimal.ZERO));
                    }
                    else {  // redundant but stay here just in case
                        if (optOwes.isEmpty()) {
                            return ResponseEntity.badRequest().body("This member does not exist");
                        }
                        return ResponseEntity.badRequest().body("This member does not exist");
                    }
                }
            }
            return ResponseEntity.created(null).body(debtsDTO);
        }
    }

    // For new member added to a group
    @PostMapping("/{owes}")        // Debt is created at the same time as member
    public ResponseEntity create (@PathVariable Integer owes) {
        Optional<Membership> optOwes = membershipService.findById(owes);
        Collection<DebtDTO> debtsDTO = new ArrayList<>();

        if (optOwes.isPresent()) {
            // get all member of group
            Membership newMember = optOwes.get();
            Set<Membership> groupMembers = newMember.getGroup().getMemberships();

            for(Membership getsBack : groupMembers) {
                Debt debt = new Debt(newMember, getsBack, BigDecimal.ZERO);
                debt.setId(new DebtPK(newMember.getId(), getsBack.getId()));
                debtService.createOrUpdate(debt);
                debtsDTO.add(entityToDto(newMember, getsBack, BigDecimal.ZERO));
            }
            return ResponseEntity.created(null).body(debtsDTO);
        }
        else {
            return ResponseEntity.badRequest().body("This member does not exist");
        }
    }

    @GetMapping("/{owes}/{getsBack}")     // HUGE attention at who owes whom - the displaying is FE's work
    public ResponseEntity findById (@PathVariable Integer owes, @PathVariable Integer getsBack) {
        Optional<Debt> optional = debtService.findById(owes, getsBack);
        if (optional.isPresent()) { //
            Debt debt = optional.get();
            DebtDTO dto = entityToDto(debt.getOwes(), debt.getGetsBack(), debt.getAmount());
            return ResponseEntity.ok(dto);
        }
        else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{owes}/{getsBack}")       // only amount -> the amount is not "changed" directly -> only add or subtract
    public ResponseEntity update(@PathVariable Integer owes, @PathVariable Integer getsBack, @RequestBody DebtDTO dto) {
        Optional<Debt> optional = debtService.findById(owes, getsBack);
        if (optional.isPresent()) {
            Debt debt = optional.get();
            BigDecimal amountModif = dto.getAmount();
            // compare who owes who
            if (debt.getOwes().getId().equals(owes.longValue())) {
                BigDecimal newAmount = debt.getAmount().add(amountModif);
                debt.setAmount(newAmount);
            }
            else {
                BigDecimal newAmount = debt.getAmount().subtract(amountModif);
                debt.setAmount(newAmount);
            }
            debtService.createOrUpdate(debt);
            DebtDTO newDto = entityToDto(debt.getOwes(), debt.getGetsBack(), debt.getAmount());
            return ResponseEntity.ok(newDto);
        }
        return ResponseEntity.badRequest().body("This debt does not exist.");
    }

    @PutMapping("/{owes}/{getsBack}/settleup")
    public ResponseEntity update(@PathVariable Integer owes, @PathVariable Integer getsBack) {
        Optional<Debt> optional = debtService.findById(owes, getsBack);
        if (optional.isPresent()) {
            Debt debt = optional.get();
            debt.setAmount(BigDecimal.ZERO);
            debtService.createOrUpdate(debt);
            DebtDTO newDto = entityToDto(debt.getOwes(), debt.getGetsBack(), debt.getAmount());
            return ResponseEntity.ok(newDto);
        }
        return ResponseEntity.badRequest().body("This debt does not exist.");
    }

    @DeleteMapping("/{owes}/{getsBack}")       // this might be redundant? Debt is deleted only when one of the member is deleted
    public ResponseEntity delete(@PathVariable Integer owes, @PathVariable Integer getsBack) {
        try {
            debtService.deleteById(owes, getsBack);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
