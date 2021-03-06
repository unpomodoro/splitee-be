package cz.cvut.fit.splitee.controller;

import cz.cvut.fit.splitee.controller.dto.BillDTO;
import cz.cvut.fit.splitee.controller.dto.MembershipDTO;
import cz.cvut.fit.splitee.controller.dto.SplitDTO;
import cz.cvut.fit.splitee.entity.Bill;
import cz.cvut.fit.splitee.entity.Group;
import cz.cvut.fit.splitee.entity.Split;
import cz.cvut.fit.splitee.service.BillService;
import cz.cvut.fit.splitee.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping(value = "/api/bills")
public class BillController {
    @Autowired
    private BillService billService;
    @Autowired
    private GroupService groupService;

    static Bill dtoToEntity(BillDTO b, Group group) {
        return new Bill(b.getDescription(), b.getAmount(), b.getDate(), b.getNotes(), group);
    }
    static BillDTO entityToDto(Bill b, MembershipDTO member) {
        return new BillDTO(b.getId(), b.getDescription(), b.getAmount(), member, b.getDate(), b.getNotes());
    }

    @PostMapping("/{groupCode}")
    public ResponseEntity create (@PathVariable String groupCode, @RequestBody BillDTO dto) {
        // always new

        System.out.println(dto);

        Optional<Group> optional = groupService.findByCode(groupCode);
        if (optional.isPresent()) {

            Timestamp date = Timestamp.valueOf(dto.getDate().toString());
            dto.setDate(date);

            Bill bill = dtoToEntity(dto, optional.get());
            BillDTO newDto = entityToDto(billService.createOrUpdate(bill), null);
            return ResponseEntity.created(null).body(newDto);
        }
        else {
            return ResponseEntity.badRequest().body("This group does not exist");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity findById (@PathVariable Integer id) {
        Optional<Bill> optional = billService.findById(id);
        if (optional.isPresent()) {
            Bill bill = optional.get();

            MembershipDTO payer = MembershipController.entityToDto(billService.findPayerById(id));
            BillDTO dto = entityToDto(bill, payer);
            return ResponseEntity.ok(dto);
        }
        else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity update(@PathVariable Integer id, @RequestBody BillDTO dto) {
        Optional<Bill> optional = billService.findById(id);
        if (optional.isPresent()) {
            Bill bill = optional.get();
            // DTO has all old and new fields
            bill.setDescription(dto.getDescription());
            bill.setAmount(dto.getAmount());
            Timestamp date = Timestamp.valueOf(dto.getDate().toString());
            dto.setDate(date);
            bill.setDate(date);
            bill.setNotes(dto.getNotes());

            MembershipDTO payer = MembershipController.entityToDto(billService.findPayerById(id));
            BillDTO dtoNew = entityToDto(billService.createOrUpdate(bill), payer);
            return ResponseEntity.ok(dtoNew);
        }
        return ResponseEntity.badRequest().body("This membership does not exist.");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable Integer id) {
        try {
            billService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // a split is added when it is created
    @GetMapping("/{id}/splits")
    public Collection<SplitDTO> listAllSplits(@PathVariable Integer id) {
        Collection<Split> entityList = billService.findAllSplitsById(id);
        Collection<SplitDTO> dtoList = new ArrayList<>();
        for (Split split : entityList) {
            SplitDTO dto = SplitController.entityToDto(split);
            dtoList.add(dto);
        }
        return dtoList;
    }

}
