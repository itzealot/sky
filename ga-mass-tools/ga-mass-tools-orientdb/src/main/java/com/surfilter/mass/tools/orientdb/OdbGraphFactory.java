package com.surfilter.mass.tools.orientdb;


import com.orientechnologies.orient.client.remote.OStorageRemote;
import com.orientechnologies.orient.core.config.OGlobalConfiguration;
import com.orientechnologies.orient.graph.batch.OGraphBatchInsert;
import com.surfilter.mass.tools.utils.Constants;
import com.tinkerpop.blueprints.impls.orient.OrientGraph;
import com.tinkerpop.blueprints.impls.orient.OrientGraphFactory;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;

/**
 * Created by wellben on 2016/10/18.
 */
public class OdbGraphFactory {

    private final static OrientGraphFactory factory = new OrientGraphFactory(Constants.OCONN, "root", "root").setupPool(100, 1000);
    private final static OGraphBatchInsert batch = new OGraphBatchInsert(Constants.OCONN, "root", "root");
    private static int count = 0;

    static {
        setConnectionStrategy();
        OGlobalConfiguration.RID_BAG_EMBEDDED_TO_SBTREEBONSAI_THRESHOLD.setValue(-1);
        OGlobalConfiguration.SQL_GRAPH_CONSISTENCY_MODE.setValue("notx_async_repair");
    }

    private static void setConnectionStrategy() {
        factory.setConnectionStrategy(OStorageRemote.CONNECTION_STRATEGY.ROUND_ROBIN_CONNECT.toString());
    }

    public static OrientGraphNoTx getFactoryNoTx() {
        OrientGraphNoTx db = factory.getNoTx();
        db.setMaxRetries(100);
        return db;
    }

    public static OrientGraph getFactoryTx() {
        return factory.getTx();
    }

    public static synchronized int increment() {
        return count++;
    }

}
