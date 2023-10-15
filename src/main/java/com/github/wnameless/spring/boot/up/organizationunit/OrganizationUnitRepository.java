package com.github.wnameless.spring.boot.up.organizationunit;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;
import com.github.wnameless.spring.boot.up.data.mongodb.querydsl.MongoProjectionRepository;

@NoRepositoryBean
public interface OrganizationUnitRepository<OU extends OrganizationUnit, ID>
    extends CrudRepository<OU, ID>, MongoProjectionRepository<OU> {

  Optional<OU> findByOrganizationUnitName(String organizationUnitName);

}
