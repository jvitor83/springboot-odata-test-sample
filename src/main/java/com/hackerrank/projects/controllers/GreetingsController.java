package com.hackerrank.projects.controllers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.Predicate;

import org.apache.olingo.odata2.annotation.processor.core.edm.AnnotationEdmProvider;
import org.apache.olingo.odata2.api.ODataService;
import org.apache.olingo.odata2.api.commons.HttpStatusCodes;
import org.apache.olingo.odata2.api.edm.Edm;
import org.apache.olingo.odata2.api.edm.EdmMapping;
import org.apache.olingo.odata2.api.edm.FullQualifiedName;
import org.apache.olingo.odata2.api.ep.callback.ReadFeedResult;
import org.apache.olingo.odata2.api.ep.callback.WriteFeedCallbackResult;
import org.apache.olingo.odata2.api.exception.ODataApplicationException;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.api.exception.ODataMessageException;
import org.apache.olingo.odata2.api.processor.ODataSingleProcessor;
import org.apache.olingo.odata2.api.rt.RuntimeDelegate;
import org.apache.olingo.odata2.api.uri.UriParser;
import org.apache.olingo.odata2.api.uri.expression.CommonExpression;
import org.apache.olingo.odata2.api.uri.expression.ExceptionVisitExpression;
import org.apache.olingo.odata2.api.uri.expression.ExpressionParserException;
import org.apache.olingo.odata2.api.uri.expression.FilterExpression;
import org.apache.olingo.odata2.api.uri.expression.OrderByExpression;
import org.apache.olingo.odata2.jpa.processor.core.ODataExpressionParser;
import org.hibernate.hql.internal.ast.HqlParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.hackerrank.projects.repositories.GenericSpecification;
import com.hackerrank.projects.repositories.PessoaRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Query;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

/**
 *
 * A sample greetings controller to return greeting text
 */
@RestController
public class GreetingsController {
	
    @Autowired
    private PessoaRepository repo;
    
    @Autowired
    private EntityManager entityManager;

    
    /**
     *
     * @param name the name to greet
     * @return greeting text
     * @throws Exception 
     */
    @RequestMapping(value = "/**", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public ODataResponse<List<Test>> greetingText(@RequestParam(name="$select", required=false) String select, @RequestParam(name="$expand", required=false) String expand, @RequestParam(name="$apply", required=false) String apply, @RequestParam(name="$filter", required=false) String filter, @RequestParam(name="$top", defaultValue="10") Integer top, @RequestParam(name="$skip", defaultValue="0") Integer skip, @RequestParam(name="$orderby", defaultValue="id desc") String orderby, @RequestParam(name="$count", defaultValue="false") Boolean count) throws Exception {
    	
    	if(select != null) {
    		throw new Exception("The $select query parameter is not supported!");
    	}
    	if(expand != null) {
    		throw new Exception("The $expand query parameter is not supported!");
    	}
    	if(apply != null) {
    		throw new Exception("The $apply query parameter is not supported!");
    	}
    	
    	
    	Class<Test> testClass = Test.class;
    	String typeName = testClass.getSimpleName();
    	String packageName = testClass.getPackage().getName();
    	
    	ODataService odataService = org.apache.olingo.odata2.annotation.processor.api.AnnotationServiceFactory.createAnnotationService(packageName);
    	Edm edm = odataService.getEntityDataModel();
    	org.apache.olingo.odata2.api.edm.EdmEntityType entityType = edm.getEntityType(packageName, typeName);
    	
		
    	javax.persistence.TypedQuery<Test> query = null;
    	Long countValue = 0L;
		try {
			// $select
			String selectClause = "SELECT * ";
			if (select != null && !StringUtils.isEmpty(select)) {
				ArrayList<String> selectParams = new ArrayList<String>(Arrays.asList(select.split(",")));
				String selectJPA = ODataExpressionParser.parseToJPASelectExpression(typeName, selectParams);
				selectClause = "SELECT " + selectJPA;
			}
			
			// from
			String fromClause = "FROM " + typeName;
			
			// $filter
			String whereClause = "";
			if(filter != null && !StringUtils.isEmpty(filter)) {
				FilterExpression filterExpression = UriParser.parseFilter(edm, entityType, filter);
				CommonExpression commonExpression = filterExpression.getExpression();
				String whereJPA = ODataExpressionParser.parseToJPAWhereExpression(commonExpression, typeName);
				whereClause = " WHERE " + whereJPA;
			}
			
			// $orderby
			String orderByClause = "";
			if(orderby != null && !StringUtils.isEmpty(orderby)) {
				OrderByExpression orderByExpression = UriParser.parseOrderBy(edm, entityType, orderby);
				String orderByJPA = ODataExpressionParser.parseToJPAOrderByExpression(orderByExpression, typeName);
				orderByClause = " ORDER BY " + orderByJPA;
			}

			
			// query
			String sql = selectClause + " " + fromClause + " " + whereClause + " " + orderByClause;
			query = (TypedQuery<Test>) entityManager.createNativeQuery(sql, testClass);
			setParametersOnQuery(query);


			if(count == true) {
				String sqlCount = "SELECT COUNT(*) FROM (" + sql + ")";
				javax.persistence.Query queryCount = entityManager.createNativeQuery(sqlCount);
				setParametersOnQuery(queryCount);
				countValue = ((java.math.BigInteger) queryCount.getSingleResult()).longValue();
			}
			
			
			// $skip
			query.setFirstResult(skip);
			
			// $top
			query.setMaxResults(top);
			
			

			
			
		} catch (ODataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		List<Test> pessoas = query.getResultList();

		ODataResponse<List<Test>> odataResponse = new ODataResponse<List<Test>>();
		odataResponse.Value = pessoas;
		if(count == true) {
			odataResponse.Count = countValue;
		}
		
    	return odataResponse;
    	
    }

	private void setParametersOnQuery(javax.persistence.Query q) {
		Map<Integer, Object> params = ODataExpressionParser.getPositionalParametersThreadLocal();
		if(params != null && params.size() > 0) {
			for (Map.Entry<Integer, Object> param : params.entrySet()) {
				q.setParameter(param.getKey(), param.getValue());
			}
		}
	}
    
    @Bean public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() { 
    	ObjectMapper mapper = new ObjectMapper(); 
    	mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false); 
    	MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(mapper); 
    	return converter; 
    }
    
}
