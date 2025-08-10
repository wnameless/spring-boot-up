package com.github.wnameless.spring.boot.up.attachment;

import java.net.URI;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface BaseLocalAttachmentRepository<A, ID> extends CrudRepository<A, ID> {

  Optional<A> findFirstByUri(URI uri);

}
