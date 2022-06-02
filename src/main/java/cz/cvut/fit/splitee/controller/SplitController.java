package cz.cvut.fit.splitee.controller;

import cz.cvut.fit.splitee.dto.SplitDTO;
import cz.cvut.fit.splitee.entity.Bill;
import cz.cvut.fit.splitee.entity.Membership;
import cz.cvut.fit.splitee.entity.Split;
import cz.cvut.fit.splitee.entity.SplitPK;
import cz.cvut.fit.splitee.service.BillService;
import cz.cvut.fit.splitee.service.MembershipService;
import cz.cvut.fit.splitee.service.SplitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping(value = "/api/splits")
public class SplitController {
    @Autowired
    private SplitService splitService;
    @Autowired
    private BillService billService;
    @Autowired
    private MembershipService membershipService;

    static SplitDTO entityToDto(Split s) {
        return new SplitDTO(s.getBill().getId(), s.getMembership().getId(), s.getAmount(),
                            s.getType(), s.getValue(), s.isPayer(), s.getAmountPaid());
    }

    @PostMapping("/{billId}/{memberId}")   // Split is created at the same time a bill -> one for each member, user chooses who to remove later
    public ResponseEntity create (@PathVariable Integer billId, @PathVariable Integer memberId, @RequestBody SplitDTO dto) {
        Optional<Bill> optBill = billService.findById(billId);
        Optional<Membership> optMember = membershipService.findById(memberId);
        if (optBill.isPresent() && optMember.isPresent()) {
            Bill bill = optBill.get();
            Membership member = optMember.get();

            SplitPK id = new SplitPK(billId.longValue(), memberId.longValue());
            Split split = new Split(id, bill, member, dto.getAmount(), dto.getType(), dto.getValue(), dto.isPayer(), dto.getAmountPaid());
            //bill.getSplits().add(split);
            SplitDTO newDTO = entityToDto(splitService.createOrUpdate(split));
            //billService.createOrUpdate(bill);
            return ResponseEntity.created(null).body(dto);
        }
        else {
            if (optBill.isEmpty()) {
                return ResponseEntity.badRequest().body("This bill does not exist");
            }
            return ResponseEntity.badRequest().body("This member does not exist");
        }
    }

    @GetMapping("/{billId}/{memberId}")
    public ResponseEntity findById (@PathVariable Integer billId, @PathVariable Integer memberId) {
        SplitPK id = new SplitPK(billId.longValue(), memberId.longValue());
        Optional<Split> optional = splitService.findById(id);
        if (optional.isPresent()) {
            SplitDTO dto = entityToDto(optional.get());
            return ResponseEntity.ok(dto);
        }
        else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{billId}/{memberId}")
    public ResponseEntity update(@PathVariable Integer billId, @PathVariable Integer memberId, @RequestBody SplitDTO dto) {
        SplitPK id = new SplitPK(billId.longValue(), memberId.longValue());

        Optional<Split> optional = splitService.findById(id);
        if (optional.isPresent()) {
            Split split = optional.get();
            // DTO has all old and new fields
            // bill & membership not changed
            split.setAmount(dto.getAmount());
            split.setType(dto.getType());
            split.setValue(dto.getValue());
            split.setPayer(dto.isPayer());
            split.setAmountPaid(dto.getAmountPaid());
            SplitDTO dtoNew = entityToDto(splitService.createOrUpdate(split));
            return ResponseEntity.ok(dtoNew);
        }
        return ResponseEntity.badRequest().body("This split does not exist.");
    }

    @DeleteMapping("/{billId}/{memberId}")
    public ResponseEntity delete(@PathVariable Integer billId, @PathVariable Integer memberId) {
        SplitPK id = new SplitPK(billId.longValue(), memberId.longValue());
        try {
            splitService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
