package cz.cvut.fit.splitee.repository;

import cz.cvut.fit.splitee.entity.Debt;
import cz.cvut.fit.splitee.entity.Membership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface MembershipRepository extends JpaRepository<Membership, Long> {
    @Query(value = "SELECT m.debtsOwes from membership m where m.id = :id")
    Collection<Debt> findAllDebtsOwesById(Long id);

    @Query(value = "SELECT m.debtsGetsBack from membership m where m.id = :id")
    Collection<Debt> findAllDebtsGetsBackById(Long id);
}
