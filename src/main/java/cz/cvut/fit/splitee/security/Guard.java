package cz.cvut.fit.splitee.security;

import cz.cvut.fit.splitee.entity.*;
import cz.cvut.fit.splitee.repository.AccountRepository;
import cz.cvut.fit.splitee.repository.BillRepository;
import cz.cvut.fit.splitee.repository.GroupRepository;
import cz.cvut.fit.splitee.repository.MembershipRepository;
import cz.cvut.fit.splitee.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;

@Component
@Transactional
public class Guard {

    @Autowired
    AccountService accountService;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    GroupRepository groupRepository;
    @Autowired
    MembershipRepository membershipRepository;
    @Autowired
    BillRepository billRepository;

    public boolean isInGroup(Authentication authentication, String groupCode) {
        Optional<Account> a = accountRepository.findByEmail(authentication.getName());

        if (a.isPresent()) {
            Collection<Group> groups = accountService.findAllGroupsById(a.get().getId().intValue());
            Optional<Group> g = groupRepository.findByCode(groupCode);
            return g.filter(groups::contains).isPresent();
        }
        else return false;
    }

    public boolean isUser (Authentication authentication, Long id) {
        Optional<Account> a = accountRepository.findByEmail(authentication.getName());
        if(a.isPresent()) {
            Account account = a.get();
            return account.getId().equals(id);
        }
        else return false;
    }

    public boolean isInGroupMember(Authentication authentication, Long mId) {
        Optional<Membership> m = membershipRepository.findById(mId);

        if (m.isPresent()) {
            // get group, call isingroup
            Group g = m.get().getGroup();
            return isInGroup(authentication, g.getCode());
        }
        else return false;
    }

    public boolean isInGroupBill(Authentication authentication, Long mId) {
        Optional<Bill> b = billRepository.findById(mId);

        if (b.isPresent()) {
            // get group, call isingroup
            Group g = b.get().getGroup();
            return isInGroup(authentication, g.getCode());
        }
        else return false;
    }

}
