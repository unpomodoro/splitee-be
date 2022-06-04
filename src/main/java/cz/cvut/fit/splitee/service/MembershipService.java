package cz.cvut.fit.splitee.service;

import cz.cvut.fit.splitee.entity.Debt;
import cz.cvut.fit.splitee.entity.Membership;
import cz.cvut.fit.splitee.repository.MembershipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;

@Service
public class MembershipService {
    @Autowired
    private MembershipRepository membershipRepository;

    public Optional<Membership> findById(Integer id) { return membershipRepository.findById(id.longValue()); }

    // All debt-relations. should be (n - 1) where n is number of members in a group
    public Collection<Debt> findAllDebtsById(Integer id) {
        // All records where 'id' is on the 'owes' side
        Collection<Debt> debts = membershipRepository.findAllDebtsOwesById(id.longValue());

        // All records where 'id' is on the 'getsBack' side --> need to change the amount
        Collection<Debt> getsBack = membershipRepository.findAllDebtsGetsBackById(id.longValue());
        for (Debt d : getsBack) {
            Debt tmp = new Debt(d.getGetsBack(), d.getOwes(), d.getAmount().negate());
            debts.add(tmp);
        }
        return debts;
    }

    @Transactional
    public Membership createOrUpdate(Membership member) { return membershipRepository.save(member); }

    @Transactional
    public void deleteById(Integer id) {
        Optional<Membership> optional = membershipRepository.findById(id.longValue());

        if(optional.isEmpty()) return;
        Membership member = optional.get();
        member.getGroup().getMemberships().remove(member);
        if (member.getAccount() != null) {
            member.getAccount().getMemberships().remove(member);
        }
        // debts has cascade --> no need to care
        membershipRepository.deleteById(id.longValue());
    }
}
