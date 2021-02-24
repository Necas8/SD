import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Set;

public interface ReducerInterface extends Remote {
    void ReceivedStorage(Set<Set<String>> dados) throws RemoteException, MalformedURLException, NotBoundException;
    void Receiveddata_client(LinkedHashMap<String, ArrayList<ResourceInfo>> client_data) throws RemoteException;
}
