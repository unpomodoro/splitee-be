package cz.cvut.fit.splitee.repository;

import cz.cvut.fit.splitee.entity.Split;
import cz.cvut.fit.splitee.entity.SplitPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SplitRepository extends JpaRepository<Split, SplitPK> {
//    Collection<Split> findByIdBill(Integer billId);
//    Collection<Split> findByIdMember(Integer memberId);
}
