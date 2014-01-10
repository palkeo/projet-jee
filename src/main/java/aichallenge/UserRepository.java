package aichallenge;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long>
{
    User findByLogin(String login);
    List<User> findByLastName(String lastName);
}
