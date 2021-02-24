import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.LinkedList;

public interface MasterInterface extends Remote {
     LinkedList<ProcessCombinationModel> criacao() throws RemoteException, NotBoundException, MalformedURLException;
}
