package cz.cvut.fit.splitee.service;

import cz.cvut.fit.splitee.entity.Bill;
import cz.cvut.fit.splitee.entity.Membership;
import cz.cvut.fit.splitee.entity.Split;
import cz.cvut.fit.splitee.entity.SplitPK;
import cz.cvut.fit.splitee.repository.BillRepository;
import cz.cvut.fit.splitee.repository.MembershipRepository;
import cz.cvut.fit.splitee.repository.SplitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class SplitService {
    @Autowired
    private SplitRepository splitRepository;
    @Autowired
    private BillRepository billRepository;
    @Autowired
    private MembershipRepository membershipRepository;

    public Split createOrUpdate(Split split) { return splitRepository.save(split); }

    public Optional<Split> findById(SplitPK id) { return splitRepository.findById(id); }

    @Transactional
    public void deleteById(SplitPK id) {
        Optional<Split> optSplit = splitRepository.findById(id);
        if(optSplit.isEmpty()) return;

        Optional<Bill> optBill = billRepository.findById(id.getBillId());
        Optional<Membership> optMember = membershipRepository.findById(id.getMembershipId());

        if(optBill.isEmpty() || optMember.isEmpty()) return;
        optBill.get().getSplits().remove(optSplit.get());
        optMember.get().getSplits().remove(optSplit.get());

        splitRepository.deleteById(id); //redundant
    }
}
