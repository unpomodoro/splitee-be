package cz.cvut.fit.splitee.repository;

import cz.cvut.fit.splitee.entity.Bill;
import cz.cvut.fit.splitee.entity.Split;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long>{
    @Query(value = "SELECT b.splits from bill b where b.id = :id")
    Collection<Split> findAllSplitsById(Long id);
}