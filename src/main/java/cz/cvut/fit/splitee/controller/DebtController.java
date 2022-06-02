package cz.cvut.fit.splitee.controller;

import cz.cvut.fit.splitee.dto.DebtDTO;
import cz.cvut.fit.splitee.entity.Debt;
import cz.cvut.fit.splitee.entity.*;
import cz.cvut.fit.splitee.service.DebtService;
import cz.cvut.fit.splitee.service.MembershipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping(value = "/api/debts")
public class DebtController {
    @Autowired
    private DebtService debtService;
    @Autowired
    private MembershipService membershipService;

    public static DebtDTO entityToDto(Debt d) { return new DebtDTO(d.getId().getOwesId(), d.getId().getGetsBackId(), d.getAmount()); }

    @PostMapping("/{owes}/{getsBack}")        // Debt is created at the same time as member
    public ResponseEntity create (@PathVariable Integer owes, @PathVariable Integer getsBack, @RequestBody DebtDTO dto) {
        Optional<Membership> optOwes = membershipService.findById(owes);
        Optional<Membership> optGetsBack = membershipService.findById(getsBack);
        if (optOwes.isPresent() && optGetsBack.isPresent()) {
            Debt debt = new Debt(optOwes.get(), optGetsBack.get(), dto.getAmount());
            debt.setId(new DebtPK(owes.longValue(), getsBack.longValue()));
            DebtDTO newDto = entityToDto(debtService.createOrUpdate(debt));
            return ResponseEntity.created(null).body(newDto);
        }
        else {
            if (optOwes.isEmpty()) {
                return ResponseEntity.badRequest().body("This member does not exist");
            }
            return ResponseEntity.badRequest().body("This member does not exist");
        }
    }

    @GetMapping("/{owes}/{getsBack}")     // HUGE attention at who owes whom - the displaying is FE's work
    public ResponseEntity findById (@PathVariable Integer owes, @PathVariable Integer getsBack) {
        Optional<Debt> optional = debtService.findById(owes, getsBack);
        if (optional.isPresent()) { //
            DebtDTO dto = entityToDto(optional.get());
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
            DebtDTO newDto = entityToDto(debtService.createOrUpdate(debt));
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
