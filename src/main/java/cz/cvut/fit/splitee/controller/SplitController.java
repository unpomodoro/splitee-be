package cz.cvut.fit.splitee.controller;

import cz.cvut.fit.splitee.controller.dto.SplitDTO;
import cz.cvut.fit.splitee.entity.*;
import cz.cvut.fit.splitee.service.BillService;
import cz.cvut.fit.splitee.service.DebtService;
import cz.cvut.fit.splitee.service.MembershipService;
import cz.cvut.fit.splitee.service.SplitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
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
    @Autowired
    private DebtService debtService;

    static SplitDTO entityToDto(Split s) {
        return new SplitDTO(s.getBill().getId(), s.getMembership().getId(), s.getAmount(),
                            s.getType(), s.getValue(), s.isPayer(), s.getAmountPaid());
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

    public ResponseEntity createSplits (Bill bill, Membership payer, SplitRequest request) {
        boolean payerIsParticipant = false;
        Collection<SplitDTO> splitDTO = new ArrayList<>();

        for (SplitObject splitObj : request.splits) {
            Optional<Membership> optMember = membershipService.findById(splitObj.memberId.intValue());
            if (optMember.isPresent()) {
                Membership participant = optMember.get();

                SplitPK id = new SplitPK(bill.getId(), participant.getId());
                Split split = new Split(id, bill, participant, splitObj.amount, request.type, splitObj.value,
                        request.payerId.equals(splitObj.memberId),
                        request.payerId.equals(splitObj.memberId)
                                ? request.amount
                                : BigDecimal.ZERO); // single payer
                splitDTO.add(entityToDto(splitService.createOrUpdate(split)));

                // recalculate debt -> participant owes payer money
                if (!participant.equals(payer)) {
                    debtService.updateDebt(participant.getId(), payer.getId(), splitObj.amount);
                }
                else {
                    payerIsParticipant = true;
                }
            }
        }
        // a 'blank' split relation to keep track of the payer
        if(!payerIsParticipant) {
            SplitPK id = new SplitPK(bill.getId(), payer.getId());
            Split split = new Split(id, bill, payer, BigDecimal.ZERO, request.type,
                    BigDecimal.ZERO, true, request.amount);
            splitDTO.add(entityToDto(splitService.createOrUpdate(split)));

        }
        return ResponseEntity.created(null).body(splitDTO);
    }

    @PostMapping("/{billId}")   // In FE, split are calculated - bill is created -> post req with array of splits in body
    public ResponseEntity create (@PathVariable Integer billId, @RequestBody SplitRequest request) {

        Optional<Bill> optBill = billService.findById(billId);
        Optional<Membership> optPayer = membershipService.findById(request.payerId.intValue());

        if (optBill.isPresent() && optPayer.isPresent()) {
            // create all the splits here
            Bill bill = optBill.get();
            Membership payer = optPayer.get();
            return createSplits(bill, payer, request);

        } else {
            return ResponseEntity.badRequest().body("This bill does not exist");
        }
    }

    @PutMapping("/{billId}") // Only called when bill is edited - reverse all old splits and then create new :) --> split is never truly 'updated'
    public ResponseEntity update(@PathVariable Integer billId, @RequestBody SplitRequest request) {
        // find all splits
        Optional<Bill> optBill = billService.findById(billId);
        Optional<Membership> optPayer = membershipService.findById(request.payerId.intValue());

        if (optBill.isPresent() && optPayer.isPresent()) {
            Collection<Split> splits = billService.findAllSplitsById(billId);
            Membership payer = optPayer.get();

            // reverse debt & delete split
            for (Split split : splits) {
                Optional<Membership> optParticipant = membershipService.findById(split.getMembership().getId().intValue());

                if (optParticipant.isPresent()) {
                    Membership participant = optParticipant.get();
                    debtService.reverseDebt(payer.getId(), participant.getId(), split.getAmount());
                }
                else {
                    return ResponseEntity.badRequest().body("This participant is not a member.");
                }

                splitService.delete(split);
            }

            // create new splits
            Bill bill = optBill.get();
            return createSplits(bill, payer, request);

        } else {
            return ResponseEntity.badRequest().body("This bill or member does not exist");
        }
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
