package aichallenge;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

public interface MatchRepository extends PagingAndSortingRepository<Match, Long>
{
    Page<Match> findAll(Pageable pageable);
}
