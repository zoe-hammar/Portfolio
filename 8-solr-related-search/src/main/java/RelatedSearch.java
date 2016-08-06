import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;

public class RelatedSearch {

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/search", new SearchHandler());
        server.setExecutor(null);
        server.start();
    }

    static class SearchHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            HttpSolrClient client = new HttpSolrClient("http://localhost:8983/solr/relatedSearch");

            QueryResponse response = null;
            try {
                response = client.query(new SolrQuery("*:*"));
            } catch (SolrServerException e) {
                e.printStackTrace();
            }
            client.close();

            t.sendResponseHeaders(200, response.getResults().toString().getBytes().length);
            OutputStream os = t.getResponseBody();
            os.write(response.getResults().toString().getBytes());
            os.close();
        }
    }

}
