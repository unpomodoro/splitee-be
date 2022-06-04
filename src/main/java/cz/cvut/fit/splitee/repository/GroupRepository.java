package cz.cvut.fit.splitee.repository;

import cz.cvut.fit.splitee.entity.Bill;
import cz.cvut.fit.splitee.entity.Group;
import cz.cvut.fit.splitee.entity.Membership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {

    Optional<Group> findByCode(String code);

    @Query(value = "SELECT g.memberships from group g where g.code = :code")
    Collection<Membership> findAllMembersByCode(String code);

    //Collection<Membership> findAllMembersWithoutAccount(Long id);

    @Query(value = "SELECT g.bills from group g where g.code = :code")
    Collection<Bill> findAllBillsByCode(String code);
}
