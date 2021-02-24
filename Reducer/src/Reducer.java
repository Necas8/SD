import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class Reducer extends UnicastRemoteObject implements ReducerInterface{
    Set<Set<String>> dados;
    private static LinkedHashMap<String, ArrayList<ResourceInfo>> client_data = new LinkedHashMap<>();
    private LinkedList<ProcessCombinationModel> combinationStatistics = new LinkedList<>();
    StorageInterface storage;
    protected Reducer() throws RemoteException {
    }
    public void Receiveddata_client(LinkedHashMap<String, ArrayList<ResourceInfo>> client_data) throws RemoteException{
        this.client_data = client_data;

    }

    @Override
    public void ReceivedStorage(Set<Set<String>> dados) throws RemoteException, MalformedURLException, NotBoundException {
        this.dados = dados;
        Processamento(dados.size());
        SendProcessamento();
    }
    private void Processamento(int fileCount) throws RemoteException{
        Set r;
        StringBuffer line = new StringBuffer();
        Iterator combIterator = dados.iterator();
        System.out.println("Número combinações " + dados.size());
        int i = 0;
        while (combIterator.hasNext()) {
            r = (Set) combIterator.next();

            line.setLength(0);
            Iterator lineIterator = r.iterator();
            while (lineIterator.hasNext()) {
                if (line.length() > 0) line.append(",");
                line.append(lineIterator.next().toString());
            }

            ProcessCombinationModel combinationInfo = new ProcessCombinationModel();
            combinationInfo.combination = line.toString();
            calculateStatistics(combinationInfo, fileCount);
            if (i++ % 100000 == 0) System.out.println("Comb " + i);
        }
    }
    private void calculateStatistics(ProcessCombinationModel combinationInfo, int fileCount){
        boolean resourceFound=false;

        String[] resources = combinationInfo.combination.split(","); // resources of each combination
        for (int i =0; i < fileCount; i++) { //controlo por run
            for(String combinationResource : resources){
                resourceFound=false;
                for(ResourceInfo comb : client_data.get(combinationResource))
                    if(comb.harRun == i) {
                        resourceFound = true;
                        combinationInfo.resourceLength += comb.resourceLength;
                        break;
                    }
                if(! resourceFound){ break;}
            }
            if(resourceFound){
                combinationInfo.numberOfRuns++;
            }
        }

        combinationInfo.percentage = (float) combinationInfo.numberOfRuns/fileCount;

        if(combinationInfo.percentage > 0.5) {
            System.out.print("Combination + probability");
            for(String s: resources) System.out.print(System.identityHashCode(s) + "  ");
            System.out.print(combinationInfo.percentage + "\n");

            this.combinationStatistics.add(combinationInfo);
            System.out.println("Comb valida. Percentagem: " + combinationInfo.percentage);
        }
    }
    private void SendProcessamento() throws RemoteException, NotBoundException, MalformedURLException {
        storage = (StorageInterface) Naming.lookup("rmi://localhost:2020/storage");
        storage.ReceivedProcessamento(combinationStatistics);
        System.out.println(" Dados processados e enviados para o Storage! ");
    }
}
