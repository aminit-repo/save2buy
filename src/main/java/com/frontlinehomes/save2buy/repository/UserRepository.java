package com.frontlinehomes.save2buy.repository;

import com.frontlinehomes.save2buy.data.users.User;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.NoSuchElementException;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long>  {

    public User save(User user) throws ConstraintViolationException;

    public Optional<User> findById(Long id) throws NoSuchElementException;

    public User findByEmail(String email);
}
