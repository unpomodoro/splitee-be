package cz.cvut.fit.splitee.service;

import cz.cvut.fit.splitee.entity.Bill;
import cz.cvut.fit.splitee.entity.Group;
import cz.cvut.fit.splitee.entity.Membership;
import cz.cvut.fit.splitee.repository.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;

@Service
public class GroupService {
    @Autowired
    private GroupRepository groupRepository;

    @Transactional
    public Group createOrUpdate(Group group) { return groupRepository.save(group); }
    public Optional<Group> findByCode(String code) { return groupRepository.findByCode(code); }

    public Optional<Group> findById(String code) {
        return groupRepository.findByCode(code);
    }

    public Collection<Membership> findAllMembersByCode(String code) { return groupRepository.findAllMembersByCode(code); }

    public Collection<Membership> findAllMembersWithoutAccount(String code) {
        Collection<Membership> memberships = groupRepository.findAllMembersByCode(code);
        memberships.removeIf(m -> m.getAccount() != null);
        return memberships;
    }

    @Transactional
    public void deleteByCode(String code) {
        Optional<Group> optional = groupRepository.findByCode(code);
        if(optional.isEmpty()) return;
        // members and bills has cascade --> no need to care
        groupRepository.deleteById(optional.get().getId());
    }

    public Collection<Bill> findAllBillsByCode(String code) { return groupRepository.findAllBillsByCode(code); }
}
