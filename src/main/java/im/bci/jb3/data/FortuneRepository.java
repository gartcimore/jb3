package im.bci.jb3.data;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface FortuneRepository extends MongoRepository<Fortune, String> {
    
}
