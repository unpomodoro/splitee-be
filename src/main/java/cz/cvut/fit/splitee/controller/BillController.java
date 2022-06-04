package cz.cvut.fit.splitee.controller;

import cz.cvut.fit.splitee.dto.BillDTO;
import cz.cvut.fit.splitee.dto.SplitDTO;
import cz.cvut.fit.splitee.entity.Bill;
import cz.cvut.fit.splitee.entity.Group;
import cz.cvut.fit.splitee.entity.Split;
import cz.cvut.fit.splitee.service.BillService;
import cz.cvut.fit.splitee.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
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
    static BillDTO entityToDto(Bill b) {
        return new BillDTO(b.getDescription(), b.getAmount(), b.getDate(), b.getNotes());
    }

    @PostMapping("/{groupCode}")
    public ResponseEntity create (@PathVariable String groupCode, @RequestBody BillDTO dto) {
        // always new
        Optional<Group> optional = groupService.findByCode(groupCode);
        if (optional.isPresent()) {

            Timestamp date = new Timestamp(new Date().getTime());
            dto.setDate(date);

            Bill bill = dtoToEntity(dto, optional.get());
            billService.createOrUpdate(bill);
            return ResponseEntity.created(null).body(dto);
        }
        else {
            return ResponseEntity.badRequest().body("This group does not exist");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity findById (@PathVariable Integer id) {
        Optional<Bill> optional = billService.findById(id);
        if (optional.isPresent()) {
            BillDTO dto = entityToDto(optional.get());
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
            bill.setDate(dto.getDate());
            bill.setNotes(dto.getNotes());
            BillDTO dtoNew = entityToDto(billService.createOrUpdate(bill));
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
