package uz.pdp.task1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.task1.entity.Customer;
import uz.pdp.task1.entity.Detail;

import java.util.Optional;


public interface DetailRepository extends JpaRepository<Detail, Integer> {
    boolean existsByOrder_Id(Integer order_id);
    Detail findByOrder_Id(Integer order_id);
}
