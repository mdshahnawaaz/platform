package com.logistic.platform.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.logistic.platform.models.Contact;
@Repository
public interface ContactRepository extends JpaRepository<Contact, Long>{

    @Query(value="select * from contact",nativeQuery=true)
    List<Contact>getAll();
    
}
