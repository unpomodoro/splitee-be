package cz.cvut.fit.splitee.service;

import cz.cvut.fit.splitee.entity.Bill;
import cz.cvut.fit.splitee.entity.Membership;
import cz.cvut.fit.splitee.entity.Split;
import cz.cvut.fit.splitee.repository.BillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;

@Service
public class BillService {
    @Autowired
    private BillRepository billRepository;

    @Transactional
    public Bill createOrUpdate(Bill bill) { return billRepository.save(bill); }

    public Optional<Bill> findById(Integer id) { return billRepository.findById(id.longValue()); }

    @Transactional
    public void deleteById(Integer id) {
        Optional<Bill> optional = billRepository.findById(id.longValue());

        if(optional.isEmpty()) return;
        Bill bill = optional.get();
        bill.getGroup().getBills().remove(bill);
        // debts & splits have cascade --> no need to care
        billRepository.deleteById(id.longValue());
    }

    public Collection<Split> findAllSplitsById(Integer id) { return billRepository.findAllSplitsById(id.longValue()); }

    public Membership findPayerById(Integer id) {
        Collection<Split> splits = findAllSplitsById(id);
        Membership payer = new Membership();
        // payer has to be in the set
        for (Split split : splits) {
            if (split.isPayer()) {
                payer = split.getMembership();
            }
        }
        return payer;
    }
}
