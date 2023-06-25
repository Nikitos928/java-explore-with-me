package ru.practicum.user.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.user.model.User;

public interface UserRepository extends JpaRepository<User, Integer> {
    @Query(" select u from User u " +
            "where u.id in ?1 " +
            "ORDER BY u.id ")
    Page<User> getUsersByIds(int[] ids, Pageable pageable);

    @Query(" select u from User u " +
            "ORDER BY u.id ")
    Page<User> getUsersOrderById(Pageable pageable);


    User findFirstByEmailOrName(String email, String name);
}
