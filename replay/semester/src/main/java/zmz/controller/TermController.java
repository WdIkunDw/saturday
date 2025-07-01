package zmz.controller;

import zmz.entity.Term;
import zmz.service.TermService;
import zmz.service.TermServiceImpl;
import java.util.List;

public class TermController {

    private final TermService termService = new TermServiceImpl();

    public boolean addTerm(Term term) {
        return termService.save(term);
    }

    public Term getTerm(Long termId) {
        return termService.getById(termId);
    }

    public boolean updateTerm(Term term) {
        return termService.update(term);
    }

    public boolean deleteTerm(Long termId) {
        return termService.delete(termId);
    }

    public List<Term> listTerms(String termName) {
        return termService.list(termName);
    }
} 