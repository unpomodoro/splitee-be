package cz.cvut.fit.splitee.service;

import cz.cvut.fit.splitee.entity.Account;
import cz.cvut.fit.splitee.entity.Group;
import cz.cvut.fit.splitee.entity.Membership;
import cz.cvut.fit.splitee.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@Service
public class AccountService implements UserDetailsService {
    @Autowired
    private AccountRepository accountRepository;

    public Optional<Account> findById(Integer id) { return accountRepository.findById(id.longValue()); }
    @Transactional
    public Account createOrUpdate(Account account) { return accountRepository.save(account); }

    public Collection<Group> findAllGroupsById(Integer id) {
        Collection<Group> groups = new ArrayList<>();
        Collection<Membership> memberships = accountRepository.findById(id.longValue()).get().getMemberships();
        for (Membership m : memberships) {
            groups.add(m.getGroup());
        }
        return groups;
    }

    public void deleteById(Integer id) {
        Optional<Account> optional = accountRepository.findById(id.longValue());

        if(optional.isEmpty()) return;
        // member should not be removed because member still can exist without an account...?
        accountRepository.deleteById(id.longValue());
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Account user = accountRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with email: " + email));
        return UserDetailsImpl.build(user);
    }
}
