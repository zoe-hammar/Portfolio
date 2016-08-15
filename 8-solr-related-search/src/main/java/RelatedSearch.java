import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import org.apache.solr.client.solrj.SolrClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;

public class RelatedSearch {

    static Logger logger = LoggerFactory.getLogger(RelatedSearch.class);

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(7777), 0);
        server.createContext("/search", new SearchHandler());
        server.createContext("/item-detail", new ItemDetailHandler());
        server.setExecutor(null);
        server.start();
    }

    static class SearchHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            QueryResponse response;
            String responseStr;
            Map<String, String> params = getParamMap(t);
            HttpSolrClient itemClient = new HttpSolrClient("http://localhost:8983/solr/sample-items");
            HttpSolrClient searchTermClient = new HttpSolrClient("http://localhost:8983/solr/related-search");
            String solrQueryStr;

            if (params.containsKey("q") && !params.get("q").equals("")) {
                solrQueryStr = params.get("q");
            } else {
                solrQueryStr = "*:*";
            }

            try {
                logger.info("search query: " + solrQueryStr);
                response = itemClient.query(new SolrQuery(solrQueryStr));
                itemClient.close();
                responseStr = "Search Results:\n" + response.getResults().toString();
            } catch (SolrServerException e) {
                e.printStackTrace();
                itemClient.close();
                sendResponse(t, 500, "Query failed to execute");
                return;
            }

            solrQueryStr = "";
            String searchTerms[] = params.get("q").split(" ");
            for (String term : searchTerms) {
                if (term.charAt(0) != '+' && term.charAt(0) != '-')
                    solrQueryStr += "+";
                if (term.charAt(0) == '-')
                    continue;
                solrQueryStr += term + " ";
            }

            if (solrQueryStr != "*:*") {
                SolrQuery query = new SolrQuery("terms_t:(" + solrQueryStr + ")");
                query.set("fl", "terms_t");
                query.set("sort", "score_i desc");
                try {
                    response = searchTermClient.query(query);
                    searchTermClient.close();
                } catch (SolrServerException e) {
                    e.printStackTrace();
                    logger.error("Failed to get related search results for query: " + params.get("q"));
                    sendResponse(t, 200, "Search Results:\n" + responseStr);
                    searchTermClient.close();
                    return;
                }

                String relatedSearchesStr = "";
                for (SolrDocument doc : response.getResults()) {
                    String storedTermsStr = doc.getFieldValues("terms_t").toArray(new String[0])[0];
                    relatedSearchesStr += storedTermsStr + "\n";
                }
                responseStr = "Related Searches:\n" + relatedSearchesStr + "\n" + responseStr;
            }

            sendResponse(t, 200, responseStr);
        }
    }

    static class ItemDetailHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            QueryResponse response;
            String responseStr;
            Map<String, String> params = getParamMap(t);
            HttpSolrClient itemClient = new HttpSolrClient("http://localhost:8983/solr/sample-items");
            HttpSolrClient searchTermClient = new HttpSolrClient("http://localhost:8983/solr/related-search");
            String itemQueryStr;
            int statusCode;

            if (params.containsKey("id") && !params.get("id").equals("")) {
                itemQueryStr = "id:" + params.get("id");
            } else {
                itemClient.close();
                searchTermClient.close();
                sendResponse(t, 400, "Item ID must be provided");
                return;
            }

            if (params.containsKey("search-terms") && !params.get("search-terms").equals("")) {
                String solrQueryStr = "";
                String searchTerms[] = params.get("search-terms").split(" ");
                int negativeTermCt = 0;
                for (String term : searchTerms) {
                    if (term.charAt(0) != '+' && term.charAt(0) != '-')
                        solrQueryStr += "+";
                    if (term.charAt(0) == '-')
                        ++negativeTermCt;
                    solrQueryStr += term + " ";
                }

                SolrQuery query = new SolrQuery("terms_t:(" + solrQueryStr + ")");
                query.set("rows", "20");    // for simplicity. Would be sure to check all results if I had time.
                query.set("fl", "terms_t");
                try {
                    response = searchTermClient.query(query);
                } catch (SolrServerException e1) {
                    e1.printStackTrace();
                    response = null;
                }

                docMatch: {
                    if (response == null)
                        break docMatch;

                    for (SolrDocument doc : response.getResults()) {
                        String storedTermsStr = doc.getFieldValues("terms_t").toArray(new String[0])[0];
                        if (searchTerms.length - negativeTermCt == storedTermsStr.split(" ").length) {
                            //increment the score of an existing searchTerm entry
                            logger.info("incrementing old doc: " + storedTermsStr);
                            try {
                                incrementSearchQueryScore(searchTermClient, storedTermsStr);
                            } catch (SolrServerException e) {
                                e.printStackTrace();
                                logger.error("Failed to increment score_i of " + storedTermsStr);
                            }
                            break docMatch;
                        }
                    }
                    // create new searchTerm entry
                    String termsStr = "";
                    for (String term : searchTerms) {
                        if (term.charAt(0) == '+')
                            termsStr += term.substring(1) + " ";
                        else if (term.charAt(0) == '-')
                            continue;
                        else
                            termsStr += term + " ";
                    }
                    termsStr = termsStr.trim();

                    try {
                        logger.info("creating new doc: " + termsStr);
                        incrementSearchQueryScore(searchTermClient, termsStr);
                    } catch (SolrServerException e) {
                        e.printStackTrace();
                        logger.error("Failed to increment score_i of " + termsStr);
                    }
                }
                searchTermClient.close();
            }

            try {
                response = itemClient.query(new SolrQuery(itemQueryStr));
                responseStr = response.getResults().toString();
                statusCode = 200;
            } catch (SolrServerException e) {
                e.printStackTrace();
                responseStr = "Query failed to execute";
                statusCode = 500;
            }
            itemClient.close();

            sendResponse(t, statusCode, responseStr);
        }
    }

    // also creates a new document and sets the score to 1 if one doesn't exist
    private static void incrementSearchQueryScore(SolrClient solr, String searchQueryStr) throws IOException, SolrServerException {
        SolrInputDocument iDoc = new SolrInputDocument();
        iDoc.addField("id", searchQueryStr);
        iDoc.addField("terms_t", searchQueryStr);
        Map<String, Object> fieldModifier = new HashMap<>(1);
        fieldModifier.put("inc", 1);
        iDoc.addField("score_i", fieldModifier);
        solr.add(iDoc);
        solr.commit();
    }

    private static void sendResponse(HttpExchange exc, int statusCode, String responseStr) throws IOException {
        exc.sendResponseHeaders(statusCode, responseStr.getBytes().length);
        OutputStream os = exc.getResponseBody();
        os.write(responseStr.getBytes());
        os.close();
    }

    private static Map<String, String> getParamMap(HttpExchange exc) {
        String query = exc.getRequestURI().getQuery();
        if (query == null) {
            query = "";
        }
        Map<String, String> result = new HashMap<String, String>();
        for (String param : query.split("&")) {

            String pair[] = param.split("=");
            if (pair.length > 1) {
                result.put(pair[0], pair[1]);
            } else {
                result.put(pair[0], "");
            }
        }
        return result;
    }

}
