import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Set;

public class Mapper extends UnicastRemoteObject implements MapperInterface {
    StorageInterface storage;
    LinkedHashMap<String, ArrayList<ResourceInfo>> mapper = new LinkedHashMap<>() ;
    protected Mapper() throws RemoteException {

    }
    public Set<Set<String>> combinacao(int len){
        ArrayList<String> resources= new ArrayList<>(mapper.keySet());
        System.out.println("Número resources " + resources.size());
        Set<Set<String>> combinations = Sets.combinations(ImmutableSet.copyOf(resources), len);
        return combinations;
    }
    public void combinusage() throws RemoteException, NotBoundException, MalformedURLException {
        storage = (StorageInterface) Naming.lookup("rmi://localhost:2020/storage");
        storage.ReceivedMapper(combinacao(10));
    }
    @Override
    public void ReceivedStorage(LinkedHashMap<String, ArrayList<ResourceInfo>> timeHarMap) throws RemoteException, MalformedURLException, NotBoundException {
        mapper = timeHarMap;
        System.out.println(" Dados recebidos da Storage! ");
        combinusage();
    }
}
