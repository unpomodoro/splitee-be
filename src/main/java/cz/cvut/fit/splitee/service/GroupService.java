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

    public Optional<Group> findById(Integer id) {
        return groupRepository.findById(id.longValue());
    }

    public Collection<Membership> findAllMembersById(Integer id) { return groupRepository.findAllMembersById(id.longValue()); }

    public Collection<Membership> findAllMembersWithoutAccount(Integer id) {
        Collection<Membership> memberships = groupRepository.findAllMembersById(id.longValue());
        memberships.removeIf(m -> m.getAccount() != null);
        return memberships;
    }

    @Transactional
    public void deleteById(Integer id) {
        Optional<Group> optional = groupRepository.findById(id.longValue());
        if(optional.isEmpty()) return;
        // members and bills has cascade --> no need to care
        groupRepository.deleteById(id.longValue());
    }

    public Collection<Bill> findAllBillsById(Integer id) { return groupRepository.findAllBillsById(id.longValue()); }
}
