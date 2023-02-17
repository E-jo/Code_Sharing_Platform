package platform.Repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import platform.Models.CodeFragment;

import java.util.List;
import java.util.Optional;

@Repository
public interface CodeFragmentRepository extends PagingAndSortingRepository<CodeFragment, String> {
    void deleteById(String id);
    Optional<CodeFragment> findById(String id);
    Page<CodeFragment> findAllByViewsLessThanEqualAndTimeLessThanEqual(Long views, Long time, Pageable paging);
    CodeFragment save(CodeFragment codeFragment);
    void deleteAll();
}