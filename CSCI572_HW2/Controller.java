import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

public class Controller {
	
	public static void main(String[] args) throws Exception{
		String crawlStorageFolder = "/data/crawl";
		int numberOfCrawlers = 7;
		int maxDepthOfCrawling = 16;
		int maxPagesToFetch = 20000;
		int politenessDelay = 200;
		String seed = "https://www.latimes.com/";
		
		try{
			BufferedWriter bw = new BufferedWriter(new FileWriter("fetch.csv"));
			bw.write("URL fetched, Status Code\n");
			bw.close();
			
			bw = new BufferedWriter(new FileWriter("urls.csv"));
			bw.write("Processed URLs, Indicator\n");
			bw.close();
			
			bw = new BufferedWriter(new FileWriter("visit.csv"));
			bw.write("Downloaded URL, Size of Downloaded File, No. of Outgoing Links, Content Type\n");
			bw.close();

		}catch(IOException ioe){
			ioe.printStackTrace();
		}
		
		CrawlConfig config = new CrawlConfig();
		config.setCrawlStorageFolder(crawlStorageFolder);
		config.setMaxDepthOfCrawling(maxDepthOfCrawling);
		config.setMaxPagesToFetch(maxPagesToFetch);
		config.setPolitenessDelay(politenessDelay);
		config.setIncludeBinaryContentInCrawling(true);
		//config.setMaxDownloadSize(10485760);
		
		PageFetcher pageFetcher = new PageFetcher(config);
		RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
		RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
		CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);
		
		controller.addSeed(seed);
		controller.start(MyCrawler.class,  numberOfCrawlers);
	}

}
