import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import harreader.HarReader;
import harreader.HarReaderException;
import harreader.model.Har;
import harreader.model.HarEntry;

public class Storage extends UnicastRemoteObject implements StorageInterface{
    ReducerInterface reducer;
    Set<Set<String>> combinacao;
    MapperInterface mapper;
    LinkedList<ProcessCombinationModel> combinationStatistics = new LinkedList<>();
    private static LinkedHashMap<String, ArrayList<ResourceInfo>> client_data = new LinkedHashMap<>();
    protected Storage() throws RemoteException {
    }

    @Override
    public void Send(String filename, byte[] array, int size, int index) throws IOException, HarReaderException {
        String filepath = ".\\Storage\\namepasta\\";
        new File(filepath).mkdirs();
        File file = new File(filepath + filename);
        file.createNewFile();
        FileOutputStream out = new FileOutputStream(file, true);
        out.write(array, 0, size);
        out.flush();
        out.close();
        CarregamentoMemoria(index);

    }

    @Override
    public void SendMapper() throws RemoteException, MalformedURLException, NotBoundException {
        mapper = (MapperInterface) Naming.lookup("rmi://localhost:2023/mapper");
        mapper.ReceivedStorage(client_data);
    }
    private void SendReducer() throws RemoteException, MalformedURLException, NotBoundException {
        reducer = (ReducerInterface) Naming.lookup("rmi://localhost:2021/reducer");
        reducer.Receiveddata_client(client_data);
        reducer.ReceivedStorage(combinacao);
    }

    public void ReceivedMapper(Set<Set<String>> combinacao) throws RemoteException, MalformedURLException, NotBoundException {
        this.combinacao = combinacao;
        SendReducer();
    }

    @Override
    public void ReceivedProcessamento(LinkedList<ProcessCombinationModel> combinationStatistics) throws RemoteException {
        this.combinationStatistics = combinationStatistics;
    }

    @Override
    public boolean VerificarCombStatistic() throws RemoteException {
        if(combinationStatistics.size() == 0){
            return false;
        }
        return true;
    }

    @Override
    public LinkedList<ProcessCombinationModel> GetCombStatistic() throws RemoteException {
       return combinationStatistics;
    }

    private void CarregamentoMemoria(int index) throws HarReaderException {
        File filepath = new File(".\\Storage\\namepasta\\");
        String contents[] = filepath.list();
        if (index == 76){
            FillResourcesMap(".\\Storage\\namepasta\\", "www_nytimes_com",client_data);
            System.out.println("Carregamento efectuado");
        }
    }


    public void FillResourcesMap(String path, String fileName, LinkedHashMap<String, ArrayList<ResourceInfo>> timeHarMap) throws HarReaderException {
        int[] count = new int[]{0};
        int fileCount = 0;
        try {
            HarReader harReader = new HarReader();
            File file = new File(path + fileName + ".har");
            while (file.exists()){
                Har otherHar = harReader.readFromFile(file);
                for (HarEntry otherEntry : otherHar.getLog().getEntries()) {
                    if (!otherEntry.getResponse().getHeaders().get(0).getValue().contains("no-cache")) {
                        ResourceInfo resourceInfo = new ResourceInfo();
                        resourceInfo.resourceTime = (float) otherEntry.getTime();
                        resourceInfo.resourceType = otherEntry.get_resourceType();
                        resourceInfo.cachedResource = otherEntry.getResponse().getHeaders().get(0).getValue();
                        resourceInfo.resourceLength = otherEntry.getResponse().getBodySize();
                        resourceInfo.harRun = fileCount;

                        if (timeHarMap.containsKey(otherEntry.getRequest().getUrl())) {
                            ArrayList<ResourceInfo> list = timeHarMap.get(otherEntry.getRequest().getUrl());
                            AtomicBoolean repeatedCall = new AtomicBoolean(false);
                            list.forEach(value -> {
                                if (value.resourceTime.equals(resourceInfo.resourceTime)) {
                                    repeatedCall.set(true);
                                    return;
                                }
                            });
                            if (!repeatedCall.get())
                                timeHarMap.get(otherEntry.getRequest().getUrl()).add(resourceInfo);
                        } else {
                            ArrayList<ResourceInfo> l = new ArrayList<>();
                            l.add(resourceInfo);
                            timeHarMap.put(otherEntry.getRequest().getUrl(), l);
                        }

                    }
                }
                file = new File(path + fileName + "_" + ++fileCount +".har");
            }
        } catch (Exception ex) {
            // e.printStackTrace();
            System.out.println(ex.getMessage());
        }
    }
}
