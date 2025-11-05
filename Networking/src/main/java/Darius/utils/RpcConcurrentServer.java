package Darius.utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import Darius.IService;
import Darius.rpcprotocol.ClientRpcWorker;

import java.net.Socket;

public class RpcConcurrentServer extends AbsConcurrentServer{
    private IService server;
    private static Logger logger = LogManager.getLogger(RpcConcurrentServer.class);

    public RpcConcurrentServer(int port, IService server) {
        super(port);
        this.server = server;
        logger.info("RpcConcurrentServer");
    }

    @Override
    protected Thread createWorker(Socket client) {
        ClientRpcWorker worker = new ClientRpcWorker(server, client);

        Thread thread = new Thread(worker);
        return thread;
    }

    @Override
    public void stop(){
        logger.info("Stopping...");
    }
}
