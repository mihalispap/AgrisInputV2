package org.fao.oekc.agris.inputRecords.index;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

/**
 * Common query methods to delete/modify part of the index
 * @author celli
 *
 */
public class QueryUtil {
	
	private final static Logger log = Logger.getLogger(QueryUtil.class.getName());
	
	//delete all index
	public static void deleteAll(SolrServer solr) throws SolrServerException, IOException{
		solr.deleteByQuery("*:*");
		solr.commit();
	}
	
	//optimize index
	public static void optimize(SolrServer solr) throws SolrServerException, IOException{
		solr.optimize(true, true, 2);
	}
	
	//delete by ARN
	public static void deleteByArn(SolrServer solr, String arn) throws SolrServerException, IOException{
		solr.deleteById(arn);
		solr.commit();
	}
	
	//delete by query
	public static void deleteByQuery(SolrServer solr, String query) throws SolrServerException, IOException{
		solr.deleteByQuery(query);
		solr.commit();
	}
	
	/**
	 * Get Number of results
	 * @param query
	 * @return
	 * @throws MalformedURLException
	 */
	public long getNumberOfResults(String query) throws MalformedURLException {
		SolrServer server = IndexServerFactory.startSolr();
		SolrQuery slrQuery = new SolrQuery(query);
		slrQuery.setStart(0);	
		slrQuery.setRows(0); 
		slrQuery.setIncludeScore(true);
		
		//prepare number of results
		long totalRes = 0;

		try {
			SolrDocumentList docs = this.getSolrDocumentResult(server, slrQuery);
			totalRes = docs.getNumFound();
		} catch (SolrServerException e) {
			log.log(Level.WARNING, e.toString());
		}
		return totalRes;
	}
	
	/**
	 * Find the max value in a given field for a given query
	 * @param query the query to be executed (usually a regex, e.g. JP20060* or [*:*])
	 * @param field the field the field in which we need to find the maximum
	 * @return the max value in the given field for the given query
	 * @throws MalformedURLException 
	 */
	public String selectMaxValue(String query, String field) throws MalformedURLException{
		SolrServer solr = IndexServerFactory.startSolr();
		//build the query	
		SolrQuery squery = new SolrQuery(field+":"+query);
		squery.setStart(0);
		squery.setRows(1);

		//order by ARN descending (max first)
		SolrQuery.ORDER order = SolrQuery.ORDER.desc;
		squery.addSort(field, order);
		squery.setParam("fl", field);
		try {
			//search
			SolrDocumentList results = this.getSolrDocumentResult(solr, squery);
			for(SolrDocument doc : results){
				return (String) doc.getFieldValue(field);
			}
		} catch (SolrServerException e) {
			log.log(Level.WARNING, e.toString());
		}
		return null;
	}
	
	/*
	 * Return the results of a query
	 */
	public static SolrDocumentList getSolrDocumentResult(SolrServer solr, SolrQuery squery) throws SolrServerException{
		//search
		QueryResponse response = solr.query(squery);
		return response.getResults();
	}

	
	/*
	 * TEST
	 */
	public static void main(String[] args) throws MalformedURLException, SolrServerException, IOException{
		
		//QueryUtil.optimize(IndexServerFactory.startSolr());
		 /*String query = "+center:GB +date:2012";
		 QueryUtil.deleteByQuery(IndexServerFactory.startSolr(), query);*/
		QueryUtil util = new QueryUtil();
		String max = util.selectMaxValue("TR20141*", "ARN");
		System.out.println(max);
	}

}
