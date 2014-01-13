package aichallenge;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface PidginRepository extends CrudRepository<Pidgin, Long>
{
    Pidgin findByLogin(String login);
    List<Pidgin> findByLastName(String lastName);
}
