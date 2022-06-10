package cz.cvut.fit.splitee.service;

import cz.cvut.fit.splitee.entity.Debt;
import cz.cvut.fit.splitee.entity.DebtPK;
import cz.cvut.fit.splitee.entity.Membership;
import cz.cvut.fit.splitee.repository.DebtRepository;
import cz.cvut.fit.splitee.repository.MembershipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class DebtService {
    @Autowired
    private DebtRepository debtRepository;
    @Autowired
    private MembershipRepository membershipRepository;

    @Transactional
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

    @Transactional
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

    @Transactional
    public void updateDebt (Long owes, Long getsBack, BigDecimal amount) {
        //get debt
        Optional<Debt> optDebt = findById(owes.intValue(), getsBack.intValue());

        if (optDebt.isPresent()) {
            Debt debt = optDebt.get();
            // the one who owes money is the 'owe' side in debt relationship
            if (debt.getOwes().getId().equals(owes)) {
                debt.setAmount(debt.getAmount().add(amount));
            }
            // the one who ows money is the 'getgBack' side in debt relationship
            else {
                debt.setAmount(debt.getAmount().subtract(amount));
            }
            debtRepository.save(debt);
        }
    }

    @Transactional
    public void reverseDebt (Long payer, Long participant, BigDecimal amount) {
        // payer pays back participant
        updateDebt(payer, participant, amount);
    }
}
