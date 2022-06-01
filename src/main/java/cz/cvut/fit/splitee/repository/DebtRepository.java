package cz.cvut.fit.splitee.repository;

import cz.cvut.fit.splitee.entity.Debt;
import cz.cvut.fit.splitee.entity.DebtPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DebtRepository extends JpaRepository<Debt, DebtPK> {
}
