package cz.cvut.fit.splitee.service;

import cz.cvut.fit.splitee.entity.Debt;
import cz.cvut.fit.splitee.entity.DebtPK;
import cz.cvut.fit.splitee.entity.Membership;
import cz.cvut.fit.splitee.repository.DebtRepository;
import cz.cvut.fit.splitee.repository.MembershipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DebtService {
    @Autowired
    private DebtRepository debtRepository;
    @Autowired
    private MembershipRepository membershipRepository;

    public Debt createOrUpdate(Debt debt) { return debtRepository.save(debt); }

    public Optional<Debt> findById(Integer id1, Integer id2) {
        DebtPK id = new DebtPK(id1.longValue(), id2.longValue());
        Optional<Debt> optional = debtRepository.findById(id);
        if (optional.isEmpty()) {
            id = new DebtPK(id2.longValue(), id1.longValue());
            optional = debtRepository.findById(id);
        }
        return optional;
    }

    public void deleteById(Integer id1, Integer id2) {
        DebtPK id = new DebtPK(id1.longValue(), id2.longValue());
        Optional<Debt> optional = debtRepository.findById(id);
        if (optional.isEmpty()) {
            id = new DebtPK(id2.longValue(), id1.longValue());
            optional = debtRepository.findById(id);
        }
        if (optional.isEmpty()) return;

        Optional<Membership> optOwes = membershipRepository.findById(id.getOwesId());
        Optional<Membership> optGetsBack = membershipRepository.findById(id.getGetsBackId());

        if(optOwes.isEmpty() || optGetsBack.isEmpty()) return;
        optOwes.get().getDebtsGetsBack().remove(optional.get());
        optGetsBack.get().getDebtsOwes().remove(optional.get());
    }
}
