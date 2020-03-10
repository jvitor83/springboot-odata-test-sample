package com.hackerrank.projects.controllers;

import java.util.List;
import java.util.Locale;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;

import org.apache.olingo.odata2.api.commons.HttpStatusCodes;
import org.apache.olingo.odata2.api.edm.EdmLiteral;
import org.apache.olingo.odata2.api.edm.EdmTyped;
import org.apache.olingo.odata2.api.exception.ODataApplicationException;
import org.apache.olingo.odata2.api.uri.expression.BinaryExpression;
import org.apache.olingo.odata2.api.uri.expression.BinaryOperator;
import org.apache.olingo.odata2.api.uri.expression.ExceptionVisitExpression;
import org.apache.olingo.odata2.api.uri.expression.ExpressionVisitor;
import org.apache.olingo.odata2.api.uri.expression.FilterExpression;
import org.apache.olingo.odata2.api.uri.expression.LiteralExpression;
import org.apache.olingo.odata2.api.uri.expression.MemberExpression;
import org.apache.olingo.odata2.api.uri.expression.MethodExpression;
import org.apache.olingo.odata2.api.uri.expression.MethodOperator;
import org.apache.olingo.odata2.api.uri.expression.OrderByExpression;
import org.apache.olingo.odata2.api.uri.expression.OrderExpression;
import org.apache.olingo.odata2.api.uri.expression.PropertyExpression;
import org.apache.olingo.odata2.api.uri.expression.SortOrder;
import org.apache.olingo.odata2.api.uri.expression.UnaryExpression;
import org.apache.olingo.odata2.api.uri.expression.UnaryOperator;
import org.apache.olingo.odata2.api.uri.expression.Visitable;

import com.hackerrank.projects.repositories.EntityManagerHolder;
import com.hackerrank.projects.repositories.GenericSpecification;

public class ExpressionBuilder<T> implements org.apache.olingo.odata2.api.uri.expression.ExpressionVisitor {

	
	// private CriteriaBuilder criteriaBuilder;
	
	public Root<T> root;
	public CriteriaQuery<?> criteriaQuery;
	public CriteriaBuilder criteriaBuilder;
	
	public ExpressionBuilder(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
		this.criteriaBuilder = builder;
		this.criteriaQuery = query;
		this.root = root;
		
		
	}
	
	
	@Override
	public Predicate visitFilterExpression(FilterExpression filterExpression, String expressionString, Object expression) {
		
		
		return (Predicate) expression;
	}

	@Override
	public Object visitBinary(BinaryExpression binaryExpression, BinaryOperator operator, Object left,
			Object right) {
		Object retorno = null;
		 if (operator == BinaryOperator.ADD
	        || operator == BinaryOperator.MODULO
	        || operator == BinaryOperator.MUL
	        || operator == BinaryOperator.DIV
	        || operator == BinaryOperator.SUB) {
	      retorno = null; //evaluateArithmeticOperation(operator, left, right);
	    } else if (operator == BinaryOperator.EQ
	        || operator == BinaryOperator.NE
	        || operator == BinaryOperator.GE
	        || operator == BinaryOperator.GT
	        || operator == BinaryOperator.LE
	        || operator == BinaryOperator.LT) {
	      retorno = evaluateComparisonOperation(operator, left, right);
	    } else if (operator == BinaryOperator.AND
	        || operator == BinaryOperator.OR) {
	    	retorno = evaluateBooleanOperation(operator, left, right);
		  }
		 
		 return retorno;
	}
	
	private Object evaluateBooleanOperation(BinaryOperator operator, Object left, Object right)
		     {
		  
		    if(operator == BinaryOperator.AND) {
		      return this.criteriaBuilder.and((Expression<Boolean>)left, (Expression<Boolean>)right);
		    } else {
		      return this.criteriaBuilder.or((Expression<Boolean>)left, (Expression<Boolean>)right);
		    }
		    
		}

	

	  private Object evaluateComparisonOperation(BinaryOperator operator, Object left, Object right) 
	       {
		  Predicate predicate = null;
	      if (operator == BinaryOperator.EQ) {
	    	  predicate = this.criteriaBuilder.equal((Expression<?>)left, right);
	      } else if (operator == BinaryOperator.NE) {
	    	  predicate = this.criteriaBuilder.notEqual((Expression<?>)left, right);
	      } else if (operator == BinaryOperator.GE) {
	    	  //predicate = this.criteriaBuilder.greaterThanOrEqualTo((Expression<?>)left, (Expression<?>)right);
	      } else if (operator == BinaryOperator.GT) {
	    	  //predicate = this.criteriaBuilder.greaterThan(left, right);
	      } else if (operator == BinaryOperator.LE) {
	        return null;
	      } else if (operator == BinaryOperator.LT) {
	        return null;
	      }
	      return predicate;
	  }


	@Override
	public Object visitOrderByExpression(OrderByExpression orderByExpression, String expressionString,
			List<Object> orders) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitOrder(OrderExpression orderExpression, Object filterResult, SortOrder sortOrder) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Expression<?> visitLiteral(LiteralExpression literal, EdmLiteral edmLiteral) {
		Expression<String> expression = this.criteriaBuilder.literal(literal.getUriLiteral());
		return expression;
	}

	@Override
	public Object visitMethod(MethodExpression methodExpression, MethodOperator method, List<Object> parameters) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitMember(MemberExpression memberExpression, Object path, Object property) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object visitProperty(PropertyExpression propertyExpression, String uriLiteral, EdmTyped edmProperty) {
		// TODO Auto-generated method stub
		// EntityType<T> modelType = root.getModel();
		// Class<T> classType = modelType.getBindableJavaType();
		
		
		
		// ParameterExpression<T> parameterExpression = this.criteriaBuilder.parameter(classType, uriLiteral);
		// return parameterExpression;
		return this.criteriaBuilder.literal(uriLiteral);
		
	}

	@Override
	public Object visitUnary(UnaryExpression unaryExpression, UnaryOperator operator, Object operand) {
		// TODO Auto-generated method stub
		return null;
	}


}
