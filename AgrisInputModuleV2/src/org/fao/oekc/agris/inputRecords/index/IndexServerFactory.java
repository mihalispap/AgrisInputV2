package org.fao.oekc.agris.inputRecords.index;

import java.net.MalformedURLException;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.BinaryRequestWriter;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.fao.oekc.agris.Defaults;

/**
 * This class builds an instance of the Solr server to query data
 * @author celli
 *
 */
public class IndexServerFactory {
	
	//instance
		private static SolrServer server;

		//singleton for the default server
		public static synchronized SolrServer startSolr() throws MalformedURLException{
			if(server==null)
				startRemoteSolr();
			return server;
		}

		//starts the connection to a remote solr server
		private static void startRemoteSolr() throws MalformedURLException{
			startRemoteSolr(Defaults.getString("SolrServer"));
		}

		//start the remote server
		private static void startRemoteSolr(String remotePath) throws MalformedURLException{
			HttpSolrServer solr = new HttpSolrServer(remotePath);
			//This tells SolrJ to use the XML format, which works on any version
			//solr.setParser(new XMLResponseParser());
			solr.setRequestWriter(new BinaryRequestWriter());	//java binary format
			server = solr;
		}

		/*
		 * TEST
		 */
		public static void main(String[] args) throws MalformedURLException, SolrServerException{
			SolrServer solrServer = IndexServerFactory.startSolr();
			System.out.println(server.toString());
			//build the query
			SolrQuery slrQuery = new SolrQuery("rice");
			slrQuery.setStart(0);	
			slrQuery.setRows(1); 
			slrQuery.setIncludeScore(true);
			QueryResponse response = solrServer.query(slrQuery);
			System.out.println(response.getResults().get(0).get("title_eng"));
		}

}
