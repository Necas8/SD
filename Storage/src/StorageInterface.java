import harreader.HarReaderException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Set;

public interface StorageInterface extends Remote {
    void Send (String filename, byte[] array, int size, int index) throws IOException, HarReaderException;
    void SendMapper() throws RemoteException, MalformedURLException, NotBoundException;
    void ReceivedMapper(Set<Set<String>> combinacao) throws RemoteException, MalformedURLException, NotBoundException;
    void ReceivedProcessamento(LinkedList<ProcessCombinationModel> combinationStatistics) throws RemoteException;
    boolean VerificarCombStatistic() throws RemoteException;
    LinkedList<ProcessCombinationModel> GetCombStatistic() throws RemoteException;
}
