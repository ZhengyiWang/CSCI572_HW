import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;

public class BuildLinks {

    private HashMap<String, String> fileUrlMap;

    private HashMap<String, String> urlFileMap;

    private CSVParser createReader(String filePath){
        CSVParser csvParser = null;
        try {
            BufferedReader reader = Files.newBufferedReader(Paths.get(filePath));
            reader.readLine();
            csvParser = new CSVParser(reader, CSVFormat.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return csvParser;
    }

    private void readCSVFile(String filePath){
        fileUrlMap = new HashMap<>();
        urlFileMap = new HashMap<>();
        CSVParser parser = createReader(filePath);
        for(CSVRecord record : parser){
            fileUrlMap.put(record.get(0), record.get(1));
            urlFileMap.put(record.get(1), record.get(0));
        }
    }

    private void generateEdges(String dirPath, String txtFilePath) throws IOException {
        File dir = new File(dirPath);
        HashSet<String> edges = new HashSet<>();
        for(File file : dir.listFiles()){
            Document doc = Jsoup.parse(file, "UTF-8", fileUrlMap.get(file.getName()));
            Elements links = doc.select("a[href]");

            for(Element link : links){
                String url = link.attr("href").trim();
                if(urlFileMap.containsKey(url)){
                    edges.add(file.getName() + " " + urlFileMap.get(url));
                }
            }
        }

        File txtFile = new File(txtFilePath);
        FileWriter fw = new FileWriter(txtFile);
        BufferedWriter bw = new BufferedWriter(fw);
        for(String str : edges){
            bw.write(str + "\n");
        }
        bw.close();
        fw.close();
    }

    public static void main(String[] args) throws IOException {

        BuildLinks buildLinks = new BuildLinks();
        buildLinks.readCSVFile("D:\\LATIMES\\URLtoHTML_latimes_news.csv");
        buildLinks.generateEdges("D:\\LATIMES\\latimes\\", "D:\\edgeslist.txt");

    }
}
