package com.mindex.challenge.dao;

import com.mindex.challenge.data.Compensation;
import org.springframework.stereotype.Repository;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * I've never used MongoDB before, so I had to google how use a Mongo repository.
 * I found that I had to name the find method findByEmployee_EmployeeId becuase
 * an employeeId is used to Query for a compensation, but compensations store
 * empoloyees and explicitly their ids.
 * The original method name of findByEmployeeId was causing an error.
 */
@Repository
public interface CompensationRepository extends MongoRepository<Compensation, String> {
    Compensation findByEmployee_EmployeeId(String id);
}
