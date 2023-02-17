package platform.Services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import platform.Models.CodeFragment;
import platform.Repositories.CodeFragmentRepository;

import java.util.List;
import java.util.Optional;

@Service
public class CodeFragmentService {

    private final CodeFragmentRepository codeFragmentRepository;

    @Autowired
    public CodeFragmentService(CodeFragmentRepository codeFragmentRepository) {
        this.codeFragmentRepository = codeFragmentRepository;
    }

    public void deleteById(String id) {
        this.codeFragmentRepository.deleteById(id);
    }

    public Page<CodeFragment> findAll (Integer pageNo, Integer pageSize) {
        Pageable paging = PageRequest.of(pageNo, pageSize);

        Page<CodeFragment> pagedResult = codeFragmentRepository.findAll(paging);

        return pagedResult;
    }

    public Page<CodeFragment> findLatest () {
        Pageable paging = PageRequest.of(0, 10, Sort.by("date").descending());

        return codeFragmentRepository.findAllByViewsLessThanEqualAndTimeLessThanEqual(0L, 0L, paging);
    }

    public Optional<CodeFragment> findById(String id) {
        return this.codeFragmentRepository.findById(id);
    }

    public CodeFragment save(CodeFragment codeFragment) {
        return this.codeFragmentRepository.save(codeFragment);
    }

    public void deleteAll() {
        codeFragmentRepository.deleteAll();
    }
}