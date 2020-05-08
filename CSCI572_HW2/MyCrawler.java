import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Pattern;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.BinaryParseData;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.parser.ParseData;
import edu.uci.ics.crawler4j.url.WebURL;

public class MyCrawler extends WebCrawler {
	
	private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|mp3|zip|gz|vcf|xml))$");
	private final static String baseUrl1 = "http://www.latimes.com/";
	private final static String baseUrl2 = "https://www.latimes.com/";
	
	@Override
	protected void handlePageStatusCode(WebURL webUrl, int statusCode, String statusDescription) {
	    // Do nothing by default
	    // Sub-classed can override this to add their custom functionality
		String urlStr = webUrl.getURL().toLowerCase();
		if(urlStr.startsWith(baseUrl1) || urlStr.startsWith(baseUrl2)){
	    	try{
	    		synchronized(this){
	    			BufferedWriter bw = new BufferedWriter(new FileWriter("fetch.csv",true));
					bw.append(webUrl.getURL().replace(",", "-")+","+statusCode+"\n");
					bw.close();
		    	}
			} catch(IOException ioe){
				ioe.printStackTrace();
			}
	    }
	    
	  }
	
	protected void onPageBiggerThanMaxSize(String urlStr, long pageSize) {
	    logger.warn("Skipping a URL: {} which was bigger ( {} ) than max allowed size", urlStr, pageSize);
	  }

	
	@Override
	public boolean shouldVisit(Page referringPage, WebURL url) {
		String href = url.getURL().toLowerCase();
		//System.out.println(referringPage.getWebURL().getURL() + "  "+href);
		try{
			synchronized(this){
	    		BufferedWriter bw = new BufferedWriter(new FileWriter("urls.csv",true));
	    		if(href.startsWith(baseUrl1) || href.startsWith(baseUrl2))
	    			bw.append(url.getURL().replace(",", "-")+", OK\n");
	    		else
	    			bw.append(url.getURL().replace(",", "-")+", N_OK\n");
	    		bw.close();
			}
		} catch(IOException ioe){
			ioe.printStackTrace();
		}
		
		return (href.startsWith(baseUrl1) || href.startsWith(baseUrl2)) && !FILTERS.matcher(href).matches();
	}
	
	/**
	* This function is called when a page is fetched and ready
	* to be processed by your program.
	*/
	@Override
	public void visit(Page page) {
		String url = page.getWebURL().getURL().toLowerCase(); 

			ParseData parseData = page.getParseData();
			//String text = htmlParseData.getText();
			Set<WebURL> links = parseData.getOutgoingUrls();
			//System.out.println("Text length: " + text.length());
			//System.out.println("Html length: " + html.length());
			//System.out.println("Number of outgoing links: " + links.size());
			//System.out.println("Status Code: "+statusCode);
			//System.out.println("Content Type: " + contentType);
			
			try {
				synchronized(this){
					if(url.endsWith(".png") || url.endsWith(".jpg") || url.endsWith(".gif"))
						System.out.println("Found image url: " + url);
		    		BufferedWriter bw = new BufferedWriter(new FileWriter("visit.csv",true));
		    		bw.append(page.getWebURL().getURL().replace(",", "-")+","+page.getContentData().length+","+links.size()+","+page.getContentType()+"\n");
		    		bw.close();
		    		
		    		
		    		}
			} catch (IOException ioe) {
				// TODO Auto-generated catch block
				ioe.printStackTrace();
			} 		
	}

}
