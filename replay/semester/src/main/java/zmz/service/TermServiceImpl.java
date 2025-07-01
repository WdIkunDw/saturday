package zmz.service;

import zmz.entity.Term;
import zmz.dao.TermDao;
import java.util.List;

public class TermServiceImpl implements TermService {
    private final TermDao termDao = new TermDao();

    @Override
    public boolean save(Term term) {
        return termDao.save(term);
    }

    @Override
    public Term getById(Long termId) {
        return termDao.getById(termId);
    }

    @Override
    public boolean update(Term term) {
        return termDao.update(term);
    }

    @Override
    public boolean delete(Long termId) {
        return termDao.delete(termId);
    }

    @Override
    public List<Term> list(String termName) {
        return termDao.list(termName);
    }
} 