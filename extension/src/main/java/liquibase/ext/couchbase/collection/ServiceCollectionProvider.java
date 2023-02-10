package liquibase.ext.couchbase.collection;

import com.couchbase.client.java.Collection;

public interface ServiceCollectionProvider {

    Collection getServiceCollection(String collectionName);

}
