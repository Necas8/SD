import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public interface MapperInterface extends Remote {
    void ReceivedStorage(LinkedHashMap<String, ArrayList<ResourceInfo>> timeHarMap) throws RemoteException, MalformedURLException, NotBoundException;
}
