package zmz.service;

import zmz.entity.Term;
import java.util.List;

public interface TermService {
    boolean save(Term term);
    Term getById(Long termId);
    boolean update(Term term);
    boolean delete(Long termId);
    List<Term> list(String termName);
} 