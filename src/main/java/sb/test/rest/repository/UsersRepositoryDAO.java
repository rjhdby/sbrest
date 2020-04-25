package sb.test.rest.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import sb.test.rest.domain.Employee;

@Repository
public interface UsersRepositoryDAO extends PagingAndSortingRepository<Employee, Long> {
    Employee findByLogin(String login);

    Page<Employee> findAllByNameLike(String name, Pageable request);
}
