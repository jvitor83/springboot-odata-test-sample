package com.hackerrank.projects.repositories;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.olingo.odata2.api.exception.ODataApplicationException;
import org.apache.olingo.odata2.api.uri.expression.ExceptionVisitExpression;
import org.apache.olingo.odata2.api.uri.expression.FilterExpression;
import org.springframework.data.jpa.domain.Specification;

import com.hackerrank.projects.controllers.ExpressionBuilder;

public class GenericSpecification<T> implements Specification<T> {
	 
    private FilterExpression filterExpression;
    public GenericSpecification(FilterExpression filterExpression) {
    	this.filterExpression = filterExpression;
    }
 
    @Override
    public Predicate toPredicate
      (Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
    	
    	ExpressionBuilder expressionBuilder = new ExpressionBuilder(root, query, builder);
    	Predicate predicate = null;
		try {
			predicate = (Predicate)filterExpression.accept(expressionBuilder);
		} catch (ExceptionVisitExpression e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ODataApplicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
    	return predicate;
  /*
        if (filterExpression.equalsIgnoreCase(">")) {
            return builder.greaterThanOrEqualTo(
              root.<String> get(criteria.getKey()), criteria.getValue().toString());
        } 
        else if (criteria.getOperation().equalsIgnoreCase("<")) {
            return builder.lessThanOrEqualTo(
              root.<String> get(criteria.getKey()), criteria.getValue().toString());
        } 
        else if (criteria.getOperation().equalsIgnoreCase(":")) {
            if (root.get(criteria.getKey()).getJavaType() == String.class) {
                return builder.like(
                  root.<String>get(criteria.getKey()), "%" + criteria.getValue() + "%");
            } else {
                return builder.equal(root.get(criteria.getKey()), criteria.getValue());
            }
        }
        return null;
        */
    }
}
